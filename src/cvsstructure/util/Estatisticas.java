/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.util;

/**
 *
 * @author ahdrein
 */
public class Estatisticas {

    public static int nTotalSistemas = 0;
    public static int nTotalTabelas = 0;
    public static int nTotalInterfaces = 0;
    public static int nTotalViews = 0;
    public static int nTotalSynonyms = 0;
    public static int nTotalSequences = 0;
    public static int nTotalPackages = 0;
    public static int nTotalSapMapeamento = 0;
    public static int nTotalIntMapeamento = 0;
    public static int nTotalFunctionsProcedures = 0;
    public static int nTotalTabelasTemporarias = 0;

    public static void zeraEstatisticas(){
        Estatisticas.nTotalInterfaces = 0;
        Estatisticas.nTotalTabelas = 0;
        Estatisticas.nTotalPackages = 0;
        Estatisticas.nTotalSequences = 0;
        Estatisticas.nTotalSynonyms = 0;
        Estatisticas.nTotalViews = 0;
        Estatisticas.nTotalSapMapeamento = 0;
        Estatisticas.nTotalIntMapeamento = 0;
        Estatisticas.nTotalFunctionsProcedures = 0;
        Estatisticas.nTotalSistemas = 0;
        Estatisticas.nTotalTabelasTemporarias = 0;
    }

}
