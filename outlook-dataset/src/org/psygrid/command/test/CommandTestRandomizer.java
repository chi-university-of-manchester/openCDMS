/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.command.test;

import java.net.ConnectException;

import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.www.xml.security.core.types.GroupType;

import randomX.randomHotBits;
import randomX.randomX;

public class CommandTestRandomizer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try{
        	RandomizationClient client = new RandomizationClient();
            StratifiedRandomizer rnd = createRandomizer();
            client.saveRandomizer(rnd, null);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public CommandTestRandomizer(){

    }

    public static StratifiedRandomizer createRandomizer() throws RandomizerException, ConnectException {
        StratifiedRandomizer rnd = new StratifiedRandomizer("CMT");

        Stratum s2 = new Stratum();
        s2.setName("centreNumber");
        for ( GroupType g: CMTGroups.allGroups() ){
            s2.getValues().add(g.getIdCode());
        }
        rnd.addStratum(s2);
        rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", 2, 4);
        int seedlen = (s2.getValues().size());
        long[] seeds= new long[seedlen];
        for(int i=0;i<seedlen;i++){
            seeds[i]=getSeed();
        }
        rnd.createRngs(seeds);

        rnd.addTreatment("Control","CMT-000");
        rnd.addTreatment("CTCH","CMT-001");

    	return rnd;
    }

	public static Long getSeed() throws ConnectException{
		//Random r = new Random((new Date()).getTime());
		//return new Long(r.nextLong());

		randomX randomizer = new randomHotBits();
		return randomizer.nextLong();

		/*
		 * random.org is no longer working...
		 *
        try{
            // Make a service
            org.random.www.RandomDotOrg_wsdl.RandomDotOrg service =
                new org.random.www.RandomDotOrg_wsdl.RandomDotOrgLocator();

            // Now use the service to get a stub which implements the SDI
            // (Service Definition Interface)
            org.random.www.RandomDotOrg_wsdl.RandomDotOrgPortType port =
                service.getRandomDotOrgPort();

            // Make the actual calls
            return port.mrand48();
        }
        catch(AxisFault fault){
            if ( fault.getCause() instanceof ConnectException ){
                throw (ConnectException)fault.getCause();
            }
            else if ( fault.getCause() instanceof UnknownHostException ||
                        fault.getCause() instanceof NoRouteToHostException ){
                ConnectException cex = new ConnectException(fault.getCause().getMessage());
                cex.initCause(fault.getCause());
                throw cex;
            }
            else{
                throw new RuntimeException(fault);
            }
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
        */
	}
}
