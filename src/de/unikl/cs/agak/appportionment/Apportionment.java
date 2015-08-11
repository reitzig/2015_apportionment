
package de.unikl.cs.agak.appportionment;

import java.util.Arrays;

/**
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public class Apportionment {
    public final double astar; // TODO find a good name; Pukelsheim: proportionality constant
    public final int[] seats;

    public Apportionment(int[] seats, double astar) {
        this.astar = astar;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Apportionment(" + "seats=" + Arrays.toString(seats) + ", astar=" + astar + ')';
    }
}
