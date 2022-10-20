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

import edu.princeton.cs.algs4.StdRandom;

/**
 * Rank selection algorithms as seen in
 * <a href="http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java.html">QuickPedantic</a>
 * by <a href="http://algs4.cs.princeton.edu/code/">Sedgewick/Wayne</a>, 
 * but modified to run on primitive double[]
 *
 * @author Sebastian Wild (s_wild@cs.uni-kl.de)
 */
public class RankSelection {
	public static double median(double[] a) {
		return a.length == 1 ? a[0] : select(a, a.length / 2 - 1);
	}

	public static double median(Double[] a) {
		return a.length == 1 ? a[0] : QuickPedantic.select(a, a.length / 2 - 1);
	}


	/**
	 * Rearranges the elements in a so that a[k] is the kth smallest element, and a[0]
	 * through a[k-1] are less than or equal to a[k], and a[k+1] through a[n-1] are greater
	 * than or equal to a[k].
	 */
	public static double select(double[] a, int k) {
		if (k < 0 || k >= a.length) {
			throw new IndexOutOfBoundsException("Selected element out of bounds");
		}
		StdRandom.shuffle(a);
		int lo = 0, hi = a.length - 1;
		while (hi > lo) {
			int i = partition(a, lo, hi);
			if (i > k) hi = i - 1;
			else if (i < k) lo = i + 1;
			else return a[i];
		}
		return a[lo];
	}

	/**
	 * Like {@link #select(double[], int)}, but ignoring positions A[hi+1], A[hi+2], ...
	 * @param a
	 * @param hi
	 * @param k
	 * @return
	 */
	public static double select(double[] a, int hi, int k) {
		if (hi >= a.length) throw new IndexOutOfBoundsException("hi > a.length");
		if (k < 0 || k >= hi) {
			throw new IndexOutOfBoundsException("Selected element out of bounds");
		}
		int lo = 0;
		StdRandom.shuffle(a, 0, hi);
		while (hi > lo) {
			int i = partition(a, lo, hi);
			if (i > k) hi = i - 1;
			else if (i < k) lo = i + 1;
			else return a[i];
		}
		return a[lo];
	}

	// partition the subarray a[lo .. hi] by returning an index j
	// so that a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
	private static int partition(double[] a, int lo, int hi) {
		int i = lo;
		int j = hi + 1;
		double v = a[lo];
		while (true) {

			// find item on lo to swap
			while (less(a[++i], v)) if (i == hi) break;

			// find item on hi to swap
			while (less(v, a[--j]))
				if (j == lo) break;      // redundant since a[lo] acts as sentinel

			// check if pointers cross
			if (i >= j) break;

			exch(a, i, j);
		}

		// put v = a[j] into position
		exch(a, lo, j);

		// with a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
		return j;
	}

	private static boolean less(double v, double w) {
		return Double.compare(v, w) < 0;
	}

	// exchange a[i] and a[j]
	private static void exch(double[] a, int i, int j) {
		double swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}

}
