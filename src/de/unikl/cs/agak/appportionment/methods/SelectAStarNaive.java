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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SelectAStarNaive extends LinearApportionmentMethod {

	public SelectAStarNaive(final double alpha, final double beta) {
		super(alpha, beta);
	}

    @Override
    public Apportionment apportion(double[] votes, int k) {
        // Compute $a^*$
        final double astar = unitSize(votes, k);

        // Derive seats
        final int[] seats = new int[votes.length];
        for (int i = 0; i < votes.length; i++) {
            seats[i] = new Double(Math.floor(deltaInv(votes[i] * astar))).intValue() + 1;
        }

        // TODO resolve ties? May assign less than k seats now

        return new Apportionment(seats, astar);
    }

    private double unitSize(double[] votes, int k) {
		final int n = votes.length;
		// Find largest population
		double maxPop = Double.NEGATIVE_INFINITY;
		for (double p : votes) {
			if (p > maxPop) maxPop = p;
		}
		double x_overbar = d(k) / maxPop; // clearly suboptimal and feasible
//		System.out.println("x_overbar = " + x_overbar);

		Collection<Integer> I_x_overbar = new LinkedList<Integer>();
		for (int i = 0; i < n; ++i) {
			if (votes[i] > d(0) / x_overbar) {
				I_x_overbar.add(i);
			}
		}

		List<Double> A = new ArrayList<Double>();

		for (int i : I_x_overbar) {
			double v_i = votes[i];
			for (int j = 0; j <= k; ++j) {
				A.add(d(j) / v_i);
			}
		}

		// poor man's selection
		Collections.sort(A);
//		System.out.println("A = " + A);
		return A.get(k-1);
	}

}
