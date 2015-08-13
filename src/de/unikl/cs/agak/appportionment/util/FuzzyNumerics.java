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

package de.unikl.cs.agak.appportionment.util;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class FuzzyNumerics {
    public static final double EPSILON = 1E-14;
    public static final long MANTISSA_EPSILON = 16L;

    /**
     * @return true iff x is within {@link #EPSILON} of an integer
     */
    public static boolean closeToInteger(double x) {
        return Math.floor(x + EPSILON) != Math.floor(x - EPSILON);
    }

    /**
     * @param x
     * @return <tt>floor(x*(1+epsilon))</tt>, for a small epsilon intended to cover
     * potential rounding errors in x
     */
    public static int fuzzyFloor(double x) { // TODO enable negative parameters
        if (x < 0) throw new IllegalArgumentException("fuzzyFloor only works for x >= 0");
        final double xTimesOnePlusEps = Double.longBitsToDouble(
                Double.doubleToRawLongBits(x) + MANTISSA_EPSILON);
        return (int) Math.floor(xTimesOnePlusEps);
    }

    /**
     * @param x
     * @return <tt>ceil(x*(1-epsilon))</tt>, for a small epsilon intended to cover
     * potential rounding errors in x
     */
    public static int fuzzyCeil(double x) { // TODO enable negative parameters
        if (x < 0) throw new IllegalArgumentException("fuzzyCeil only works for x >= 0");
        final double xTimesOneMinusEps = Double.longBitsToDouble(
                Double.doubleToRawLongBits(x) - MANTISSA_EPSILON);
        return (int) Math.ceil(xTimesOneMinusEps);
    }

    /**
     * @return int i if x is within {@link #EPSILON} of an integer i, null otherwise
     */
    public static Integer integer(double x) {
        return closeToInteger(x) ? (int) Math.floor(x + EPSILON) : null;
    }

    /**
     * @param x
     * @param y
     * @return <code>true</code> iff x < y - {@link #EPSILON}
     */
    public static boolean fuzzyLess(double x, double y) {
        return x < y - EPSILON;
    }

    /**
     * @param x
     * @param y
     * @return <code>true</code> iff x > y + {@link #EPSILON}
     */
    public static boolean fuzzyGreater(double x, double y) {
        return x > y + EPSILON;
    }

    /**
     * @param x
     * @param y
     * @return <code>true</code> iff |x - y| < {@link #EPSILON}
     */
    public static boolean closeToEqual(double x, double y) {
        return Math.abs(x - y) < EPSILON;
    }
}
