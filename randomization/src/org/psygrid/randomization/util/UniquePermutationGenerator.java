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

package org.psygrid.randomization.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to generate unique permutations of an array of strings.
 * <p>
 * This is not very efficient at the moment as first all permutations
 * are generated, then unique permutations are taken from these.
 * 
 * @author Rob Harper
 *
 */
public class UniquePermutationGenerator {

    public List<String> getUniquePermutations(String[] elements){
        int[] indices;
        PermutationGenerator x = new PermutationGenerator (elements.length);
        StringBuffer permutation;
        Set<String> unique = new LinkedHashSet<String>();
        while (x.hasMore ()) {
            permutation = new StringBuffer ();
            indices = x.getNext ();
            for (int i = 0; i < indices.length; i++) {
                if ( i > 0 ){
                    permutation.append(",");
                }
                permutation.append(elements[indices[i]]);
            }
            //add this permutation to a set - this is an easy way to get
            //just the unique permutations
            unique.add(permutation.toString());
        }
        List<String> l = new ArrayList<String>();
        for ( String s: unique ){
            l.add(s);
        }
        return l;
    }
    
}
