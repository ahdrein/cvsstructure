/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.model;

/**
 *
 * @author andrein
 */
public class ObjetosIntegracao {

    private static ObjetosIntegracao instance;

    static {
            instance = new ObjetosIntegracao();
    }

    private ObjetosIntegracao(){
    }

    public static ObjetosIntegracao getInstance(){
            return instance;
    }
}
