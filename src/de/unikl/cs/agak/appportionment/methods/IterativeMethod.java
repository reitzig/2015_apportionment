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

package de.unikl.cs.agak.appportionment.methods;

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.closeToEqual;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
abstract public class IterativeMethod extends LinearApportionmentMethod {

    public IterativeMethod(double alpha, double beta) {
        super(alpha, beta);
    }

    abstract public Apportionment apportion(ApportionmentInstance instance);

    Apportionment determineTies(final ApportionmentInstance instance, final int[] seats, final double astar) {
        final int[] tiedSeats = new int[seats.length];

        // TODO can we do this faster?
        for (int i = 0; i < seats.length; i++) {
            if ( closeToEqual(astar, d(seats[i] - 1) / instance.votes[i]) ) {
                // Party i got a seat with value astar
                seats[i] -= 1;
                tiedSeats[i] = 1;
            }
            else if ( closeToEqual(astar, d(seats[i]) / instance.votes[i]) ) {
                // Party i did *not* did also have value astar but did not get a seat
                // Note how we don't have to check the current value since the sequences are
                // incresing.
                tiedSeats[i] = 1;
            }
        }

        return new Apportionment(instance.k, seats, tiedSeats, astar);
    }
}
