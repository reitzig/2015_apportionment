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

import de.unikl.cs.agak.appportionment.ApportionmentInstance;

import java.util.*;

@Deprecated
public class SandwichSelectNaive extends SelectionBasedMethod {

  public SandwichSelectNaive(final double alpha, final double beta) {
    super(alpha, beta);
	}


    @Override
    double unitSize(final ApportionmentInstance instance) {
        final int n = instance.votes.length;

		// Find largest population
		double maxPop = Double.NEGATIVE_INFINITY;
        for (double p : instance.votes) {
            if (p > maxPop) maxPop = p;
		}
        double x_overbar = d(instance.k) / maxPop; // clearly suboptimal and feasible
//		System.out.println("x_overbar = " + x_overbar);

		Collection<Integer> I_x_overbar = new LinkedList<>();
		for (int i = 0; i < n; ++i) {
            if (instance.votes[i] > d(0) / x_overbar) {
                I_x_overbar.add(i);
			}
		}

		List<Double> A = new ArrayList<>();

		for (int i : I_x_overbar) {
            double v_i = instance.votes[i];
            for (int j = 0; j <= instance.k; ++j) {
                A.add(d(j) / v_i);
			}
		}

		// poor man's selection
		Collections.sort(A);
//		System.out.println("A = " + A);
        return A.get(instance.k - 1);
    }

}
