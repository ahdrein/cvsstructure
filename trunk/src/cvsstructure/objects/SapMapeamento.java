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
import sfw.structure.database.ConnectionIntegracao;
import sfw.structure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class SapMapeamento {
    private String idInterface = "";
    private String idSistema = "";

    private PreparedStatement psSapMapeamentoTabela = null;
    private PreparedStatement psSapBlocks = null;

    private PreparedStatement psSapMapeamento = null;
 	private ResultSet rsSapMapeamento = null;

    private PreparedStatement psSapMapeamentoColuna = null;
 	private ResultSet rsSapMapeamentoColuna = null;

    private static PreparedStatement psPermissaoTabela = null;
    private static ResultSet rsPermissaoTabela = null;

    private static PreparedStatement psSistemaInterfaceDaTabela = null;
    private static ResultSet rsSistemaInterfaceDaTabela = null;

	private static PreparedStatement psCountPermissaoTabela = null;
	private static ResultSet rsCountPermissaoTabela = null;

	private static ResultSet rsDadosInterface = null;
    
    public SapMapeamento(String system, CVSStructure cvsStructure) throws IOException{

        // Informações InOut
        String executavel = "";
        String tipoInterface = "";
        
        String descricao = "";
        String userName = "";
        String tempoMedio = "";
        String executavelCompl = "";

        String fileNameScripts = "";
        String fileName = "";
        String sap_saida = "";

		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        StringBuffer sbSapMapeamento = new StringBuffer();
        sbSapMapeamento.append("select * from sap_interface_tables");

        StringBuffer sbSapMapeamentoColuna = new StringBuffer();
        sbSapMapeamentoColuna.append("select * from sap_interface_columns where table_id = ?");

        //
        StringBuffer sbPermissaoTabela = new StringBuffer();
        sbPermissaoTabela.append("select * from permissao_tabela where table_name = ?");

        StringBuffer sbCountPermissaoTabela = new StringBuffer();
        sbCountPermissaoTabela.append("select count(*) TOTAL from permissao_tabela where table_name = ?");

        StringBuffer sbInterfaceDaTabela = new StringBuffer();
        sbInterfaceDaTabela.append("select distinct it.* ");
        sbInterfaceDaTabela.append("from permissao_tabela ta,");
        sbInterfaceDaTabela.append("     sistema_interface it");
        sbInterfaceDaTabela.append(" where ta.table_name like '%' || upper( ? ) ||'%'");
        sbInterfaceDaTabela.append(" and it.id_interface = ta.id_interface");

        // Obtendo condições do select para encontrar o as interfaces existentes
        StringBuffer sbDadosInterface = new StringBuffer();
		sbDadosInterface.append("select replace( replace( replace( substr(interfaces.executavel, instr(interfaces.executavel, '\\')+1, length(interfaces.executavel)), '#IDENT#INTERFACES\\SAP\\', ''), '#IDENT#INTERFACES\\', '') , '#IDENT#INTEGRACAO\\', '') executavel,");
        sbDadosInterface.append("interfaces.id_interface,");
        sbDadosInterface.append("interfaces.tipo_interface,");
        sbDadosInterface.append("nvl(sistema_interface.id_sistema, 'sfw') id_sistema,");
        sbDadosInterface.append("interfaces.descricao,");
        sbDadosInterface.append("interfaces.username,");
        sbDadosInterface.append("interfaces.tempo_medio,");
        sbDadosInterface.append("interfaces.executavelCompl executavelCompl");
        sbDadosInterface.append(" from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,");
        sbDadosInterface.append(" sistema_interface");
        sbDadosInterface.append(" where interfaces.id_interface = sistema_interface.id_interface (+)");
        sbDadosInterface.append("and sistema_interface.id_interface = ?");

        try{
            if(PrepararConsultas.existObject("SAP_INTERFACE_TABLES", system) >= 1 ){
                    
                if(system.toUpperCase().equals("INOUT")){
                    psSapMapeamento = ConnectionInout.getConnection().prepareStatement(sbSapMapeamento.toString());
                }else{
                    psSapMapeamento = ConnectionIntegracao.getConnection().prepareStatement(sbSapMapeamento.toString());
                }
                rsSapMapeamento = psSapMapeamento.executeQuery();
                while(rsSapMapeamento.next()){
                    sap_saida = "";
                    if(psPermissaoTabela == null){
                        psPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbPermissaoTabela.toString());
                    }
                    psPermissaoTabela.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                    rsPermissaoTabela = psPermissaoTabela.executeQuery();

                    fileNameScripts = "";
                    while(rsPermissaoTabela.next()){
                        idInterface = rsPermissaoTabela.getString("ID_INTERFACE");

                        if(psCountPermissaoTabela == null){
                            psCountPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbCountPermissaoTabela.toString());
                        }
                        psCountPermissaoTabela.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                        rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
                        rsCountPermissaoTabela.next();
                        int nTotPer = rsCountPermissaoTabela.getInt("TOTAL");

                        boolean sisFlag = true;
                        if (nTotPer > 1){
                            if(psSistemaInterfaceDaTabela == null){
                                psSistemaInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(sbInterfaceDaTabela.toString());
                            }
                            psSistemaInterfaceDaTabela.setString(1, rsSapMapeamento.getString("TABLE_NAME"));
                            rsSistemaInterfaceDaTabela = psSistemaInterfaceDaTabela.executeQuery();
                            rsSistemaInterfaceDaTabela.next();

                            if (!CVSStructure.id_sistema_it.equals("")){
                                if (CVSStructure.id_sistema_it.equals(rsSistemaInterfaceDaTabela.getString("ID_SISTEMA"))){
                                    sisFlag = false;
                                }
                            }
                        }

                        fileName = rsSapMapeamento.getString("IO_TABLE").toLowerCase() + ".sql";
                        if(idInterface == null || idInterface.equals("") || !sisFlag){
                            fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        }else{
                            rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface);
                            while(rsDadosInterface.next()){
                                executavel = rsDadosInterface.getString("EXECUTAVEL");
                                tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                                idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                                descricao = rsDadosInterface.getString("DESCRICAO");
                                //userNameApp = rsDadosInterface.getString("USERNAME");
                                tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                                executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");
                            }

                            if(tipoInterface.trim().equals("S")){
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                            }else if(tipoInterface.trim().equals("E")){
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                            }else{
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\"  + fileName;
                            }

                        }
                    }

                    if(fileNameScripts.equals("")){
                        fileName = rsSapMapeamento.getString("SAP_TABLE").toLowerCase() + ".sql";
                        fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\"  + fileName;
                        sap_saida = "S";
                    }

                    try{
                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);
                        strOutScripts = new StringBuffer();

                        /******************************************
                        * Gerando arquivos na pasta de Scripts
                        ******************************************/

                        if(CVSStructure.chConexaoPorArquivos.equals("S")){
                            if(system.toUpperCase().equals("INOUT")){
                                strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                            }else{
                                strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                            }
                        }
                        
                        strOutScripts.append(CVSStructure.quebraLinha);

                        strOutScripts.append("SET SERVEROUTPUT ON" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);
                        strOutScripts.append("--desabilitando todas as trigger da tabela" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables disable all triggers;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_blocks disable all triggers;" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables disable constraint fk_sap_interface_tables;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables disable constraint fk_sap_interface_block;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_blocks disable constraint pk_sap_if_blocks;" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);

                        strOutScripts.append("/*###############################" + CVSStructure.quebraLinha);
                        strOutScripts.append((sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + CVSStructure.quebraLinha);
                        strOutScripts.append("###############################*/" + CVSStructure.quebraLinha);
                        strOutScripts.append("--garante que não existirão registros duplicados" + CVSStructure.quebraLinha);
                        strOutScripts.append("declare" + CVSStructure.quebraLinha);
                        strOutScripts.append("    n_id   number := null;" + CVSStructure.quebraLinha);
                        strOutScripts.append("    err_msg 			varchar2(200);" + CVSStructure.quebraLinha);
                        strOutScripts.append("    n_parent_table_id	number := null;" + CVSStructure.quebraLinha);
                        strOutScripts.append("    n_id_header number := null;" + CVSStructure.quebraLinha);
                        strOutScripts.append("begin" + CVSStructure.quebraLinha);

                        if(rsSapMapeamento.getString("PARENT_TABLE_ID") != null){
                            // Bloco para verificar se já existe o mapeamento na int mapeamento
                            strOutScripts.append("-- buscando tabela mapeada" + CVSStructure.quebraLinha);
                            strOutScripts.append("    begin" + CVSStructure.quebraLinha);
                            strOutScripts.append("        select l.table_id into n_id_header" + CVSStructure.quebraLinha);
                            strOutScripts.append("          from sap_interface_tables l " + CVSStructure.quebraLinha);
                            strOutScripts.append("         where sap_table = '" + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + "'" + CVSStructure.quebraLinha);
                            strOutScripts.append("           and block_name = '" + rsSapMapeamento.getString("BLOCK_NAME") + "'; " + CVSStructure.quebraLinha);
                            strOutScripts.append("        exception" + CVSStructure.quebraLinha);
                            strOutScripts.append("            when NO_DATA_FOUND then" + CVSStructure.quebraLinha);
                            strOutScripts.append("                 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                            strOutScripts.append("                 dbms_output.put_line('An error was encountered NO_DATA_FOUND: " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " - ' || err_msg);" + CVSStructure.quebraLinha);
                            strOutScripts.append("            when OTHERS then" + CVSStructure.quebraLinha);
                            strOutScripts.append("            	 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                            strOutScripts.append("            	 dbms_output.put_line('An error was encountered: " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " - ' || err_msg);" + CVSStructure.quebraLinha);
                            strOutScripts.append("    end;" + CVSStructure.quebraLinha);
                            strOutScripts.append(CVSStructure.quebraLinha);
                        }

                        // Bloco para verificar se já existe o mapeamento na int mapeamento
                        strOutScripts.append("    begin" + CVSStructure.quebraLinha);
                        strOutScripts.append("        -- buscando tabela mapeada" + CVSStructure.quebraLinha);
                        strOutScripts.append("        select l.table_id, l.parent_table_id into n_id, n_parent_table_id" + CVSStructure.quebraLinha);
                        strOutScripts.append("          from sap_interface_tables l " + CVSStructure.quebraLinha);
                        strOutScripts.append("         where sap_table = '" + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + "'" + CVSStructure.quebraLinha);
                        strOutScripts.append("           and block_name = '" + rsSapMapeamento.getString("BLOCK_NAME") + "'; " + CVSStructure.quebraLinha);
                        strOutScripts.append("        exception" + CVSStructure.quebraLinha);
                        strOutScripts.append("            when NO_DATA_FOUND then" + CVSStructure.quebraLinha);
                        strOutScripts.append("                 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                        strOutScripts.append("                 dbms_output.put_line('An error was encountered NO_DATA_FOUND: " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " - ' || err_msg);" + CVSStructure.quebraLinha);
                        strOutScripts.append("            when OTHERS then" + CVSStructure.quebraLinha);
                        strOutScripts.append("            	 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                        strOutScripts.append("            	 dbms_output.put_line('An error was encountered: " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " - ' || err_msg);" + CVSStructure.quebraLinha);
                        strOutScripts.append("    end;" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);

                        // Bloco para excluir os registros da int mapeamento
                        strOutScripts.append("    if n_id != 0 then" + CVSStructure.quebraLinha);
                        strOutScripts.append("        begin" + CVSStructure.quebraLinha);
                        strOutScripts.append("            -- deletando as tabelas e colunas" + CVSStructure.quebraLinha);
                        strOutScripts.append("            delete from sap_interface_columns c where c.table_id = n_id;" + CVSStructure.quebraLinha);
                        strOutScripts.append("            delete from sap_interface_tables l where l.table_id = n_id;" + CVSStructure.quebraLinha);
                        strOutScripts.append( CVSStructure.quebraLinha);

                        // deletando os filhos encontrados
                        if(rsSapMapeamento.getString("PARENT_TABLE_ID") == null){
                            strOutScripts.append("            -- deletando os filhos encontrados" + CVSStructure.quebraLinha);
                            strOutScripts.append("            for tables_sap in (select table_id from sap_interface_tables l where l.parent_table_id = n_parent_table_id) " + CVSStructure.quebraLinha);
                            strOutScripts.append("            loop" + CVSStructure.quebraLinha);
                            strOutScripts.append("            	delete from sap_interface_columns c where c.parent_table_id = tables_sap.table_id;" + CVSStructure.quebraLinha);
                            strOutScripts.append("            end loop;" + CVSStructure.quebraLinha);
                            strOutScripts.append("            delete from sap_interface_tables l where l.parent_table_id = n_parent_table_id;" + CVSStructure.quebraLinha);
                            strOutScripts.append(CVSStructure.quebraLinha);
                            strOutScripts.append("            commit;" + CVSStructure.quebraLinha);
                        }

                        strOutScripts.append( CVSStructure.quebraLinha);
                        strOutScripts.append("            exception" + CVSStructure.quebraLinha);
                        strOutScripts.append("                when NO_DATA_FOUND then" + CVSStructure.quebraLinha);
                        strOutScripts.append("                     err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                        strOutScripts.append("                     dbms_output.put_line('An error was encountered NO_DATA_FOUND: " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " on sap_interface_tables - ' || err_msg);" + CVSStructure.quebraLinha);
                        strOutScripts.append("                when OTHERS then" + CVSStructure.quebraLinha);
                        strOutScripts.append("                    	 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                        strOutScripts.append("                	 dbms_output.put_line('An error was encountered: When deleting " + (sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")) + " on sap_interface_tables - ' || err_msg);" + CVSStructure.quebraLinha);
                        strOutScripts.append("        end;" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);
                        strOutScripts.append("    else" + CVSStructure.quebraLinha);
                        strOutScripts.append("    	-- encontrando o maior numero do table_id para a primeira inserssão" + CVSStructure.quebraLinha);
                        strOutScripts.append("    	select max(l.table_id)+1 into n_id from sap_interface_tables l;" + CVSStructure.quebraLinha);
                        strOutScripts.append("    end if;" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);

                        // Interfaces de Saida Genérica SAP
                        // Buscando bloco
                        if((rsSapMapeamento.getString("BLOCK_NAME") != null)
                                && (rsSapMapeamento.getString("PARENT_TABLE_ID") == null)
                                ){

                            strOutScripts.append("	begin" + CVSStructure.quebraLinha);
                            strOutScripts.append("		-- deletando o bloco caso ele exista" + CVSStructure.quebraLinha);
                            strOutScripts.append("		delete from sap_interface_blocks b where b.block_name = '" + rsSapMapeamento.getString("BLOCK_NAME") + "';" + CVSStructure.quebraLinha);
                            strOutScripts.append("		" + CVSStructure.quebraLinha);
                            strOutScripts.append("		commit;" + CVSStructure.quebraLinha);
                            strOutScripts.append("    exception" + CVSStructure.quebraLinha);
                            strOutScripts.append("        when NO_DATA_FOUND then" + CVSStructure.quebraLinha);
                            strOutScripts.append("			 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                            strOutScripts.append("             dbms_output.put_line('An error was encountered NO_DATA_FOUND: " + rsSapMapeamento.getString("BLOCK_NAME") + " on sap_interface_blocks - ' || err_msg);" + CVSStructure.quebraLinha);
                            strOutScripts.append("		when OTHERS then" + CVSStructure.quebraLinha);
                            strOutScripts.append("			 err_msg := substr(SQLERRM, 1, 200);" + CVSStructure.quebraLinha);
                            strOutScripts.append("			 dbms_output.put_line('An error was encountered: When deleting " + rsSapMapeamento.getString("BLOCK_NAME") + " on sap_interface_blocks - ' || err_msg);" + CVSStructure.quebraLinha);
                            strOutScripts.append("	end;" + CVSStructure.quebraLinha);

                            if(psSapBlocks == null){
                                if(system.toUpperCase().equals("INOUT")){
                                    psSapBlocks = ConnectionInout.getConnection().prepareStatement("select * from sap_interface_blocks b where b.block_name = ?");
                                }else{
                                    psSapBlocks = ConnectionIntegracao.getConnection().prepareStatement("select * from sap_interface_blocks b where b.block_name =  ?");
                                }
                            }
                            psSapBlocks.setObject(1, rsSapMapeamento.getString("BLOCK_NAME"));
                            strOutScripts.append(new DataTableLayout(system,
                                                                    "sap_interface_blocks",
                                                                     psSapBlocks).create());

                            strOutScripts.append("	commit;" + CVSStructure.quebraLinha);
                            strOutScripts.append(CVSStructure.quebraLinha);
                        }

                        // Bloco para gerar os insert do int mapemaneto tabela
                        if(psSapMapeamentoTabela == null){
                            if(system.toUpperCase().equals("INOUT")){
                                psSapMapeamentoTabela = ConnectionInout.getConnection().prepareStatement("select * from sap_interface_tables where table_id = ?");
                            }else{
                                psSapMapeamentoTabela = ConnectionIntegracao.getConnection().prepareStatement("select * from sap_interface_tables where table_id = ?");
                            }
                        }
                        psSapMapeamentoTabela.setObject(1, rsSapMapeamento.getString("TABLE_ID"));
                        strOutScripts.append(new DataTableLayout(system,
                                                                "sap_interface_tables",
                                                                 psSapMapeamentoTabela).create());


                        strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsSapMapeamento.getString("BLOCK_NAME")  + CVSStructure.quebraLinha );
                        strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + CVSStructure.quebraLinha);

                        if(psSapMapeamentoColuna == null){
                            if(system.toUpperCase().equals("INOUT")){
                                psSapMapeamentoColuna = ConnectionInout.getConnection().prepareStatement(sbSapMapeamentoColuna.toString());
                            }else{
                                psSapMapeamentoColuna = ConnectionIntegracao.getConnection().prepareStatement(sbSapMapeamentoColuna.toString());
                            }
                        }

                        // Bloco para gerar os inserts da int mapeamento coluna
                        psSapMapeamentoColuna.setObject(1, rsSapMapeamento.getString("TABLE_ID"));
                        strOutScripts.append(new DataTableLayout(system,
                                                                "sap_interface_columns",
                                                                 psSapMapeamentoColuna).create());

                        strOutScripts.append(CVSStructure.quebraLinha);
                        strOutScripts.append("commit;" + CVSStructure.quebraLinha);

                        strOutScripts.append("end;" + CVSStructure.quebraLinha);
                        strOutScripts.append("/" + CVSStructure.quebraLinha);
                        strOutScripts.append(CVSStructure.quebraLinha);

                        strOutScripts.append("--habilitando as triggers" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables enable all triggers;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_blocks enable all triggers;" + CVSStructure.quebraLinha);
                        strOutScripts.append("" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_blocks enable constraint pk_sap_if_blocks;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables enable constraint fk_sap_interface_tables;" + CVSStructure.quebraLinha);
                        strOutScripts.append("alter table sap_interface_tables enable constraint fk_sap_interface_block;" + CVSStructure.quebraLinha);

                        if(strOutScripts != null && !strOutScripts.toString().equals("")){
                            fileScripts.createNewFile();
                            fwScripts = new FileWriter(fileScripts, false);
                            fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                            fwScripts.close();

                            CVSStructure.nTotalSapMapeamento++;
                            CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                        }
                    }catch(IOException ioex){
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }

                }
            }else{
                SfwLogger.saveLog("Não encontrado a tabela SAP_INTERFACE_TABLES em " + system);
            }
        }catch(SQLException sqlex){
            SfwLogger.saveLog(sqlex.getClass().toString(), sqlex.getStackTrace());
            sqlex.printStackTrace();
        }finally{
            try{
                if(rsSapMapeamento != null) rsSapMapeamento.close();
                if(rsPermissaoTabela != null) rsPermissaoTabela.close();
                if(psPermissaoTabela != null) psPermissaoTabela.close();
                if(rsPermissaoTabela != null) rsPermissaoTabela.close();
                if(rsSistemaInterfaceDaTabela != null) rsSistemaInterfaceDaTabela.close();
                if(rsSistemaInterfaceDaTabela != null) rsSistemaInterfaceDaTabela.close();
            }catch(SQLException sqlex){
                SfwLogger.log(sqlex);
                sqlex.printStackTrace();
            }
        }
   }

    public String getIdInterface(){
            return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    public String getNomePasta(String tipo){
        String pasta = "";

        if(tipo.equals("") || CVSStructure.chNomePasta.equals("N")){
            pasta = getIdInterface();
        }else if (tipo.equals("IN")){
            pasta = getIdSistema() + "_in_" + getIdInterface();
        }else if (tipo.equals("OUT")){
            pasta = getIdSistema() + "_out_" + getIdInterface();
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
