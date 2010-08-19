/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import static cvsstructure.CVSStructure.chNomePasta;
import cvsstructure.util.Diretorio;
import cvsstructure.util.DataTableLayout;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.PrepararConsultas;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;

/**
 *
 * @author andrein
 */
public class SapMapeamento implements Runnable {

    private String idInterface = "";
    private String idSistema = "";
    private String system;
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

    public SapMapeamento(String system) {
        this.system = system;
    }

    @Override
    public void run() {

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

        CvsStructureFile fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;

        StringBuilder sbSapMapeamento = new StringBuilder();
        sbSapMapeamento.append("select * from sap_interface_tables");

        StringBuilder sbSapMapeamentoColuna = new StringBuilder();
        sbSapMapeamentoColuna.append("select * from sap_interface_columns where table_id = ?");

        //
        StringBuilder sbPermissaoTabela = new StringBuilder();
        sbPermissaoTabela.append("select * from permissao_tabela where table_name = ?");

        StringBuilder sbCountPermissaoTabela = new StringBuilder();
        sbCountPermissaoTabela.append("select count(*) TOTAL from permissao_tabela where table_name = ?");

        StringBuilder sbInterfaceDaTabela = new StringBuilder();
        sbInterfaceDaTabela.append("select distinct it.* ");
        sbInterfaceDaTabela.append("from permissao_tabela ta,");
        sbInterfaceDaTabela.append("     sistema_interface it");
        sbInterfaceDaTabela.append(" where ta.table_name like '%' || upper( ? ) ||'%'");
        sbInterfaceDaTabela.append(" and it.id_interface = ta.id_interface");

        // Obtendo condições do select para encontrar o as interfaces existentes
        StringBuilder sbDadosInterface = new StringBuilder();
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

        try {
            if (PrepararConsultas.existObject("SAP_INTERFACE_TABLES", system) >= 1) {

                if (system.toUpperCase().equals("INOUT")) {
                    psSapMapeamento = ConnectionInout.getConnection().prepareStatement(sbSapMapeamento.toString());
                } else {
                    psSapMapeamento = ConnectionIntegracao.getConnection().prepareStatement(sbSapMapeamento.toString());
                }
                rsSapMapeamento = psSapMapeamento.executeQuery();
                while (rsSapMapeamento.next()) {
                    sap_saida = "";
                    if (psPermissaoTabela == null) {
                        psPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbPermissaoTabela.toString());
                    }
                    psPermissaoTabela.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                    rsPermissaoTabela = psPermissaoTabela.executeQuery();

                    fileNameScripts = "";
                    while (rsPermissaoTabela.next()) {
                        idInterface = rsPermissaoTabela.getString("ID_INTERFACE");

                        if (psCountPermissaoTabela == null) {
                            psCountPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbCountPermissaoTabela.toString());
                        }
                        psCountPermissaoTabela.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                        rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
                        rsCountPermissaoTabela.next();
                        int nTotPer = rsCountPermissaoTabela.getInt("TOTAL");

                        boolean sisFlag = true;
                        if (nTotPer > 1) {
                            if (psSistemaInterfaceDaTabela == null) {
                                psSistemaInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(sbInterfaceDaTabela.toString());
                            }
                            psSistemaInterfaceDaTabela.setString(1, rsSapMapeamento.getString("TABLE_NAME"));
                            rsSistemaInterfaceDaTabela = psSistemaInterfaceDaTabela.executeQuery();
                            rsSistemaInterfaceDaTabela.next();

                            if (!cvsstructure.CVSStructure.id_sistema_it.isEmpty()) {
                                if (cvsstructure.CVSStructure.id_sistema_it.equals(rsSistemaInterfaceDaTabela.getString("ID_SISTEMA"))) {
                                    sisFlag = false;
                                }
                            }
                        }

                        fileName = rsSapMapeamento.getString("IO_TABLE").toLowerCase() + ".sql";
                        if (idInterface == null || idInterface.isEmpty() || !sisFlag) {
                            fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        } else {
                            rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface);
                            while (rsDadosInterface.next()) {
                                executavel = rsDadosInterface.getString("EXECUTAVEL");
                                tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                                idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                                descricao = rsDadosInterface.getString("DESCRICAO");
                                //userNameApp = rsDadosInterface.getString("USERNAME");
                                tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                                executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");
                            }

                            if (tipoInterface.trim().equals("S")) {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                            } else if (tipoInterface.trim().equals("E")) {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                            } else {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                            }

                        }
                    }

                    if (fileNameScripts.isEmpty()) {
                        fileName = rsSapMapeamento.getString("SAP_TABLE").toLowerCase() + ".sql";
                        fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        sap_saida = "S";
                    }

                    try {
                        fileScripts = new CvsStructureFile(fileNameScripts);
                        if (!fileScripts.exists()) {

                            SfwLogger.log("Creating or appending to file " + fileNameScripts);
                            strOutScripts = new StringBuilder();

                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/
                            if (chConexaoPorArquivos.equals("S")) {
                                if (system.toUpperCase().equals("INOUT")) {
                                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                } else {
                                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                }
                            }

                            strOutScripts.append(QUEBRA_LINHA);

                            strOutScripts.append("SET SERVEROUTPUT ON").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);
                            strOutScripts.append("--desabilitando todas as trigger da tabela").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables disable all triggers;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_blocks disable all triggers;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables disable constraint fk_sap_interface_tables;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables disable constraint fk_sap_interface_block;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_blocks disable constraint pk_sap_if_blocks;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);

                            strOutScripts.append("/*###############################").append(QUEBRA_LINHA);
                            strOutScripts.append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(QUEBRA_LINHA);
                            strOutScripts.append("###############################*/").append(QUEBRA_LINHA);
                            strOutScripts.append("--garante que não existirão registros duplicados").append(QUEBRA_LINHA);
                            strOutScripts.append("declare").append(QUEBRA_LINHA);
                            strOutScripts.append("    n_id   number := null;").append(QUEBRA_LINHA);
                            strOutScripts.append("    err_msg 			varchar2(200);").append(QUEBRA_LINHA);
                            strOutScripts.append("    n_parent_table_id	number := null;").append(QUEBRA_LINHA);
                            strOutScripts.append("    n_id_header number := null;").append(QUEBRA_LINHA);
                            strOutScripts.append("begin").append(QUEBRA_LINHA);

                            if (rsSapMapeamento.getString("PARENT_TABLE_ID") != null) {
                                // Bloco para verificar se já existe o mapeamento na int mapeamento
                                strOutScripts.append("-- buscando tabela mapeada").append(QUEBRA_LINHA);
                                strOutScripts.append("    begin").append(QUEBRA_LINHA);
                                strOutScripts.append("        select l.table_id into n_id_header").append(QUEBRA_LINHA);
                                strOutScripts.append("          from sap_interface_tables l ").append(QUEBRA_LINHA);
                                strOutScripts.append("         where sap_table = '").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append("'").append(QUEBRA_LINHA);
                                strOutScripts.append("           and block_name = '").append(rsSapMapeamento.getString("BLOCK_NAME")).append("'; ").append(QUEBRA_LINHA);
                                strOutScripts.append("        exception").append(QUEBRA_LINHA);
                                strOutScripts.append("            when NO_DATA_FOUND then").append(QUEBRA_LINHA);
                                strOutScripts.append("                 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                                strOutScripts.append("                 dbms_output.put_line('An error was encountered NO_DATA_FOUND: ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" - ' || err_msg);").append(QUEBRA_LINHA);
                                strOutScripts.append("            when OTHERS then").append(QUEBRA_LINHA);
                                strOutScripts.append("            	 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                                strOutScripts.append("            	 dbms_output.put_line('An error was encountered: ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" - ' || err_msg);").append(QUEBRA_LINHA);
                                strOutScripts.append("    end;").append(QUEBRA_LINHA);
                                strOutScripts.append(QUEBRA_LINHA);
                            }

                            // Bloco para verificar se já existe o mapeamento na int mapeamento
                            strOutScripts.append("    begin").append(QUEBRA_LINHA);
                            strOutScripts.append("        -- buscando tabela mapeada").append(QUEBRA_LINHA);
                            strOutScripts.append("        select l.table_id, l.parent_table_id into n_id, n_parent_table_id").append(QUEBRA_LINHA);
                            strOutScripts.append("          from sap_interface_tables l ").append(QUEBRA_LINHA);
                            strOutScripts.append("         where sap_table = '").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append("'").append(QUEBRA_LINHA);
                            strOutScripts.append("           and block_name = '").append(rsSapMapeamento.getString("BLOCK_NAME")).append("'; ").append(QUEBRA_LINHA);
                            strOutScripts.append("        exception").append(QUEBRA_LINHA);
                            strOutScripts.append("            when NO_DATA_FOUND then").append(QUEBRA_LINHA);
                            strOutScripts.append("                 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                            strOutScripts.append("                 dbms_output.put_line('An error was encountered NO_DATA_FOUND: ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" - ' || err_msg);").append(QUEBRA_LINHA);
                            strOutScripts.append("            when OTHERS then").append(QUEBRA_LINHA);
                            strOutScripts.append("            	 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                            strOutScripts.append("            	 dbms_output.put_line('An error was encountered: ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" - ' || err_msg);").append(QUEBRA_LINHA);
                            strOutScripts.append("    end;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);

                            // Bloco para excluir os registros da int mapeamento
                            strOutScripts.append("    if n_id != 0 then").append(QUEBRA_LINHA);
                            strOutScripts.append("        begin").append(QUEBRA_LINHA);
                            strOutScripts.append("            -- deletando as tabelas e colunas").append(QUEBRA_LINHA);
                            strOutScripts.append("            delete from sap_interface_columns c where c.table_id = n_id;").append(QUEBRA_LINHA);
                            strOutScripts.append("            delete from sap_interface_tables l where l.table_id = n_id;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);

                            // deletando os filhos encontrados
                            if (rsSapMapeamento.getString("PARENT_TABLE_ID") == null) {
                                strOutScripts.append("            -- deletando os filhos encontrados").append(QUEBRA_LINHA);
                                strOutScripts.append("            for tables_sap in (select table_id from sap_interface_tables l where l.parent_table_id = n_parent_table_id) ").append(QUEBRA_LINHA);
                                strOutScripts.append("            loop").append(QUEBRA_LINHA);
                                strOutScripts.append("            	delete from sap_interface_columns c where c.parent_table_id = tables_sap.table_id;").append(QUEBRA_LINHA);
                                strOutScripts.append("            end loop;").append(QUEBRA_LINHA);
                                strOutScripts.append("            delete from sap_interface_tables l where l.parent_table_id = n_parent_table_id;").append(QUEBRA_LINHA);
                                strOutScripts.append(QUEBRA_LINHA);
                                strOutScripts.append("            commit;").append(QUEBRA_LINHA);
                            }

                            strOutScripts.append(QUEBRA_LINHA);
                            strOutScripts.append("            exception").append(QUEBRA_LINHA);
                            strOutScripts.append("                when NO_DATA_FOUND then").append(QUEBRA_LINHA);
                            strOutScripts.append("                     err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                            strOutScripts.append("                     dbms_output.put_line('An error was encountered NO_DATA_FOUND: ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" on sap_interface_tables - ' || err_msg);").append(QUEBRA_LINHA);
                            strOutScripts.append("                when OTHERS then").append(QUEBRA_LINHA);
                            strOutScripts.append("                    	 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                            strOutScripts.append("                	 dbms_output.put_line('An error was encountered: When deleting ").append(sap_saida.equals("S") ? rsSapMapeamento.getString("SAP_TABLE") : rsSapMapeamento.getString("IO_TABLE")).append(" on sap_interface_tables - ' || err_msg);").append(QUEBRA_LINHA);
                            strOutScripts.append("        end;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);
                            strOutScripts.append("    else").append(QUEBRA_LINHA);
                            strOutScripts.append("    	-- encontrando o maior numero do table_id para a primeira inserssão").append(QUEBRA_LINHA);
                            strOutScripts.append("    	select max(l.table_id)+1 into n_id from sap_interface_tables l;").append(QUEBRA_LINHA);
                            strOutScripts.append("    end if;").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);

                            // Interfaces de Saida Genérica SAP
                            // Buscando bloco
                            if ((rsSapMapeamento.getString("BLOCK_NAME") != null)
                                    && (rsSapMapeamento.getString("PARENT_TABLE_ID") == null)) {

                                strOutScripts.append("	begin").append(QUEBRA_LINHA);
                                strOutScripts.append("		-- deletando o bloco caso ele exista").append(QUEBRA_LINHA);
                                strOutScripts.append("		delete from sap_interface_blocks b where b.block_name = '").append(rsSapMapeamento.getString("BLOCK_NAME")).append("';").append(QUEBRA_LINHA);
                                strOutScripts.append("		").append(QUEBRA_LINHA);
                                strOutScripts.append("		commit;").append(QUEBRA_LINHA);
                                strOutScripts.append("    exception").append(QUEBRA_LINHA);
                                strOutScripts.append("        when NO_DATA_FOUND then").append(QUEBRA_LINHA);
                                strOutScripts.append("			 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                                strOutScripts.append("             dbms_output.put_line('An error was encountered NO_DATA_FOUND: ").append(rsSapMapeamento.getString("BLOCK_NAME")).append(" on sap_interface_blocks - ' || err_msg);").append(QUEBRA_LINHA);
                                strOutScripts.append("		when OTHERS then").append(QUEBRA_LINHA);
                                strOutScripts.append("			 err_msg := substr(SQLERRM, 1, 200);").append(QUEBRA_LINHA);
                                strOutScripts.append("			 dbms_output.put_line('An error was encountered: When deleting ").append(rsSapMapeamento.getString("BLOCK_NAME")).append(" on sap_interface_blocks - ' || err_msg);").append(QUEBRA_LINHA);
                                strOutScripts.append("	end;").append(QUEBRA_LINHA);

                                if (psSapBlocks == null) {
                                    if (system.toUpperCase().equals("INOUT")) {
                                        psSapBlocks = ConnectionInout.getConnection().prepareStatement("select * from sap_interface_blocks b where b.block_name = ?");
                                    } else {
                                        psSapBlocks = ConnectionIntegracao.getConnection().prepareStatement("select * from sap_interface_blocks b where b.block_name =  ?");
                                    }
                                }
                                psSapBlocks.setObject(1, rsSapMapeamento.getString("BLOCK_NAME"));
                                strOutScripts.append(new DataTableLayout(system,
                                        "sap_interface_blocks",
                                        psSapBlocks).create());

                                strOutScripts.append("	commit;").append(QUEBRA_LINHA);
                                strOutScripts.append(QUEBRA_LINHA);
                            }

                            // Bloco para gerar os insert do int mapemaneto tabela
                            if (psSapMapeamentoTabela == null) {
                                if (system.toUpperCase().equals("INOUT")) {
                                    psSapMapeamentoTabela = ConnectionInout.getConnection().prepareStatement("select * from sap_interface_tables where table_id = ?");
                                } else {
                                    psSapMapeamentoTabela = ConnectionIntegracao.getConnection().prepareStatement("select * from sap_interface_tables where table_id = ?");
                                }
                            }
                            psSapMapeamentoTabela.setObject(1, rsSapMapeamento.getString("TABLE_ID"));
                            strOutScripts.append(new DataTableLayout(system,
                                    "sap_interface_tables",
                                    psSapMapeamentoTabela).create());


                            strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag ").append(rsSapMapeamento.getString("BLOCK_NAME")).append(QUEBRA_LINHA);
                            strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT").append(QUEBRA_LINHA);

                            if (psSapMapeamentoColuna == null) {
                                if (system.toUpperCase().equals("INOUT")) {
                                    psSapMapeamentoColuna = ConnectionInout.getConnection().prepareStatement(sbSapMapeamentoColuna.toString());
                                } else {
                                    psSapMapeamentoColuna = ConnectionIntegracao.getConnection().prepareStatement(sbSapMapeamentoColuna.toString());
                                }
                            }

                            // Bloco para gerar os inserts da int mapeamento coluna
                            psSapMapeamentoColuna.setObject(1, rsSapMapeamento.getString("TABLE_ID"));
                            strOutScripts.append(new DataTableLayout(system,
                                    "sap_interface_columns",
                                    psSapMapeamentoColuna).create());

                            strOutScripts.append(QUEBRA_LINHA);
                            strOutScripts.append("commit;").append(QUEBRA_LINHA);

                            strOutScripts.append("end;").append(QUEBRA_LINHA);
                            strOutScripts.append("/").append(QUEBRA_LINHA);
                            strOutScripts.append(QUEBRA_LINHA);

                            strOutScripts.append("--habilitando as triggers").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables enable all triggers;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_blocks enable all triggers;").append(QUEBRA_LINHA);
                            strOutScripts.append("").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_blocks enable constraint pk_sap_if_blocks;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables enable constraint fk_sap_interface_tables;").append(QUEBRA_LINHA);
                            strOutScripts.append("alter table sap_interface_tables enable constraint fk_sap_interface_block;").append(QUEBRA_LINHA);

                            if (strOutScripts != null && !strOutScripts.toString().isEmpty()) {
                                fileScripts.saveArquivo(strOutScripts);
                                Estatisticas.nTotalSapMapeamento++;
                                SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                            }
                        }
                    } catch (IOException ioex) {
                        SfwLogger.log("File " + fileNameScripts + " was error generated.");
                        SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }

                }
            } else {
                SfwLogger.log("Não encontrado a tabela SAP_INTERFACE_TABLES em " + system);
            }
        } catch (SQLException sqlex) {
            SfwLogger.debug(sqlex.getClass().toString(), sqlex.getStackTrace());
            sqlex.printStackTrace();
        } finally {
            try {
                if (rsSapMapeamento != null) {
                    rsSapMapeamento.close();
                }
                if (rsPermissaoTabela != null) {
                    rsPermissaoTabela.close();
                }
                if (psPermissaoTabela != null) {
                    psPermissaoTabela.close();
                }
                if (rsPermissaoTabela != null) {
                    rsPermissaoTabela.close();
                }
                if (rsSistemaInterfaceDaTabela != null) {
                    rsSistemaInterfaceDaTabela.close();
                }
                if (rsSistemaInterfaceDaTabela != null) {
                    rsSistemaInterfaceDaTabela.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.log(sqlex.getLocalizedMessage());
                sqlex.printStackTrace();
            }
        }
    }

    public String getIdInterface() {
        return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    public String getNomePasta(String tipo) {
        String pasta = "";

        if (tipo.isEmpty() || chNomePasta.equals("N")) {
            pasta = getIdInterface();
        } else if (tipo.equals("IN")) {
            pasta = getIdSistema() + "_in_" + getIdInterface();
        } else if (tipo.equals("OUT")) {
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
