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


package org.psygrid.collection.entry.chooser;

import java.util.List;

/**
 * Interface representing anything that can be "chosen" in a
 * Chooser.
 * <p>
 * A Chooser is used to display a list of Choosables to the user.
 * Selecting a Choosable will result in the list being refreshed
 * to show the children of that Choosable, unless the Choosable
 * is at the bottom of the hierarchy and its selection triggers
 * the closing of the Chooser and the commencement of further
 * processing.
 * 
 * @author Rob Harper
 *
 */
public interface Choosable   {
    
	/**
	 * Get the text that will be rendered when the Choosable is
	 * displayed in a Chooser.
	 * 
	 * @return The text.
	 */
    public String getDisplayText();
    
    /**
     * Get the description - additional help text - that will be
     * rendered when the Choosable is displayed in a Chooser.
     * 
     * @return The description (help text).
     */
    public String getDescription();
    
    /**
     * Get the type of the Choosable.
     * <p>
     * The type is an enumeration that describes what kind of object
     * the Choosable is. Often used in a Chooser to decide when the
     * correct type of Choosable has been selected to facilitate
     * further processing.
     * 
     * @return The type of the Choosable.
     */
    public ChoosableType getType();
    
    /**
     * This states whether a Choosable object has been locked i.e.
     * it is not permitted for it to be selected.
     * 
     * @return boolean
     */
    public boolean isLocked();
    
    /**
     * Get the parent of the Choosable i.e. the Choosable object
     * imeediately above it in a hierarchy of Choosables.
     * 
     * @return The parent.
     */
    public Choosable getParent();
    
    /**
     * Mostly applicable to Documents, this states whether
     * a Choosable object has been completed.
     * 
     * @return boolean
     */
    public boolean isComplete();
    
    /**
     * Returns the children of the current Choosable. Depending on the
     * implementor of the interface, it may be possible for this operation
     * to take a relatively long time. The reason for this is that implementors
     * are free to retrieve the children from slower mediums (network, disk,
     * etc.). Therefore, depending on the circumstances, it may be worth
     * calling it on a separate thread of execution.
     * 
     * @return children of the current Choosable.
     * @throws ChoosableException if a problem occurs while retriving the
     * children. If an underlying exception caused the problem, then this
     * can be retrieved with ChoosableException#getCause.
     */
    public List<? extends Choosable> getChildren() throws ChoosableException;
    
}
