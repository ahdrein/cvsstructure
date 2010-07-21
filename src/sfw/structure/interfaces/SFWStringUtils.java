/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.interfaces;

/**
 *
 * @author andrein
 */
public class SFWStringUtils {
	public static String lpad(String valueToPad, String filler, int size) {
         while (valueToPad.length() < size) {
             valueToPad = filler + valueToPad;
         }
         return valueToPad;
     }

     public static String rpad(String valueToPad, String filler, int size) {
         while (valueToPad.length() < size) {
             valueToPad = valueToPad+filler;
         }
         return valueToPad;
     }

}

