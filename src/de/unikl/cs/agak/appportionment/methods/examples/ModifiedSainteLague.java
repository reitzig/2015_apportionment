package de.unikl.cs.agak.appportionment.methods.examples;

import de.unikl.cs.agak.appportionment.methods.AlmostLinearDivisorMethod;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class ModifiedSainteLague extends AlmostLinearDivisorMethod {
  @Override
  public double getAlpha() {
    return 2;
  }

  @Override
  public double getBetaUpper() {
    return 1.4;
  }

  @Override
  public double getBetaLower() {
    return 1;
  }

  @Override
  public double d(int j) {
    if ( j < 0 ) {
      throw new IllegalArgumentException("Got j=" + j);
      // The convention in the article was -infty for negative values,
      // but a single convention that can consistently be used in all cases seems
      // to be impossible, so check for negative values on call site.
      // Usually, this makes the difference between a party that has 0 seats
      // and one that has at least one seat, so often a case distinction is
      // needed anyway.
    }

    if ( j < 1 ) {
      return 1.6 * j + 1.4;
    }
    else {
      return 2*j + 1;
    }
  }

  @Override
  public double deltaInvRaw(double y) {
    if ( y >= 3.0 ) {
      return 0.5 * ( -1 + y );
    }
    else {
      return -0.625 * (1.4 - y);
    }
  }

  @Override
  public boolean isStationary() {
    return false;
    //TODO This is the correct answer in the strict sense, but shouldn't we answer "yes" as for SL?
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
