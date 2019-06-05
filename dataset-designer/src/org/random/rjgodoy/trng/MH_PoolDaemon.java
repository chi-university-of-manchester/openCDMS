package org.random.rjgodoy.trng;

/**Information and configuration of the pool daemon.<P>
 *
 * The pool daemon is a thread which fetches some bits from the remote source whenever the quota is 1.000.000
 *
 * @author Javier Godoy
 */
public class MH_PoolDaemon {

	static boolean running;
	static long    blockSize=200000;
	static long    poolSize;

	/**Returns whether the PoolDaemon is running
	 * @return <code>true</code> if the pool daemon is running, <code>false</code> otherwise.*/
	public static boolean isRunning() {
		return running;
	}

	/**Returns the amount of bits that will be collected.<P>
	 * Collection is only performed when the quota is 1.000.000.
	 * @return What I have just said.*/
	public static long getBlockSize() {
		return blockSize;
	}

	/**Return the amount of bits available in the pool.
	 * @return What I have just said (note they are bits, not bytes).*/
	public static long getPoolSize() {
		return poolSize;
	}

	/**Sets the amount of bits that will be collected.<P>
	 * Collection is only performed when the quota is 1.000.000.
	 * The value must be non-negative and lower or equal than 200.000 (0 will disable the pooling, but it will not kill the daemon).
	 * The default value is 200.000.
	 *
	 * @param  blockSize the amount of <U>bits</U> that will be collected.
	 * @throws IllegalArgumentException if the value is negative or greater than 200.000
	 */

	public static void setBlockSize(long blockSize) {
		if (blockSize<0||blockSize>200000) throw new IllegalArgumentException();
		MH_PoolDaemon.blockSize = blockSize;
	}
}
