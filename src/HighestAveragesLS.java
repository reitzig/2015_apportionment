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

public class HighestAveragesLS extends LinearApportionment {

	public HighestAveragesLS(final double alpha, final double beta) {
		super(alpha, beta);
	}

  @Override 
  double unitSize(final double[] population, int k) {
    // Initialize current values
    final double[] values = new double[population.length];
      
    // Seed heap with initial values
    for ( int i=0; i<population.length; i++ ) {
      values[i] = d(0) / population[i];
    }
    
    // Subsequently assign seats
    final int[] seats = new int[population.length];
    int imin = 0;
    while ( k > 1 ) {      
      // Find index with maximum value
      imin = 0;
      for ( int i=1; i<values.length; i++ ) {
        if ( values[i] < values[imin] ) imin = i;
      }
      
      seats[imin]++;     
      values[imin] = d(seats[imin])/population[imin];
      k--;
    }
    
    // Find maximum for last seat
    imin = 0;
    for ( int i=1; i<values.length; i++ ) {
      if ( values[i] < values[imin] ) imin = i;
    }
    return values[imin]; 
  }
}
