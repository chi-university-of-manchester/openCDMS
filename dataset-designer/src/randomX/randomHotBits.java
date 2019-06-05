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
package randomX;

import java.net.*;
import java.io.*;
import randomX.*;

/**
    Implementation of a <b>randomX</b>-compliant class which obtains
    genuine random data from <a href="http://www.fourmilab.ch/">John
    Walker</a>'s <a href="http://www.fourmilab.ch/hotbits/">HotBits</a>
    radioactive decay random sequence generator.

    <p>
    Designed and implemented in July 1996 by
    <a href="http://www.fourmilab.ch/">John Walker</a>.
*/

public class randomHotBits extends randomX {
    long state;
    int nuflen = 256, buflen = 0;
    byte[] buffer;
    int bufptr = -1;

    //  Constructors

    /** Creates a new random sequence generator.  */

    public randomHotBits() {
        buffer = new byte[nuflen];
    }

    /*  Private method to fill buffer from HotBits server.  */

    private void fillBuffer()
        throws java.io.IOException
    {
        URL u = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes=128&fmt=bin");
        InputStream s = u.openStream();
        int l;

        buflen = 0;
        while ((l = s.read()) != -1) {
            buffer[buflen++] = (byte) l;
        }
        s.close();
        bufptr = 0;
    }

    /** Get next byte from generator.

@return the next byte from the generator.
    */

    public byte nextByte()  {
        try {
            synchronized (buffer) {
                if (bufptr < 0 || bufptr >= buflen) {
                    fillBuffer();
                }
                return buffer[bufptr++];
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot obtain HotBits");
        }
    }
};
