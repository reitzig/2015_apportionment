package de.unikl.cs.agak.appportionment.experiments;

import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.util.SedgewickRandom;

import java.util.Arrays;

/**
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
