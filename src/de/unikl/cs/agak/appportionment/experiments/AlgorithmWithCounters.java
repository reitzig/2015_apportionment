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

package de.unikl.cs.agak.appportionment.experiments;

/**
 * <p>Some algorithms have combinatoric properties worth investigating.
 * In order to not overburden or water down the functional interfaces,
 * we use this <strong>unsafe hack</strong> for getting counters.</p>
 *
 * <p>Method {@link AlgorithmWithCounters#getLastCounter(int)} return the value stored by the last
 * run of the algorithm. This is <em>not</em> assumed to be thread-safe,
 * so you better use each instance in a sequential fashion.</p>
 *
 * @author Raphael Reitzig (reitzig@cs.uni-kl.de)
 */
public interface AlgorithmWithCounters {
  /**
   * @return The number of counters.
   */
  int numberOfCounters();

    /**
     * <strong>Warning:</strong> this is not thread-safe!
     * @param i Counter index, starting with 0.
     * @return The value stored by the i-th counter by the last run of the algorithm.
     *    The result is not specified if <code>i < 0</code> or <code>i >= numberOfCounters()</code>.
     */
    int getLastCounter(int i);

    /**
     * @param i Counter index, starting with 0.
     * @return A name for the i-th counter value.
     *    The result is not specified if <code>i < 0</code> or <code>i >= numberOfCounters()</code>.
     */
    String getCounterLabel(int i);
}
