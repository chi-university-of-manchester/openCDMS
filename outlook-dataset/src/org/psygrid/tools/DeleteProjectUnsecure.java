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

package org.psygrid.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.psygrid.data.repository.client.RepositoryClient;

/**
 * Delete a project from the repository permanently.
 *
 * @author Rob Harper
 *
 */
public class DeleteProjectUnsecure {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        Long dataSetId = Long.parseLong(args[0]);
        String projectCode = args[1];

        System.out.println("About to permanently delete dataset with id="+dataSetId+" and project code="+projectCode+". This operation cannot be reversed.");
        System.out.println("Please confirm (type 'yes' to confirm):");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String str = in.readLine();
        if ( str.equals("yes") ){
            System.out.println("Deleting...");
        }
        else{
            System.out.println("Aborting.");
            return;
        }

        RepositoryClient client = new RepositoryClient();

        client.removePublishedDataSet(dataSetId, projectCode, null);
        System.out.println("Dataset deleted.");
    }

}
