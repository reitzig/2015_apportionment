package de.unikl.cs.agak.appportionment.methods;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public abstract class AlmostLinearDivisorMethod extends DivisorMethod {
  public abstract double getAlpha();
  public abstract double getBetaUpper();
  public abstract double getBetaLower();
}
