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

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
abstract public class IterativeMethod extends LinearApportionmentMethod {

    public IterativeMethod(double alpha, double beta) {
        super(alpha, beta);
    }

    abstract public Apportionment apportion(ApportionmentInstance instance);

    Apportionment determineTies(final int k, final int[] seats, final double astar) {
        final int[] tiedSeats = new int[seats.length];

        // TODO implement

        return new Apportionment(k, seats, tiedSeats, astar);
    }
}
