package de.unikl.cs.agak.appportionment.experiments;

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.*;
import de.unikl.cs.agak.appportionment.util.FuzzyNumerics;
import de.unikl.cs.agak.appportionment.util.SedgewickRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class TestMain {
    private static final int MIN_N = 10;
    private static final int MAX_N = 100;
    private static final int FACT_K = 10;
    private static final int REPS  = 1000;
    private static final double MIN_ALPHA = 1.0;
    private static final double MAX_ALPHA = 2.0;
    // beta out of [0,alpha]

    private static List<Class<? extends LinearApportionmentMethod>> algs = Arrays.asList(
            IterativeDMLS.class,
            IterativeDMPQ.class,
            PukelsheimPQ.class,
            AStarChengEppstein.class,
            SelectAStar.class,
            SelectAStarNaive.class,
            SelectAStarWithOptimalityCheck.class
    );

    public static void main(final String[] args) throws Exception {
        // Create instances
        final long seed = System.currentTimeMillis();
        System.out.println("Seed: " + seed);
        final SedgewickRandom r = new SedgewickRandom(seed);
        final ApportionmentInstanceFactory.KFactory kFactory = new ApportionmentInstanceFactory.KFactory(FACT_K);
        final List<ApportionmentInstanceWithMethod> tests = new LinkedList<ApportionmentInstanceWithMethod>();
        for ( int i=0; i<REPS; i++ ) {
            final ApportionmentInstance inst = ApportionmentInstanceFactory.uniformRandomInstance(r, r.uniform(MIN_N, MAX_N), kFactory);
            final double alpha = r.uniform(MIN_ALPHA, MAX_ALPHA);
            tests.add(new ApportionmentInstanceWithMethod(inst.votes, inst.k, alpha, r.uniform(0.0, alpha)));
        }

        for ( Class<? extends LinearApportionmentMethod> alg : algs ) {
            System.out.println();
            boolean correct = true;
            final ArrayList<String> errors = new ArrayList<String>(4);
            for ( ApportionmentInstanceWithMethod inst : tests ) {
                // Instantiate implementation and run on instance
                LinearApportionmentMethod algInst = alg.getConstructor(double.class, double.class).newInstance(inst.alpha, inst.beta);
                Apportionment result = algInst.apportion(inst.votes, inst.k);

                // Tests against dumb mistakes
                if ( result.seats.length != inst.votes.length ) {
                    errors.add("wrong number of parties served (" + result.seats.length + ")");
                    correct = false;
                }

                int sumSeats = 0;
                for ( int s : result.seats ) { sumSeats += s; }
                if ( sumSeats != inst.k ) {
                    errors.add("wrong number of seats assigned (" + sumSeats + ")");
                    correct = false;
                }

                // Verify apportionment according to the min-max-inequality (Pukelsheim Theorem 4.5)
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                for ( int i=0; i<inst.votes.length; i++ ) {
                    double quotient = inst.votes[i] / algInst.d(result.seats[i]);
                    if ( quotient > max ) max = quotient;
                    quotient = inst.votes[i] / algInst.d(result.seats[i]-1);
                    if ( quotient < min ) min = quotient;
                }
                if ( max > min ) {
                    errors.add("seat assignment wrong");
                    correct = false;
                }
                if ( 1/result.astar > min || 1/result.astar < max ) {
                    errors.add("astar wrong");
                    correct = false;
                }

                if ( !correct ) {
                    printError(errors, alg, inst, result);
                    break;
                }
            }

            if ( correct ) {
                System.out.println(alg.getSimpleName() + " correct.");
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
