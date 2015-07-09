# Linear-Time Algorithms for Proportional Apportionment

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

If you can use Ant, execute `ant test` for basic correctness testing.
Command `ant run` executes a comparative runtime experiment.

Compile and execute `RunningTimeMain.java` for performing runtime tests.
You will need [`stdlib.jar`](http://introcs.cs.princeton.edu/stdlib/stdlib.jar)
from the [book website of Sedgewick/Wayne](http://algs4.cs.princeton.edu/code/).

The remaining files provide interfaces and utility code. 
Some files are taken or adapted from 
  [Sedgewick/Wayne](http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java.html)
with our thanks; we re-release their files in agreement with their 
license statement (see Q + A [here](http://algs4.cs.princeton.edu/code/)).
