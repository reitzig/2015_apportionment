package de.unikl.cs.agak.appportionment.methods.examples;

import de.unikl.cs.agak.appportionment.methods.LinearDivisorMethod;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class SmallestDivisors extends LinearDivisorMethod {
  public SmallestDivisors() {
    super(1, 0);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
