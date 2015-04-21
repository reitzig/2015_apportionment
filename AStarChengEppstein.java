/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


import java.util.*;

/**
 * Implements the algorithm presented in
 * <p>
 *   Cheng, Z., Eppstein, D.<br/>
 *   Linear-time Algorithms for Proportional Apportionment. <br/>
 *   In: International Symposium on Algorithms and Computation (ISAAC) 2014. <br/>
 *   Springer (2014)
 * </p>
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class AstarChengEppstein extends LinearApportionment {
  public AstarChengEppstein(double alpha, double beta) {
    super(alpha, beta);
  }

  @Override
  double unitSize(double[] population, int k) {
    // Initialize sequences
    // TODO move to 𝒜, ξ? Seems to skrew with most IDEs, though.
    Collection<Sequence> A = new LinkedList<Sequence>();
    for ( double v_i : population ) {
      A.add(new Sequence(v_i));
    }
    
    Collection<Sequence> C = findContributingSequences(A, k);
    double coarse = sInv(C,k);
    
    if ( r(coarse, A) >= k ) {
      coarse = lowerRankCoarseSolution(A, k, coarse);
    }
    assert r(coarse, A) < k;
    
    return coarseToExact(A, k, coarse);
  }
  
  Collection<Sequence> findContributingSequences(Collection<Sequence> A, int k) {
    Collection<Sequence> C = new LinkedList<Sequence>();

    while ( A.size() > 0 ) {
      // Compute median as per line 3
      double[] x_As = new double[A.size()];
      int i = 0;
      for ( Sequence a : A ) {
        x_As[i++] = a.x_A();
      }
      double x = RankSelectionUtil.median(x_As);
      
      // Lines 4-9
      Collection<Sequence> Anew = new LinkedList<Sequence>();
      if ( s(x,A) > k ) {
        for ( Sequence a : A ) {
          if ( a.x_A() < x ) {
            Anew.add(a);
          }
        }
      }
      else {
        for ( Sequence a : A ) {
          if ( a.x_A() <= x ) {
            C.add(a);
          }
          else { // a.x_A > x
            Anew.add(a);
          }
        }
      }
      A = Anew;
    }    
    
    return C;
  }
  
  double lowerRankCoarseSolution(Collection<Sequence> A, int k, double coarse) {
    assert r(coarse, A) >= k;
    assert k > 0;
    
    double u = coarse;
    final int n = A.size(); // TODO yea?
    
    while ( true ) {
      // Line 3
      double[] L = L(u, A);
      double x = RankSelectionUtil.median(L);
      
      // Lines 4-12
      if ( r(x,A) >= k ) {
        u = x;
      }
      else if ( r(x,A) < k - n ) {
        // Prepare updates as per lines 7-9
        Collection<Sequence> Anew = new LinkedList<Sequence>();
        int sum = 0;
        for ( Sequence a : A ) {
          if ( a.L(u) <= x ) {
            // a in B
            sum += a.r(x);
          }
          else {
            Anew.add(a);
          }
        }
        
        // Updates
        k = k - sum;
        A = Anew;
      }
      else {
        assert r(x, A) < k;
        return x;
      }
    }
  }
  
  double coarseToExact(Collection<Sequence> A, int k, double coarse) {
    assert r(coarse, A) < k;
    assert k > 0;
    
    double l = coarse;
    double u = Double.POSITIVE_INFINITY;
    int    m = 0;
    Double t = null;
    
    double[] G = G(l, A);
    boolean singleValue;
    do {
      // Line 4
      double x = RankSelectionUtil.median(G);
      
      // Literal translation of lines 5-10
      if ( r(x,A) < k ) {
        l = x;
      }
      else {
        u = x;
        m = 0;
      }
      
      // Prepare updates
      Collection<Sequence> Anew = new LinkedList<Sequence>();
      int sum = 0;
      int countEqual = 0;
      boolean pickedEqual = false;
      for ( Sequence a : A ) { 
        if ( closeToEqual(a.G(l),u) ) {
          countEqual += 1; // NOT CLEAR; use A or Ã?
          if ( !pickedEqual ) {
            Anew.add(a);
            pickedEqual = true;
          }
          else {
            // a not in Anew
            sum += a.r(l);
          }
        }
        else if ( a.G(l) < u ) {
          Anew.add(a);
        }
        else {
          // a not in Anew
          sum += a.r(l);
        }
      }
      //assert pickedEqual; // NOT CLEAR; is this required?
	    // NO: NOT true, as l might have changed in this round!
      
      // Updates as per lines 11-16
      if ( countEqual >= 1 ) {
        m += countEqual - 1;
      }
      A = Anew;
      k = k - sum;
      
      // Prepare loop condition
      singleValue = true;
      G = G(l, A);
      Double check = null;
      for ( Double g : G ) {
        if ( check == null ) {
          check = g;
        }
        else {
          if ( !closeToEqual(g, check) ) {
            singleValue = false;
            break;
          }
        }
      }
      if ( singleValue ) {
        t = check;
      }
        
      assert !singleValue || t != null;
    } while ( !( singleValue && ( r(t,A) >= k || (closeToEqual(t,u) && r(t,A) >= k-m) ) ) );
    return t;
  }
  
  /**
   * Computes s as per equation (2)
   */
  double s(double x, Collection<Sequence> A) {
    double sum  = 0;
    
    for ( Sequence a : A ) {
      if ( x >= a.x_A() ) {
        sum += (x - a.x_A())/a.y_A();
      }
    }
    
    return sum;
  }
  
  /**
   * Computes s^(-1) as per equation (3)
   */
  double sInv(Collection<Sequence> C, int k) {
    double ySum  = 0;
    double xySum = 0;
    
    for ( Sequence a : C ) {
      ySum  += 1/a.y_A();
      xySum += a.x_A()/a.y_A();
    }
    
    return 1/ySum * (k + xySum);
  }

	/**
   * Lifts Sequence.L to sets of sequences
   */
  double[] L(double u, Collection<Sequence> A) {
	  double[] res = new double[A.size()];
	  int i = -1;
	  for (Sequence a : A) {
		  res[++i] = a.L(u);
	  }
	  return res;
  }

	/**
   * Lifts Sequence.G to sets of sequences
   */
  double[] G(double u, Collection<Sequence> A) {
	  double[] res = new double[A.size()];
	  int i = -1;
	  for (Sequence a : A) {
		  res[++i] = a.G(u);
	  }
	  return res;
  }

  /**
   * Lifts Sequence.r to sets of sequences
   */
  int r(double x, Collection<Sequence> A) {
    int r = 0;
    for ( Sequence A_i : A ) {
      r += A_i.r(x);
    }
    return r;
  }
 
  
  /**
   * Wraps a sequence of d_j/v_i for one i
   */
  private class Sequence {
    private double v_i; // Population
    
    Sequence(double v_i) {
      this.v_i = v_i;
    }
    
    // Stuff from paper; more or less verbatim. Hopefully equivalent.
    
    double L(double u) {
	    if (u <= jth(0)) return Double.NEGATIVE_INFINITY;
	    int j = r(u) - 1;
	    return jth(j) < u ? jth(j) : jth(j-1);
    }

    double G(double l) {
      int j = r(l) - 1;
	    if (j == -1) return jth(0);
      return jth(j) > l ? jth(j) : jth(j+1);
    }

    int r(double x) {
      if ( x >= x_A() ) {
	      return 1 + fuzzyFloor( (x - x_A()) / y_A() );
      }
      else {
        return 0;
      }
    }
    
    double x_A() {
      return d(0)/v_i;
    }
    
    double y_A() {
      return alpha/v_i;
    }
    
    double jth(int j) {
      return d(j)/v_i;
    }
  }
}
