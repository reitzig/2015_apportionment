## Linear-Time Algorithms for Proportional Apportionment

This repository contains implementations of algorithms for proportional 
apportionment with divisor methods. Inspect and use at your own risk.

 * File [`SelectAStar.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/SelectAStar.java)
  contains an implementation of the algorithm we have presented in

  > Wild, S. and Reitzig, R.  
  > A Simple and Fast Linear-Time Algorithm for Proportional Apportionment  
  > [[preprint](http://arxiv.org/abs/1504.06475) (v1,2015)]

 * In [`AStarChengEppstein.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/AStarChengEppstein.java)
  we provide an implementation of the algorithm proposed in

  > Cheng, Z. and Eppstein, D.  
  > [Linear-time Algorithms for Proportional  Apportionment](http://link.springer.com/chapter/10.1007/978-3-319-13075-0_46)  
  > In: International Symposium on Algorithms and Computation (ISAAC) 2014.  
  > Springer (2014)  
  > [[preprint](http://arxiv.org/abs/1409.2603) (v1,2014)]

 * Furthermore, we implement the jump-and-step algorithm from

  > Pukelsheim, F.  
  > Proportional Representation  
  > Springer, 2014

  in [`PukelsheimPQ.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/PukelsheimPQ.java) with priority queues for determining the next party to modify, and in [`PukelsheimLS.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/PukelsheimLS.java) using linear scan.

 * Finally, we give implementations of the naive algorithm using
  priority queues resp. a linear scan for finding maxima in
  [`IterativeDMPQ.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/IterativeDMPQ.java)
  and [`IterativeDMLS.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/de/unikl/cs/agak/appportionment/methods/IterativeDMLS.java),
  respectively.

The core algorithms start in the respective implementations of method `unitSize` resp. `apportion`.

The remaining files provide interfaces and utility code. 
Some files are taken or adapted from 
  [Sedgewick/Wayne](http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java.html)
with our thanks; we re-release their files in agreement with their 
license statement (see Q + A [here](http://algs4.cs.princeton.edu/code/)).

### Compilation

Execute `ant compile`; you will need [`stdlib-package.jar`](http://algs4.cs.princeton.edu/code/stdlib-package.jar)
(in folder `lib`) from the [book website of Sedgewick/Wayne](http://algs4.cs.princeton.edu/code/).

### Usage

Run `ant test` for testing correctness of the implementations.
Besides rudimentary sanity checks, the test basically check Pukelsheim's 
min-max-inequality for *all* computed assignments, 
i.e. for all ways to resolve ties.

Command `ant run` executes a sample runtime experiment.

Run your own experiments by defining the parameters in a space-separated file
(see e.g. [arxiv.experiment](https://github.com/reitzig/2015_apportionment/blob/master/arxiv.experiment); those are the ones from the article)
and passing it as parameter to [`run_experiments.rb`](https://github.com/reitzig/2015_apportionment/blob/master/run_experiments.rb).
