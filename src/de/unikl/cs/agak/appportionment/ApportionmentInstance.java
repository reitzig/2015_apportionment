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

import java.util.Arrays;

/**
 * @author Sebastian Wild (s_wild@cs.uni-kl.de)
 */
public class ApportionmentInstance {
    final public double[] votes;
    final public int k;

    public ApportionmentInstance(final double[] votes, final int k) {
        this.votes = votes;
        this.k = k;
    }

    @Override
    public String toString() {
        return "Instance(" + "population=" + Arrays.toString(votes) + ", k=" + k + ')';
    }
}

