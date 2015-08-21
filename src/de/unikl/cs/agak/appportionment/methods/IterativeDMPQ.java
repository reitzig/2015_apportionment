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

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A naive implementation of divisor methods that assigns one seat after the other
 * using a priority queue for each step.
 */
public class IterativeDMPQ extends IterativeMethod {

    public IterativeDMPQ(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Initialize heap
        final ArrayList<Entry> initials = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            initials.add(new Entry(i, d(0) / instance.votes[i]));
        }
        final PriorityQueue<Entry> heap = new PriorityQueue<>(initials);

        // Subsequently assign seats
        final int[] seats = new int[n];
        int k = instance.k;
        while (k > 1) {
            final Entry e = heap.poll();
            final int i = e.index;
            seats[i]++;
            e.value = d(seats[i]) / instance.votes[i];
            heap.add(e);
            k--;
        }

        // Last seat determines astar
        final Entry e = heap.poll();
        seats[e.index]++;

        return determineTies(instance, seats, e.value);
    }

    private static class Entry implements Comparable<Entry> {
        final int index;
        double value;

        Entry(int index, double value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(final Entry e) {
            return Double.compare(this.value, e.value);
        }
    }
}
