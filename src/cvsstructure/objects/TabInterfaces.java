/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

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

    private static PreparedStatement psPermissaoTabela = null;
    private static ResultSet rsPermissaoTabela = null;

    private static StringBuffer sbTabInterface;
    private static StringBuffer sbColunasTabInterface;
    private static StringBuffer sbDependHeaderItem;
    private static StringBuffer sbDependHeaderItemTrigger;
    private static StringBuffer sbCountPermissaoTabela;

    public TabInterfaces(String tableName, 
                         String idInterface,
                         String idSistema,
                         CVSStructure cvsStructure) throws SQLException, IOException{
        String fileName = "";
        String fileNameScripts = "";
        StringBuffer strOut = new StringBuffer();

        boolean flagSelectInterface = false;

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

        StringBuffer sbPermissaoTabela = new StringBuffer();
        sbPermissaoTabela.append("select * from permissao_tabela where table_name = ?");

        fileName = tableName.toLowerCase() + ".sql";

        try{
            psCountPermissaoTabela.setString(1, tableName);
            rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
            rsCountPermissaoTabela.next();

            psTabInterface.setString(1, tableName);
            rsTabInterface = psTabInterface.executeQuery();

            if(rsCountPermissaoTabela.getInt("TOTAL_INTERFACES") >= 1 ){
                //
                if(psPermissaoTabela == null){
                    psPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbPermissaoTabela.toString());
                }
                psPermissaoTabela.setString(1, tableName.toUpperCase());
                rsPermissaoTabela = psPermissaoTabela.executeQuery();

                fileNameScripts = "";

                flagSelectInterface = false;
                while(rsPermissaoTabela.next()){
                    idInterface = rsPermissaoTabela.getString("ID_INTERFACE");


                    PreparedStatement psInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement("select * from interfaces where id_interface = ?");
                    psInterfaceDaTabela.setString(1, idInterface);
                    ResultSet rsInterfaceDaTabela = psInterfaceDaTabela.executeQuery();
                    rsInterfaceDaTabela.next();

                    if(!flagSelectInterface){
                        // Validando Interface selecionada
                        // Quando  for seleciona pelo menos uma interface
                        if(cvsStructure.getSSelectInterfaces().length != 0){
                            for(int j=0; j < cvsStructure.getSSelectInterfaces().length; j++){
                                if(rsInterfaceDaTabela.getString("DESCRICAO").equals(cvsStructure.getSSelectInterfaces()[j])){
                                    flagSelectInterface = true;
                                    break;
                                }
                            }
                        }else{
                            // Quando nehuma interface for selecionada, gerar de todas
                            flagSelectInterface = true;
                            break;
                        }
                    }
                }
            // A tabela só é chamado por uma unica interface ou a Permissão tabela está com 0
            }else if(rsCountPermissaoTabela.getInt("TOTAL_INTERFACES") == 0 ){
                // Quando nehuma interface for selecionada, gerar de todas
                flagSelectInterface = false;
            }


            if(flagSelectInterface){
                if(rsCountPermissaoTabela.getInt("TOTAL_INTERFACES") == 1){
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("IN", idInterface) + "\\INOUT\\Tabelas\\" + fileName;
                }else{
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INOUT\\Tabelas\\" + fileName;
                }


                File fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists()){
 



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
                        strOut.append("  set");
                        strOut.append(trataUpdateCamposTabInterface("DESCRICAO", rsTabInterface.getString("DESCRICAO")));
                        strOut.append(trataUpdateCamposTabInterface("CTL_NAME", rsTabInterface.getString("CTL_NAME")));
                        strOut.append(trataUpdateCamposTabInterface("PREFIX_FILE", rsTabInterface.getString("PREFIX_FILE")));
                        strOut.append(trataUpdateCamposTabInterface("GERAR_CTL", rsTabInterface.getString("GERAR_CTL")));
                        strOut.append(trataUpdateCamposTabInterface("PRIORIDADE", rsTabInterface.getString("PRIORIDADE")));
                        strOut.append(trataUpdateCamposTabInterface("ODBC_SOURCE_NAME", rsTabInterface.getString("ODBC_SOURCE_NAME")));
                        strOut.append(trataUpdateCamposTabInterface("ODBC_PASSWORD", rsTabInterface.getString("ODBC_PASSWORD")));
                        strOut.append(trataUpdateCamposTabInterface("ODBC_TABLE_NAME", rsTabInterface.getString("ODBC_TABLE_NAME")));
                        strOut.append(trataUpdateCamposTabInterface("ODBC_WHERE", rsTabInterface.getString("ODBC_WHERE")));
                        strOut.append(trataUpdateCamposTabInterface("ODBC_SELECT_ESPECIFICO", rsTabInterface.getString("ODBC_SELECT_ESPECIFICO")));
                        strOut.append(trataUpdateCamposTabInterface("TIPO_INTERFACE", rsTabInterface.getString("TIPO_INTERFACE")));
                        strOut.append(trataUpdateCamposTabInterface("ORACLE_INITIAL_EXTENT", rsTabInterface.getString("ORACLE_INITIAL_EXTENT")));
                        strOut.append(trataUpdateCamposTabInterface("ORACLE_NEXT_EXTENT", rsTabInterface.getString("ORACLE_NEXT_EXTENT")));
                        strOut.append(trataUpdateCamposTabInterface("ORACLE_INDEX_TABLESPACE", rsTabInterface.getString("ORACLE_INDEX_TABLESPACE")));
                        strOut.append(trataUpdateCamposTabInterface("ELIMINAR_REG_EXECUCAO", rsTabInterface.getString("ELIMINAR_REG_EXECUCAO")));
                        strOut.append(trataUpdateCamposTabInterface("ID_SISTEMA", rsTabInterface.getString("ID_SISTEMA")));
                        strOut.append(trataUpdateCamposTabInterface("PROCEDURE_NAME", rsTabInterface.getString("PROCEDURE_NAME")));
                        strOut.append(trataUpdateCamposTabInterface("SEPARADOR", rsTabInterface.getString("SEPARADOR")));
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

                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("DESCRICAO")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("CTL_NAME")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("PREFIX_FILE")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("GERAR_CTL")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("PRIORIDADE")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_SOURCE_NAME")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_USER")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_PASSWORD")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_TABLE_NAME")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_WHERE")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("TIPO_INTERFACE")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ORACLE_INITIAL_EXTENT")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ORACLE_NEXT_EXTENT")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("COMANDO_EXTRA_LOADER")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("ID_SISTEMA")));
                        strOut.append(trataCamposTabInterface(rsTabInterface.getString("PROCEDURE_NAME")));

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

                            if(rsColunasTabInterface.getString("TABLE_NAME") == null){
                                strOut.append("('', '");
                            }else{
                                strOut.append("('" + rsColunasTabInterface.getString("TABLE_NAME") + "', '");
                            }

                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("COLUMN_NAME")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("TIPO_LOADER")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("FORMATO")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("COMANDO_EXTRA")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("TAMANHO")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("ORDEM")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("DESCRICAO")));
                            strOut.append(trataCamposColunasTabInterface(rsColunasTabInterface.getString("ARG_NAME")));

                            if(rsColunasTabInterface.getString("ARG_FUNCTION") == null){
                                strOut.append("');");
                            }else{
                                strOut.append(rsColunasTabInterface.getString("ARG_FUNCTION") + "');");
                            }

                            strOut.append(CVSStructure.quebraLinha + "" + CVSStructure.quebraLinha);

                            // Depend Header Item
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

                    if(strOut != null && !strOut.equals("")){
                        fileScripts.createNewFile();

                        FileWriter fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOut.toString(),0,strOut.length());
                        fwScripts.close();

                        CVSStructure.nTotalTabelas++;
                        CVSStructure.logMessage("File " + fileName + " was succesfull generated.");
                    }
                }
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

    public String trataCamposTabInterface(String campo){
        if(campo == null){
            return "', " + CVSStructure.quebraLinha + "        '";
        }else{
            return campo.replaceAll("'",  "' || chr(39) || '") + "', " + CVSStructure.quebraLinha + "        '";
        }
    }

    public String trataCamposColunasTabInterface(String campo){
        if(campo == null){
            return "', '";
        }else{
            return campo.replaceAll("'",  "' || chr(39) || '") + "', '";
        }
    }

    public String trataUpdateCamposTabInterface(String nomeCampo, String value){
        if(value == null){
            return "        " + nomeCampo + " = '', " + CVSStructure.quebraLinha;
        }else{
            return "        " + nomeCampo + " = '" + value.replaceAll("'",  "' || chr(39) || '") + "'," + CVSStructure.quebraLinha;
        }
    }

    /**
     * @return the idSistema
     */
    public String getIdSistema() {
        return idSistema;
    }
}
