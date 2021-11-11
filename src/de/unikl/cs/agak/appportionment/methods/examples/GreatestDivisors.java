package de.unikl.cs.agak.appportionment.methods.examples;

import de.unikl.cs.agak.appportionment.methods.LinearDivisorMethod;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class GreatestDivisors extends LinearDivisorMethod {
  public GreatestDivisors() {
    super(1, 1);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
