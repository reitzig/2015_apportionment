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
import de.unikl.cs.agak.appportionment.experiments.AlgorithmWithCounters;
import de.unikl.cs.agak.appportionment.methods.AlmostLinearDivisorMethod;
import de.unikl.cs.agak.appportionment.methods.DivisorMethod;
import de.unikl.cs.agak.appportionment.util.RankSelection;

import java.util.ArrayList;
import java.util.Collection;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.*;


/**
 * Implements the linear-time apportionment algorithm from
 * <dir>
 * Reitzig, R. and Wild, S.<br/>
 * A Practical and Worst-Case Efficient Algorithm for Divide-and-Round Apportionment<br/>
 * arXiv:1504.06475v3 (2016)
 * </dir>
 * <p/>
 * This version iterates finding sandwich bounds by using a_overbar as new x_overbar
 * as long as the candidate set shrinks.
 *
 * Deprecated; benchmarks show worse performance than SandSelv3.
 * In most cases, two iterations were performed without shrinking I_x any.
 */
@Deprecated
public class SandwichSelectIter extends SelectionBasedAlgorithm implements AlgorithmWithCounters {
  /* Interface AlgorithmWithCounters and this member variable are only
   * for purposes of experiments. In a productive environment, remove both.
   */
  private int lastIx = -1;
  private int lastCandSize = -1;
  private int lastBoundingIters = -1;

  @Override
  double unitSize(final ApportionmentInstance instance, final DivisorMethod method) {
    if (  !(method instanceof AlmostLinearDivisorMethod) ) {
      throw new IllegalArgumentException(this.getClass().getSimpleName() + " only works for almost linear divisor sequences");
    }
    final AlmostLinearDivisorMethod dm = (AlmostLinearDivisorMethod)method;
    final double alpha = dm.getAlpha();
    final double betaU = dm.getBetaUpper();
    final double betaL = dm.getBetaLower();

    final int n = instance.votes.length;

    // Find largest population
    double maxPop = Double.NEGATIVE_INFINITY;
    for ( double p : instance.votes ) {
      if ( p > maxPop ) maxPop = p;
    }
    double x_overbar = dm.d(instance.k - 1) / maxPop + 5 * EPSILON;
    // x_overbar clearly feasible and suboptimal

    final Collection<Integer> I_x_overbar = new ArrayList<>(n);
    double a_overbar;
    double a_underbar;
    int I_x_size;
    int iterations = 0;
    do {
      I_x_size = I_x_overbar.isEmpty() ? Integer.MAX_VALUE : I_x_overbar.size();
      I_x_overbar.clear();

      double Sigma_I_x_overbar = 0;
      for ( int i = 0; i < n; ++i ) {
        if ( instance.votes[i] > dm.d(0) / x_overbar ) {
          I_x_overbar.add(i);
          Sigma_I_x_overbar += instance.votes[i];
        }
      }
      lastIx = I_x_overbar.size();

      a_overbar = (alpha * instance.k + betaU * I_x_overbar.size()) / Sigma_I_x_overbar;
      a_underbar = Math.max(0, (alpha * instance.k - (alpha - betaL) * I_x_overbar.size()) / Sigma_I_x_overbar);

      x_overbar = a_overbar; // Every a_overbar can serve as new (maybe) better x_overbar
      iterations += 1;
    } while ( I_x_overbar.size() < I_x_size ); // Shrink candidate set as much as possible
    // From the way of how we define the sandwhich bounds, we know that we won't get another improvement now.

    // Update benchmark counter
    lastBoundingIters = iterations;

    final int A_hat_bound = 2 * I_x_overbar.size();

    // step 6
    final double[] A_hat = new double[A_hat_bound];
    // TODO how is this better than just using an ArrayList?
    //            (rank selection below works on arrays, so there may be a tradeoff)

    int A_hat_size = 0;
    int k_hat = instance.k;

    for ( int i : I_x_overbar ) {
      double v_i = instance.votes[i];
      // If sequence is not contributing, deltaInvRaw might be invalid (< 0 etc),
      // so explicitly handle that case:
      if ( dm.d(0) / v_i > a_overbar ) continue;

      // otherwise: add all elements between a_underbar and a_overbar
      final double realMinJ = dm.deltaInvRaw(v_i * a_underbar);
      final int minJ = realMinJ <= 0 ? 0 : fuzzyCeil(realMinJ);
      final int maxJ = fuzzyFloor(dm.deltaInvRaw(v_i * a_overbar));
      for ( int j = minJ; j <= maxJ; ++j ) {
        A_hat[A_hat_size++] = dm.d(j) / v_i;
      }
      k_hat -= minJ; // Elements 0,1,...,minJ-1 missing from A_hat
    }

    // Update benchmark counter
    lastCandSize = A_hat_size;

    // Selection algorithm is zero-based!
    return RankSelection.select(A_hat, A_hat_size - 1, k_hat - 1);
  }

  @Override
  public int numberOfCounters() {
    return 3;
  }

  @Override
  public int getLastCounter(int i) {
    switch ( i ) {
      case 0:
        return lastIx;
      case 1:
        return lastCandSize;
      case 2:
        return lastBoundingIters;
      default:
        return -1;
    }
  }

  @Override
  public String getCounterLabel(int i) {
    switch ( i ) {
      case 0:
        return "|I_x|";
      case 1:
        return "|A|";
      case 2:
        return "BoundingIters";
      default:
        return "NoSuchCounter";
    }
  }
}
