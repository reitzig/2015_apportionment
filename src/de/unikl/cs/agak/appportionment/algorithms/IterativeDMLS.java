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

/**
 * A naive implementation of divisor methods that assigns one seat after the other
 * using linear scan for each step.
 */
public class IterativeDMLS extends IterativeMethod {

    @Override
    public Apportionment apportion(final ApportionmentInstance instance, final DivisorMethod dm) {
        final int n = instance.votes.length;

        // Initialize current values
        final double[] values = new double[n];

        // Seed list with initial values
        for (int i = 0; i < n; i++) {
            values[i] = dm.d(0) / instance.votes[i];
        }

        // Subsequently assign seats
        final int[] seats = new int[n];
        int imin;
        int k = instance.k;
        while (k > 1) {
            // Find index with maximum value
            imin = 0;
            for (int i = 1; i < n; i++) {
                if (values[i] < values[imin]) imin = i;
            }

            seats[imin]++;
            values[imin] = dm.d(seats[imin]) / instance.votes[imin];
            k--;
        }

        // Find maximum for last seat
        imin = 0;
        for (int i = 1; i < n; i++) {
            if (values[i] < values[imin]) imin = i;
        }

        final double astar = values[imin];
        seats[imin]++;

        return determineTies(instance, dm, seats, astar);
    }
}
