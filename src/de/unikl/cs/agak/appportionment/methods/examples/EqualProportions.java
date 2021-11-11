package de.unikl.cs.agak.appportionment.methods.examples;

import de.unikl.cs.agak.appportionment.methods.AlmostLinearDivisorMethod;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class EqualProportions extends AlmostLinearDivisorMethod {
  @Override
  public double getAlpha() {
    return 1;
  }

  @Override
  public double getBetaUpper() {
    return 0.5;
  }

  @Override
  public double getBetaLower() {
    return 0;
  }

  @Override
  public double d(int j) {
    return Math.sqrt(j*(j+1));
  }

  @Override
  public double deltaInvRaw(double y) {
    return 0.5 * (-1 + Math.sqrt(1 + 4* y * y));
  }

  @Override
  public boolean isStationary() {
    return false;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
