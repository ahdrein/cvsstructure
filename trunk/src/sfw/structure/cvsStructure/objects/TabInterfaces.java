/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.cvsStructure.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sfw.structure.cvsStructure.CVSStructure;
import sfw.structure.database.ConnectionInout;
import sfw.structure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class TabInterfaces {
    private String idInterface;
    private String idSistema;
	private static PreparedStatement psTabInterface;
	private ResultSet rsTabInterface = null;

	private static PreparedStatement psColunasTabInterface;
	private ResultSet rsColunasTabInterface = null;

	private static PreparedStatement psDependHeaderItem;
    private static PreparedStatement psDependHeaderItemTrigger;
	private ResultSet rsDependHeaderItemTrigger = null;

	private static PreparedStatement psCountPermissaoTabela;
	private ResultSet rsCountPermissaoTabela = null;

    private static StringBuffer sbTabInterface;
    private static StringBuffer sbColunasTabInterface;
    private static StringBuffer sbDependHeaderItem;
    private static StringBuffer sbDependHeaderItemTrigger;
    private static StringBuffer sbCountPermissaoTabela;

    public TabInterfaces(String tableName, 
                         String idInterface,
                         String idSistema) throws SQLException, IOException{
        String fileName = "";
        String fileNameScripts = "";
        StringBuffer strOut = new StringBuffer();

        if(sbTabInterface == null){
            sbTabInterface = new StringBuffer();
            sbTabInterface.append("select table_name table_name, nvl(ctl_name, '') ctl_name, ");
            sbTabInterface.append(" nvl(prefix_file, '') prefix_file, ctl_fixo ctl_fixo, ");
            sbTabInterface.append(" nvl(gerar_ctl, '') gerar_ctl, ");
            sbTabInterface.append(" nvl(prioridade, '') prioridade, ");
            sbTabInterface.append(" nvl(descricao, '') descricao, ");
            sbTabInterface.append(" nvl(tipo_interface, '') tipo_interface, ");
            sbTabInterface.append(" nvl(odbc_source_name, '') odbc_source_name, ");
            sbTabInterface.append(" nvl(odbc_user, '') odbc_user, ");
            sbTabInterface.append(" nvl(odbc_password, '') odbc_password, ");
            sbTabInterface.append(" nvl(odbc_table_name, '') odbc_table_name, ");
            sbTabInterface.append(" nvl(oracle_initial_extent, '') oracle_initial_extent, ");
            sbTabInterface.append(" nvl(oracle_next_extent, '') oracle_next_extent, ");
            sbTabInterface.append(" nvl(oracle_index_tablespace, '') oracle_index_tablespace, ");
            sbTabInterface.append(" nvl(odbc_where, '') odbc_where, ");
            sbTabInterface.append(" nvl(odbc_select_especifico, '') odbc_select_especifico, ");
            sbTabInterface.append(" nvl(eliminar_reg_execucao, '') eliminar_reg_execucao, ");
            sbTabInterface.append(" nvl(comando_extra_loader, '') comando_extra_loader, ");
            sbTabInterface.append(" nvl(trigger1, '') trigger1, ");
            sbTabInterface.append(" nvl(trigger2, '') trigger2, ");
            sbTabInterface.append(" nvl(id_sistema, '') id_sistema, ");
            sbTabInterface.append(" nvl(procedure_name, '') procedure_name, ");
            sbTabInterface.append(" nvl(separador, '') separador ");
            sbTabInterface.append(" from tab_interface where table_name = ?");

            psTabInterface = ConnectionInout.getConnection().prepareStatement(sbTabInterface.toString());
        }

        if(sbColunasTabInterface == null){
            sbColunasTabInterface = new StringBuffer();
            sbColunasTabInterface.append("select * from colunas_tab_interface where table_name = ?");

            psColunasTabInterface = ConnectionInout.getConnection().prepareStatement(sbColunasTabInterface.toString());
        }

        if(sbDependHeaderItem == null){
            sbDependHeaderItem = new StringBuffer();
            sbDependHeaderItem.append("select * from depend_header_item where table_name = ? and column_name = ?");

            psDependHeaderItem = ConnectionInout.getConnection().prepareStatement(sbDependHeaderItem.toString());
        }

        if(sbDependHeaderItemTrigger == null){
            sbDependHeaderItemTrigger = new StringBuffer();
            sbDependHeaderItemTrigger.append("select * from depend_header_item where table_name = ?");

            psDependHeaderItemTrigger = ConnectionInout.getConnection().prepareStatement(sbDependHeaderItemTrigger.toString());
        }

        if(sbCountPermissaoTabela == null){
            sbCountPermissaoTabela = new StringBuffer();
            sbCountPermissaoTabela.append("select count(*) total_interfaces from permissao_tabela where table_name = ?");

            psCountPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbCountPermissaoTabela.toString());
        }

        fileName = tableName.toLowerCase() + ".sql";

        psCountPermissaoTabela.setString(1, tableName);
        rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
        rsCountPermissaoTabela.next();

        if(rsCountPermissaoTabela.getInt("TOTAL_INTERFACES") == 1){
            fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("IN", idInterface) + "\\INOUT\\Tabelas\\" + fileName;
        }else{
            fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INOUT\\Tabelas\\" + fileName;
        }

        try{
            File fileScripts = new File(fileNameScripts);
            if(!fileScripts.exists()){
                fileScripts.createNewFile();

                FileWriter fwScripts = new FileWriter(fileScripts, false);

                //psTabInterface = ConnectionInout.getConnection().prepareStatement(sSelectTabInterface);
                psTabInterface.setString(1, tableName);
                rsTabInterface = psTabInterface.executeQuery();

                CVSStructure.logMessage("Creating or appending to file " + fileName);

                while(rsTabInterface.next()){

                    StringBuffer strOutCltFixo = new StringBuffer();

                    if(rsTabInterface.getCharacterStream("CTL_FIXO") != null){
                        BufferedReader brCtlFixo = new BufferedReader(rsTabInterface.getCharacterStream("CTL_FIXO"));

                        if (brCtlFixo != null){
                            String aux;

                            while ((aux=brCtlFixo.readLine())!=null){
                                strOutCltFixo.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','CTL_FIXO','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                                strOutCltFixo.append( CVSStructure.quebraLinha );
                                strOutCltFixo.append( CVSStructure.quebraLinha );
                            }
                        }
                    }

                    if(CVSStructure.chConexaoPorArquivos.equals("S")){
                        strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS");
                    }

                    strOut.append( CVSStructure.quebraLinha );
                    strOut.append( CVSStructure.quebraLinha );
                    strOut.append("--  ///////" + CVSStructure.quebraLinha);
                    strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + CVSStructure.quebraLinha);
                    strOut.append("--  ///////     TABELA: " + rsTabInterface.getString("TABLE_NAME")  + CVSStructure.quebraLinha);
                    strOut.append("--  ///////" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    strOut.append("delete from DEPEND_HEADER_ITEM_TMP;" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    strOut.append("delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "';" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    strOut.append("begin" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    strOut.append("  update    TAB_INTERFACE" + CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("DESCRICAO") == null){
                        strOut.append("  set       DESCRICAO = '',");
                    }else{
                        strOut.append("  set       DESCRICAO = '" + rsTabInterface.getString("DESCRICAO") + "',");
                    }
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("         CTL_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "'," + CVSStructure.quebraLinha);
                    strOut.append("        PREFIX_FILE = '" + rsTabInterface.getString("PREFIX_FILE") + "'," + CVSStructure.quebraLinha);
                    strOut.append("        GERAR_CTL = '" + rsTabInterface.getString("GERAR_CTL") + "'," + CVSStructure.quebraLinha);
                    strOut.append("        PRIORIDADE = " + rsTabInterface.getString("PRIORIDADE") + "," + CVSStructure.quebraLinha);

                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("        ODBC_SOURCE_NAME = '',");
                    }else{
                        strOut.append("        ODBC_SOURCE_NAME = '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("        ODBC_USER = '',");
                    }else{
                        strOut.append("        ODBC_USER = '" + rsTabInterface.getString("ODBC_USER") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ODBC_PASSWORD") == null){
                        strOut.append("        ODBC_PASSWORD = '',");
                    }else{
                        strOut.append("        ODBC_PASSWORD = '" + rsTabInterface.getString("ODBC_PASSWORD") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);

                    if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
                        strOut.append("        ODBC_TABLE_NAME = '',");
                    }else{
                        strOut.append("        ODBC_TABLE_NAME = '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ODBC_WHERE") == null){
                        strOut.append("        ODBC_WHERE = '',");
                    }else{
                        strOut.append("        ODBC_WHERE = '" + rsTabInterface.getString("ODBC_WHERE") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
                        strOut.append("        ODBC_SELECT_ESPECIFICO = '',");
                    }else{
                        strOut.append("        ODBC_SELECT_ESPECIFICO = '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("TIPO_INTERFACE") == null){
                        strOut.append("        TIPO_INTERFACE = '',");
                    }else{
                        strOut.append("        TIPO_INTERFACE = '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
                        strOut.append("        ORACLE_INITIAL_EXTENT = '',");
                    }else{
                        strOut.append("        ORACLE_INITIAL_EXTENT = '"+rsTabInterface.getString("ORACLE_INITIAL_EXTENT")+"',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
                        strOut.append("        ORACLE_NEXT_EXTENT = '',");
                    }else{
                        strOut.append("        ORACLE_NEXT_EXTENT = '"+rsTabInterface.getString("ORACLE_NEXT_EXTENT")+"',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
                        strOut.append("        ORACLE_INDEX_TABLESPACE = '',");
                    }else{
                        strOut.append("        ORACLE_INDEX_TABLESPACE = '"+rsTabInterface.getString("ORACLE_INDEX_TABLESPACE")+"',");
                    }
                    strOut.append( CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
                        strOut.append("        ELIMINAR_REG_EXECUCAO = '',");
                    }else{
                        strOut.append("        ELIMINAR_REG_EXECUCAO = '"+rsTabInterface.getString("ELIMINAR_REG_EXECUCAO")+"',");
                    }
                    strOut.append(CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("ID_SISTEMA") == null){
                        strOut.append("        ID_SISTEMA = '',");
                    }else{
                        strOut.append("        ID_SISTEMA = '"+rsTabInterface.getString("ID_SISTEMA")+"',");
                    }
                    strOut.append(CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("PROCEDURE_NAME") == null){
                        strOut.append("        PROCEDURE_NAME = '',");
                    }else{
                        strOut.append("        PROCEDURE_NAME = '"+rsTabInterface.getString("PROCEDURE_NAME")+"',");
                    }
                    strOut.append(CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("SEPARADOR") == null){
                        strOut.append("        SEPARADOR = '',");
                    }else{
                        strOut.append("        SEPARADOR = '"+rsTabInterface.getString("SEPARADOR")+"',");
                    }
                    strOut.append(CVSStructure.quebraLinha);

                    strOut.append("        CTL_FIXO = null,");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("        TRIGGER1 = null,");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("        TRIGGER2 = null,");
                    strOut.append(CVSStructure.quebraLinha);
                    if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
                        strOut.append("        COMANDO_EXTRA_LOADER = ''");
                    }else{
                        strOut.append("        COMANDO_EXTRA_LOADER = '"+rsTabInterface.getString("COMANDO_EXTRA_LOADER")+"'");
                    }
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("  where TABLE_NAME = '"+rsTabInterface.getString("TABLE_NAME")+"';" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    strOut.append("  if SQL%notfound then" + CVSStructure.quebraLinha);
                    strOut.append("        insert into TAB_INTERFACE" + CVSStructure.quebraLinha);
                    strOut.append("        (TABLE_NAME, " + CVSStructure.quebraLinha + "        DESCRICAO, " + CVSStructure.quebraLinha + "        CTL_NAME, " + CVSStructure.quebraLinha + "        PREFIX_FILE, " + CVSStructure.quebraLinha + "        GERAR_CTL, " + CVSStructure.quebraLinha + "        PRIORIDADE, " + CVSStructure.quebraLinha + "        ODBC_SOURCE_NAME, " + CVSStructure.quebraLinha + "        ODBC_USER, " + CVSStructure.quebraLinha + "        ODBC_PASSWORD, " + CVSStructure.quebraLinha + "        ODBC_TABLE_NAME, " + CVSStructure.quebraLinha + "        ODBC_WHERE, " + CVSStructure.quebraLinha + "        ODBC_SELECT_ESPECIFICO, " + CVSStructure.quebraLinha + "        TIPO_INTERFACE," + CVSStructure.quebraLinha);
                    strOut.append("        ORACLE_INITIAL_EXTENT, " + CVSStructure.quebraLinha + "        ORACLE_NEXT_EXTENT, " + CVSStructure.quebraLinha + "        ORACLE_INDEX_TABLESPACE, " + CVSStructure.quebraLinha + "        ELIMINAR_REG_EXECUCAO, " + CVSStructure.quebraLinha + "        COMANDO_EXTRA_LOADER, " + CVSStructure.quebraLinha + "        ID_SISTEMA, " + CVSStructure.quebraLinha + "        PROCEDURE_NAME, " + CVSStructure.quebraLinha + "        SEPARADOR)" + CVSStructure.quebraLinha);
                    strOut.append("        values" + CVSStructure.quebraLinha);
                    //strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', '" + rsTabInterface.getString("CTL_NAME") + "', '" + rsTabInterface.getString("PREFIX_FILE") + "', '" + rsTabInterface.getString("GERAR_CTL") + "', " + rsTabInterface.getString("PRIORIDADE") + ", '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "', '" + rsTabInterface.getString("ODBC_USER") +"', '" + rsTabInterface.getString("ODBC_PASSWORD") + "', '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "', '" + rsTabInterface.getString("ODBC_WHERE") + "', '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
                    if(rsTabInterface.getString("TABLE_NAME") == null){
                        strOut.append("        ('', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("DESCRICAO") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("DESCRICAO") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("CTL_NAME") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("CTL_NAME") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("PREFIX_FILE") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("PREFIX_FILE") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("GERAR_CTL") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("GERAR_CTL") + "', " + CVSStructure.quebraLinha + "        ");
                    }

                    strOut.append(rsTabInterface.getString("PRIORIDADE") + ", " + CVSStructure.quebraLinha + "        '");

                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_SOURCE_NAME") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_USER") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_USER") +"', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_PASSWORD") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_PASSWORD") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_TABLE_NAME") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_WHERE") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_WHERE") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("TIPO_INTERFACE") == null){
                        strOut.append("'," + CVSStructure.quebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("TIPO_INTERFACE") + "'," + CVSStructure.quebraLinha + "        ");
                    }
                    //strOut.append(sQuebraLinha);

                    //strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "','" + rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "','" + rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "',");
                    if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
                        strOut.append("        '', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
                        strOut.append("', " + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', " + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
                        strOut.append("'," + CVSStructure.quebraLinha + "        '" );
                    }else{
                        strOut.append(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "'," + CVSStructure.quebraLinha + "        '" );
                    }

                    if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
                        strOut.append("'," + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "'," + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
                        strOut.append("'," + CVSStructure.quebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "'," + CVSStructure.quebraLinha + "        ");
                    }

                    //strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "','" + rsTabInterface.getString("PROCEDURE_NAME") + "','" + rsTabInterface.getString("SEPARADOR") +"');");

                    if(rsTabInterface.getString("ID_SISTEMA") == null){
                        strOut.append("        ''," + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append("'" + rsTabInterface.getString("ID_SISTEMA") + "'," + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("PROCEDURE_NAME") == null){
                        strOut.append("'," + CVSStructure.quebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("PROCEDURE_NAME") + "'," + CVSStructure.quebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("SEPARADOR") == null){
                        strOut.append("');");
                    }else{
                        strOut.append(rsTabInterface.getString("SEPARADOR") + "')");
                    }

                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("  end if;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("end;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("/");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);

                    strOut.append(strOutCltFixo);

                    if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
                        String aux;
                        BufferedReader br2 = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

                        while ((aux=br2.readLine())!=null){
                            strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER1','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                            strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                        }
                    }


                    if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

                        while ((aux=br.readLine())!=null){
                            strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER2','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                            strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                        }
                    }

                    //psColunasTabInterface = ConnectionInout.getConnection().prepareStatement(sSelectColunasTabInterface);
                    psColunasTabInterface.setString(1, rsTabInterface.getString("TABLE_NAME"));
                    rsColunasTabInterface = psColunasTabInterface.executeQuery();

                    while(rsColunasTabInterface.next()){
                        strOut.append("insert into COLUNAS_TAB_INTERFACE");
                        strOut.append(CVSStructure.quebraLinha);
                        strOut.append("(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)");
                        strOut.append(CVSStructure.quebraLinha);
                        strOut.append("values");
                        strOut.append(CVSStructure.quebraLinha);
                        //strOut.append("('" + rsColunasTabInterface.getString("TABLE_NAME") + "', '" + rsColunasTabInterface.getString("COLUMN_NAME") + "', '" + rsColunasTabInterface.getString("TIPO_LOADER") + "', '" + rsColunasTabInterface.getString("FORMATO") + "', '" + rsColunasTabInterface.getString("COMANDO_EXTRA") + "', '" + rsColunasTabInterface.getString("TAMANHO") + "', '" + rsColunasTabInterface.getString("ORDEM") + "','" + rsColunasTabInterface.getString("DESCRICAO") + "','" + rsColunasTabInterface.getString("ARG_NAME") + "','" + rsColunasTabInterface.getString("ARG_FUNCTION") + "');");

                        if(rsColunasTabInterface.getString("TABLE_NAME") == null){
                            strOut.append("('', '");
                        }else{
                            strOut.append("('" + rsColunasTabInterface.getString("TABLE_NAME") + "', '");
                        }

                        if(rsColunasTabInterface.getString("COLUMN_NAME") == null){
                            strOut.append("', '");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("COLUMN_NAME") + "', '");
                        }

                        if(rsColunasTabInterface.getString("TIPO_LOADER") == null){
                            strOut.append("', '");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("TIPO_LOADER") + "', '");
                        }

                        if(rsColunasTabInterface.getString("FORMATO") == null){
                            strOut.append("', '");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("FORMATO") + "', '");
                        }

                        if(rsColunasTabInterface.getString("COMANDO_EXTRA") == null){
                            strOut.append("', '");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("COMANDO_EXTRA") + "', '");
                        }

                        if(rsColunasTabInterface.getString("TAMANHO") == null){
                            strOut.append("', '");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("TAMANHO") + "', '");
                        }

                        if(rsColunasTabInterface.getString("ORDEM") == null){
                            strOut.append("','");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("ORDEM") + "','");
                        }

                        if(rsColunasTabInterface.getString("DESCRICAO") == null){
                            strOut.append("','");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("DESCRICAO") + "','");
                        }

                        if(rsColunasTabInterface.getString("ARG_NAME") == null){
                            strOut.append("','");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("ARG_NAME") + "','");
                        }

                        if(rsColunasTabInterface.getString("ARG_FUNCTION") == null){
                            strOut.append("');");
                        }else{
                            strOut.append(rsColunasTabInterface.getString("ARG_FUNCTION") + "');");
                        }

                        strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);

                        psDependHeaderItem.setString(1, rsColunasTabInterface.getString("TABLE_NAME"));
                        psDependHeaderItem.setString(2, rsColunasTabInterface.getString("COLUMN_NAME"));
                        strOut.append(new DataTableLayout("INOUT",
                                                                "depend_header_item",
                                                                 psDependHeaderItem).create());

                    }

                    strOut.append("commit;");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("set serveroutput on");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("exec PRC_SINCRONIZA_TABELA('"+rsTabInterface.getString("TABLE_NAME")+"');");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);


                    if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

                        while ((aux=br.readLine())!=null){
                            strOut.append(aux);
                            strOut.append(CVSStructure.quebraLinha);
                        }
                        strOut.append("/" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                    }


                    if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

                        while ((aux=br.readLine())!=null){
                            strOut.append(aux);
                            strOut.append(CVSStructure.quebraLinha);
                        }
                        strOut.append("/" + CVSStructure.quebraLinha);
                    }

                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("TRIGGER TI_" + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("BEFORE INSERT ON " + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("begin");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("    select seq_id_interface.nextval into :new.id from dual;");

                    //buscando o header

                    psDependHeaderItemTrigger.setString(1, rsTabInterface.getString("TABLE_NAME"));
                    rsDependHeaderItemTrigger = psDependHeaderItemTrigger.executeQuery();
                    if( rsDependHeaderItemTrigger.next() && rsDependHeaderItemTrigger.getString("TABLE_NAME") != null ){
                        strOut.append(CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        strOut.append("  begin" + CVSStructure.quebraLinha);
                        strOut.append("    select id" + CVSStructure.quebraLinha);
                        strOut.append("    into :new.id_ref" + CVSStructure.quebraLinha);
                        strOut.append("    from " + rsDependHeaderItemTrigger.getString("TABLE_NAME_HEADER") + CVSStructure.quebraLinha);
                        strOut.append("    where id_importacao = :new.id_importacao" + CVSStructure.quebraLinha);
                        strOut.append("    and " + rsDependHeaderItemTrigger.getString("COLUMN_NAME") + " = :new." + rsDependHeaderItemTrigger.getString("COLUMN_NAME") );
                        while(rsDependHeaderItemTrigger.next()){
                            strOut.append(CVSStructure.quebraLinha);
                            strOut.append("    and " + rsDependHeaderItemTrigger.getString("COLUMN_NAME") + " = :new." + rsDependHeaderItemTrigger.getString("COLUMN_NAME"));
                        }
                        strOut.append(";" + CVSStructure.quebraLinha);
                        strOut.append("  exception" + CVSStructure.quebraLinha);
                        strOut.append("    when no_data_found then" + CVSStructure.quebraLinha);
                        strOut.append("      :new.id_ref := null;" + CVSStructure.quebraLinha);
                        strOut.append("    when others then" + CVSStructure.quebraLinha);
                        strOut.append("      raise_application_error(-20001,'Falha em busca de header: ' || sqlerrm(sqlcode()));" + CVSStructure.quebraLinha);
                        strOut.append("  end;" + CVSStructure.quebraLinha);
                    }

                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("end;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("/");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("TRIGGER TIA_" + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("AFTER INSERT ON " + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("begin");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("    insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("    values (:new.id, :new.id_importacao, '" + rsTabInterface.getString("TABLE_NAME") + "', :new.id_ref);");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("end;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("/");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("TRIGGER TD_" + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("BEFORE DELETE ON " + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("begin");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("    delete from REGISTROS_INTERFACES where id = :old.id;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("end;");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("/");
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);
                    strOut.append("-- //////");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas");
                    strOut.append(CVSStructure.quebraLinha);
                    strOut.append("-- //////");

                }
                fwScripts.write(strOut.toString(),0,strOut.length());
                fwScripts.close();

                CVSStructure.logMessage("File " + fileName + " was succesfull generated.");
            }
        }catch(IOException ioex){
            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        }catch(Exception ex){
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        }
    }

    public String getIdInterface(String idInterface){
            return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }


    private int getOrdemTable(String nameTable){
        int ordem;
 

        return 1;
    }

    public String getIdInterface(){
            return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    
    public String getNomePasta(String tipo, String idInterface){
        String pasta = "";

        if(tipo.equals("") || CVSStructure.chNomePasta.equals("N")){
            pasta = getIdInterface(idInterface);
        }else if (tipo.equals("IN")){
            pasta = getIdSistema() + "_in_" + getIdInterface(idInterface);
        }else if (tipo.equals("OUT")){
            pasta = getIdSistema() + "_out_" + getIdInterface(idInterface);
        }

        return pasta;
    }

    /**
     * @return the idSistema
     */
    public String getIdSistema() {
        return idSistema;
    }
}
