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
     * Computes $a^*$.
     *
     * @Deprecated Allows no fair comparison; use <code>apportion</code> for
     * experiments.
     */
    public abstract double unitSize(double[] population, int k); // TODO refactor?

    /**
     * Standard implementation that uses <code>unitSize</code>;
     * overwrite if a method allows for direct computation of
     * a seat apportionment vector.
     */
    public int[] apportion(final double[] population, final int k) { // TODO return unitSize as well?
        // Compute $a^*$
        final double astar = unitSize(population, k);

        // Derive seats
        final int[] seats = new int[population.length];
        for (int i = 0; i < population.length; i++) {
            seats[i] = new Double(Math.floor(deltaInv(population[i] * astar))).intValue() + 1;
        }

        // TODO resolve ties? May assign more than k seats now

        return seats;
    }



    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + "alpha=" + alpha + ", beta=" + beta
                + ')';
    }

}
