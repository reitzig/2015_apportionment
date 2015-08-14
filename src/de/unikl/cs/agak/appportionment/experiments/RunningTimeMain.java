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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.*;

/**
 * @author Sebastian Wild (s_wild@cs.uni-kl.de)
 */
public class RunningTimeMain {

    static String SEP = "\t";

    /**
     * Writes a line of strings separated by {@link #SEP} to the given stream, and flushes it.
     * @param target Stream to write to.
     * @param line   List of values to write
     * @throws IOException
     */
    static void writeSeparatedLine(BufferedWriter target, String... line) throws IOException {
        int i = 0;
        for ( String item : line ) {
            if ( i > 0 ) { target.write(SEP); } else { i++; }
            target.write(item);
        }
        target.newLine();
        target.flush();
    }

    public static void main(String[] args)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IOException {
        if (args.length < 1) {
            System.out.println(
                    "Usage: RunningTimeMain algo1,algo2,... [n1,n2,...] [repetitions-per-timing] [inputs-per-n] [seed] [uniform|exponential] [alpha] [beta]");
            System.exit(42);
        }

        // Initialize defaults
        List<Integer> ns = Arrays.asList(10, 20);
        ApportionmentInstanceFactory.KFactory k = new ApportionmentInstanceFactory.KFactory(5, 10);
        int repetitions = 1;
        int inputsPerN = 1;
        String inputType = "uniform";
        double alpha = 1, beta = 1;
        long seed = System.currentTimeMillis();

        // Parse command-line parameters
        final String[] algosArray = args[0].split("\\s*,\\s*");
        if (args.length >= 2) {
            final String[] nsArray = args[1].split("\\s*,\\s*");
            ns = new ArrayList<>(nsArray.length);
            for (final String n : nsArray) {
                ns.add(Integer.parseInt(n));
            }
        }
        if (args.length >= 3) {
            final String[] ksArray = args[2].split("\\s*,\\s*");
            if (ksArray.length == 1) {
                k = new ApportionmentInstanceFactory.KFactory(Integer.parseInt(ksArray[0]));
            } else if (ksArray.length == 2) {
                k = new ApportionmentInstanceFactory.KFactory(Integer.parseInt(ksArray[0]), Integer.parseInt(ksArray[1]));
            }
        }
        if (args.length >= 4) {
            repetitions = Integer.parseInt(args[3]);
        }
        if (args.length >= 5) {
            inputsPerN = Integer.parseInt(args[4]);
        }
        if (args.length >= 6) {
            seed = Long.parseLong(args[5]);
        }
        if (args.length >= 7) {
            inputType = args[6];
        }
        if (args.length >= 8) {
            alpha = Double.parseDouble(args[7]);
        }
        if (args.length >= 9) {
            beta = Double.parseDouble(args[8]);
        }

        final List<String> algoNames = new LinkedList<>();
        if ("all".equalsIgnoreCase(algosArray[0])) {
            algoNames.addAll(algorithms.keySet());
        } else {
            for (final String algo : algosArray) {
                if (algorithms.containsKey(algo)) {
                    algoNames.add(algo);
                } else if (abbreviations.containsKey(algo)) {
                    algoNames.add(abbreviations.get(algo));
                } else {
                    throw new IllegalArgumentException("Unknown algorithm " + algo);
                }
            }
        }

        System.out.println("ns = " + ns);
        System.out.println("ks ~ " + k.toString());
        System.out.println("repetitions = " + repetitions);
        System.out.println("algoNames = " + algoNames);
        System.out.println("inputType = " + inputType);
        System.out.println("alpha = " + alpha);
        System.out.println("beta = " + beta);
        System.out.println("seed = " + seed);

        warmup(algoNames, alpha, beta);

        final String name = "N=" + ns.toString().replaceAll("\\s+", "") +
                "-K=" + k.toString() +
                "-votes=" + inputType +
                "-(alpha,beta)=(" + alpha + "," + beta + ")" +
                "-reps=" + repetitions +
                "-perN=" + inputsPerN +
                "-seed=" + seed;

        // Perform experiments
        BufferedWriter out = null;
        BufferedWriter avgOut = null;
        try {
            out = new BufferedWriter(new FileWriter("times-" + name + ".tab"));
            writeSeparatedLine(out, "algo", "n", "k", "input-nr", "1/unit-size", "repetitions",
                "total-ms", "single-run-ms", "single-run-ms/n",
                "input-type", "alpha", "beta", "seed", "ns");

            avgOut = new BufferedWriter(new FileWriter("avgtimes-" + name + ".tab"));
            writeSeparatedLine(avgOut, "algo", "n", "avg-run-ms", "avg-run-ms/n");

            for (final String algoName : algoNames) {
                final SedgewickRandom random = new SedgewickRandom(seed);
                final LinearApportionmentMethod alg = algoInstance(algoName, alpha, beta);
                System.out.println("\n\n\nStarting with algo " + algoName + now());

                for (final int n : ns) {
                    System.out.println("\tUsing n=" + n + now());

                    double sizeTotalRunMillis = 0.0;

                    for (int inputNr = 1; inputNr <= inputsPerN; ++inputNr) {
                        System.out.println("\t\tinputNr=" + inputNr + now());
                        final ApportionmentInstance input;
                        if ("uniform".equals(inputType)) {
                            input = ApportionmentInstanceFactory.uniformRandomInstance(random, n, k);
                        } else if ("exponential".equals(inputType)) {
                            input = ApportionmentInstanceFactory.exponentialRandomInstance(random, n, k);
                        } else {
                            throw new IllegalArgumentException(
                                    "Unknown input type " + inputType);
                        }
                        Apportionment app = null;

                        final long startTime = System.nanoTime();
                        for (int r = 0; r < repetitions; ++r) {
                            app = alg.apportion(input);
                        }
                        final long endTime = System.nanoTime();
                        final long nanos = endTime - startTime;
                        final double millis = nanos / 1000. / 1000;
                        final double perRunMillis = millis / repetitions;
                        sizeTotalRunMillis += perRunMillis;

                        // do something so the calls to apportion are not optimized away
                        for (int i = 0; i < app.seats.length; i++) {
                            app.seats[i] -= 1;
                        }

                        writeSeparatedLine(out, algoName, String.valueOf(n), String.valueOf(input.k), String.valueOf(inputNr),
                                String.valueOf(1 / app.astar), String.valueOf(repetitions), String.valueOf(millis),
                                String.valueOf(perRunMillis), String.valueOf(perRunMillis / n), inputType,
                                String.valueOf(alpha), String.valueOf(beta), String.valueOf(seed),
                                "\"" + ns.toString().replaceAll("\\s+", "") + "\"");
                        System.out.print("\33[1A\33[2K"); // Overwrite "inputNr=..." line
                    }

                    writeSeparatedLine(avgOut, algoName, String.valueOf(n),
                            String.valueOf(sizeTotalRunMillis / inputsPerN),
                            String.valueOf(sizeTotalRunMillis / inputsPerN / n));
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (avgOut != null) {
                avgOut.close();
            }
        }

        // Write gnuplot script that plots the results
        out = null;
        try {
            out = new BufferedWriter(new FileWriter("plots-" + name + ".gp"));
            out.write("set terminal pngcairo linewidth 2;"); out.newLine();
            out.write("set key top left;"); out.newLine();

            // Averages per size in one plot
            out.write("set output \"avgtimes-" + name + ".png\""); out.newLine();
            out.write("plot ");
            int i = 0;
            for (final String algoName : algoNames) {
                if (i > 0) { out.write(",\\"); out.newLine(); }
                else i++;
                out.write("  \"<(grep -e " + algoName + "[[:space:]] \\\"avgtimes-" + name + ".tab\\\")\" using 2:3 ti \"" + algoName + "\"");
            }
            out.newLine(); out.newLine();

            // Averages per size normalized by n in one plot
            out.write("set output \"avgtimesNorm-" + name + ".png\""); out.newLine();
            out.write("plot ");
            int j = 0;
            for (final String algoName : algoNames) {
                if (j > 0) { out.write(",\\"); out.newLine(); }
                else j++;
                out.write("  \"<(grep -e " + algoName + "[[:space:]] \\\"avgtimes-" + name + ".tab\\\")\" using 2:4 ti \"" + algoName + "\"");
            }
            out.newLine(); out.newLine();

            // One plot with all points per algorithm
            for (final String algoName : algoNames) {
                out.write("set output \"times-" + algoName + "-" + name + ".png\""); out.newLine();
                out.write("plot \"<(grep -e " + algoName + "[[:space:]] \\\"times-" + name + ".tab\\\")\" using 2:8 ti \"" + algoName + "\""); out.newLine();
                out.newLine();
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static String now() {
        return " (" + DateFormat.getTimeInstance().format(new Date()) + ")";
    }

    private static void warmup(final List<String> algoNames, final double alpha,
                               final double beta)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {// warumup
        System.out.println("Starting warmup" + now());

        for (int i = 0; i < 12000; ++i) {
            final ApportionmentInstance instance =
                    ApportionmentInstanceFactory.uniformRandomInstance(30, new ApportionmentInstanceFactory.KFactory(5));
            for (final String algoName : algoNames) {
                algoInstance(algoName, alpha, beta).apportion(instance);
            }
        }

        System.out.println("Warumup finished" + now());
    }

    public static Map<String, Class<? extends LinearApportionmentMethod>> algorithms =
            new LinkedHashMap<>();
    public static Map<String, String> abbreviations = new HashMap<>();

    static {
        algorithms.put("SelectAstarNaive", SelectAStarNaive.class);
        algorithms.put("SelectAstarOptimalityCheck", SelectAStarWithOptimalityCheck.class);
        algorithms.put("SelectAstar", SelectAStar.class);
        algorithms.put("AStarChengEppstein", AStarChengEppstein.class);
        algorithms.put("IterativeDMLS", IterativeDMLS.class);
        algorithms.put("IterativeDMPQ", IterativeDMPQ.class);
        algorithms.put("PukelsheimLS", PukelsheimLS.class);
        algorithms.put("PukelsheimPQ", PukelsheimPQ.class);

        abbreviations.put("naive", "SelectAstarNaive");
        abbreviations.put("n", "SelectAstarNaive");
        abbreviations.put("rw", "SelectAstar");
        abbreviations.put("ce", "AStarChengEppstein");
        abbreviations.put("dmpq", "IterativeDMPQ");
        abbreviations.put("dmls", "IterativeDMLS");
        abbreviations.put("puls", "PukelsheimLS");
        abbreviations.put("pupq", "PukelsheimPQ");
    }

    public static LinearApportionmentMethod algoInstance(String name, double alpha, double beta)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException,
            InvocationTargetException {
        return algorithms.get(name).getConstructor(double.class, double.class).newInstance(
                alpha, beta);
    }

}
