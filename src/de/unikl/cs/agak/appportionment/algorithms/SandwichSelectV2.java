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
 * arXiv:1504.06475v2 (2015)
 * </dir>
 * Deprecated in favor of the v3 version which uses an improved version of Lemma 2.
 */
@Deprecated
public class SandwichSelectV2 extends SelectionBasedAlgorithm implements AlgorithmWithCounters {
  /* Interface AlgorithmWithCounters and this member variable are only
   * for purposes of experiments. In a productive environment, remove both.
   */
  private int lastIx = -1;
  private int lastCandSize = -1;

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
    for ( double p : instance.votes ) {
      if ( p > maxPop ) maxPop = p;
    }
    double x_overbar = dm.d(instance.k - 1) / maxPop + 5 * EPSILON;
    // x_overbar clearly feasible and suboptimal

    Collection<Integer> I_x_overbar = new ArrayList<>(n);
    double Sigma_I_x_overbar = 0;
    for ( int i = 0; i < n; ++i ) {
      if ( instance.votes[i] > dm.d(0) / x_overbar ) {
        I_x_overbar.add(i);
        Sigma_I_x_overbar += instance.votes[i];
      }
    }
    lastIx = I_x_overbar.size();

    final double a_overbar =
        (alpha * instance.k + beta * I_x_overbar.size()) / Sigma_I_x_overbar;
    final double a_underbar = Math.max(0,
        a_overbar - ((alpha + beta) * I_x_overbar.size()) / Sigma_I_x_overbar);

    final int A_hat_bound = (int)Math.ceil(
        2 * (1 + beta / alpha) * I_x_overbar.size());

    // step 6
    final double[] A_hat = new double[A_hat_bound];
    // TODO how is this better than just using an ArrayList?

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

    lastCandSize = A_hat_size;

    // Selection algorithm is zero-based!
    return RankSelection.select(A_hat, A_hat_size - 1, k_hat - 1);
  }

  @Override
  public int numberOfCounters() {
    return 2;
  }

  @Override
  public int getLastCounter(int i) {
    switch ( i ) {
      case 0:
        return lastIx;
      case 1:
        return lastCandSize;
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
      default:
        return "NoSuchCounter";
    }
  }
}
