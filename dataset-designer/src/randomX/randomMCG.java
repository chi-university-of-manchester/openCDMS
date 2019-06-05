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

import randomX.*;

/**
    Implementation of a <b>randomX</b>-compliant class using the
    "Minimal Standard" multiplicative congruential generator of Park
    and Miller.  [Park, S.K. and K.W. Miller, <cite>Communications of
    the ACM</cite> <b>31</b>, 1192-1201 (1988).]

    <p>
    The generation algorithm is:

    <p>
    <center>
        <em>I<sub>j+1</sub></em> = (<em>I<sub>j</sub></em> � 16807) & 0x7FFFFFFF
    </center>

    <p>
    Note that the intermediate value of the multiplication by 16807
    (7<sup>5</sup>) exceeds that representable in 32 bits; this has
    deterred use of this generator in most portable languages.
    Fortunately, Java's <tt>long</tt> type is guaranteed to be
    64 bits, so a straightforward and portable implementation is
    possible.

    <p>
    Designed and implemented in July 1996 by
    <a href="http://www.fourmilab.ch/">John Walker</a>
*/

public class randomMCG extends randomX {
    long state;

    //  Constructors

    /** Creates a new pseudorandom number generator, seeded from
        the current time. */

    public randomMCG() {
        this.setSeed(System.currentTimeMillis());
    }

    /** Creates a new pseudorandom number generator with a
        specified nonzero seed.

@param seed initial seed for the generator
    */

    public randomMCG(long seed) throws IllegalArgumentException {
        this.setSeed(seed);
    }

    //  Seed access

    /** Set seed for generator.  Subsequent values will be based
        on the given nonzero seed.

@param seed seed for the generator
    */

    public void setSeed(long seed) throws IllegalArgumentException {
        int i;

        if (seed == 0) {
            throw new IllegalArgumentException("seed must be nonzero");
        }
        super.setSeed();              // Notify parent seed has changed
        state = seed & 0xFFFFFFFFL;
        for (i = 0; i < 11; i++) {
            nextByte();
        }
    }

    /** Get next byte from generator.

@return the next byte from the generator.
    */

    public byte nextByte() {
        state = (state * 16807) & 0x7FFFFFFFL;
        return (byte) ((state >> 11) & 0xFF);
    }
};
