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
import de.unikl.cs.agak.appportionment.experiments.AlgorithmWithCounter;

import static edu.princeton.cs.introcs.StdStats.sum;

/**
 * Implements the jump-and-step algorithm from
 * <dir>
  * Friedrich Pukelsheim<br/>
  * Proportional Representation<br/>
  * Springer, 2014<br/>
  * </dir>
 * The implementation uses a linear scan in each step.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class PukelsheimLS extends IterativeMethod implements AlgorithmWithCounter {
    /* Interface AlgorithmWithCounter and this member variable are only
     * for purposes of experiments. In a productive environment, remove both.
     */
    private int lastCounter = -1;

    public PukelsheimLS(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Compute initial assignment
        final int[] seats = new int[n];

        double sumPop = sum(instance.votes);

        final double D;
	    if ( isStationary() ) {
	        // Use recommended estimator (cf Pukelsheim, section 6.1) resp. its reciprocal
	        D = alpha * (instance.k + n * (beta / alpha - 0.5)) / sumPop;
	    } else if (beta / alpha <= 1.0) {
	        // Fallback to the universal estimator
	        D = alpha * instance.k / sumPop;
	    } else {
		    // Generalized estimator for sequences that are not real signposts
		    D = alpha * (instance.k + n * Math.floor(beta / alpha)) / sumPop;
	    }

        for (int i = 0; i < n; i++) {
            seats[i] = dRound(instance.votes[i] * D) + 1;
        }

        int sumSeats = sum(seats);
        lastCounter = instance.k - sumSeats;

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
	            if (step == +1 || seats[i] > 0) { // in s>k setting, skip parties without any seats
                values[i] = d(seats[i] + offset) / instance.votes[i];
	            }
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
	            if (step == -1 && seats[im] <= 0) {
		            // No more seats to remove, make sure never picked again
		            values[im] = Double.NEGATIVE_INFINITY;
	            } else {
		            values[im] = d(seats[im] + offset) / instance.votes[im];
	            }
                sumSeats += step;
            }

            // Compute astar; TODO can we do this smarter?
            double astar = 0.0;
            for (int i = 0; i < seats.length; i++) {
	            if (seats[i] == 0) continue;
                double cand = d(seats[i] - 1) / instance.votes[i];
                if (cand > astar) astar = cand;
            }

            return determineTies(instance, seats, astar);
        }
    }

    @Override
    public int getLastCounter() {
        return lastCounter;
    }

    @Override
    public String getCounterLabel() {
        return "missingSeats";
    }
}
