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

import java.util.Comparator;
import java.util.PriorityQueue;

public class IterativeDMPQ extends LinearApportionmentMethod {

    public IterativeDMPQ(final double alpha, final double beta) {
        super(alpha, beta);
    }

    @Override
    public Apportionment apportion(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

        // Initialize heap
        final PriorityQueue<Entry> heap = new PriorityQueue<>(n,
                new Comparator<Entry>() {
                    @Override
                    public int compare(final Entry e1, final Entry e2) {
                        return Double.compare(e1.value, e2.value);
                    }
                });

        // Seed heap with initial values
        for (int i = 0; i < n; i++) {
            heap.add(new Entry(i, d(0) / instance.votes[i]));
        }

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

        int[] tiedSeats = new int[n];
        // TODO find tied seats!

        // Next element determines the last seat
        return new Apportionment(instance.k, seats, tiedSeats, e.value);
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
