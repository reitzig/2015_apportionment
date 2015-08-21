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
import static edu.princeton.cs.introcs.StdStats.sum;

/**
 * Implements the jump-and-step algorithm from
 * <p/>
 * Friedrich Pukelsheim
 * Proportional Representation
 * Springer, 2014
 * <p/>
 * The implementation uses a linear scan in each step steps.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class PukelsheimLS extends IterativeMethod {

    public PukelsheimLS(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Compute initial assignment using guess sum(population)/k
        final int[] seats = new int[n];

        double sumPop = sum(instance.votes);

        final double D;
        if (beta <= alpha) {
          /* In this case, we have a stationary divisor method and
           * can use Pukelsheim's recommended divisor (cf section 6.1),
           * resp. it's reciprocal.
           * Note that r = alpha/beta follows from
           *    s(n+1) = d_n = alpha * n + beta
           * which normalizes to
           *    s(n) = n - 1 + beta/alpha.
           */
            D = (instance.k + n * (beta / alpha - 0.5)) / sumPop;
        } else {
            // Fallback to the universal estimator
            D = instance.k / sumPop;
        }

        for (int i = 0; i < n; i++) {
            seats[i] = (int) Math.floor(deltaInv(instance.votes[i] * D) + EPSILON) + 1;
            // TODO correct? use fuzzy floor when it can deal with negative parameters?
        }

        int sumSeats = sum(seats);
        // TODO log instance.k - sumSeats

        final int order;
        final int offset;
        final int step;
        if (sumSeats == instance.k) {
            // seats and tiedSeats are already correct since we assign *all* seats with value a*
            return new Apportionment(instance.k, seats, new int[n], D);
        } else {
            if (sumSeats < instance.k) {
                // Setup: max-heap, offset for next d_i, add seats
                order = 1;
                offset = 0;
                step = +1;
            } else { // s > k
                // Setup: min-heap, offset for previous d_i, remove seats
                order = -1;
                offset = -1;
                step = -1;
            }


            // Initialize current values
            final double[] values = new double[n];

            // Seed list with initial values
            for (int i = 0; i < n; i++) {
                values[i] = d(seats[i] + offset) / instance.votes[i];
            }

            // Subsequently assign seats
            int im;
            while (sumSeats != instance.k) {
                // Find index with maximum value
                im = 0;
                for (int i = 1; i < n; i++) {
                    if (order * values[i] < order * values[im]) im = i;
                }

                seats[im] += step;
                values[im] = d(seats[im] + offset) / instance.votes[im];
                sumSeats += step;
            }

            // Compute astar; TODO can we do this smarter?
            double astar = 0.0;
            for (int i = 0; i < seats.length; i++) {
                double cand = d(seats[i] - 1) / instance.votes[i];
                if (cand > astar) astar = cand;
            }

            return determineTies(instance, seats, astar);
        }
    }
}
