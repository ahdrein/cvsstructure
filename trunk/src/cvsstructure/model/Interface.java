/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.model;

import cvsstructure.database.ConnectionInout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ahdrein
 */
public class Interface {
    private String idInterface;
    private String executavel = "";
    private String tipoInterface = "";
    private String idSistema = "";
    private String descricao = "";
    private String interfereProcessamentoDireto = null;
    private String tempoMedio = "";
    private String executavelCompl = "";

    StringBuilder sbCountInterface = null;
    StringBuilder sbCountSistemaPorInterface = null;

    private PreparedStatement psCountInterface = null;
    private PreparedStatement psCountSistemaPorInterface = null;

    private ResultSet rsCountInterface = null;

    private static Interface instance;

    static {
            instance = new Interface();
    }

    public static Interface getInstance(){
            return instance;
    }

    public Interface(){

    }

    public Interface(String idInterface){
        this.idInterface = idInterface;
    }

    /*
     * Obtem o IDInterface
     */
    public String getIdInterface() {
        return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    /*
     * Obtem o IDSistema
     */
    public String getIdSistema() {
        return idSistema.trim().toLowerCase();
    }

    /**
     * @param idInterface the idInterface to set
     */
    public void setIdInterface(String idInterface) {
        this.idInterface = idInterface;
    }

    /**
     * @return the executavel
     */
    public String getExecutavel() {
        return executavel;
    }

    /**
     * @param executavel the executavel to set
     */
    public void setExecutavel(String executavel) {
        this.executavel = executavel;
    }

    /**
     * @return the tipoInterface
     */
    public String getTipoInterface() {
        return tipoInterface;
    }

    /**
     * @param tipoInterface the tipoInterface to set
     */
    public void setTipoInterface(String tipoInterface) {
        this.tipoInterface = tipoInterface;
    }

    /**
     * @param idSistema the idSistema to set
     */
    public void setIdSistema(String idSistema) {
        this.idSistema = idSistema;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the interfereProcessamentoDireto
     */
    public String getInterfereProcessamentoDireto() {
        return interfereProcessamentoDireto;
    }

    /**
     * @param interfereProcessamentoDireto the interfereProcessamentoDireto to set
     */
    public void setInterfereProcessamentoDireto(String interfereProcessamentoDireto) {
        this.interfereProcessamentoDireto = interfereProcessamentoDireto;
    }

    /**
     * @return the tempoMedio
     */
    public String getTempoMedio() {
        return tempoMedio;
    }

    /**
     * @param tempoMedio the tempoMedio to set
     */
    public void setTempoMedio(String tempoMedio) {
        this.tempoMedio = tempoMedio;
    }

    /**
     * @return the executavelCompl
     */
    public String getExecutavelCompl() {
        return executavelCompl;
    }

    /**
     * @param executavelCompl the executavelCompl to set
     */
    public void setExecutavelCompl(String executavelCompl) {
        this.executavelCompl = executavelCompl;
    }

    /**
     * @return the psCountInterface
     */
    public ResultSet getCountInterfaceByExecutavel() throws SQLException {
        if(sbCountInterface == null){
            sbCountInterface = new StringBuilder();
            sbCountInterface.append("select count(*) total_arquivos");
            sbCountInterface.append(" from (select replace(replace(replace(substr(interfaces.executavel,");
            sbCountInterface.append(" instr(interfaces.executavel, '\\') + 1,");
            sbCountInterface.append("length(interfaces.executavel)),");
            sbCountInterface.append("'#IDENT#INTERFACES\\SAP\\',");
            sbCountInterface.append("''),");
            sbCountInterface.append("'#IDENT#INTERFACES\\',");
            sbCountInterface.append("''),");
            sbCountInterface.append("'#IDENT#INTEGRACAO\\',");
            sbCountInterface.append("'') executavel");
            sbCountInterface.append(" from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces)");
            sbCountInterface.append(" where executavel like '%'||?||'%'");
        }
        
        if(psCountInterface == null){
            psCountInterface = ConnectionInout.getConnection().prepareStatement(sbCountInterface.toString());
        }

        psCountInterface.setString(1, this.executavel);
        rsCountInterface = psCountInterface.executeQuery();
        rsCountInterface.next();

        return rsCountInterface;
    }

    public ResultSet getCountSistemaPorInterface(String idInterface) throws SQLException {

        if(sbCountSistemaPorInterface == null){
            sbCountSistemaPorInterface = new StringBuilder();
            sbCountSistemaPorInterface.append("select count(*) TOTAL");
            sbCountSistemaPorInterface.append(" from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,");
            sbCountSistemaPorInterface.append(" sistema_interface");
            sbCountSistemaPorInterface.append(" where interfaces.id_interface = sistema_interface.id_interface (+)");
            sbCountSistemaPorInterface.append(" and interfaces.id_interface like '%'||?||'%'");
        }

        if(psCountSistemaPorInterface == null){
            psCountSistemaPorInterface = ConnectionInout.getConnection().prepareStatement(sbCountSistemaPorInterface.toString());
        }

        psCountSistemaPorInterface.setString(1, idInterface);
        rsCountInterface = psCountSistemaPorInterface.executeQuery();
        rsCountInterface.next();

        return rsCountInterface;
    }

}
