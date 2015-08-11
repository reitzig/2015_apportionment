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

/**
 * Implements the jump-and-step algorithm from
 *
 *      Friedrich Pukelsheim
 *      Proportional Representation
 *      Springer, 2014
 *
 * The implementation uses a priority queue for (asymptotically) efficient steps.
 */
public class PukelsheimPQ extends LinearApportionmentMethod {

	public PukelsheimPQ(final double alpha, final double beta) {
		super(alpha, beta);
	}


  @Override
  public Apportionment apportion(final double[] votes, int k) {
    // Compute initial assignment using guess sum(population)/k
    final int[] seats = new int[votes.length];
    
    double sumPop = 0;
    for ( double p : votes ) { sumPop += p; }
    final double D = k / sumPop; // TODO if beta <= alpha then D = k / (sumPop + votes.length * (beta/alpha - 0.5))
    
    for ( int i=0; i<votes.length; i++ ) {
      seats[i] = (int) Math.floor(deltaInv(votes[i] * D)) + 1; // TODO correct?
    }
    
    int sumSeats = 0;
    for ( int s : seats ) { sumSeats += s; }
    
    final int order;
    final int offset;
    final int step;
    if ( sumSeats == k ) {
      return new Apportionment(seats, D);
    }
    else if ( sumSeats < k ) {
      // Setup: max-heap, offset for next d_i, add seats
      order = 1;
      offset = 0;
      step = +1;
    }
    else { // s > k
      // Setup: min-heap, offset for previous d_i, remove seats
      order = -1;
      offset = -1;
      step = -1;
    }
    
    // Initialize heap
    final PriorityQueue<Entry> heap = new PriorityQueue<Entry>(votes.length,
      new Comparator<Entry>() {
        @Override
        public int compare(final Entry e1, final Entry e2) {
          return order * Double.compare(e1.value, e2.value);
        }
      });
      
    // Seed heap with initial values
    for ( int i=0; i<votes.length; i++ ) {
      heap.add(new Entry(i, d(seats[i] + offset) / votes[i]));
    }
    
    // Subsequently adapt seats
    while ( sumSeats != k ) {
      final Entry e = heap.poll();
      final int i = e.index;
      seats[i] += step;
      e.value = d(seats[i] + offset) / votes[i];
      heap.add(e);
      sumSeats += step;
    }
    
    return new Apportionment(seats,0.0); // TODO compute value
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
