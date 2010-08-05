/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import cvsstructure.database.ConnectionIntegracao;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.Arquivo;
import cvsstructure.util.Diretorio;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ahdrein
 */
public class ObjetosIntegracao {
    Cliente cliente;

    private void GerarObjetosIntegracao(Cliente cliente){
        this.cliente = cliente;
    }

    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    private void GerarObjetosIntegracao() throws Exception {
        Arquivo fileScripts;
        StringBuffer strOutScripts;
        String fileName;
        String fileNameScripts;

        PreparedStatement psFoundObjectsIT = null;
        ResultSet rsFoundObjectsIT = null;

        PreparedStatement psInsertReferencesObjectsIT = null;
        PreparedStatement psCreateTableIT = null;

        if (ConnectionIntegracao.getConnection() != null) {
            StringBuffer sbAllObjectsSystem = new StringBuffer();
            sbAllObjectsSystem.append("select object_name, OBJECT_TYPE tipo from user_objects a ");
            sbAllObjectsSystem.append("where ");
            sbAllObjectsSystem.append("object_type in ('PROCEDURE','FUNCTION','PACKAGE','TABLE')");
            sbAllObjectsSystem.append("and object_name not in ");
            sbAllObjectsSystem.append("( 'SET_CONTEUDO_ARQ' ,");
            sbAllObjectsSystem.append("'PROC_CTL_FIXO' ,");
            sbAllObjectsSystem.append("'PROC_CONTEUDO_ARQ' ,");
            sbAllObjectsSystem.append("'PROCESSA_INTERFACES_IN' ,");
            sbAllObjectsSystem.append("'PROCESSA_ID_SISTEMA' ,");
            sbAllObjectsSystem.append("'PROCESSA_GRANTS_SISTEMA' ,");
            sbAllObjectsSystem.append("'PROCESSA_ERRO_INTERFACE' ,");
            sbAllObjectsSystem.append("'PRC_UPDATE_CONCATENA_LONG' ,");
            sbAllObjectsSystem.append("'PRC_SINCRONIZA_TABELA' ,");
            sbAllObjectsSystem.append("'PRC_PROCESSA_DIRETO' ,");
            sbAllObjectsSystem.append("'PRC_POPULA_SAP_COLUMNS' ,");
            sbAllObjectsSystem.append("'PRC_INSERE_LOG_GERAL' ,");
            sbAllObjectsSystem.append("'PRC_INDEXA_HEADERS' ,");
            sbAllObjectsSystem.append("'NOTIFICA_RESUMO_INTERFACE' ,");
            sbAllObjectsSystem.append("'INTERFACE_EXCLUI_REGISTROS' ,");
            sbAllObjectsSystem.append("'INSERE_NOTIFICACAO_TABELA' ,");
            sbAllObjectsSystem.append("'INSERE_INFO_ODBC' ,");
            sbAllObjectsSystem.append("'INSERE_HISTORICO_SAIDA_ID_IMP' ,");
            sbAllObjectsSystem.append("'INSERE_HISTORICO_SAIDA' ,");
            sbAllObjectsSystem.append("'INSERE_ERRO_IMPORTACAO' ,");
            sbAllObjectsSystem.append("'INFORMA_SUCESSO_ERRO' ,");
            sbAllObjectsSystem.append("'INFORMA_QTDE_REGISTROS_IMPORT' ,");
            sbAllObjectsSystem.append("'INFORMA_OBS_IMPORTACAO' ,");
            sbAllObjectsSystem.append("'INFORMA_OBS_ID_IMPORTACAO' ,");
            sbAllObjectsSystem.append("'GET_CONTEUDO_ARQ' ,");
            sbAllObjectsSystem.append("'FINALIZA_INTERFACE' ,");
            sbAllObjectsSystem.append("'EXCLUI_ERRO_IMPORTACAO' ,");
            sbAllObjectsSystem.append("'ELIMINA_REGISTROS_ANTIGOS' ,");
            sbAllObjectsSystem.append("'ELIMINA_INTERFACE' ,");
            sbAllObjectsSystem.append("'CONCATENA_CONTEUDO' ,");
            sbAllObjectsSystem.append("'COMPILE_INVALID' ,");
            sbAllObjectsSystem.append("'ATUALIZA_IMPORTACAO_EXECUTADA',");
            sbAllObjectsSystem.append("'INT_MAPEAMENTO_LAYOUT',");
            sbAllObjectsSystem.append("'INT_MAPEAMENTO_COLUNA',");
            sbAllObjectsSystem.append("'PKG_IT_GEN',");
            sbAllObjectsSystem.append("'PKG_INT_IT_SFW',");
            sbAllObjectsSystem.append("'SFWXMLCONCAT',");
            sbAllObjectsSystem.append("'SFWXMLELEMENT',");
            sbAllObjectsSystem.append("'SFWXMLFOREST',");
            sbAllObjectsSystem.append("'TMP_CVS_STRUCTURE'");
            sbAllObjectsSystem.append(")");

            psFoundObjectsIT = ConnectionIntegracao.getConnection().prepareStatement(sbAllObjectsSystem.toString());

            StringBuffer sbCreateTableCvsStructure = new StringBuffer();
            sbCreateTableCvsStructure.append("create table TMP_CVS_STRUCTURE");
            sbCreateTableCvsStructure.append("(");
            //sbCreateTableCvsStructure.append(" LEVEL_OBJ        number,");
            sbCreateTableCvsStructure.append(" NAME             varchar2(100),");
            sbCreateTableCvsStructure.append(" TYPE             varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_OWNER varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_NAME  varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_TYPE  varchar2(100)");
            sbCreateTableCvsStructure.append(")");

        //psCreateTable = ConnectionInout.getConnection().prepareCall(sbCreateTableCvsStructure.toString());

        //psInsertReferencesObjects = ConnectionInout.getConnection().prepareCall(sbInsertTableCvsStructure.toString());
            psCreateTableIT = ConnectionIntegracao.getConnection().prepareCall(sbCreateTableCvsStructure.toString());

            StringBuffer sbInsertTableCvsStructure = new StringBuffer();
            //sbInsertTableCvsStructure.append("insert into TMP_CVS_STRUCTURE (");
            //sbInsertTableCvsStructure.append("select *");
            //sbInsertTableCvsStructure.append("  from (select distinct LEVEL,");
            //sbInsertTableCvsStructure.append("                        NAME,");
            //sbInsertTableCvsStructure.append("                        TYPE,");
            //sbInsertTableCvsStructure.append("                        u.referenced_owner,");
            //sbInsertTableCvsStructure.append("                        u.referenced_name,");
            //sbInsertTableCvsStructure.append("                        u.referenced_type");
            //sbInsertTableCvsStructure.append("          from user_dependencies u");
            //sbInsertTableCvsStructure.append("         where REFERENCED_OWNER = ?  and ");
            //sbInsertTableCvsStructure.append("           name <> referenced_name");
            //sbInsertTableCvsStructure.append("        connect by prior referenced_name = name)");
            //sbInsertTableCvsStructure.append(" where (TYPE in ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))");
            //sbInsertTableCvsStructure.append("   and (REFERENCED_TYPE in");
            //sbInsertTableCvsStructure.append("       ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))");
            //sbInsertTableCvsStructure.append(")");

            sbInsertTableCvsStructure.append("insert into TMP_CVS_STRUCTURE (");
            sbInsertTableCvsStructure.append("select distinct ");
            sbInsertTableCvsStructure.append("                        NAME,");
            sbInsertTableCvsStructure.append("                        TYPE,");
            sbInsertTableCvsStructure.append("                        referenced_owner,");
            sbInsertTableCvsStructure.append("                        referenced_name,");
            sbInsertTableCvsStructure.append("                        referenced_type");
            sbInsertTableCvsStructure.append("  from (select *");
            sbInsertTableCvsStructure.append("         from user_dependencies u");
            sbInsertTableCvsStructure.append("         where u.REFERENCED_OWNER = ?  and ");
            sbInsertTableCvsStructure.append("           name <> u.referenced_name");
            sbInsertTableCvsStructure.append("           )");
            sbInsertTableCvsStructure.append(" where (TYPE in ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))");
            sbInsertTableCvsStructure.append("   and (REFERENCED_TYPE in");
            sbInsertTableCvsStructure.append("       ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))");
            sbInsertTableCvsStructure.append(")");

            psInsertReferencesObjectsIT = ConnectionIntegracao.getConnection().prepareCall(sbInsertTableCvsStructure.toString());
        }

        try {
            try {
                psCreateTableIT.executeQuery();
                ConnectionIntegracao.getConnection().commit();
            } catch (Exception ex) {
                //logMessage("Creando tabela caso não exista");
                //logMessage(ex.getLocalizedMessage());
                SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            psInsertReferencesObjectsIT.setString(1, cliente.getItUser().getUser().toUpperCase());
            psInsertReferencesObjectsIT.executeUpdate();
            ConnectionIntegracao.getConnection().commit();

            rsFoundObjectsIT = psFoundObjectsIT.executeQuery();
            while (rsFoundObjectsIT.next()) {
                //psCountArquivosExternosNaoGerados = ConnectionInout.getConnection().prepareStatement(this.countArquivosSynonyms);
                //psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                //rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                //rsCountArquivosExternosNaoGerados.next();
                //int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                //if(nTotalArquivos > 1){
                fileName = "";
                fileNameScripts = "";
                if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("bat")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("sql")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase();
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("function")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("procedure")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("table")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
                }
                /*
                }else{
                if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".")+1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                psFoundSQLInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                rsSqlInterface = psFoundSQLInterface.executeQuery();
                while(rsSqlInterface.next()){
                IDInterface = rsSqlInterface.getString("ID_INTERFACE");
                }
                }else{
                psBatInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                rsBatInterface = psBatInterface.executeQuery();
                while(rsBatInterface.next()){
                IDInterface = rsBatInterface.getString("ID_INTERFACE");
                }
                }

                psFoundDadosInterface.setString(1, IDInterface);
                rsDadosInterface = psFoundDadosInterface.executeQuery();
                while(rsDadosInterface.next()){
                sExecutavel = rsDadosInterface.getString("EXECUTAVEL");
                sTipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                sIdSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                sDescricao = rsDadosInterface.getString("DESCRICAO");
                sUserName = rsDadosInterface.getString("USERNAME");
                sTempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                sExecutavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");
                }

                if(sTipoInterface.trim().equals("S")){
                if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                }
                }else if(sTipoInterface.trim().equals("E")){
                if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIdSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                }
                }else{
                //fileNameScripts = ".\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                }
                }
                }
                 */
                try {
                    fileScripts = new Arquivo(fileNameScripts);
                    if (!fileScripts.exists()) {

                        //logMessage("Creating or appending to file " + fileNameScripts);

                        if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("function")
                                || rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("procedure")) {

                            //
                            new FunctionProcedure("INTEGRACAO",
                                    rsFoundObjectsIT.getString("TIPO"),
                                    rsFoundObjectsIT.getString("OBJECT_NAME"),
                                    fileName,
                                    fileNameScripts);

                        } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("table")) {

                            //
                            new Tables("INTEGRACAO",
                                    rsFoundObjectsIT.getString("OBJECT_NAME"),
                                    fileName,
                                    fileNameScripts);

                        } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package")
                                || rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package body")) {

                            //
                            new Packages("INTEGRACAO",
                                    rsFoundObjectsIT.getString("OBJECT_NAME"),
                                    fileName,
                                    fileNameScripts);
                        }

                    }
                } catch (IOException ioex) {
                    //CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                    //SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                    ioex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            //logMessage("Error generating " + fileName);
            //logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            //psDropTableIT = ConnectionIntegracao.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE");
            //psDropTableIT.execute();
        }

    }

}
