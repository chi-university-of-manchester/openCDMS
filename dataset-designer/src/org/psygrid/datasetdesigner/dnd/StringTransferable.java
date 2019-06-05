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
package org.psygrid.datasetdesigner.dnd;

import java.awt.datatransfer.*;

import java.io.*;

import java.util.*;


/***
  * Class for transferal of data in String Format and plain text format
  *
  */
public class StringTransferable implements Transferable, ClipboardOwner {
    /**
     * Plain text flavor
     */
    public static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;

    /**
     * String flavor
     */
    public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;

    /**
     * Flavors for data transfer
     */
    public static final DataFlavor[] flavors = {
            StringTransferable.plainTextFlavor,
            StringTransferable.localStringFlavor
        };

    /**
     * list of flavors supported
     */
    private static final List flavorList = Arrays.asList(flavors);

    /**
     * String to be transferred
     */
    final private String string;

    /**
     * Constructor.
     * Sets the string value
     */
    public StringTransferable(String string) {
        this.string = string;
    }

    /**
       * Do nothing; must implement
       * @param flavor
       */
    private void dumpFlavor(DataFlavor flavor) {
    }

    /**
     * Return supported flavors
     */
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * If flavor is contained in supproted list, return true; else return false
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavorList.contains(flavor);
    }

    /**
     * Get the data to transfer
     */
    public synchronized Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
        dumpFlavor(flavor);

        if (flavor.equals(StringTransferable.plainTextFlavor)) {
            return new ByteArrayInputStream(this.string.getBytes("Unicode"));
        } else if (StringTransferable.localStringFlavor.equals(flavor)) {
            return this.string;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
       * Return simple transferable string
       */
    public String toString() {
        return "StringTransferable";
    }

    /**
     * Do nothing; must be implemented
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
