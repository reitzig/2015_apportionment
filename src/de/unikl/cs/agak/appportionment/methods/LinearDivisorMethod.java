package de.unikl.cs.agak.appportionment.methods;

/**
 *  A divisor method whose divisor sequence is linear, i.e.
 * conforms to <code>a*j + b</code> for some constants
 * <code>a</code> and <code>b</code>.
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>, 2016
 */
public class LinearDivisorMethod extends AlmostLinearDivisorMethod {
  private final double alpha;
  private final double beta;

  public LinearDivisorMethod(double alpha, double beta) {
    assert alpha >= 0 && beta >= 0 : "Illegal method parameters alpha=" + alpha + ", beta=" + beta;
    this.alpha = alpha;
    this.beta = beta;
  }

  /**
     * @param j Index in the divisor sequence, i.e. a non-negative number.
     * @return The value <code>d(j) = alpha * j + beta</code> for <code>j >= 0</code>,
     *               negative infinity otherwise.
     * @throws IllegalArgumentException if j < 0.
     */
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

    return alpha * j + beta;
  }

  /**
     * Computes the inverse function of the canonical continuation of
     * {@link DivisorMethod#d(int)}
     *
     * @param y Some number
     * @return <code>(x - beta) / alpha</code>
     */
  @Override
  public double deltaInvRaw(double y) {
    return (y - beta) / alpha;
  }

  public boolean isStationary() {
    return 0 <= beta / alpha && beta / alpha <= 1;
  }

  @Override
  public double getAlpha() {
    return alpha;
  }

  @Override
  public double getBetaUpper() {
    return beta;
  }

  @Override
  public double getBetaLower() {
    return beta;
  }

  public double getBeta() {
    return beta;
  }

  @Override
  public String toString() {
    return "LDM(" + alpha + "," + beta + ")";
  }
}
