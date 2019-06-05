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
package org.psygrid.esl.randomise;

public class StrataStats {

    private String[][] strata = new String[0][0];
    
    private String[][] stats = new String[0][0];

    public String[][] getStats() {
        return stats;
    }

    public void setStats(String[][] stats) {
        this.stats = stats;
    }

    public String[][] getStrata() {
        return strata;
    }

    public void setStrata(String[][] strata) {
        this.strata = strata;
    }
    
}
