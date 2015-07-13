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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class RunningTimeMain {

	static String SEP = "\t";

	public static void main(String[] args)
		  throws InvocationTargetException, NoSuchMethodException, InstantiationException,
		  IllegalAccessException, IOException {
		if (args.length < 1) {
			System.out.println(
				  "Usage: RunningTimeMain algo1,algo2,... [n1,n2,...] [repetitions-per-timing] [inputs-per-n] [seed] [uniform|exponential] [alpha] [beta]");
			System.exit(42);
		}

		List<Integer> ns = Arrays.asList(10, 20);
		ApportionmentInstance.KFactory k = new ApportionmentInstance.KFactory(5,10);
		int repetitions = 1;
		int inputsPerN = 1;
		String inputType = "uniform";
		double alpha = 1, beta = 1;
		long seed = System.currentTimeMillis();

		final String[] algosArray = args[0].split("\\s*,\\s*");
		if (args.length >= 2) { 
			final String[] nsArray = args[1].split("\\s*,\\s*");
			ns = new ArrayList<Integer>(nsArray.length);
			for (final String n : nsArray) {
				ns.add(Integer.parseInt(n));
			}
		}
		if (args.length >= 3) {
		  final String[] ksArray = args[2].split("\\s*,\\s*");
			if ( ksArray.length == 1 ) {
			  k = new ApportionmentInstance.KFactory(Integer.parseInt(ksArray[0]));
			}
			else if ( ksArray.length == 2 ) {
			  k = new ApportionmentInstance.KFactory(Integer.parseInt(ksArray[0]), Integer.parseInt(ksArray[1]));
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

		final List<String> algoNames = new LinkedList<String>();
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

		final String name = inputType + "-ns-" + ns.toString().replaceAll("\\s+","") + "-rep-" + repetitions + "-per-n-" + inputsPerN + "-seed-" + seed;
		//new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		BufferedWriter out = null;
		BufferedWriter avgOut = null;
		try {
			out = new BufferedWriter(new FileWriter("apportionment-times-" + name + ".tab"));
			out.write("algo");
			out.write(SEP);
			out.write("n");
			out.write(SEP);
			out.write("k");
			out.write(SEP);
			out.write("input-nr");
			out.write(SEP);
			out.write("1/unit-size");
			out.write(SEP);
			out.write("repetitions");
			out.write(SEP);
			out.write("total-ms");
			out.write(SEP);
			out.write("single-run-ms");
			out.write(SEP);
			out.write("single-run-ms/n");
			out.write(SEP);
			out.write("input-type");
			out.write(SEP);
			out.write("alpha");
			out.write(SEP);
			out.write("beta");
			out.write(SEP);
			out.write("seed");
			out.write(SEP);
			out.write("ns");
			out.newLine();
			out.flush();
			
			avgOut = new BufferedWriter(new FileWriter("apportionment-times-" + name + "-avgs.tab"));
			avgOut.write("algo");
			avgOut.write(SEP);
			avgOut.write("n");
			avgOut.write(SEP);
			avgOut.write("avg-run-ms");
			avgOut.write(SEP);
			avgOut.write("avg-run-ms/n");
			avgOut.newLine();
			avgOut.flush();

			for (final String algoName : algoNames) {
				final SedgewickRandom random = new SedgewickRandom(seed);
				final LinearApportionment alg = algoInstance(algoName, alpha, beta);
				System.out.println("\n\n\nStarting with algo " + algoName + now());
				for (final int n : ns) {
					System.out.println("\tUsing n=" + n + now());
					
					double sizeTotalRunMillis = 0.0;
					
					for (int inputNr = 1; inputNr <= inputsPerN; ++inputNr) {
						System.out.println("\t\tinputNr=" + inputNr + now());
						final ApportionmentInstance input;
						if ("uniform".equals(inputType)) {
							input = ApportionmentInstance.uniformRandomInstance(random, n, k);
						} else if ("exponential".equals(inputType)) {
							input = ApportionmentInstance.exponentialRandomInstance(random,n,k);
						} else {
							throw new IllegalArgumentException(
								  "Unknown input type " + inputType);
						}
						double unitSize = 0;

						final long startTime = System.nanoTime();
						for (int r = 0; r < repetitions; ++r) {
							unitSize = alg.unitSize(input.population, input.k);
						}
						final long endTime = System.nanoTime();
						final long nanos = endTime - startTime;
						final double millis = nanos / 1000. / 1000;
						final double perRunMillis = millis / repetitions;
            sizeTotalRunMillis += perRunMillis;

						out.write(algoName); //"algo"
						out.write(SEP);
						out.write(String.valueOf(n)); //"n"
						out.write(SEP);
						out.write(String.valueOf(input.k)); //"n"
						out.write(SEP);
						out.write(String.valueOf(inputNr)); //"input-nr"
						out.write(SEP);
						out.write(String.valueOf(1 / unitSize)); //"1/unit-size"
						out.write(SEP);
						out.write(String.valueOf(repetitions)); //"repetitions"
						out.write(SEP);
						out.write(String.valueOf(millis)); // "total-time"
						out.write(SEP);
						out.write(String.valueOf(perRunMillis)); // "single-run-ms"
						out.write(SEP);
						out.write(String.valueOf(perRunMillis / n)); // "single-run-ms/n"
						out.write(SEP);
						out.write(inputType);//"input-type"
						out.write(SEP);
						out.write(String.valueOf(alpha)); //"alpha"
						out.write(SEP);
						out.write(String.valueOf(beta));// "beta"
						out.write(SEP);
						out.write(String.valueOf(seed)); //"seed"
						out.write(SEP);
						out.write("\"" + ns.toString().replaceAll("\\s+","") + "\""); // "ns"
						out.newLine();
						System.out.print("\33[1A\33[2K");
					}
					
					avgOut.write(algoName);
			    avgOut.write(SEP);
			    avgOut.write(String.valueOf(n));
			    avgOut.write(SEP);
			    avgOut.write(String.valueOf(sizeTotalRunMillis / inputsPerN) );
			    avgOut.write(SEP);
			    avgOut.write(String.valueOf(sizeTotalRunMillis / inputsPerN / n));
			    avgOut.newLine();
			    avgOut.flush();
				}
				out.flush();
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (avgOut != null) {
				avgOut.close();
			}
		}
		
		// Write gnuplot script the plots the results
		out = null;
		try {
			out = new BufferedWriter(new FileWriter("apportionment-times-" + name + ".gp"));
			out.write("set terminal pngcairo linewidth 2;"); out.newLine();
			
			// Averages per size in one plot
			out.write("set output \"apportionment-times-" + name + "-avgs.png\""); out.newLine();
			out.write("plot "); 
			int i = 0;
			for (final String algoName : algoNames) {
			  if ( i > 0 ) out.write(", "); else i++;
			  out.write("\"<(grep -e " + algoName + "[[:space:]] apportionment-times-" + name + "-avgs.tab)\" using 2:3 ti \"" + algoName + "\"");
			}
			out.newLine(); out.newLine();
			
			// Averages per size normalized by n in one plot
			out.write("set output \"apportionment-times-" + name + "-avgsNorm.png\""); out.newLine();
			out.write("plot "); 
			int j = 0;
			for (final String algoName : algoNames) {
			  if ( j > 0 ) out.write(", "); else j++;
			  out.write("\"<(grep -e " + algoName + "[[:space:]] apportionment-times-" + name + "-avgs.tab)\" using 2:4 ti \"" + algoName + "\"");
			}
			out.newLine(); out.newLine();
			
			// One plot with all points per algorithm
			for (final String algoName : algoNames) {
			  out.write("set output \"apportionment-times-" + name + "-" + algoName + ".png\""); out.newLine();
  			out.write("plot \"<(grep -e " + algoName + "[[:space:]] apportionment-times-" + name + ".tab)\" using 2:8 ti \"" + algoName + "\""); 
  			out.newLine(); out.newLine();
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
				  ApportionmentInstance.uniformRandomInstance(30, new ApportionmentInstance.KFactory(5));
			for (final String algoName : algoNames) {
				algoInstance(algoName, alpha, beta).unitSize(instance.population, 33);
			}
		}

		System.out.println("Warumup finished" + now());
	}

	public static Map<String, Class<? extends LinearApportionment>> algorithms =
		  new LinkedHashMap<String, Class<? extends LinearApportionment>>();
	public static Map<String, String> abbreviations = new HashMap<String, String>();

	static {
		algorithms.put("SelectAstarNaive", SelectAStarNaive.class);
		algorithms.put("SelectAstarOptimalityCheck", SelectAStarWithOptimalityCheck.class);
		algorithms.put("SelectAstar", SelectAStar.class);
		algorithms.put("AStarChengEppstein", AStarChengEppstein.class);
		algorithms.put("HighestAveragesLS", HighestAveragesLS.class);
		algorithms.put("HighestAveragesPQ", HighestAveragesPQ.class);

		abbreviations.put("naive", "SelectAstarNaive");
		abbreviations.put("n", "SelectAstarNaive");
		abbreviations.put("rw", "SelectAstar");
		abbreviations.put("ce", "AStarChengEppstein");
		abbreviations.put("hapq", "HighestAveragesPQ");
		abbreviations.put("hals", "HighestAveragesLS");
	}

	public static LinearApportionment algoInstance(String name, double alpha, double beta)
		  throws IllegalAccessException, InstantiationException, NoSuchMethodException,
		  InvocationTargetException {
		return algorithms.get(name).getConstructor(double.class, double.class).newInstance(
			  alpha, beta);
	}

}
