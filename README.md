# Linear-Time Algorithms for Proportional Apportionment

This repository contains implementations of algorithms for proportional 
apportionment from two separate articles. Inspect and use at your own risk.

In `AStarChengEppstein.java` we provide an implementation of the algorithm
proposed in

> Cheng, Z. and Eppstein, D.  
> [Linear-time Algorithms for Proportional Apportionment](http://link.springer.com/chapter/10.1007/978-3-319-13075-0_46).  
> In: International Symposium on Algorithms and Computation (ISAAC) 2014.  
> Springer (2014)  
> [[preprint](http://arxiv.org/abs/1409.2603) (v1,2014)]

Files `SelectAStar{Naive}?.java` contain implementations
of the algorithms we have presented in

> Reitzig, R. and Wild, S.  
> [A Simple and Fast Linear-Time Algorithm for Proportional Apportionment]()  
> [[preprint]() (v1,2015)]

The core algorithms start in the respective implementations of method `unitSize`.

Compile and execute `RunningTimeMain.java` for performing runtime tests.
You will need [`stdlib.jar`](http://introcs.cs.princeton.edu/stdlib/stdlib.jar)
from the [book website of Sedgewick/Wayne](http://algs4.cs.princeton.edu/code/).

The remaining files provide interfaces and utility code. 
Some files are taken or adapted from 
  [Sedgewick/Wayne](http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java.html)
with our thanks; we re-release their files in agreement with their 
license statement (see Q + A [here](http://algs4.cs.princeton.edu/code/)).
