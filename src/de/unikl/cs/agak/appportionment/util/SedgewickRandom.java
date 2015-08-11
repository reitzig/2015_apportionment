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

import java.util.Random;

/**
 * Instance-class variant of 
 * <a href="http://algs4.cs.princeton.edu/code/">Sedgewick/Wayne's</a> {@link StdRandom}
 *
 * @author Sebastian Wild (s_wild@cs.uni-kl.de)
 */
public class SedgewickRandom {

	private final Random random;    // pseudo-random number generator
	private final long seed;        // pseudo-random number generator seed

	public final static SedgewickRandom instance = new SedgewickRandom();

	public SedgewickRandom() {
		// this is how the seed was set in Java 1.4
		seed = System.currentTimeMillis();
		random = new Random(seed);
	}

	public SedgewickRandom(final long seed) {
		this.seed = seed;
		this.random = new Random(seed);
	}

	/** Returns the seed of the psedurandom number generator. */
	public long getSeed() {
		return seed;
	}

	/** Return real number uniformly in [0, 1). */
	public double uniform() {
		return random.nextDouble();
	}

	/**
	 * Returns an integer uniformly between 0 (inclusive) and N (exclusive).
	 *
	 * @throws IllegalArgumentException if <tt>N <= 0</tt>
	 */
	public int uniform(int N) {
		if (N <= 0) throw new IllegalArgumentException("Parameter N must be positive");
		return random.nextInt(N);
	}

	///////////////////////////////////////////////////////////////////////////
	//  METHODS BELOW RELY ON JAVA.UTIL.RANDOM ONLY INDIRECTLY VIA
	//  THE METHODS ABOVE.
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a real number uniformly in [0, 1).
	 *
	 * @deprecated clearer to use {@link #uniform()}
	 */
	public double random() {
		return uniform();
	}

	/**
	 * Returns an integer uniformly in [a, b).
	 *
	 * @throws IllegalArgumentException if <tt>b <= a</tt>
	 * @throws IllegalArgumentException if <tt>b - a >= Integer.MAX_VALUE</tt>
	 */
	public int uniform(int a, int b) {
		if (b <= a) throw new IllegalArgumentException("Invalid range");
		if ((long) b - a >= Integer.MAX_VALUE) throw new IllegalArgumentException(
			  "Invalid range");
		return a + uniform(b - a);
	}

	/**
	 * Returns a real number uniformly in [a, b).
	 *
	 * @throws IllegalArgumentException unless <tt>a < b</tt>
	 */
	public double uniform(double a, double b) {
		if (!(a < b)) throw new IllegalArgumentException("Invalid range");
		return a + uniform() * (b - a);
	}

	/**
	 * Returns a boolean, which is true with probability p, and false otherwise.
	 *
	 * @throws IllegalArgumentException unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
	 */
	public boolean bernoulli(double p) {
		if (!(p >= 0.0 && p <= 1.0)) throw new IllegalArgumentException(
			  "Probability must be between 0.0 and 1.0");
		return uniform() < p;
	}

	/** Returns a boolean, which is true with probability .5, and false otherwise. */
	public boolean bernoulli() {
		return bernoulli(0.5);
	}

	/** Returns a real number with a standard Gaussian distribution. */
	public double gaussian() {
		// use the polar form of the Box-Muller transform
		double r, x, y;
		do {
			x = uniform(-1.0, 1.0);
			y = uniform(-1.0, 1.0);
			r = x * x + y * y;
		} while (r >= 1 || r == 0);
		return x * Math.sqrt(-2 * Math.log(r) / r);

		// Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
		// is an independent random gaussian
	}

	/** Returns a real number from a gaussian distribution with given mean and stddev */
	public double gaussian(double mean, double stddev) {
		return mean + stddev * gaussian();
	}

	/**
	 * Returns an integer with a geometric distribution with mean 1/p.
	 *
	 * @throws IllegalArgumentException unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
	 */
	public int geometric(double p) {
		if (!(p >= 0.0 && p <= 1.0)) throw new IllegalArgumentException(
			  "Probability must be between 0.0 and 1.0");
		// using algorithm given by Knuth
		return (int) Math.ceil(Math.log(uniform()) / Math.log(1.0 - p));
	}

	/**
	 * Return an integer with a Poisson distribution with mean lambda.
	 *
	 * @throws IllegalArgumentException unless <tt>lambda > 0.0</tt> and not infinite
	 */
	public int poisson(double lambda) {
		if (!(lambda > 0.0)) throw new IllegalArgumentException(
			  "Parameter lambda must be positive");
		if (Double.isInfinite(lambda)) throw new IllegalArgumentException(
			  "Parameter lambda must not be infinite");
		// using algorithm given by Knuth
		// see http://en.wikipedia.org/wiki/Poisson_distribution
		int k = 0;
		double p = 1.0;
		double L = Math.exp(-lambda);
		do {
			k++;
			p *= uniform();
		} while (p >= L);
		return k - 1;
	}

	/**
	 * Returns a real number with a Pareto distribution with parameter alpha.
	 *
	 * @throws IllegalArgumentException unless <tt>alpha > 0.0</tt>
	 */
	public double pareto(double alpha) {
		if (!(alpha > 0.0)) throw new IllegalArgumentException(
			  "Shape parameter alpha must be positive");
		return Math.pow(1 - uniform(), -1.0 / alpha) - 1.0;
	}

	/** Returns a real number with a Cauchy distribution. */
	public double cauchy() {
		return Math.tan(Math.PI * (uniform() - 0.5));
	}

	/**
	 * Returns a number from a discrete distribution: i with probability a[i]. throws
	 * IllegalArgumentException if sum of array entries is not (very nearly) equal to
	 * <tt>1.0</tt> throws IllegalArgumentException unless <tt>a[i] >= 0.0</tt> for each
	 * index <tt>i</tt>
	 */
	public int discrete(double[] a) {
		double EPSILON = 1E-14;
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			if (!(a[i] >= 0.0)) throw new IllegalArgumentException(
				  "array entry " + i + " must be nonnegative: " + a[i]);
			sum = sum + a[i];
		}
		if (sum > 1.0 + EPSILON || sum < 1.0 - EPSILON) throw new IllegalArgumentException(
			  "sum of array entries does not approximately equal 1.0: " + sum);

		// the for loop may not return a value when both r is (nearly) 1.0 and when the
		// cumulative sum is less than 1.0 (as a result of floating-point roundoff error)
		while (true) {
			double r = uniform();
			sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + a[i];
				if (sum > r) return i;
			}
		}
	}

	/**
	 * Returns a real number from an exponential distribution with rate lambda.
	 *
	 * @throws IllegalArgumentException unless <tt>lambda > 0.0</tt>
	 */
	public double exp(double lambda) {
		if (!(lambda > 0.0)) throw new IllegalArgumentException(
			  "Rate lambda must be positive");
		return -Math.log(1 - uniform()) / lambda;
	}

	/** Rearrange the elements of an array in random order. */
	public void shuffle(Object[] a) {
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int r = i + uniform(N - i);     // between i and N-1
			Object temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/** Rearrange the elements of a double array in random order. */
	public void shuffle(double[] a) {
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int r = i + uniform(N - i);     // between i and N-1
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/** Rearrange the elements of an int array in random order. */
	public void shuffle(int[] a) {
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int r = i + uniform(N - i);     // between i and N-1
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}


	/** Rearrange the elements of the subarray a[lo..hi] in random order. */
	public void shuffle(Object[] a, int lo, int hi) {
		if (lo < 0 || lo > hi || hi >= a.length) {
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++) {
			int r = i + uniform(hi - i + 1);     // between i and hi
			Object temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/** Rearrange the elements of the subarray a[lo..hi] in random order. */
	public void shuffle(double[] a, int lo, int hi) {
		if (lo < 0 || lo > hi || hi >= a.length) {
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++) {
			int r = i + uniform(hi - i + 1);     // between i and hi
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/** Rearrange the elements of the subarray a[lo..hi] in random order. */
	public void shuffle(int[] a, int lo, int hi) {
		if (lo < 0 || lo > hi || hi >= a.length) {
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++) {
			int r = i + uniform(hi - i + 1);     // between i and hi
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}
}

