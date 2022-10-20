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

import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.AlmostLinearDivisorMethod;
import de.unikl.cs.agak.appportionment.methods.DivisorMethod;
import de.unikl.cs.agak.appportionment.util.RankSelection;

import java.util.Collection;
import java.util.LinkedList;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.*;

@Deprecated
public class SandwichSelectWithOptimalityCheck extends SelectionBasedAlgorithm {

    @Override
    double unitSize(final ApportionmentInstance instance, final DivisorMethod method) {
      if (  !(method instanceof AlmostLinearDivisorMethod) ) {
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " only works for almost linear divisor sequences");
      }
      final AlmostLinearDivisorMethod dm = (AlmostLinearDivisorMethod)method;
      final double alpha = dm.getAlpha();
      final double beta = dm.getBetaUpper();

      final int n = instance.votes.length;

		// Find largest population
		double maxPop = Double.NEGATIVE_INFINITY;
        for (double p : instance.votes) {
            if (p > maxPop) maxPop = p;
		}
        double x_overbar = dm.d(instance.k - 1) / maxPop;
//		System.out.println("x_overbar = " + x_overbar);
        if (isOptimal(instance.votes, instance.k, x_overbar, dm)) return x_overbar;

		Collection<Integer> I_x_overbar = new LinkedList<>();
		double Sigma_I_x_overbar = 0;
		for (int i = 0; i < n; ++i) {
            if (instance.votes[i] > dm.d(0) / x_overbar) {
                I_x_overbar.add(i);
                Sigma_I_x_overbar += instance.votes[i];
            }
		}

		final double a_overbar =
                (alpha * instance.k + beta * I_x_overbar.size()) / Sigma_I_x_overbar;
        final double a_underbar = Math.max(0,
			  a_overbar - ((alpha + beta) * I_x_overbar.size()) / Sigma_I_x_overbar);
//		System.out.println("a_underbar = " + a_underbar);
//		System.out.println("a_overbar = " + a_overbar);

		final int A_hat_bound = (int) Math.ceil(
			  2 * (1 + beta / alpha) * I_x_overbar.size());

		// step 6
		final double[] A_hat = new double[A_hat_bound];

		int A_hat_size = 0;
        int k_hat = instance.k;

		for (int i : I_x_overbar) {
            double v_i = instance.votes[i];
            // If sequence is not contributing, deltaInv might be invalid (< 0 etc),
			// so explicitly handle that case:
			if (dm.d(0) / v_i > a_overbar) continue;

			// otherwise: add all elements between a_underbar and a_overbar
			final double realMinJ = dm.deltaInvRaw(v_i * a_underbar);
			final int minJ = realMinJ <= 0 ? 0 : fuzzyCeil(realMinJ);
			final int maxJ = fuzzyFloor(dm.deltaInvRaw(v_i * a_overbar));
			for (int j = minJ; j <= maxJ; ++j) {
				A_hat[A_hat_size++] = dm.d(j) / v_i;
			}
			k_hat -= minJ; // Elements 0,1,...,minJ-1 missing from A_hat
		}

//		Arrays.sort(A_hat,0,A_hat_size);
//		System.out.println("A_hat = " + Arrays.toString(A_hat));
//		System.out.println("A_hat_size = " + A_hat_size);
//		System.out.println("k_hat = " + k_hat);

		// Selection algorithm is zero-based!
		return RankSelection.select(A_hat, A_hat_size - 1, k_hat - 1);
	}

	boolean isOptimal(final double[] population, final int k, final double x, final DivisorMethod dm) {
		int rankX = 0;
		int rankXMinusEpsilon = 0;

		for (double v_i : population) {
			// If sequence is not contributing, deltaInv might be invalid (< 0 etc),
			// so explicitly handle that case:
			if (dm.d(0) / v_i > x) continue;
			double deltaInv = dm.deltaInvRaw(v_i * x);

			Integer deltaInvInt = integer(deltaInv);
			if (deltaInvInt != null) {
				// we are at a jump site and x - epsilon would have lower rank
				rankX += deltaInvInt + 1;
				rankXMinusEpsilon += deltaInvInt;
			} else {
				// ordinary fractional deltaInv
				int deltaInvFloored = (int) Math.floor(deltaInv);
				rankX += deltaInvFloored + 1;
				rankXMinusEpsilon += deltaInvFloored + 1;
			}
		}

		return rankX >= k // feasible
			  && rankXMinusEpsilon < k; //eps smaller->infeasible
	}
}
