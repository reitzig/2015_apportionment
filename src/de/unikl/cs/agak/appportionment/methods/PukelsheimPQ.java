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

import java.util.PriorityQueue;
import java.util.Comparator;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.EPSILON;
import static edu.princeton.cs.introcs.StdStats.sum;

/**
 * Implements the jump-and-step algorithm from
 * <p/>
 * Friedrich Pukelsheim
 * Proportional Representation
 * Springer, 2014
 * <p/>
 * The implementation uses a priority queue for (asymptotically) efficient steps.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class PukelsheimPQ extends LinearApportionmentMethod {

    public PukelsheimPQ(final double alpha, final double beta) {
        super(alpha, beta);
    }


    @Override
    public Apportionment apportion(final double[] votes, int k) {
        // Compute initial assignment using guess sum(population)/k
        final int[] seats = new int[votes.length];
        final int[] tiedSeats = new int[votes.length];
        final double astar;

        double sumPop = sum(votes);

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
            D = (k + votes.length * (beta / alpha - 0.5)) / (sumPop);
        } else {
            // Fallback to the universal estimator
            D = k / sumPop;
        }

        for (int i = 0; i < votes.length; i++) {
            seats[i] = (int) Math.floor(deltaInv(votes[i] * D) + EPSILON) + 1;
            // TODO correct? use fuzzy floor when it can deal with negative parameters?
        }

        int sumSeats = sum(seats);

        final int order;
        final int offset;
        final int step;
        if (sumSeats == k) {
            // seats and tiedSeats are already correct
            astar = D;
        } else {
            if (sumSeats < k) {
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


            // Initialize heap
            final PriorityQueue<Entry> heap = new PriorityQueue<>(votes.length,
                    new Comparator<Entry>() {
                        @Override
                        public int compare(final Entry e1, final Entry e2) {
                            return order * Double.compare(e1.value, e2.value);
                        }
                    });

            // Seed heap with initial values
            for (int i = 0; i < votes.length; i++) {
                heap.add(new Entry(i, d(seats[i] + offset) / votes[i]));
            }

            // Subsequently adapt seats
            while (sumSeats != k) {
                final Entry e = heap.poll();
                final int i = e.index;
                seats[i] += step;
                e.value = d(seats[i] + offset) / votes[i];
                heap.add(e);
                sumSeats += step;
            }

            // Compute astar; TODO can we do this smarter?
            double astarT = 0.0;
            for (int i = 0; i < seats.length; i++) {
                double cand = d(seats[i] - 1) / votes[i];
                if (cand > astarT) astarT = cand;
            }
            astar = astarT;

            // TODO compute tied seats
        }


        return new Apportionment(k, seats, tiedSeats, astar);
    }

    private static class Entry {
        final int index;
        double value;

        Entry(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }
}
