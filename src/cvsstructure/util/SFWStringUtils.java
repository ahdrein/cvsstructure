package cvsstructure.util;

/**
 *
 * @author andrein
 */
public class SFWStringUtils {
    public static String lpad(String valueToPad, String filler, int size) {
         StringBuffer strValueToPad = new StringBuffer();
         while (valueToPad.length() < size) {
             strValueToPad.append(filler);
             strValueToPad.append(valueToPad);
         }
         return strValueToPad.toString();
     }

     public static String rpad(String valueToPad, String filler, int size) {
         StringBuffer strValueToPad = new StringBuffer();
         while (valueToPad.length() < size) {
             strValueToPad.append(valueToPad);
             strValueToPad.append(filler);
         }
         return strValueToPad.toString();
     }

}

