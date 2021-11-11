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
import de.unikl.cs.agak.appportionment.algorithms.*;
import de.unikl.cs.agak.appportionment.methods.AlmostLinearDivisorMethod;
import de.unikl.cs.agak.appportionment.methods.LinearDivisorMethod;
import de.unikl.cs.agak.appportionment.methods.examples.*;
import de.unikl.cs.agak.appportionment.util.SedgewickRandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.unikl.cs.agak.appportionment.experiments.ApportionmentInstanceFactory.*;

/**
 * Executes running time experiments for all divisor method implementations in
 * {@link de.unikl.cs.agak.appportionment.algorithms}.
 *
 * @author Sebastian Wild (wild@cs.uni-kl.de)
 */
public class RunningTimeMain {

  private static String SEP = "\t";

  /**
   * Writes a line of strings separated by {@link #SEP} to the given stream, and flushes it.
   *
   * @param target Stream to write to.
   * @param line   List of values to write
   * @throws IOException
   */
  static void writeSeparatedLine(BufferedWriter target, String... line) throws IOException {
    int i = 0;
    for ( String item : line ) {
      if ( i > 0 ) {
        target.write(SEP);
      }
      else {
        i++;
      }
      target.write(item);
    }
    target.newLine();
    target.flush();
  }

  /**
   * Prints the gnuplot commands necessary to setup a nice plot to the given writer
   *
   * @param out        The target stream
   * @param targetFile The file the plot is saved to.
   * @param title      The title of the plot
   * @param xlabel     The label of the x-axis
   * @param ylabel     The label of the y-axis
   * @throws IOException
   */
  private static void setupPlot(BufferedWriter out, String targetFile, String title, String xlabel, String ylabel)
      throws IOException {
    out.write("set output \"" + targetFile + "\"");
    out.newLine();
    out.write("set title \"" + title + "\"");
    out.newLine();
    out.write("set xlabel \"" + xlabel + "\"");
    out.newLine();
    out.write("set ylabel \"" + ylabel + "\"");
    out.newLine();
  }

  /**
   * Assumes that the following folders are in place and writable:
   * .
   * |- data
   * |- plots
   * |   |- times
   * |   |- counters
   * |   |- scatter
   * |   |- averages
   * |- tmp
   *
   * @param args The experiment parameters
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IOException
   */
  public static void main(String[] args)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException, IOException {
    if ( args.length < 1 ) {
      System.out.println(
          "Usage: RunningTimeMain algo1,algo2,... [n1,n2,...] [c|cmin,cmax] [repetitions-per-timing] [inputs-per-n] [seed] [uniform|exponential|pareto3] [alpha] [beta]");
      System.exit(42);
    }

    // Initialize defaults
    List<Integer> ns = Arrays.asList(10, 20);
    ApportionmentInstanceFactory.KFactory k = new ApportionmentInstanceFactory.KFactory(5, 10);
    int repetitions = 1;
    int inputsPerN = 1;
    String inputType = "uniform";
    AlmostLinearDivisorMethod dm = new LinearDivisorMethod(1, 1);
    long seed = System.currentTimeMillis();

    // Parse command-line parameters
    final String[] algosArray = args[0].split("\\s*,\\s*");
    if ( args.length >= 2 ) {
      final String[] nsArray = args[1].split("\\s*,\\s*");
      ns = new ArrayList<>(nsArray.length);
      for ( final String n : nsArray ) {
        ns.add(Integer.parseInt(n));
      }
    }
    if ( args.length >= 3 ) {
      final String[] ksArray = args[2].split("\\s*,\\s*");
      if ( ksArray.length == 1 ) {
        k = new ApportionmentInstanceFactory.KFactory(Integer.parseInt(ksArray[0]));
      }
      else if ( ksArray.length == 2 ) {
        k = new ApportionmentInstanceFactory.KFactory(Integer.parseInt(ksArray[0]), Integer.parseInt(ksArray[1]));
      }
    }
    if ( args.length >= 4 ) {
      repetitions = Integer.parseInt(args[3]);
    }
    if ( args.length >= 5 ) {
      inputsPerN = Integer.parseInt(args[4]);
    }
    if ( args.length >= 6 ) {
      seed = Long.parseLong(args[5]);
    }
    if ( args.length >= 7 ) {
      inputType = args[6];
    }
    if ( args.length >= 8 ) {
      dm = dmInstance(args[7]);
    }

    final List<String> algoNames = new LinkedList<>();
    if ( "all".equalsIgnoreCase(algosArray[0]) ) {
      algoNames.addAll(algorithms.keySet());
    }
    else {
      for ( final String algo : algosArray ) {
        if ( algorithms.containsKey(algo) ) {
          algoNames.add(algo);
        }
        else if ( algAbbreviations.containsKey(algo) ) {
          algoNames.add(algAbbreviations.get(algo));
        }
        else {
          throw new IllegalArgumentException("Unknown algorithm " + algo);
        }
      }
    }

    System.out.println("ns = " + ns);
    System.out.println("ks ~ " + k.toString());
    System.out.println("repetitions = " + repetitions);
    System.out.println("algoNames = " + algoNames);
    System.out.println("inputType = " + inputType);
    System.out.println("dm = " + dm);
    System.out.println("seed = " + seed);

    warmup(algoNames, dm);

    final String name = "N=" + ns.toString().replaceAll("\\s+", "") +
        "-K=" + k.toString() +
        "-votes=" + inputType +
        "-dm=" + dm +
        "-reps=" + repetitions +
        "-perN=" + inputsPerN +
        "-seed=" + seed;

    // Perform experiments
    BufferedWriter out = null;
    BufferedWriter avgOut = null;
    try {
      out = new BufferedWriter(new FileWriter("data" + System.getProperty("file.separator") +
          "times-" + name + ".tab"));
      writeSeparatedLine(out, "algo", "n", "k", "input-nr", "1/unit-size", "repetitions",
          "total-ms", "single-run-ms", "single-run-ms/n",
          "input-type", "dm", "seed", "ns", "[counter counter/n]*");

      avgOut = new BufferedWriter(new FileWriter("data" + System.getProperty("file.separator") +
          "avgtimes-" + name + ".tab"));
      writeSeparatedLine(avgOut, "algo", "n", "avg-run-ms", "avg-run-ms/n");

      for ( final String algoName : algoNames ) {
        final SedgewickRandom random = new SedgewickRandom(seed);

        final ApportionmentAlgorithm alg = algoInstance(algoName);
        System.out.println("\n\n\nStarting with algo " + algoName + now());

        for ( final int n : ns ) {
          System.out.println("\tUsing n=" + n + now());

          double sizeTotalRunMillis = 0.0;

          for ( int inputNr = 1; inputNr <= inputsPerN; ++inputNr ) {
            System.out.println("\t\tinputNr=" + inputNr + now());
            final VoteFactory vf;
            switch ( inputType ) {
              case "uniform":
                vf = UniformVotes;
                break;
              case "exponential":
                vf = ExponentialVotes;
                break;
              case "poisson":
                vf = PoissonVotes;
                break;
              case "pareto1.5":
                vf = Pareto1_5Votes;
                break;
              case "pareto2":
                vf = Pareto2Votes;
                break;
              case "pareto3":
                vf = Pareto3Votes;
                break;
              default:
                throw new IllegalArgumentException("Unknown input type " + inputType);
            }
            final ApportionmentInstance input = ApportionmentInstanceFactory.randomInstance(random, vf, n, k);
            Apportionment app = null;

            final long startTime;
            final long endTime;
            try {
              startTime = System.nanoTime();
              for ( int r = 0; r < repetitions; ++r ) {
                app = alg.apportion(input, dm);
              }
              endTime = System.nanoTime();
            }
            catch ( Exception e ) {
              System.err.println("Critical error during experiment; this should not happen!");
              System.err.println("");
              System.err.println(e.getMessage());
              e.printStackTrace(System.err);
              System.err.println("");
              System.err.println("Happened for " + alg + " on:");
              System.err.println(input.toString());
              System.err.println();
              continue;
            }

            final long nanos = endTime - startTime;
            final double millis = nanos / 1000. / 1000;
            final double perRunMillis = millis / repetitions;
            sizeTotalRunMillis += perRunMillis;

            // do something so the calls to apportion are not optimized away
            for ( int i = 0; i < app.seats.length; i++ ) {
              app.seats[i] -= 1;
            }

            String counters = "-1";
            if ( alg instanceof AlgorithmWithCounters ) {
              final AlgorithmWithCounters awc = (AlgorithmWithCounters)alg;
              counters = "";
              for ( int i = 0; i < awc.numberOfCounters(); i++ ) {
                counters = counters + SEP + awc.getLastCounter(i) + SEP + ((double)awc.getLastCounter(i)) / n;
              }
            }
            writeSeparatedLine(out, algoName, String.valueOf(n), String.valueOf(input.k), String.valueOf(inputNr),
                String.valueOf(1 / app.astar), String.valueOf(repetitions), String.valueOf(millis),
                String.valueOf(perRunMillis), String.valueOf(perRunMillis / n),
                inputType, dm.toString(), String.valueOf(seed),
                "\"" + ns.toString().replaceAll("\\s+", "") + "\"",
                counters);
            System.out.print("\33[1A\33[2K"); // Overwrite "inputNr=..." line so the shell is not totally swamped
          }

          writeSeparatedLine(avgOut, algoName, String.valueOf(n),
              String.valueOf(sizeTotalRunMillis / inputsPerN),
              String.valueOf(sizeTotalRunMillis / inputsPerN / n));
        }
      }
    } finally {
      if ( out != null ) {
        out.close();
      }
      if ( avgOut != null ) {
        avgOut.close();
      }
    }

    // Write gnuplot script that plots the results
    out = null;
    try {
      out = new BufferedWriter(new FileWriter("tmp" + System.getProperty("file.separator") +
          "plots-" + name + ".gp"));
      out.write("set terminal pngcairo linewidth 2;");
      out.newLine();
      out.write("set key top left;");
      out.newLine();

      // Averages per size in one plot
      setupPlot(out,
          "plots/averages/avgtimes-" + name + ".png",
          dm + " on " + inputType + " votes for k=" + k,
          "n",
          "ms");
      out.write("plot ");
      int i = 0;
      for ( final String algoName : algoNames ) {
        if ( i > 0 ) {
          out.write(",\\");
          out.newLine();
        }
        else i++;
        out.write("  \"<(grep -e " + algoName + "[[:space:]] \\\"data/avgtimes-" + name + ".tab\\\")\" using 2:3 ti \"" + algoName + "\"");
      }
      out.newLine();
      out.newLine();

      // Averages per size normalized by n in one plot
      setupPlot(out,
          "plots/averages/avgtimesNorm-" + name + ".png",
          dm + " on " + inputType + " votes for k=" + k,
          "n",
          "ms/n");
      out.write("plot ");
      i = 0;
      for ( final String algoName : algoNames ) {
        if ( i > 0 ) {
          out.write(",\\");
          out.newLine();
        }
        else i++;
        out.write("  \"<(grep -e " + algoName + "[[:space:]] \\\"data/avgtimes-" + name + ".tab\\\")\" using 2:4 ti \"" + algoName + "\"");
      }
      out.newLine();
      out.newLine();

      // Plots for single algorithms
      for ( final String algoName : algoNames ) {
        // One plot with all points per algorithm
        setupPlot(out,
            "plots/times/times-" + algoName + "-" + name + ".png",
            algoName + " with " + dm + " on " + inputType + " votes for k=" + k,
            "n",
            "ms");
        out.write("plot \"<(grep -e " + algoName + "[[:space:]] \\\"data/times-" + name + ".tab\\\")\" using 2:8");
        out.newLine();
        out.newLine();

        // Another with the same times, but normalized
        setupPlot(out,
            "plots/times/timesNorm-" + algoName + "-" + name + ".png",
            algoName + " with " + dm + " on " + inputType + " votes for k=" + k,
            "n",
            "ms/n");
        out.write("plot \"<(grep -e " + algoName + "[[:space:]] \\\"data/times-" + name + ".tab\\\")\" using 2:9");
        out.newLine();
        out.newLine();

        // If this algorithm has counters, plot them as well
        final ApportionmentAlgorithm dummyInst = algoInstance(algoName);
        if ( dummyInst instanceof AlgorithmWithCounters ) {
          final AlgorithmWithCounters awc = (AlgorithmWithCounters)dummyInst;

          // Handle each counter separately
          for ( int c = 0; c < awc.numberOfCounters(); c++ ) {
            // One plot for the raw counter values
            setupPlot(out,
                "plots/counters/counter-" + algoName + "-" + name + "-" + awc.getCounterLabel(c) + ".png",
                algoName + " with " + dm + " on " + inputType + " votes for k=" + k,
                "n",
                awc.getCounterLabel(c));
            out.write("plot \"<(grep -e " + algoName + "[[:space:]] \\\"data/times-" + name + ".tab\\\")\" using 2:" + (14 + 2 * c));
            out.newLine();
            out.newLine();
            // One for the normalized version
            setupPlot(out,
                "plots/counters/counterNorm-" + algoName + "-" + name + "-" + awc.getCounterLabel(c) + ".png",
                algoName + " with " + dm + " on " + inputType + " votes for k=" + k,
                "n",
                awc.getCounterLabel(c) + "/n");
            out.write("plot \"<(grep -e " + algoName + "[[:space:]] \\\"data/times-" + name + ".tab\\\")\" using 2:" + (14 + 2 * c + 1));
            out.newLine();
            out.newLine();

            // And a scatterplots of counters vs times for each n
            for ( int n : ns ) {
              setupPlot(out,
                  "plots/scatter/scatter-" + algoName + "-" + name + "-" + awc.getCounterLabel(c) + "-" + n + ".png",
                  algoName + " with " + dm + " on " + inputType + " votes for n=" + n + ", k=" + k,
                  awc.getCounterLabel(c),
                  "ms");
              out.write("plot \"<(grep -e " + algoName + "[[:space:]]" + n + "[[:space:]] " +
                  "\\\"data/times-" + name + ".tab\\\")\" using " + (14 + 2 * c) + ":8");
              out.newLine();
              out.newLine();
            }

            // Aaand a scatterplot for all n vs time
            setupPlot(out,
                "plots/scatter/scatter-" + algoName + "-" + name + "-" + awc.getCounterLabel(c) + ".png",
                algoName + " with " + dm + " on " + inputType + " votes for k=" + k,
                awc.getCounterLabel(c),
                "ms");
            out.write("plot ");
            i = 0;
            for ( int n : ns ) {
              if ( i > 0 ) {
                out.write(",\\");
                out.newLine();
              }
              else i++;
              out.write("  \"<(grep -e " + algoName + "[[:space:]]" + n + "[[:space:]] " +
                  "\\\"data/times-" + name + ".tab\\\")\" using " + (14 + 2 * c) + ":8");
            }
            out.newLine();
            out.newLine();
          }
        }
      }
    } finally {
      if ( out != null ) {
        out.close();
      }
    }
  }

  private static String now() {
    return " (" + DateFormat.getTimeInstance().format(new Date()) + ")";
  }

  private static void warmup(final List<String> algoNames, final AlmostLinearDivisorMethod dm)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    System.out.println("Starting warmup" + now());

    SedgewickRandom r = SedgewickRandom.instance;
    for ( int i = 0; i < 12000; ++i ) {
      final VoteFactory[] vfs = new VoteFactory[] {
          UniformVotes, ExponentialVotes, PoissonVotes, Pareto1_5Votes, Pareto2Votes, Pareto3Votes
      };
      final ApportionmentInstance instance =
          ApportionmentInstanceFactory.randomInstance(r, vfs[r.uniform(0, vfs.length)], 50, new ApportionmentInstanceFactory.KFactory(5));
      for ( final String algoName : algoNames ) {
        algoInstance(algoName).apportion(instance, dm);
      }
    }

    System.out.println("Warumup finished" + now());
  }

  public static Map<String, Class<? extends ApportionmentAlgorithm>> algorithms = new LinkedHashMap<>();
  public static Map<String, Class<? extends AlmostLinearDivisorMethod>> methods = new LinkedHashMap<>();
  public static Map<String, String> algAbbreviations = new HashMap<>();
  public static Pattern ldmSpec = Pattern.compile("\\ALDM\\((?<alpha>\\d*\\.?\\d+),(?<beta>\\d*\\.?\\d+)\\)\\z");

  static {
    //algorithms.put("SelectAstarNaive", SandwichSelectNaive.class);
    //algorithms.put("SelectAstarOptimalityCheck", SandwichSelectWithOptimalityCheck.class);
    algorithms.put("SandwichSelect", SandwichSelect.class);
    algorithms.put("ChengEppsteinSelect", ChengEppsteinSelect.class);
    algorithms.put("IterativeDMLS", IterativeDMLS.class);
    algorithms.put("IterativeDMPQ", IterativeDMPQ.class);
    algorithms.put("PukelsheimLS", PukelsheimLS.class);
    algorithms.put("PukelsheimPQ", PukelsheimPQ.class);

    algAbbreviations.put("rw", "SandwichSelect");
    algAbbreviations.put("rwit", "SandwichSelectIter");
    algAbbreviations.put("ce", "ChengEppsteinSelect");
    algAbbreviations.put("dmls", "IterativeDMLS");
    algAbbreviations.put("dmpq", "IterativeDMPQ");
    algAbbreviations.put("puls", "PukelsheimLS");
    algAbbreviations.put("pupq", "PukelsheimPQ");

    // Legacy:
    algorithms.put("SandwichSelectIter", SandwichSelectIter.class);
    algorithms.put("SandwichSelectV2", SandwichSelectV2.class);
    algAbbreviations.put("rw3", "SandwichSelect");
    algAbbreviations.put("rw3it", "SandwichSelectIter");
    algAbbreviations.put("rw2", "SandwichSelectV2");

    methods.put("SmallestDivisors", SmallestDivisors.class);
    methods.put("GreatestDivisors", GreatestDivisors.class);
    methods.put("SainteLague", SainteLague.class);
    methods.put("ModifiedSainteLague", ModifiedSainteLague.class);
    methods.put("HarmonicMean", HarmonicMean.class);
    methods.put("EqualProportions", EqualProportions.class);
    methods.put("Imperiali", Imperiali.class);
    methods.put("Danish", Danish.class);
  }

  public static ApportionmentAlgorithm algoInstance(String name)
      throws IllegalAccessException, InstantiationException, NoSuchMethodException,
      InvocationTargetException {
    name = algAbbreviations.containsKey(name) ? algAbbreviations.get(name) : name;
    return algorithms.get(name).getConstructor().newInstance();
  }

  public static AlmostLinearDivisorMethod dmInstance(String name)
        throws IllegalAccessException, InstantiationException, NoSuchMethodException,
        InvocationTargetException {
      if ( methods.containsKey(name) ) {
        return methods.get(name).getConstructor().newInstance();
      }
      else {
        Matcher m = ldmSpec.matcher(name);
        if ( m.matches() ) {
          return new LinearDivisorMethod(Double.parseDouble(m.group("alpha")), Double.parseDouble(m.group("beta")));
        }
        else {
          throw new IllegalArgumentException("No divisor method with name '" + name + "'.");
        }
      }

    }

}
