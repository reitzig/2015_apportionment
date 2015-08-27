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

/**
 * Samples apportionment instances randomly.
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class ApportionmentInstanceFactory {
  static final VoteFactory UniformVotes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return r.uniform(1.0, 3.0);
    }
  };

  static final VoteFactory ExponentialVotes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return 1.0 + r.exp(1.0);
    }
  };

  static final VoteFactory PoissonVotes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return 1.0 + r.poisson(100)/100.0;
    }
  };

  static final VoteFactory Pareto1_5Votes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return 1.0 + r.pareto(1.5);
    }
  };

  @Deprecated
  static final VoteFactory Pareto2Votes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return 1.0 + r.pareto(2.0);
    }
  };

  @Deprecated
  static final VoteFactory Pareto3Votes = new VoteFactory() {
    @Override
    public double next(SedgewickRandom r) {
      return 1.0 + r.pareto(3.0);
    }
  };

  static ApportionmentInstance randomInstance(final SedgewickRandom random, final VoteFactory vf, final int n, final KFactory k) {
    final double[] votes = new double[n];
    for ( int i = 0; i < votes.length; i++ ) {
      votes[i] = vf.next(random);
    }
    return new ApportionmentInstance(votes, k.sampleFactor(random) * n);
  }

  public interface VoteFactory {
    double next(SedgewickRandom r);
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
      if ( minK == maxK )
        return minK;
      else
        return r.uniform(minK, maxK);
    }

    @Override
    public String toString() {
      if ( minK == maxK )
        return Integer.toString(minK) + "*n";
      else
        return "U[" + minK + "*n, " + maxK + "*n]";
    }
  }
}
