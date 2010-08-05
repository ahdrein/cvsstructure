package cvsstructure.objects;

import cvsstructure.util.Diretorio;
import cvsstructure.util.DataTableLayout;
import cvsstructure.util.PrepararConsultas;
import cvsstructure.CVSStructure;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.Arquivo;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andrein
 */
public class IntMapeamento {

    private String idInterface = "";
    private String idSistema = "";
    private static PreparedStatement psIntMapeamento = null;
    private static ResultSet rsIntMapeamento = null;
    private static PreparedStatement psIntMapeamentoColuna = null;
    private static ResultSet rsIntMapeamentoColuna = null;
    private static PreparedStatement psPermissaoTabela = null;
    private static ResultSet rsPermissaoTabela = null;
    private static PreparedStatement psSistemaInterfaceDaTabela = null;
    private static ResultSet rsInterfaceDaTabela = null;
    private static PreparedStatement psCountPermissaoTabela = null;
    private static ResultSet rsCountPermissaoTabela = null;
    private static PreparedStatement psDadosInterface = null;
    private static ResultSet rsDadosInterface = null;

    public IntMapeamento(String system, CVSStructure cvsStructure) throws IOException {

        // Informações InOut
        String executavel = "";
        String tipoInterface = "";

        String descricao = "";
        String userName = "";
        String tempoMedio = "";
        String executavelCompl = "";

        String fileNameScripts = "";
        String fileName = "";

        Arquivo fileScripts;
        StringBuilder strOutScripts;

        StringBuilder sbIntMapeamento = new StringBuilder();
        sbIntMapeamento.append("select * from int_mapeamento_layout");

        StringBuilder sbIntMapeamentoColuna = new StringBuilder();
        sbIntMapeamentoColuna.append("select * from int_mapeamento_coluna where id = ?");

        StringBuilder sbPermissaoTabela = new StringBuilder();
        sbPermissaoTabela.append("select * from permissao_tabela where table_name = ?");

        StringBuilder sbCountPermissaoTabela = new StringBuilder();
        sbCountPermissaoTabela.append("select count(*) TOTAL from permissao_tabela where table_name = ?");

        StringBuilder sbSistemaInterfaceDaTabela = new StringBuilder();
        sbSistemaInterfaceDaTabela.append("select distinct it.* ");
        sbSistemaInterfaceDaTabela.append("from permissao_tabela ta,");
        sbSistemaInterfaceDaTabela.append("     sistema_interface it");
        sbSistemaInterfaceDaTabela.append(" where ta.table_name like '%' || upper( ? ) ||'%'");
        sbSistemaInterfaceDaTabela.append(" and it.id_interface = ta.id_interface");

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
            if (PrepararConsultas.existObject("INT_MAPEAMENTO_LAYOUT", system) >= 1) {

                if (system.toUpperCase().equals("INOUT")) {
                    psIntMapeamento = ConnectionInout.getConnection().prepareStatement(sbIntMapeamento.toString());
                } else {
                    psIntMapeamento = ConnectionIntegracao.getConnection().prepareStatement(sbIntMapeamento.toString());
                }
                rsIntMapeamento = psIntMapeamento.executeQuery();
                while (rsIntMapeamento.next()) {

                    psPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbPermissaoTabela.toString());
                    psPermissaoTabela.setString(1, rsIntMapeamento.getString("LAYOUT"));
                    rsPermissaoTabela = psPermissaoTabela.executeQuery();

                    while (rsPermissaoTabela.next()) {
                        idInterface = rsPermissaoTabela.getString("ID_INTERFACE");

                        psCountPermissaoTabela = ConnectionInout.getConnection().prepareStatement(sbCountPermissaoTabela.toString());
                        psCountPermissaoTabela.setString(1, rsIntMapeamento.getString("LAYOUT"));
                        rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
                        rsCountPermissaoTabela.next();
                        int nTotPer = rsCountPermissaoTabela.getInt("TOTAL");

                        boolean sisFlag = true;
                        if (nTotPer > 1) {
                            psSistemaInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(sbSistemaInterfaceDaTabela.toString());
                            psSistemaInterfaceDaTabela.setString(1, rsIntMapeamento.getString("LAYOUT"));
                            rsInterfaceDaTabela = psSistemaInterfaceDaTabela.executeQuery();
                            rsInterfaceDaTabela.next();

                            if (!CVSStructure.id_sistema_it.equals("")) {
                                if (CVSStructure.id_sistema_it.equals(rsInterfaceDaTabela.getString("ID_SISTEMA"))) {
                                    sisFlag = false;
                                }
                            }
                        }

                        fileName = rsIntMapeamento.getString("LAYOUT").toLowerCase() + ".sql";
                        if (idInterface == null || idInterface.equals("") || sisFlag) {
                            fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento\\" + fileName;
                        } else {
                            if (psDadosInterface == null) {
                                psDadosInterface = ConnectionInout.getConnection().prepareStatement(sbDadosInterface.toString());
                            }

                            psDadosInterface.setString(1, idInterface);
                            rsDadosInterface = psDadosInterface.executeQuery();
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
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                            } else if (tipoInterface.trim().equals("E")) {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                            } else {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + idSistema + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                            }
                        }

                        try {
                            fileScripts = new Arquivo(fileNameScripts);
                            if (!fileScripts.exists()) {
                                CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);
                            }
                            strOutScripts = new StringBuilder();

                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/
                            if (CVSStructure.chConexaoPorArquivos.equals("S")) {
                                if (system.toUpperCase().equals("INOUT")) {
                                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                                } else {
                                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                                }
                            }

                            strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("/*###############################" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append(rsIntMapeamento.getString("LAYOUT") + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("###############################*/" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("--garante que não existirão registros duplicados" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("declare" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("    n_id   number := null;" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("begin" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("    select l.id into n_id" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("      from int_mapeamento_layout l " + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("     where layout = '" + rsIntMapeamento.getString("LAYOUT") + "' " + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("       and api = '" + rsIntMapeamento.getString("API") + "'; " + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("    delete from int_mapeamento_coluna c where c.id = n_id;" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("    delete from int_mapeamento_layout l where l.id = n_id;" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("    EXCEPTION" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("        WHEN NO_DATA_FOUND THEN" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("             null;" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("end;" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("/" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("insert into int_mapeamento_layout (id, layout, api, processa, ordem_processamento, tipo)" + CVSStructure.QUEBRA_LINHA);
                            //strOutScripts.append("values ( seq_int_mapeamento_layout.nextval, '" + rsIntMapeamento.getString("LAYOUT") + "', '" + rsIntMapeamento.getString("API") + "', '" + rsIntMapeamento.getString("PROCESSA") + "', "+rsIntMapeamento.getString("ORDEM_PROCESSAMENTO")+", '"+rsIntMapeamento.getString("TIPO")+"');" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("values ( " + rsIntMapeamento.getString("ID") + ", '" + rsIntMapeamento.getString("LAYOUT") + "', '" + rsIntMapeamento.getString("API") + "', '" + rsIntMapeamento.getString("PROCESSA") + "', " + rsIntMapeamento.getString("ORDEM_PROCESSAMENTO") + ", '" + rsIntMapeamento.getString("TIPO") + "');" + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsIntMapeamento.getString("API") + CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + CVSStructure.QUEBRA_LINHA);

                            if (psIntMapeamentoColuna == null) {
                                if (system.toUpperCase().equals("INOUT")) {
                                    psIntMapeamentoColuna = ConnectionInout.getConnection().prepareStatement(sbIntMapeamentoColuna.toString());
                                } else {
                                    psIntMapeamentoColuna = ConnectionIntegracao.getConnection().prepareStatement(sbIntMapeamentoColuna.toString());
                                }
                            }
                            psIntMapeamentoColuna.setObject(1, rsIntMapeamento.getString("ID"));
                            //rsIntMapeamentoColuna = psIntMapeamentoColuna.executeQuery();
                            //while(rsIntMapeamentoColuna.next()){
                            //    strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            //    strOutScripts.append("insert into int_mapeamento_coluna (id, api_coluna, layout_coluna,layout_formula)" + CVSStructure.QUEBRA_LINHA);
                            //    if(rsIntMapeamentoColuna.getString("LAYOUT_FORMULA")==null || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("") || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("null")){
                            //         strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '');      " + CVSStructure.QUEBRA_LINHA);
                            //     }else{
                            //         strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_FORMULA") + "');      " + CVSStructure.QUEBRA_LINHA);
                            //     }
                            // }

                            strOutScripts.append(new DataTableLayout(system,
                                    "int_mapeamento_coluna",
                                    psIntMapeamentoColuna).create());

                            strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                            strOutScripts.append("commit;" + CVSStructure.QUEBRA_LINHA);


                            if (strOutScripts != null && !strOutScripts.toString().equals("")) {
                                fileScripts.saveArquivo(strOutScripts);

                                Estatisticas.nTotalIntMapeamento++;
                                CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                            }
                        } catch (IOException ioex) {
                            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                            ioex.printStackTrace();
                        }

                    }
                }
            } else {
                SfwLogger.saveLog("Não encontrado a tabela INT_MAPEAMENTO_LAYOUT em " + system);
            }
        } catch (SQLException sqlex) {
            SfwLogger.saveLog(sqlex.getClass().toString(), sqlex.getStackTrace());
            sqlex.printStackTrace();
        } finally {
            try {
                if (psIntMapeamento != null) {
                    psIntMapeamento.close();
                }
                if (psIntMapeamentoColuna != null) {
                    psIntMapeamentoColuna.close();
                }
                if (psPermissaoTabela != null) {
                    psPermissaoTabela.close();
                }
                if (rsPermissaoTabela != null) {
                    rsPermissaoTabela.close();
                }
                if (rsInterfaceDaTabela != null) {
                    rsInterfaceDaTabela.close();
                }
                if (rsInterfaceDaTabela != null) {
                    rsInterfaceDaTabela.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.saveLog(sqlex.getClass().toString(), sqlex.getStackTrace());
                sqlex.printStackTrace();
            }
        }
    }

    public String getIdInterface() {
        return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    public String getNomePasta(String tipo) {
        String pasta = "";

        if (tipo.equals("") || CVSStructure.chNomePasta.equals("N")) {
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
