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

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.EPSILON;
import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.closeToEqual;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
abstract public class SelectionBasedMethod extends LinearApportionmentMethod {
    SelectionBasedMethod(double alpha, double beta) {
        super(alpha, beta);
    }

    @Override
    final public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Compute $a^*$
        final double astar = unitSize(instance);

        // Derive seats
        final int[] seats = new int[n];
        for (int i = 0; i < n; i++) {
            seats[i] = (int) Math.floor(deltaInv(instance.votes[i] * astar) + EPSILON) + 1;
            // TODO use fuzzy floor when it can deal with negative parameters? Or just add 1 before rounding?
        }

        // Now we have *all* seats with value astar, which may be too many.
        // Identify ties for the last few seats!
        final int[] tiedSeats = new int[n];
        for (int i = 0; i < n; i++) {
            if (closeToEqual(d(seats[i] - 1) / instance.votes[i], astar)) {
                tiedSeats[i] = 1;
                seats[i] -= 1;
            }
            /* TODO If astar occurs only once, this creats a one-party tie.
                    That's not wrong, but do we want that? */
        }

        return new Apportionment(instance.k, seats, tiedSeats, astar);
    }

    /**
     * @param instance An instance of the apportionment problem.
     * @return The (reciprocal of the) proportionality constant (a*).
     */
    abstract double unitSize(ApportionmentInstance instance);
}