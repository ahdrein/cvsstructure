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
 * @author andrein
 */
public class ArquivosExternos {
    private PreparedStatement psGerarArquivosExternos = null;
    private ResultSet rsArquivosExternos = null;

    private PreparedStatement psPermissaoTabela = null;
    private ResultSet rsPermissaoTabela = null;

    StringBuilder sbArquivosExternos = null;
    StringBuilder sbExportarArquivosExternos = null;

    private static ArquivosExternos instance;

    static {
            instance = new ArquivosExternos();
    }

    private ArquivosExternos(){
    }

    public static ArquivosExternos getInstance(){
            return instance;
    }

    public ResultSet getArquivosExternosByNomeArquivo(String nomeArquivo) throws SQLException {

        if(sbArquivosExternos == null){
            sbArquivosExternos = new StringBuilder();
            sbArquivosExternos.append("select NOME_ARQUIVO,");
            sbArquivosExternos.append("PATH_RELATIVO,");
            sbArquivosExternos.append("DESCRICAO,");
            sbArquivosExternos.append("CONTEUDO");
            sbArquivosExternos.append(" from arquivo_externo");
            sbArquivosExternos.append(" where arquivo_externo.nome_arquivo like '%'|| ? || '%'");
        }

        if(psGerarArquivosExternos == null){
            psGerarArquivosExternos = ConnectionInout.getConnection().prepareStatement(sbArquivosExternos.toString());
        }

        psGerarArquivosExternos.setString(1, nomeArquivo);

        return psGerarArquivosExternos.executeQuery();
    }



    public ResultSet getPermissaoTabelaByIdInterface(String idInterface) throws SQLException {

        if(psPermissaoTabela == null){
            psPermissaoTabela = ConnectionInout.getConnection().prepareStatement("select id_interface, table_name from permissao_tabela where lower(table_name) != 'tmp_cvs_structure' and id_interface = ?");
        }

        psPermissaoTabela.setString(1, idInterface);
        rsPermissaoTabela = psPermissaoTabela.executeQuery();

        return rsPermissaoTabela;
    }

    public PreparedStatement getPermissaoTabelaByIdInterface() throws SQLException {

        if(psPermissaoTabela == null){
            psPermissaoTabela = ConnectionInout.getConnection().prepareStatement("select id_interface, table_name from permissao_tabela where lower(table_name) != 'tmp_cvs_structure' and id_interface = ?");
        }

        return psPermissaoTabela;
    }
    private PreparedStatement psExportarArquivosExternos = null;

    public PreparedStatement getExportarArquivosExternosByNomeArquivo() throws SQLException {
        if(sbExportarArquivosExternos != null){
            sbExportarArquivosExternos = new StringBuilder();
            sbExportarArquivosExternos.append("select NOME_ARQUIVO,");
            sbExportarArquivosExternos.append("PATH_RELATIVO,");
            sbExportarArquivosExternos.append("DESCRICAO,");
            sbExportarArquivosExternos.append("CONTEUDO");
            sbExportarArquivosExternos.append(" from arquivo_externo");
            sbExportarArquivosExternos.append(" where arquivo_externo.nome_arquivo like '%'|| ? || '%'");
        }

        if(psExportarArquivosExternos != null){
            psExportarArquivosExternos = ConnectionInout.getConnection().prepareStatement(sbExportarArquivosExternos.toString());
        }

        return psExportarArquivosExternos;
    }

    public void dropTableTmpCvsStructure() throws SQLException{
        ConnectionInout.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE").executeQuery();
    }

}
