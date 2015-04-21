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


import java.util.Arrays;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
class ApportionmentInstance {
	ApportionmentInstance(final double[] population, final int k) {
		this.population = population;
		this.k = k;
	}

	double[] population;
	int k;

	static ApportionmentInstance uniformRandomInstance(int n) {
		return uniformRandomInstance(SedgewickRandom.instance, n);
	}

	static ApportionmentInstance uniformRandomInstance(SedgewickRandom random, int n) {
		final double[] population = new double[n];
		for (int i = 0; i < population.length; i++) {
			population[i] = random.uniform(1, 100);
		}
		final int k = random.uniform(n, 10 * n);
		return new ApportionmentInstance(population, k);
	}

	static ApportionmentInstance exponentialRandomInstance(int n) {
		return exponentialRandomInstance(SedgewickRandom.instance, n);
	}

	static ApportionmentInstance exponentialRandomInstance(SedgewickRandom random, int n) {
		final double[] population = new double[n];
		for (int i = 0; i < population.length; i++) {
			population[i] = 1 + random.exp(10);
		}
		final int k = random.uniform(n, 10 * n);
		return new ApportionmentInstance(population, k);
	}

	@Override public String toString() {
		return "Instance(" + "population=" + Arrays.toString(population) + ", k=" + k
			  + ')';
	}
}
