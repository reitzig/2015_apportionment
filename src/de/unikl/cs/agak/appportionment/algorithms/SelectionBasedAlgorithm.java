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

package de.unikl.cs.agak.appportionment.algorithms;

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.DivisorMethod;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.fuzzyEquals;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
abstract public class SelectionBasedAlgorithm implements ApportionmentAlgorithm {

  @Override
  final public Apportionment apportion(final ApportionmentInstance instance, final DivisorMethod method) {
    final int n = instance.votes.length;

    // Compute $a^*$
    final double astar = unitSize(instance, method);

    // Derive seats
    final int[] seats = new int[n];
    for ( int i = 0; i < n; i++ ) {
      seats[i] = method.dRound(instance.votes[i] * astar) + 1;
    }

    // Now we have *all* seats with value astar, which may be too many.
    // Identify ties for the last few seats!
    int theOnlyTie = -1;
    final int[] tiedSeats = new int[n];
    for ( int i = 0; i < n; i++ ) {
      if ( seats[i] == 0 ) {
        if ( fuzzyEquals(method.d(0) / instance.votes[i], astar) ) {
          tiedSeats[i] = 1;
          if ( theOnlyTie == -1 ) theOnlyTie = i;
          else theOnlyTie = -42;
          // TODO This should actually never happen according to above comment.
          throw new IllegalStateException();
        }
      }
      else if ( fuzzyEquals(method.d(seats[i] - 1) / instance.votes[i], astar) ) {
        tiedSeats[i] = 1;
        seats[i] -= 1;
        if ( theOnlyTie == -1 ) theOnlyTie = i;
        else theOnlyTie = -42;
      }
    }
    if ( theOnlyTie >= 0 ) {
      tiedSeats[theOnlyTie] = 0;
      seats[theOnlyTie] += 1;
    }

    return new Apportionment(instance.k, seats, tiedSeats, astar);
  }

  /**
   * @param instance An instance of the apportionment problem.
   * @return The (reciprocal of the) proportionality constant (a*).
   */
  abstract double unitSize(ApportionmentInstance instance, DivisorMethod method);
}
