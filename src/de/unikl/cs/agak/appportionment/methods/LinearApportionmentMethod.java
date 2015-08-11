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

import java.util.*;

public abstract class LinearApportionmentMethod {
    final double alpha;
    final double beta;

    public LinearApportionmentMethod(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public final double d(int j) {
        if (j < 0) {
            // By convention (cf. article) we return -infty for negative values.
            return Double.MIN_VALUE;
        }

        return alpha * j + beta;
    }

    public final double deltaInv(double x) {
        return (x - beta) / alpha;
    }

    /**
     * Finds an apportionment for the given parameters.
     */
    public abstract Apportionment apportion(final double[] votes, final int k);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + "alpha=" + alpha + ", beta=" + beta + ')';
    }



}
