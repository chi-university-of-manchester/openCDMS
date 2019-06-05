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

import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionException;

/**
 * JEval function to perform an "if" evaluation.
 * <p>
 * Should be called with three arguments e.g. "if(a,b,c)". If
 * a evaluates to true (greater than zero) then the expression evaluates
 * to b; if a evaluates to false (less than or equal to zero) then the 
 * expression evaluates to c.
 * 
 * @author Rob Harper
 *
 */
public class If implements Function {

	public String execute(Evaluator evaluator, String arguments) throws FunctionException {
		String[] args = arguments.split(",");
		if ( 3 != args.length ){
			throw new FunctionException("Three arguments are required");
		}
		try{
			double exp = Double.parseDouble(args[0]);
			if ( exp > 0 ){
				return args[1];
			}
			else{
				return args[2];
			}
		}
		catch(NumberFormatException nfe){
			throw new FunctionException("The first argument does not evaluate to a number");
		}
	}

	public String getName() {
		return "if";
	}

}
