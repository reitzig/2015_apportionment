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
import edu.princeton.cs.introcs.Stopwatch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Main {
	public static void main(String[] args) throws Exception {

		smallExample();

//		final double v = new SelectAStarPrimitive(1, 1).unitSize(
//			  new double[]{79.0, 92.0, 47.0, 4.0, 74.0, 68.0, 2.0, 3.0, 12.0, 94.0}, 4);
//		System.out.println("1/v = " + 1/v);
//		new SelectAStar(2,1).unitSize(new double[]{30.0, 150.0},4);
//		new SelectAStar(2,1).unitSize(new double[]{20.0, 30.0, 150.0, 17.0, 3.0},4);

//		runAllOn(Instance.uniformRandomInstance(1000), 1, 1);
//		for (int i = 0; i < 10; ++i)
//			runLinearAlgsOn(Instance.exponentialRandomInstance(100000), 1, 1);


//		warmup();


		for (int i = 0; i < 5; ++i)
			runAllOn(ApportionmentInstanceFactory.exponentialRandomInstance(30, new ApportionmentInstanceFactory.KFactory(5)), 1, 0);

//		runAlgsOn(Instance.uniformRandomInstance(1000000),
//			  Arrays.<LinearApportionmentMethod>asList(new SelectAStarNoOptimalityCheck(2, 1)));
//		runAlgsOn(Instance.uniformRandomInstance(100000),
//			  Arrays.<LinearApportionmentMethod>asList(new ChengEppstein(2, 1)));

//		printPeakMemoryUsage();
	}

	private static void numericExperimentsPropagationOfUncertainty() {
		double xa = 1./150.0;
		double ya = 2./150.0;
		System.out.println("xa = " + xa);
		System.out.println("bin(xa) = " + Long.toBinaryString(
			  Double.doubleToLongBits(xa)));

		System.out.println("ya = " + ya);
		double x = 5e10-1;
		x /= 15e10;
		double ss = x - xa;
		double r = ss / ya;
		System.out.println("ss = " + ss);
		System.out.println("r = " + r);
		System.out.println(
			  "Long.toBinaryString(Double.doubleToLongBits(r)) = " + Long.toBinaryString(
					 Double.doubleToLongBits(r)));
		System.out.println(
					  "Long.toBinaryString(Double.doubleToLongBits(r)+1) = " + Long.toBinaryString(
							 Double.doubleToLongBits(r)+1));
		System.out.println(
					  "r+1L = " + Double.longBitsToDouble(Double.doubleToLongBits(r)+1));

		double n = Double.longBitsToDouble(Double.doubleToLongBits(1.)-1);
		double d = Double.longBitsToDouble(Double.doubleToLongBits(1.)+1);
		System.out.println("n/d = " + n/d);
		System.out.println("n/d = " + Long.toBinaryString(Double.doubleToLongBits(n / d)));


		System.out.println(
			  "(0.02 - 0.006666667) / 0.013333333 = " + (0.02 - 0.006666667) / 0.013333333);
	}

	private static void warmup() {// warm up JIT
		for (int i = 0; i < 12000; ++i) {
			final ApportionmentInstance
				  instance = ApportionmentInstanceFactory.uniformRandomInstance(30, new ApportionmentInstanceFactory.KFactory(5,10));
			new SelectAStarNaive(2, 1).apportion(instance.votes, 33);
			new SelectAStarWithOptimalityCheck(2, 1).apportion(instance.votes, 33);
			new SelectAStar(2, 1).apportion(instance.votes, 33);
			new AStarChengEppstein(2, 1).apportion(instance.votes, 33);
			new IterativeDMPQ(2, 1).apportion(instance.votes, 33);
			new IterativeDMLS(2, 1).apportion(instance.votes, 33);
			new PukelsheimPQ(2, 1).apportion(instance.votes, 33);
		}

		System.out.println("Warmup finished");
	}

	private static void printPeakMemoryUsage() {
		// stolen from http://www.inoneo.com/en/blog/9/java/get-the-jvm-peak-memory-usage
		// according to http://stackoverflow.com/a/1764271
		// we want to consider the sum Eden Space plus Old Gen plus Survivor Space


		try {
//			String memoryUsage = new String();
			List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
			long sum = 0;
			for (MemoryPoolMXBean pool : pools) {
				MemoryUsage peak = pool.getPeakUsage();
				if ("PS Eden Space".equals(pool.getName()) || "PS Survivor Space".equals(
					  pool.getName()) || "PS Old Gen".equals(pool.getName()))
					sum += peak.getUsed();
//				memoryUsage += String.format("Peak %s memory used: %,d MB%n", pool.getName(),
//					  (long) (peak.getUsed() / 1024. / 1024));
//				memoryUsage += String.format("Peak %s memory reserved: %,d%n", pool.getName(),
//					  peak.getCommitted());
			}

			// we print the result in the console
//			System.out.println(memoryUsage);

			System.out.printf("Peak Memory used: %.2f MB.", (sum / 1024. / 1024));

		} catch (Throwable t) {
			System.err.println("Exception in agent: " + t);
		}

	}


	private static void runAllOn(ApportionmentInstance instance, double alpha, double beta)
		  throws Exception {
		List<LinearApportionmentMethod> algs = Arrays.asList(
		  new SelectAStarNaive(alpha, beta), 
		  new SelectAStarWithOptimalityCheck(alpha, beta), 
		  new AStarChengEppstein(alpha, beta),
			new SelectAStar(alpha, beta),
			new IterativeDMPQ(alpha, beta),
			new IterativeDMPQ(alpha, beta),
			new PukelsheimPQ(alpha, beta));
		runAlgsOn(instance, algs);
	}

	private static void runLinearAlgsOn(ApportionmentInstance instance, double alpha, double beta)
		  throws Exception {
		List<LinearApportionmentMethod> algs = Arrays.asList(
		  new SelectAStarWithOptimalityCheck(alpha, beta),
		  new AStarChengEppstein(alpha, beta), 
		  new SelectAStar(alpha, beta),
			new IterativeDMLS(alpha, beta),
			new IterativeDMPQ(alpha, beta),
			new PukelsheimPQ(alpha, beta));
		runAlgsOn(instance, algs);
	}

	private static void runAlgsOn(final ApportionmentInstance instance,
		  final List<LinearApportionmentMethod> algs) throws Exception {
		Map<LinearApportionmentMethod, Apportionment> apportionments =
                new HashMap<>(8);
		for (final LinearApportionmentMethod alg : algs) {

			try {
				final Stopwatch stopwatch = new Stopwatch();
				final Apportionment app = alg.apportion(instance.votes, instance.k);
				System.out.println(alg + " took " + stopwatch.elapsedTime() + "s.");

				apportionments.put(alg, app);
			} catch (Exception e) {
				System.out.println("instance = " + instance);
				throw e;
			}
		}
		System.out.println("apportionments = " + apportionments);
		// all equal? 
		// TODO does not make sense anymore
		Apportionment value = null;
		for (final LinearApportionmentMethod alg : algs) {
			if (value == null) value = apportionments.get(alg);
			else if (!value.equals(apportionments.get(alg))) {
				System.out.println("\tNOT ALL EQUAL!");
				System.exit(1);
			}
		}
		System.out.println("\tall equal.");
	}

	private static void smallExample() {
		boolean allEqual = true;
		final double alpha = 2.0, beta = 1.0;

		for (int k = 1; k < 50; ++k) {

			double[] example = {20.0, 30.0, 150.0, 17.0, 3.0};
//		example = new double[]{30.0, 150.0};
//		int k = 6;
			System.out.println("\n\nk = " + k);

			LinearApportionmentMethod ce = new AStarChengEppstein(alpha, beta);
			LinearApportionmentMethod sel = new SelectAStarWithOptimalityCheck(alpha, beta);
			LinearApportionmentMethod pri = new SelectAStarNaive(alpha, beta);
			LinearApportionmentMethod puk = new PukelsheimPQ(alpha, beta);
			System.out.println("\tExample: " + sel.apportion(example, k) + "");
			System.out.println("\tExample: " + pri.apportion(example, k) + "");
			System.out.println("\tExample 1/unit: " + 1 / sel.apportion(example, k).astar + "");
			System.out.println("\tExample: " + ce.apportion(example, k) + "");
			System.out.println(
				  "sel.unitSize(example,k) == ce.unitSize(example,k) = " + (sel.apportion(
                          example, k) == ce.apportion(example, k)));
			allEqual &= pri.apportion(example, k) == ce.apportion(example, k);
			allEqual &= pri.apportion(example, k) == sel.apportion(example, k);
		}
		System.out.println("allEqual = " + allEqual);
	}

}
