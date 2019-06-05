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


package org.psygrid.collection.entry.jeval;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.jeval.Evaluator;

/**
 * Class containing helper methods for using the JEval 
 * functional expression parsing and evaluation library.
 * 
 * @author Rob Harper
 *
 */
public class JEvalHelper {

	public static final String VARIABLE_OPEN = "#{";
	public static final String VARIABLE_CLOSE = "}";
	
	private static final Set<String> reservedWords = createReservedWordsList();
	
	public static String escapeVariable(String var){
		return VARIABLE_OPEN + var + VARIABLE_CLOSE;
	}
	
	/**
	 * Helper method to escape the variables in a formula so that it is
	 * suitable for evaluation by JEval.
	 * </p>
	 * For example, the formula a+b+c becomes #{a}+#{b}+#{c}.
	 * 
	 * @param formula The formula to escape.
	 * @param varList The list of variables that the formula contains.
	 * @return The formula, with variables escaped.
	 */
    public static String escapeVariablesInFormula(String formula, List<String> varList){
    	//Step 1: arrange variables in order of descending number of chars
    	//This is so we can do a simple search and replace in the formula
    	//without having to worry about (for instance) replacing "a" before "a1"
    	Collections.sort(varList, new StringLengthComparator());
    	
    	//Step 2: do the replacement to escape variables for JEval e.g. "a" -> "#{a}"
    	for ( String variable: varList ){
    		int pos = 0;
    		while ( pos<formula.length() ){
    			//first, check to see if the char at the current position is the start 
    			//of an escaped variable
    			if ( formula.substring(pos, pos+1).equals(VARIABLE_OPEN.substring(0, 1))){
    				//try to match the rest of the start variable string
    				boolean match = true;
    				for ( int j=1, d=VARIABLE_OPEN.length(); j<d; j++ ){
    					if ( pos+j >= formula.length() || !formula.substring(pos+j, pos+j+1).equals(VARIABLE_OPEN.substring(j,j+1))){
    						match = false;
    					}
    				}
    				if ( match ){
    					//found the start of an escaped variable - find the end of it
    					//so we can fast-forward over this variable
    					pos = formula.indexOf(VARIABLE_CLOSE, pos) + VARIABLE_CLOSE.length();
    					continue;
    				}
    			}
    			
    			//next, check to see if the char at the current position is the start of
    			//a reserved word
    			boolean reservedWord = false;
    			for ( String rw: reservedWords ){
        			if ( formula.substring(pos, pos+1).equals(rw.substring(0, 1))){
        				//try to match the rest of the reserved word string
        				boolean match = true;
        				for ( int j=1, d=rw.length(); j<d; j++ ){
        					if ( pos+j >= formula.length() || !formula.substring(pos+j, pos+j+1).equals(rw.substring(j,j+1))){
        						match = false;
        					}
        				}
        				if ( match ){
        					//found the start of an escaped variable - find the end of it
        					//so we can fast-forward over this variable
        					pos = pos + rw.length();
        					reservedWord = true;
        					break;
        				}
        			}
    			}
    			if ( reservedWord ){
    				continue;
    			}
    			
    			//finally, check to see if the char at the current position is the start 
    			//of the variable that we are looking for
    			if ( formula.substring(pos, pos+1).equals(variable.substring(0, 1))){
    				//try to match the rest of the variable string
    				boolean match = true;
    				for ( int j=1, d=variable.length(); j<d; j++ ){
    					if ( pos+j >= formula.length() || !formula.substring(pos+j, pos+j+1).equals(variable.substring(j,j+1))){
    						match = false;
    					}
    				}
    				if ( match ){
    					//a complete match for this variable has been found so
    					//escape the variable
        				String start = formula.substring(0, pos);
        				String end = formula.substring(pos+variable.length());
        				formula = start + escapeVariable(variable) + end;
        				//fast forward the position to the position after the end of
        				//the (now escaped) variable
        				pos = pos + VARIABLE_OPEN.length() + variable.length() + VARIABLE_CLOSE.length();
    					continue;
    				}
    			}
    			
				//not a complete match for either an already escaped variable or the variable
    			//we are currently searching for, so increment pos to start the search again
				//at the next position
				pos++;

    		}
    		
    	}
    	
    	return formula;
    }
    
    /**
     * Create a list of reserved words in JEval formulae.
     * 
     * @return List of reserved words.
     */
    private static final Set<String> createReservedWordsList(){
    	Set<String> rwl = new HashSet<String>();
    	Evaluator eval = new Evaluator();
    	for ( Object o: eval.getFunctions().keySet() ){
    		rwl.add((String)o);
    	}
    	//add custom functions
    	rwl.add("if");
    	return rwl;
    }
}
