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

public class IterativeDMLS extends IterativeMethod {

    public IterativeDMLS(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Initialize current values
        final double[] values = new double[n];

        // Seed list with initial values
        for (int i = 0; i < n; i++) {
            values[i] = d(0) / instance.votes[i];
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
            values[imin] = d(seats[imin]) / instance.votes[imin];
            k--;
        }

        // Find maximum for last seat
        imin = 0;
        for (int i = 1; i < n; i++) {
            if (values[i] < values[imin]) imin = i;
        }

        final double astar = values[imin];
        seats[imin]++;

        return determineTies(instance.k, seats, astar);
    }
}
