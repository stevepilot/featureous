/*
 * Featureous is distributed under the GPLv3 license.
 *
 * University of Southern Denmark, 2011
 */
package dk.sdu.mmmi.srcUtils.testbench.interfaces;

public class FruitLover {
	
	public void drink(OrangeJuice j){
		j.checkColour();
		j.smell();
		j.drink();
	}
}
