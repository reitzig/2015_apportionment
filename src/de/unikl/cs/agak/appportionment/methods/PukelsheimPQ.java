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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.EPSILON;
import static edu.princeton.cs.introcs.StdStats.sum;

/**
 * Implements the jump-and-step algorithm from
 * <dir>
 * Friedrich Pukelsheim<br/>
 * Proportional Representation<br/>
 * Springer, 2014<br/>
 * </dir>
 * The implementation uses a priority queue for (asymptotically) efficient steps.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class PukelsheimPQ extends IterativeMethod implements AlgorithmWithCounter {
    /* Interface AlgorithmWithCounter and this member variable are only
     * for purposes of experiments. In a productive environment, remove both.
     */
    private int lastCounter = -1;

    public PukelsheimPQ(final double alpha, final double beta) {
        super(alpha, beta);
    }


    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Compute initial assignment using guess sum(population)/k
        final int[] seats = new int[n];

        double sumPop = sum(instance.votes);

        final double D;
        if ( isStationary() ) {
            // Use recommended estimator (cf Pukelsheim, section 6.1) resp. its reciprocal
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
        lastCounter = instance.k - sumSeats;

        final Comparator<Entry> order;
        final int offset;
        final int step;
        if (sumSeats == instance.k) {
            // no ties since we assign *all* seats with value a*
            return new Apportionment(instance.k, seats, new int[n], D);
        } else {
            if (sumSeats < instance.k) {
                // Setup: max-heap, offset for next d_i, add seats
                order = new Comparator<Entry>() {
                    @Override
                    public int compare(final Entry e1, final Entry e2) {
                        return Double.compare(e1.value, e2.value);
                    }
                };
                offset = 0;
                step = +1;
            } else { // s > k
                // Setup: min-heap, offset for previous d_i, remove seats
                order = new Comparator<Entry>() {
                    @Override
                    public int compare(final Entry e1, final Entry e2) {
                        return -Double.compare(e1.value, e2.value);
                    }
                };
                offset = -1;
                step = -1;
            }


            // Initialize heap
            final ArrayList<Entry> initials = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                initials.add(new Entry(i, d(seats[i] + offset) / instance.votes[i], order));
            }
            final PriorityQueue<Entry> heap = new PriorityQueue<>(initials);

            // Subsequently adapt seats
            while (sumSeats != instance.k) {
                final Entry e = heap.poll();
                final int i = e.index;
                seats[i] += step;
                e.value = d(seats[i] + offset) / instance.votes[i];
                heap.add(e);
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

    @Override
    public int getLastCounter() {
        return lastCounter;
    }

    @Override
    public String getCounterLabel() {
        return "missingSeats";
    }

    private static class Entry implements Comparable<Entry> {
        final Comparator<Entry> order;
        final int index;
        double value;

        Entry(int index, double value, Comparator<Entry> order) {
            this.index = index;
            this.value = value;
            this.order = order;
        }

        @Override
        public int compareTo(final Entry e) {
            return order.compare(this, e);
        }
    }
}
