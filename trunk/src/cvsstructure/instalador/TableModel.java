/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.instalador;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author andrein
 */
public class TableModel extends JTable {
    private String[][] dados = null;
    private String[] colunas;
    private DefaultTableModel modelo;

    public TableModel(){
        dados = new String [][]{
            {"",""}
        };

        colunas = new String []{"Objeto Acessado", "Caminho"};

        // Ao inves de passar direto, colocamos os dados em um modelo
        modelo = new DefaultTableModel(getColunas(), 0);

        // e passamos o modelo para criar a jtable
        this.setModel( modelo );
    }

    /**
     * @return the dados
     */
    public String[][] getDados() {
        return dados;
    }

    /**
     * @param dados the dados to set
     */
    public void setDados(String[][] dados) {
        this.dados = dados;
    }

    /**
     * @return the colunas
     */
    public String[] getColunas() {
        return colunas;
    }

    /**
     * @param colunas the colunas to set
     */
    public void setColunas(String[] colunas) {
        this.colunas = colunas;
    }

    /**
     * @return the modelo
     */
    public DefaultTableModel getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(DefaultTableModel modelo) {
        this.modelo = modelo;
    }
}
