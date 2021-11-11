/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.unikl.cs.agak.appportionment.algorithms;

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.DivisorMethod;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.fuzzyEquals;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
abstract public class IterativeMethod implements ApportionmentAlgorithm {

    /**
     * Given <em>one</em> valid seat assignment, determine all possible ones
     * that result from different tie-breaking.
     * @param instance The original apportionment instance.
     * @param seats A valid seat assignment.
     * @param astar The (reciprocal of) the proportionality constant.
     * @return A symbolic representation of all valid seat assignments for the given instance.
     */
    Apportionment determineTies(final ApportionmentInstance instance, final DivisorMethod dm, final int[] seats, final double astar) {
        final int[] tiedSeats = new int[seats.length];

        // TODO can we do this faster?
	    int theOnlyTie = -1;
        for (int i = 0; i < seats.length; i++) {
            if ( seats[i] > 0 && fuzzyEquals(astar, dm.d(seats[i] - 1) / instance.votes[i]) ) {
                // Party i got a seat with value astar
                seats[i] -= 1;
                tiedSeats[i] = 1;
	            if (theOnlyTie == -1) theOnlyTie = i; else theOnlyTie = -42;
            }
            else if ( fuzzyEquals(astar, dm.d(seats[i]) / instance.votes[i]) ) {
                // Party i did *not* get a seat, but also has value astar
                // Note how we don't have to check the current value since the
                // sequences are increasing.
                tiedSeats[i] = 1;
	            if (theOnlyTie == -1) theOnlyTie = i; else theOnlyTie = -42;
            }
        }
	    if (theOnlyTie >= 0) {
		    tiedSeats[theOnlyTie] = 0;
		    seats[theOnlyTie] += 1;
	    }

	    return new Apportionment(instance.k, seats, tiedSeats, astar);
    }
}
