## Linear-Time Algorithms for Proportional Apportionment

This repository contains implementations of algorithms for proportional 
apportionment from two separate articles. Inspect and use at your own risk.

In [`AStarChengEppstein.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/AStarChengEppstein.java) we provide an implementation of the algorithm
proposed in

> Cheng, Z. and Eppstein, D.  
> [Linear-time Algorithms for Proportional Apportionment](http://link.springer.com/chapter/10.1007/978-3-319-13075-0_46)  
> In: International Symposium on Algorithms and Computation (ISAAC) 2014.  
> Springer (2014)  
> [[preprint](http://arxiv.org/abs/1409.2603) (v1,2014)]

Files [`SelectAStar.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/SelectAStar.java) contains an implementation of the algorithm we 
have presented in

> Wild, S. and Reitzig, R.  
> A Simple and Fast Linear-Time Algorithm for Proportional Apportionment  
> [[preprint](http://arxiv.org/abs/1504.06475) (v1,2015)]

Finally, we give implementations of the method-defining algorithm using
priority queues resp. a linear scan for finding maxima in
[`HighestAveragesPQ.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/HighestAveragesPQ.java) and [`HighestAveragesLS.java`](https://github.com/reitzig/2015_apportionment/blob/master/src/HighestAveragesLS.java), respectively.

The core algorithms start in the respective implementations of method `unitSize`.

The remaining files provide interfaces and utility code. 
Some files are taken or adapted from 
  [Sedgewick/Wayne](http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java.html)
with our thanks; we re-release their files in agreement with their 
license statement (see Q + A [here](http://algs4.cs.princeton.edu/code/)).

### Compilation

Execute `ant compile`; you will need [`stdlib.jar`](http://introcs.cs.princeton.edu/stdlib/stdlib.jar)
(in folder `lib`) from the [book website of Sedgewick/Wayne](http://algs4.cs.princeton.edu/code/).

### Usage

Run `ant test` for basic correctness testing.
Command `ant run` executes a sample runtime experiment.

Run your own experiments by defining the parameters in a space-separated file
(e.g. [arxiv.experiment](https://github.com/reitzig/2015_apportionment/blob/master/arxiv.experiment); those are the one from the article) and passing it as parameter
to [`run_experiments.rb`](https://github.com/reitzig/2015_apportionment/blob/master/run_experiments.rb).
