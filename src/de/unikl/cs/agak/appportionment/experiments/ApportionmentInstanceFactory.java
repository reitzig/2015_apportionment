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

package de.unikl.cs.agak.appportionment.experiments;

import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.util.SedgewickRandom;

import java.util.Arrays;

/**
 * Samples apportionment instances randomly.
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class ApportionmentInstanceFactory {
    static ApportionmentInstance uniformRandomInstance(final int n, final KFactory k) {
        return uniformRandomInstance(SedgewickRandom.instance, n, k);
    }

    static ApportionmentInstance uniformRandomInstance(final SedgewickRandom random, final int n, final KFactory k) {
        final double[] votes = new double[n];
        for (int i = 0; i < votes.length; i++) {
            votes[i] = random.uniform(1, 100);
        }
        return new ApportionmentInstance(votes, k.sampleFactor(random) * n);
    }

    static ApportionmentInstance exponentialRandomInstance(final int n, final KFactory k) {
        return exponentialRandomInstance(SedgewickRandom.instance, n, k);
    }

    static ApportionmentInstance exponentialRandomInstance(final SedgewickRandom random, final int n, final KFactory k) {
        final double[] votes = new double[n];
        for (int i = 0; i < votes.length; i++) {
            votes[i] = 1 + random.exp(10);
        }
        return new ApportionmentInstance(votes, k.sampleFactor(random) * n);
    }



    public static class KFactory {
        int minK;
        int maxK;

        /**
         * Creates a factory that always samples k*n.
         */
        public KFactory(int k) {
            minK = k;
            maxK = k;
        }

        /**
         * Creates a factory that samples uniformly from interval [min*n,max*n].
         */
        public KFactory(int min, int max) {
            assert min <= max;
            minK = min;
            maxK = max;
        }

        public int sampleFactor(SedgewickRandom r) {
            if (minK == maxK)
                return minK;
            else
                return r.uniform(minK, maxK);
        }

        @Override
        public String toString() {
            if (minK == maxK)
                return Integer.toString(minK) + "*n";
            else
                return "U[" + minK + "*n, " + maxK + "*n]";
        }
    }
}
