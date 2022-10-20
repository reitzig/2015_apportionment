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

package de.unikl.cs.agak.appportionment.algorithms;

import de.unikl.cs.agak.appportionment.Apportionment;
import de.unikl.cs.agak.appportionment.ApportionmentInstance;
import de.unikl.cs.agak.appportionment.methods.DivisorMethod;
import de.unikl.cs.agak.appportionment.util.FuzzyNumerics;

/**
 * An algorithm that solves the apportionment problem using a divisor method.
 */
public interface ApportionmentAlgorithm {
	/**
     * Finds the apportionment for the given parameters.
     * @param instance An instance of the apportionment problem, consisting of
     *                 votes and house size.
     * @return The apportionment for the given instance, representing all
     *          seats assignment that are valid w.r.t. this divisor method.
     */
  Apportionment apportion(ApportionmentInstance instance, DivisorMethod method);

}
