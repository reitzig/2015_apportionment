package de.unikl.cs.agak.appportionment.methods.examples;

import de.unikl.cs.agak.appportionment.methods.LinearDivisorMethod;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class SainteLague extends LinearDivisorMethod {
  public SainteLague() {
    super(2,1);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
