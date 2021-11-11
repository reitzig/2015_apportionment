package de.unikl.cs.agak.appportionment.methods;

import de.unikl.cs.agak.appportionment.util.FuzzyNumerics;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public abstract class DivisorMethod {
 /**
   * Computes the continuation delta of the divisor sequences as per RW16.
   * For integer parameters, you get the entries from the divisor sequence
   * (hence the parameter name).
   * @param j Index in the divisor sequence, i.e. a non-negative number.
   * @return The j-th value in the this divisor sequence.
   */
  public abstract double d(int j);

  /**
   * Computes the inverse function of the canonical continuation of
   * {@link DivisorMethod#d(int)}
   *
   * @param y Some number
   * @return x s.t. d(x) = y
   */
  public abstract double deltaInvRaw(double y);

  /**
   * Similar to {@link #deltaInvRaw(double)}, but truncates the image to [-1, infty].
   *
   * @param x Some number
   * @return <code>max(-1, deltaInvRaw(x)</code>
   */
  public final double deltaInv(double x) {
    return Math.max(-1, deltaInvRaw(x));
  }

  /**
   * Rounds the given value according to the rounding method implied by the
   * divisor sequence d induced by this method (cf Puk14 p49)..
   *
   * @param x Number to d-round
   * @return The index <code>j</code> in the divisor sequence for which <code>d(j) <= x < d(j+1)</code>,
   *                that is an integer of at least -1.
   */
  public final int dRound(final double x) {
    return FuzzyNumerics.fuzzyFloor(deltaInv(x));
  }

  public abstract boolean isStationary();

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
