package org.random.rjgodoy.trng;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/** <tt>SecureRandomSpi</tt> implementation, which uses the True Random Number generator from
* <A href="http://www.random.org">www.random.org</A>.
*
* Before the first instance of this class is constructed, several global System properties must be defined for configuring the shared the HTTP connection (which cannot be modified later).
* Other properties are related to the TRNG behaviour, then they are instance-specific and may be specified before constructing each instance.
* See {@link MH_SecureRandom}.<P>
*
* @author Javier Godoy
*/

//TODO count how many SPIs are using the daemon

public class MH_SecureRandomSpi extends SecureRandomSpi {

	private final static long           NAP        = 2*60*1000; //how long to sleep between quota requests
	private final static long           FULL_QUOTA = 1000000;      //full quota which will trigger the pool daemon.

	private final static ReentrantLock  LOCK        = new ReentrantLock(true); //a lock with a fair policy (used for controlling access to the HTTP client)
	private final static ReentrantLock  DAEMON_LOCK = new ReentrantLock(true); //a lock with a fair policy (used for controlling access to the shared pool)
	private final static Log            LOG         = LogFactory.getLog("MH_SecureRandomSpi");
	private final static Log            LOG_DAEMON  = LogFactory.getLog("MH_Daemon");
	private final static ByteBuffer     buffer      = (ByteBuffer) ByteBuffer.wrap(new byte[8192]).limit(0);
	private static       MH_HttpClient  httpClient;
	private final static AtomicInteger  instances    = new AtomicInteger(0);
	private final static AtomicInteger  using_daemon = new AtomicInteger(0);
	private final static Queue<byte[]>  pool         = new LinkedList<byte[]>();

	private boolean uses_deamon;
	private SecureRandom   prng = null;
	private static Thread  daemon = null;
	private FallbackPolicy fallback;
	private GeneratorMode  mode;

	/**Initializes an instance of <tt>MH_SecureRandomSpi</tt>.<P>
	 *
	 * The execution of this constructor is synchronized with the MH_SecureRandom class object for reading system properties
	 * @throws NoSuchAlgorithmException if {@link MH_SecureRandom#INSTANCE_PRNG_ALGORITHM org.random.rjgodoy.trng.prng_algorithm} was specified, but no provider implements such algorithm.
	 * @throws NoSuchProviderException  if {@link MH_SecureRandom#INSTANCE_PRNG_PROVIDER org.random.rjgodoy.trng.prng_provider} wsas specified, but it is not installed
	 * @throws IllegalArgumentException  if any of {@link MH_SecureRandom#INSTANCE_MODE org.random.rjgodoy.trng.mode}, {@link MH_SecureRandom#INSTANCE_FALLBACK org.random.rjgodoy.trng.fallback} is specified and it is invalid
	 */
	public MH_SecureRandomSpi() throws NoSuchAlgorithmException, NoSuchProviderException{
		instances.incrementAndGet();

		String prng_algorithm, prng_provider, fallback, mode, pool_daemon;

		synchronized (MH_SecureRandom.class) {
			if (httpClient==null) httpClient = new MH_HttpClient();
			prng_algorithm     = System.getProperty(MH_SecureRandom.INSTANCE_PRNG_ALGORITHM);
			prng_provider      = System.getProperty(MH_SecureRandom.INSTANCE_PRNG_PROVIDER);
			fallback           = System.getProperty(MH_SecureRandom.INSTANCE_FALLBACK);
			mode               = System.getProperty(MH_SecureRandom.INSTANCE_MODE);
			pool_daemon        = System.getProperty(MH_SecureRandom.POOL_DAEMON);
		}

		this.mode     = mode == null?GeneratorMode.TRNG:GeneratorMode.valueOf(mode);
		this.fallback = fallback == null?FallbackPolicy.TRNG:FallbackPolicy.valueOf(fallback);

		LOG.info("mode="+this.mode+"; fallback="+this.fallback);

		if (this.mode==GeneratorMode.TRNG&&this.fallback==FallbackPolicy.TRNG) {
			if (prng_provider!=null)  LOG.warn(MH_SecureRandom.INSTANCE_PRNG_PROVIDER+" specified, but mode=TRNG and fallback=TRNG");
			if (prng_algorithm!=null) LOG.warn(MH_SecureRandom.INSTANCE_PRNG_ALGORITHM+" specified, but mode=TRNG and fallback=TRNG");
		} else {
			if (prng_provider!=null)
				prng = SecureRandom.getInstance(prng_algorithm, prng_provider);
			 else if (prng_algorithm!=null)
				prng = SecureRandom.getInstance(prng_algorithm);
			else
				prng = new SecureRandom();
			if (prng!=null) {
				LOG.info("PRNG ["+prng.getProvider()+":"+prng.getAlgorithm()+"]");
			}
		}

		this.uses_deamon=pool_daemon!=null && pool_daemon.equalsIgnoreCase("TRUE");
		//initializes the pool daemon
		if (uses_deamon&&using_daemon.getAndIncrement()==0) {
			  DAEMON_LOCK.lock();
				try {
			   if (daemon==null) {
					LOG_DAEMON.trace("new instance");
					daemon = new Thread() {
						long last_full_quota = 0;
						DateFormat df = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ss");

					 @Override
					 public void run() {
						LOG_DAEMON.info("Started");
						Thread.currentThread().setName("MH-TRNG-Daemon");
						Thread.currentThread().setPriority(MAX_PRIORITY);
						LOG_DAEMON.info(this);
						try {
							MH_PoolDaemon.running=true;
							while (!isInterrupted()) {
								LOCK.lock();
								try {
									long quota = httpClient.estimateQuota();
									if (quota>0&&quota<FULL_QUOTA) quota = httpClient.checkQuota();
									LOG_DAEMON.debug("Quota="+quota+"; Available="+MH_PoolDaemon.poolSize);

									if (quota==FULL_QUOTA) {

										long now = System.currentTimeMillis();
										if (LOG.isInfoEnabled()){
											String s = "full quota at "+df.format(new Date(now));
											if (last_full_quota!=0)
												s+="; last full quota at "+df.format(new Date(last_full_quota));
											LOG.info(s);
										}
										last_full_quota=now;

										int blockSize = (int)Math.ceil(MH_PoolDaemon.blockSize/8.0);
										while (blockSize>=8192) {
												int length = fetch(8192);
												if (length<=0) break;
												blockSize-=length;
											}
										if (blockSize>0)
											fetch(blockSize);
									}
								}
								finally {LOCK.unlock();
								}
								LOG_DAEMON.debug("Idle");
								Thread.sleep(NAP);
							}
						} catch (InterruptedException e) {;}
						finally {MH_PoolDaemon.running=false;}
						LOG_DAEMON.info("Stopped");
					}

					private int fetch(int nbytes) {
						byte array[] = new byte[8192];
						int length = httpClient.nextBytes(array,0,nbytes);
						if (length<=0) return 0;
						if (length<nbytes) {
							byte array2[] = new byte[length];
							System.arraycopy(array, 0, array2, 0, length);
							array=array2;
						}
						DAEMON_LOCK.lock();
						try {
							pool.add(array);
							MH_PoolDaemon.poolSize+=length*8;
							LOG_DAEMON.debug("Available: "+MH_PoolDaemon.poolSize);
						}
						finally {DAEMON_LOCK.unlock();}
						return length;
					}
				 };
				 DAEMON_LOCK.unlock();
				 httpClient.estimateQuota();
				 daemon.start();
				 Thread.yield();
				}}
				finally {if(DAEMON_LOCK.isHeldByCurrentThread()) DAEMON_LOCK.unlock();}
			}


	}

	/** Returns the given number of seed bytes. This call may be used to seed other random number generators.<P>
	 * This method invoked {@link #engineNextBytes(byte[])} and pass it a byte array of length <code>numBytes</code>.
	 *
	 * @param numBytes the number of seed bytes to generate.
	 * @return the seed bytes.
	 * @throws ProviderException see {@link #engineNextBytes(byte[])}
	 */
	@Override
	protected byte[] engineGenerateSeed(int numBytes) {
		LOG.trace("engineGenerateSeed("+numBytes+")");
		byte[] bytes = new byte[numBytes];
		engineNextBytes(bytes);
		return bytes;
	}

	/**Reseeds this random object.
	 * The given seed supplements, rather than replaces, the existing seed. Thus, repeated calls are guaranteed never to reduce randomness.<P>
	 * If a PRNG is being used, this method invokes the PNRG's {@link SecureRandom#setSeed(byte[])} method with the given argument.
	 * If no PRNG is being used, this method does nothing.
	 * @param seed the seed.
	 */
	@Override
	protected void engineSetSeed(byte[] seed) {
		LOG.trace("engineSetSeed(byte["+seed.length+"})");
		if (prng!=null) prng.setSeed(seed);
	}

	private static String bufferStatus() {
		return "buffer [capacity="+buffer.capacity()+" remaining="+buffer.remaining()+"]";
	}

	private int fillFromPool(byte[] array) {
		DAEMON_LOCK.lock();
		try {
			if (MH_PoolDaemon.poolSize==0) return 0;
			byte array2[] = pool.poll();
			if (array2==null) return 0;
			int length = Math.min(array2.length,array.length);
			System.arraycopy(array2, 0, array, 0, length);
			MH_PoolDaemon.poolSize-=array2.length*8;
			return length;
		} finally {DAEMON_LOCK.unlock();}
	}

	private void fillBuffer() {
		LOG.trace("fillBuffer()");
		long quota=httpClient.estimateQuota();
		LOG.info("Quota="+(quota==Long.MIN_VALUE?"N/A":quota));
		buffer.clear();
		byte[] array = buffer.array();
		if (quota<0) //not enough quota
			switch (fallback) {
			   case PRNG: prng.nextBytes(array); break;
			   case TRNG: {
				   while (quota<0) {
				  	 LOCK.unlock();
					   try {Thread.sleep(NAP);}
					   catch (InterruptedException e) {
					  	 throw new ProviderException(e);
					   }
				  	 LOCK.lock();
				  	 quota=httpClient.estimateQuota();
				 		 LOG.info("Quota="+(quota==Long.MIN_VALUE?"N/A":quota));
				   }
			   }
		};

		int length=0;

		if (uses_deamon&&MH_PoolDaemon.poolSize>0) {
			length=fillFromPool(array);
		}

		if (length==0)
			length = httpClient.nextBytes(array,0,array.length);

		if (length==0 && fallback==FallbackPolicy.PRNG) {
			prng.nextBytes(array);
		  length=array.length;
		}

		if (mode==GeneratorMode.TRNG_XOR_PRNG) {
				byte[] random = new byte[length];
				prng.nextBytes(random);
				for (int i=0;i<length;i++)
					array[i]^=random[i];
			}
		buffer.limit(length);
		if (LOG.isDebugEnabled()) LOG.debug(bufferStatus());
		if (length==0) throw new ProviderException("The random server returned 0 bits");
	}

	private void _engineNextBytes(byte[] bytes) {
		LOG.trace("_engineNextBytes(byte["+bytes.length+"])");
		int length = bytes.length;
		int offset = 0;
		int remaining = Math.min(buffer.remaining(),length);
		buffer.get(bytes, offset, remaining);
		offset+=remaining;
		length-=remaining;
		while (length>0) {
			fillBuffer();
			remaining = Math.min(buffer.remaining(),length);
			buffer.get(bytes, offset, remaining);
			offset+=remaining;
			length-=remaining;
			if (LOG.isTraceEnabled()) LOG.debug(bufferStatus());
		}
	}

	/**
	 * Generates a user-specified number of random bytes.<P>
	 * If a call to {@link #engineSetSeed(byte[])} had not occurred previously,
	 * the first call to this method forces this <tt>SecureRandom</tt> implementation to seed itself.
	 * This self-seeding will not occur if engineSetSeed was previously called.
	 *
	 * @param bytes the array to be filled in with random bytes.
	 *
	 * @throws ProviderException if fallback is {@link FallbackPolicy#TRNG TRNG},
	 *                           there are no available bytes,
	 *                           and the thread is interrupted while waiting for the quota top-up.
	 */	@Override
	protected void engineNextBytes(byte[] bytes) {
		LOG.trace("engineNextBytes(byte["+bytes.length+"})");
		LOCK.lock();
		try {
			if (buffer.remaining()>=bytes.length) {
				//serves bytes from the pool
				buffer.get(bytes);
				LOG.debug(bufferStatus());
			} else {
				//fetch fresh bytes
				_engineNextBytes(bytes);
				if (LOG.isDebugEnabled()&&!LOG.isTraceEnabled())
					LOG.debug(bufferStatus());
			}
		} finally {
			if (LOCK.isHeldByCurrentThread()) LOCK.unlock();
		}
	}

	/** Called by the garbage collector on an object when garbage collection determines that there are no more references to the object.<P>
	 *  Closes the shared socket upon finalization.
	 *  @throws Throwable */
	@Override
	protected void finalize() throws Throwable {
		if (instances.decrementAndGet()==0) httpClient.closeSocket();
		if (uses_deamon) if (using_daemon.decrementAndGet()==0) {
			DAEMON_LOCK.lock();
			try {
				daemon.interrupt();
				daemon=null;
			}
			finally {
				DAEMON_LOCK.unlock();
			}
		}
		super.finalize();
	}
}
