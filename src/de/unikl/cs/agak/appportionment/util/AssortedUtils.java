package de.unikl.cs.agak.appportionment.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static de.unikl.cs.agak.appportionment.util.AssortedUtils.sum;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class AssortedUtils {
    public static boolean isBinary(int[] arr) {
        for ( int i : arr )
            if ( i < 0 || i > 1 ) return false;
        return true;
    }



    /**
     * Returns the sum of all values in the specified array.
     *
     * @param  a the array
     * @return the sum of all values in the array {@code a[]};
     *         {@code 0.0} if no such value
     */
    public static double sum(double[] a) {
        validateNotNull(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }


    /**
     * Returns the sum of all values in the specified array.
     *
     * @param  a the array
     * @return the sum of all values in the array {@code a[]};
     *         {@code 0.0} if no such value
     */
    public static int sum(int[] a) {
        validateNotNull(a);
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }


    // throw an IllegalArgumentException if x is null
    // (x is either of type double[] or int[])
    private static void validateNotNull(Object x) {
        if (x == null)
            throw new IllegalArgumentException("argument is null");
    }


    /**
     * Computes all possibilities to pick <code>i</code> ones from the given 0-1 vector <code>v</code>.
     *
     * <strong>Warning:</strong> May destroy <code>v</code>!
     * @param v An binary array that contains only 0 and 1
     * @param k A non-negative number
     * @return The set of all possibilities.
     */
    public static Collection<int[]> kSubsets(int[] v, int k) {
        assert v != null && isBinary(v);

        Collection<int[]> result = new LinkedList<>();
        if ( k <= 0 ) {
            // return only the zero-vector
            result.add(new int[v.length]);
        }
        else if ( sum(v) < k ) {
            // not enough ones --> return empty set
        }
        else { // k > 0 && sum(v) >= k
            // Find first 1
            int j;
            for ( j = 0; j < v.length; j++) {
               if ( v[j] == 1 ) break;
            }
            assert j < v.length && v[j] == 1;

            // Recurse for picking this vs not picking this 1
            v[j] = 0;
            int[] v1 = Arrays.copyOf(v, v.length);

            // For picking this 1
            Collection<int[]> res1 = kSubsets(v1, k-1);
            for ( int[] a : res1 ) {
                a[j] = 1;
                result.add(a);
            }
            // For not picking this 1
            v1 = Arrays.copyOf(v, v.length);
            result.addAll(kSubsets(v1, k));
        }

        return result;
    }
}
