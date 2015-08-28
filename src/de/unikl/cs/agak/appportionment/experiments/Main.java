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
import edu.princeton.cs.introcs.Stopwatch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.unikl.cs.agak.appportionment.experiments.ApportionmentInstanceFactory.ExponentialVotes;
import static de.unikl.cs.agak.appportionment.experiments.ApportionmentInstanceFactory.UniformVotes;
import static edu.princeton.cs.introcs.StdStats.sum;

@Deprecated
class Main {
	public static void main(String[] args) throws Exception {

//		final LinearApportionmentMethod m = new PukelsheimPQ(1,1.5);
//		final LinearApportionmentMethod m = new SandwichSelect(1.353375202278564,1.9663096697020648);
//		final LinearApportionmentMethod m = new SandwichSelect(1.35,1.96);
    final LinearApportionmentMethod m = new PukelsheimLS( 1.2196377621480445,1.2774171590615349);
		final ApportionmentInstance instance = new ApportionmentInstance(
			  new double[]{25.0, 68.0, 76.0, 26.0, 55.0, 19.0, 31.0, 48.0, 81.0, 10.0, 80.0}, 44);
//		final ApportionmentInstance instance = new ApportionmentInstance(
//			  new double[]{46.0, 1.0, 74.0, 9.0, 44.0, 23.0, 85.0, 36.0, 72.0, 12.0, 2.0,
//					 89.0, 95.0, 41.0, 7.0, 7.0, 39.0, 37.0, 60.0, 63.0, 77.0, 18.0, 58.0,
//					 24.0, 95.0, 92.0, 86.0, 20.0, 50.0, 71.0, 99.0, 61.0, 79.0, 32.0, 39.0,
//					 74.0, 25.0, 69.0, 71.0, 48.0, 74.0, 61.0, 87.0}, 430);
		System.out.println("instance = " + instance);
		final Apportionment apportion = m.apportion(instance);
		System.out.println("apportion = " + apportion);
		System.out.println("sum(apportion.seats) = " + sum(apportion.seats));
		System.out.println("sum(apportion.tiedSeats) = " + sum(apportion.tiedSeats));

		final Apportionment apportion2 = new PukelsheimLS(1.35, 1.96).apportion(instance);
		System.out.println("apportion2 = " + apportion2);
		System.out.println("sum(apportion2.seats) = " + sum(apportion2.seats));
		System.out.println("sum(apportion2.tiedSeats) = " + sum(apportion2.tiedSeats));

		System.exit(0);

		
		System.out.println("THIS TEST IS DEPRECATED. USE TestMain INSTEAD!");

		smallExample();

//		final double v = new SelectAStarPrimitive(1, 1).unitSize(
//			  new double[]{79.0, 92.0, 47.0, 4.0, 74.0, 68.0, 2.0, 3.0, 12.0, 94.0}, 4);
//		System.out.println("1/v = " + 1/v);
//		new SandwichSelect(2,1).unitSize(new double[]{30.0, 150.0},4);
//		new SandwichSelect(2,1).unitSize(new double[]{20.0, 30.0, 150.0, 17.0, 3.0},4);

//		runAllOn(Instance.uniformRandomInstance(1000), 1, 1);
//		for (int i = 0; i < 10; ++i)
//			runLinearAlgsOn(Instance.exponentialRandomInstance(100000), 1, 1);


//		warmup();


		for (int i = 0; i < 5; ++i)
			runAllOn(ApportionmentInstanceFactory.randomInstance(SedgewickRandom.instance, ExponentialVotes, 30, new ApportionmentInstanceFactory.KFactory(5)), 1, 0);

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
				  instance = ApportionmentInstanceFactory.randomInstance(SedgewickRandom.instance, UniformVotes, 30, new ApportionmentInstanceFactory.KFactory(5,10));
      new SandwichSelectNaive(2, 1).apportion(instance);
      new SandwichSelectWithOptimalityCheck(2, 1).apportion(instance);
      new SandwichSelect(2, 1).apportion(instance);
      new ChengEppsteinSelect(2, 1).apportion(instance);
            new IterativeDMPQ(2, 1).apportion(instance);
            new IterativeDMLS(2, 1).apportion(instance);
            new PukelsheimPQ(2, 1).apportion(instance);
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
        new SandwichSelectNaive(alpha, beta),
        new SandwichSelectWithOptimalityCheck(alpha, beta),
        new ChengEppsteinSelect(alpha, beta),
        new SandwichSelect(alpha, beta),
        new IterativeDMPQ(alpha, beta),
			new IterativeDMPQ(alpha, beta),
			new PukelsheimPQ(alpha, beta));
		runAlgsOn(instance, algs);
	}

	private static void runLinearAlgsOn(ApportionmentInstance instance, double alpha, double beta)
		  throws Exception {
		List<LinearApportionmentMethod> algs = Arrays.asList(
        new SandwichSelectWithOptimalityCheck(alpha, beta),
        new ChengEppsteinSelect(alpha, beta),
        new SandwichSelect(alpha, beta),
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
                final Apportionment app = alg.apportion(instance);
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

            ApportionmentInstance example = new ApportionmentInstance(
                    new double[]{20.0, 30.0, 150.0, 17.0, 3.0}, k);

			System.out.println("\n\nk = " + k);

      LinearApportionmentMethod ce = new ChengEppsteinSelect(alpha, beta);
      LinearApportionmentMethod sel = new SandwichSelectWithOptimalityCheck(alpha, beta);
      LinearApportionmentMethod pri = new SandwichSelectNaive(alpha, beta);
      LinearApportionmentMethod puk = new PukelsheimPQ(alpha, beta);
            System.out.println("\tExample: " + sel.apportion(example) + "");
            System.out.println("\tExample: " + pri.apportion(example) + "");
            System.out.println("\tExample 1/unit: " + 1 / sel.apportion(example).astar + "");
            System.out.println("\tExample: " + ce.apportion(example) + "");
            System.out.println(
				  "sel.unitSize(example,k) == ce.unitSize(example,k) = " + (sel.apportion(
                          example) == ce.apportion(example)));
            allEqual &= pri.apportion(example) == ce.apportion(example);
            allEqual &= pri.apportion(example) == sel.apportion(example);
        }
		System.out.println("allEqual = " + allEqual);
	}

}
