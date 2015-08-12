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

public class IterativeDMLS extends LinearApportionmentMethod {

    public IterativeDMLS(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final double[] votes, int k) {
        // Initialize current values
        final double[] values = new double[votes.length];

        // Seed list with initial values
        for (int i = 0; i < votes.length; i++) {
            values[i] = d(0) / votes[i];
        }

        // Subsequently assign seats
        final int[] seats = new int[votes.length];
        int imin = 0;
        while (k > 1) {
            // Find index with maximum value
            imin = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] < values[imin]) imin = i;
            }

            seats[imin]++;
            values[imin] = d(seats[imin]) / votes[imin];
            k--;
        }

        // Find maximum for last seat
        imin = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[imin]) imin = i;
        }

        final double astar = values[imin];
        seats[imin]++;

        return new Apportionment(seats, astar);
    }
}
