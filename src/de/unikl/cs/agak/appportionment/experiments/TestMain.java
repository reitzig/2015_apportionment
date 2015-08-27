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

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.*;
import de.unikl.cs.agak.appportionment.util.SedgewickRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static de.unikl.cs.agak.appportionment.experiments.ApportionmentInstanceFactory.UniformVotes;
import static de.unikl.cs.agak.appportionment.util.AssortedUtils.isBinary;
import static de.unikl.cs.agak.appportionment.util.FuzzyNumerics.*;
import static edu.princeton.cs.introcs.StdStats.sum;

/**
 * Executes basic plausibility and mathematical correctness tests of all
 * the divisor method implementations given in {@link de.unikl.cs.agak.appportionment.methods}.
 * Correctness is established via the min-max-inequality (Pukelsheim Theorem 4.5).
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class TestMain {
  private static final int MIN_N = 10;
  private static final int MAX_N = 100;
  private static final int FACT_K = 10;
  private static final int REPS = 1000;
  private static final double MIN_ALPHA = 1.0;
  private static final double MAX_ALPHA = 2.0;
  // beta out of [0,alpha]

  private static List<Class<? extends LinearApportionmentMethod>> algs = Arrays.asList(
      IterativeDMLS.class,
      IterativeDMPQ.class,
      PukelsheimLS.class,
      PukelsheimPQ.class,
      AStarChengEppstein.class,
      SelectAStar.class,
      SelectAStarNaive.class,
      SelectAStarWithOptimalityCheck.class
  );

  public static void main(final String[] args) throws Exception {
    // Initialize random source
    final long seed = args.length > 0 ? Long.parseLong(args[0]) : System.currentTimeMillis();
    System.out.println("Seed: " + seed);
    final SedgewickRandom r = new SedgewickRandom(seed);

    // Create instances
    final ApportionmentInstanceFactory.KFactory kFactory = new ApportionmentInstanceFactory.KFactory(FACT_K);
    final List<ApportionmentInstanceWithMethod> tests = new LinkedList<>();
    for ( int i = 0; i < REPS; i++ ) {
      final ApportionmentInstance inst = ApportionmentInstanceFactory.randomInstance(r, UniformVotes, r.uniform(MIN_N, MAX_N), kFactory);
      final double alpha = r.uniform(MIN_ALPHA, MAX_ALPHA);
      final double beta = r.uniform(0.0, 1.5 * alpha);
      tests.add(new ApportionmentInstanceWithMethod(inst.votes, inst.k, alpha, beta));
    }

    // Test all combinations of algorithm and instance
    for ( Class<? extends LinearApportionmentMethod> alg : algs ) {
      System.out.println();
      boolean correct = true;
      final ArrayList<String> errors = new ArrayList<>(4);
      for ( ApportionmentInstanceWithMethod inst : tests ) {
        // Instantiate implementation and run on instance
        LinearApportionmentMethod algInst = alg.getConstructor(double.class, double.class).newInstance(inst.alpha, inst.beta);
        Apportionment result = algInst.apportion(inst);

        final int n = inst.votes.length;

        // Tests against dumb mistakes
        if ( result.seats.length != n || result.tiedSeats.length != n ) {
          errors.add("wrong number of parties served (" + result.seats.length + ";" + result.tiedSeats.length + ")");
          correct = false;
        }

        int sumSeats = sum(result.seats);
        if ( sumSeats > inst.k ) {
          errors.add("too many seats assigned (" + sumSeats + ")");
          correct = false;
        }

        int sumTiedSeats = sum(result.tiedSeats);
        if ( !isBinary(result.tiedSeats) ) {
          errors.add("bad number of tied seats");
          correct = false;
        }
        if ( sumSeats + sumTiedSeats < inst.k ) {
          errors.add("not enough seats assigned (" + sumSeats + " + [" + sumTiedSeats + "])");
          correct = false;
        }

        // If there are ties to break, verify that all tied seats have value astar, and the others a smaller.
        // First, count the number of parties who have tied with the last seat but were not considered
        int sumAstarsNotTaken = 0;
        for ( int i = 0; i < result.seats.length; i++ ) {
          final double nextVal = algInst.d(result.seats[i]) / inst.votes[i];
          if ( fuzzyEquals(nextVal, result.astar) ) {
            sumAstarsNotTaken++;
          }
        }
        if ( sumAstarsNotTaken > 0 ) {
          // There are ties to be broken

          for ( int i = 0; i < result.tiedSeats.length; i++ ) {
            if ( result.tiedSeats[i] == 0 && result.seats[i] > 0 ) {
              // Party is not tied, so this party should NOT have gotten its last seat with value astar!
              final double lastVal = algInst.d(result.seats[i] - 1) / inst.votes[i];

              if ( !fuzzyLess(lastVal, result.astar) ) {
                errors.add("tied seats are wrong (i=" + i + " not tied, but lastVal=" + lastVal + ")");
                correct = false;
              }
            }
            if ( result.tiedSeats[i] == 1 ) {
              // Party is tied for its last seat, so its next value should be astar!
              final double nextVal = algInst.d(result.seats[i]) / inst.votes[i];

              if ( !fuzzyEquals(nextVal, result.astar) ) {
                errors.add("tied seats are wrong (i=" + i + " tied, but nextVal=" + nextVal + ")");
                correct = false;
              }
            }
          }
        }

        // Verify all implied seat assignments according to the min-max-inequality (Pukelsheim Theorem 4.5)
        if ( correct ) {
          // Test each assignment
          for ( int[] asgnm : result.assignments() ) {
            if ( asgnm.length != result.seats.length ) {
              errors.add("derived assignment has wrong size (" +
                  asgnm.length + "; " + Arrays.toString(asgnm) + ")");
            }
            if ( sum(asgnm) != inst.k ) {
              errors.add("derived assignment has wrong seat number (" +
                  sum(asgnm) + "; " + Arrays.toString(asgnm) + ")");
              correct = false;
            }

            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for ( int i = 0; i < n; i++ ) {
              double quotient = fuzzyEquals(algInst.d(result.seats[i]), 0) ?
                  Double.NEGATIVE_INFINITY :
                  inst.votes[i] / algInst.d(result.seats[i]);
              if ( quotient > max ) max = quotient;
              quotient = result.seats[i] == 0 ? Double.POSITIVE_INFINITY :
                  inst.votes[i] / algInst.d(result.seats[i] - 1);
              if ( quotient < min ) min = quotient;
            }
            if ( max > min ) {
              errors.add("seat assignment wrong (" + max + " > " + min + ")" + " for " + System.getProperty("line.separator") +
                  "\t" + Arrays.toString(asgnm));
              correct = false;
            }
            if ( fuzzyGreater(1 / result.astar, min) || fuzzyLess(1 / result.astar, max) ) {
              errors.add("astar not between " + 1 / min + " and " + 1 / max + " for " + System.getProperty("line.separator") +
                  "\t" + Arrays.toString(asgnm));
              correct = false;
            }
          }
        }

        // Verify that the counters (if any) make sense
        if ( algInst instanceof AlgorithmWithCounters ) {
          final AlgorithmWithCounters awc = (AlgorithmWithCounters)algInst;

          if ( "missingSeats".equals(awc.getCounterLabel(0)) ) {
                        /* This is a Pukelsheim implementation. It should never have more than
                         * n resp. floor(n/2) seats missing or too many. */
            // TODO realistic? We do fuzzy stuff, after all
            final double divisor = algInst.isStationary() ? 2 : 1;
            if ( Math.abs(awc.getLastCounter(0)) > n / divisor ) {
              errors.add("Estimator is off by too much"
                  + (algInst.isStationary() ? " for a stationary method" : "")
                  + "; missed house size by " + awc.getLastCounter(0) + ".");
              correct = false;
            }
          }
          else if ( "|I_x|".equals(awc.getCounterLabel(0)) ) {
            // This is SelectAStar.
            // Check that I_x was neither empty nor too large
            if ( awc.getLastCounter(0) < 0 || awc.getLastCounter(0) > n ) {
              errors.add("I_x has weird size: " + awc.getLastCounter(0));
              correct = false;
            }

            // Check number of candidates against the proven upper size bound.
            if ( algInst.isStationary() ) {
              // Stationary method; upper bound 2n, plus allowance for fuzzy arithmetics
              if ( awc.getLastCounter(1) > 3 * n ) {
                errors.add("Candidate set too large for stationary method: "
                    + awc.getLastCounter(1));
                correct = false;
              }
            }
            else {
              // General bound of 2 * (1 + beta/alpha) * n, plus allowance for fuzzy arithmetics
              if ( awc.getLastCounter(1) > (2 * (1 + inst.beta / inst.alpha) + 1) * n ) {
                errors.add("Candidate set too large: "
                    + awc.getLastCounter(1));
                correct = false;
              }
            }
          }
          else {
            errors.add("Untested counter '" + awc.getCounterLabel(0) + "'");
            correct = false;
          }
        }

        if ( !correct ) {
          printError(errors, algInst, inst, result);
          break;
        }
      }

      if ( correct ) {
        System.out.println(alg.getSimpleName() + " is correct. Hopefully.");
      }
    }
  }

  private static void printError(final Iterable<String> msgs, Object... details) {
    for ( String msg : msgs ) {
      System.out.println("ERROR: " + msg);
    }
    for ( Object o : details ) {
      System.out.println(o.toString());
    }
  }

  private static class ApportionmentInstanceWithMethod extends ApportionmentInstance {
    final double alpha;
    final double beta;

    public ApportionmentInstanceWithMethod(double[] votes, int k, double alpha, double beta) {
      super(votes, k);
      this.alpha = alpha;
      this.beta = beta;
    }
  }
}
