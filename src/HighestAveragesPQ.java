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

import java.util.PriorityQueue;
import java.util.Comparator;

public class HighestAveragesPQ extends LinearApportionment {

	public HighestAveragesPQ(final double alpha, final double beta) {
		super(alpha, beta);
	}

  @Override 
  double unitSize(final double[] population, int k) {
    // Initialize heap
    final PriorityQueue<Entry> heap = new PriorityQueue<Entry>(population.length,     
      new Comparator<Entry>() {
        @Override
        public int compare(final Entry e1, final Entry e2) {
          return Double.compare(e1.value, e2.value);
        }
      });
      
    // Seed heap with initial values
    for ( int i=0; i<population.length; i++ ) {
      heap.add(new Entry(i, d(0)/population[i]));
    }
    
    // Subsequently assign seats
    final int[] seats = new int[population.length];
    while ( k > 1 ) {
      final Entry e = heap.poll();
      final int i = e.index;
      seats[i]++;
      e.value = d(seats[i])/population[i];
      heap.add(e);
      k--;
    }
    
    // Next element determines the last seat
    return heap.peek().value; 
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
