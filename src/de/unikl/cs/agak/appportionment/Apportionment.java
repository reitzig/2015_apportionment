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

package de.unikl.cs.agak.appportionment;

import de.unikl.cs.agak.appportionment.util.FuzzyNumerics;

import java.util.Arrays;
import java.util.Collection;

import static de.unikl.cs.agak.appportionment.util.AssortedUtils.isBinary;
import static de.unikl.cs.agak.appportionment.util.AssortedUtils.kSubsets;
import static de.unikl.cs.agak.appportionment.util.AssortedUtils.sum;

/**
 * Represents a result of apportionment, that is a set of valid seat assignments.
 * These are essentially one assignment plus different ways of resolving ties, if any.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public final class Apportionment {
  /**
   * The proportionality constant; each seat represents approximately this many votes.
   */
  public final double astar; // TODO find a good name; Pukelsheim: proportionality constant
  /**
   * The number of seats per party we definitely assign.
   */
  public final int[] seats; // TODO not safe
  /**
   * Contains 1 for every party the <em>tied</em> for the last couple of seats.
   * You can pick any <code>k - sum(seats)</code> of these and assign one seat each;
   * no assignment is more "fair" than any other according to the used divisor method.
   */
  public final int[] tiedSeats; // TODO not safe
  /**
   * The number of seats we have apportioned for.
   */
  public final int k;

  public Apportionment(int k, int[] seats, int[] tiedSeats, double astar) {
    assert k >= 0 && seats != null && tiedSeats != null && astar > 0;

    this.k = k;
    this.tiedSeats = tiedSeats;
    this.astar = astar;
    this.seats = seats;
  }

  /**
   * @return All implied assignments.
   */
  public Collection<int[]> assignments() {
    Collection<int[]> untied =
        kSubsets(Arrays.copyOf(tiedSeats, tiedSeats.length), k - sum(seats));

    for ( final int[] u : untied ) {
      assert u.length == seats.length && isBinary(u) : "Bad tiebreaker! " + Arrays.toString(u);

      for ( int i = 0; i < u.length; i++ ) {
        u[i] += seats[i];
      }
      //assert sum(u) == k : "Computed assignment has bad number of seats!";
    }

    return untied;
  }

  @Override
  public String toString() {
    return "Apportionment(" + System.getProperty("line.separator") +
        "\tseats=" + Arrays.toString(seats) + "," + System.getProperty("line.separator") +
        "\ttiedSeats=" + Arrays.toString(tiedSeats) + "," + System.getProperty("line.separator") +
        "\tastar=" + astar + System.getProperty("line.separator") +
        "\tsum(seats)=" + sum(seats) + System.getProperty("line.separator") +
        "\tsum(tiedSeats)=" + sum(tiedSeats) + System.getProperty("line.separator") +
        ')';
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Apportionment
        && FuzzyNumerics.fuzzyEquals(this.astar, ((Apportionment)o).astar)
        && Arrays.equals(this.seats, ((Apportionment)o).seats);
  }
}
