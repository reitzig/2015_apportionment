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
import de.unikl.cs.agak.appportionment.util.FuzzyNumerics;

import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.EPSILON;

/**
 * A divisor method whose divisor sequence is linear, i.e.
 * conforms to <code>a*j + b</code> for some constants
 * <code>a</code> and <code>b</code>.
 */
public abstract class LinearApportionmentMethod {
    final double alpha;
    final double beta;

    /**
     * Creates a new instance of a divisor method with
     * <code>d(j) = alpha * j + beta</code> (for <code>j >= 0</code>).
     * @param alpha The factor.
     * @param beta The offset.
     */
    public LinearApportionmentMethod(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    /**
     * @param j Index in the divisor sequence
     * @return The value <code>d(j) = alpha * j + beta</code> for <code>j >= 0</code>,
     *          negative infinity otherwise.
     */
    public final double d(int j) {
        if (j < 0) {
	         throw new IllegalArgumentException("Got j="+j);
           // The convention in the article was -infty for negative values,
	        // but a single convention that can consistently be used in all cases seems
	        // to be impossible, so check for negative values on call site.
	        // Usually, this makes the difference between a party that has 0 seats
	        // and one that has at least one seat, so often a case distinction is
	        // needed anyway.
        }

        return alpha * j + beta;
    }

    /**
     * Computes the inverse function of the canonical continuation of
     * {@link LinearApportionmentMethod#d(int)}
     * @param x Some number
     * @return <code>(x - beta) / alpha</code>
     */
    public final double deltaInvRaw(double x) {
        return (x - beta) / alpha;
    }

    public final double deltaInv(double x) {
	    final double raw = (x - beta) / alpha;
	    return raw < -1 ? -1 : raw;
    }

    /**
     * Rounds the given value according to the rounding method implied by the
     * divisor sequence d induced by this method.
     *
     * @param x Number to d-round
     * @return The index <code>j</code> in the divisor sequence for which <code>d(j) <= x < d(j+1)</code>,
     * that is an integer from at least -1.
     */
    public final int dRound(final double x) {
	    return FuzzyNumerics.fuzzyFloor(deltaInv(x));
    }

	/**
     * Finds the apportionment for the given parameters.
     * @param instance An instance of the apportionment problem, consisting of
     *                 votes and house size.
     * @return The apportionment for the given instance, representing all
     *          seats assignment that are valid w.r.t. this divisor method.
     */
    public abstract Apportionment apportion(ApportionmentInstance instance);

    /**
     * Determines if this method uses a <em>stationary</em> divisor sequence as defined
     * by Pukelsheim. Note that r = <code>beta</code>/<code>alpha</code> if this quotient
     * is in [0,1]; that is, for instance,
     * <ul>
     *     <li><code>alpha = 1</code> and <code>beta = 1</code> correspond to downward rounding (r=1),</li>
     *     <li><code>alpha = 2</code> and <code>beta = 1</code> correspond to standard rounding (r=1/2), and </li>
     *     <li><code>alpha = 1</code> and <code>beta = 0</code> correspond to upward rounding (r=0).</li>
     * </ul>
     * Above identity follows from
     * <dir>
     *     s(n+1) = d_n = <code>alpha</code> * n + <code>beta</code>
     * </dir>
     * which normalizes to
     * <dir>
     *    s(n) = n - 1 + <code>beta</code>/<code>alpha</code>.
     * </dir>
     * @return <code>true</code> iff this method is stationary.
     */
    public boolean isStationary() {
        return 0 <= beta / alpha && beta / alpha <= 1;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + "alpha=" + alpha + ", beta=" + beta + ')';
    }
}
