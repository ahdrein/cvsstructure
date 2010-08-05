package cvsstructure;

import cvsstructure.model.Cliente;
import cvsstructure.gui.JFrameCVS;
import cvsstructure.database.ConnectionInout;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import cvsstructure.objects.IntMapeamento;
import cvsstructure.objects.SapMapeamento;
import cvsstructure.objects.Sequence;
import cvsstructure.objects.Sistemas;
import cvsstructure.objects.Synonyms;
import cvsstructure.objects.TabInterfaces;
import cvsstructure.objects.Views;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Interface;
import cvsstructure.objects.ArquivosExternos;
import cvsstructure.objects.ArquivosExternosNaoGerados;
import cvsstructure.util.Diretorio;
import cvsstructure.objects.ExportarArquivosExternos;
import cvsstructure.objects.Interfaces;
import cvsstructure.objects.ObjetosIntegracao;
import cvsstructure.objects.TabInterfaceSemPermissao;
import cvsstructure.gui.SfwValidaScripts;

public class CVSStructure {

    // public String sQuebraLinha = System.getProperty("line.separator");
    public static final String QUEBRA_LINHA = "\r\n";
    public String s_User;
    public String s_Pass;
    public String s_Conn;
    public String s_ItUser;
    public static String s_ItUser2;
    public String s_ItPass;
    public static String chNomePasta;
    public static String chConexaoPorArquivos;
    public static String chScriptsSemVinculoInterface;
    private Object[] selectInterfaces;

    /* Selects Utilizados */
    private String selectGerarArquivosExternos;
    private String selectExportarArquivosExternos;
    private String selectUsers;
    private String sistema;
    private String interfaceDaTabela;
    private String sessionSchema;
    private String role;
    // Variaveis
    public static String id_sistema_it = "";
    private String fileName = "";
    private String fileNameScripts = "";
    private Clob clob;
    public static String sDebug = "N";
    private boolean flag = true;
    private String caminhoGeracao;
    private String sSynonymsAll = "";
    // PreparedStatement
    private PreparedStatement psInterfaces = null;
    private ResultSet rsInterfaces = null;
    private PreparedStatement psExportarArquivosExternos = null;
    private PreparedStatement psUsersUser = null;
    private ResultSet rsUsersUser = null;

    private ResultSet rsPermissaoTabela = null;
    

    private PreparedStatement psSistema = null;
    private ResultSet rsSistema = null;
    private PreparedStatement psInterfaceDaTabela = null;
    private ResultSet rsInterfaceDaTabela = null;

    
    private ResultSet rsSqlInterface = null;
    private ResultSet rsBatInterface = null;
    private ResultSet rsDadosInterface = null;
    public static JTextArea textAreaCVS;
    private static JFrameCVS jframe;

    // Imprime Mensagens na Tela e coloca mensagem no log
    public static void logMessage(String p_msg) {
        //System.out.println(p_msg);
        if (CVSStructure.jframe != null) {
            CVSStructure.jframe.setTextArea(p_msg + QUEBRA_LINHA);
        }
    }

    private void intialize() throws SQLException, IOException {

        //System.getProperty("user.dir");
        setCaminhoGeracao(CVSStructure.jframe.getTxCaminhaGeracao());
        if (getCaminhoGeracao().equals(".\\")) {
            Diretorio.path = new File(".").getCanonicalPath();
        } else {
            if (getCaminhoGeracao().substring(getCaminhoGeracao().length() - 1).equals("\\")) {
                Diretorio.path = getCaminhoGeracao().substring(0, getCaminhoGeracao().length() - 1);
            } else {
                Diretorio.path = getCaminhoGeracao();
            }
        }

        this.selectUsers = "select username from user_users";


        this.sistema = "select * from sistema where user_oracle like '%'|| ? || '%'";


        this.interfaceDaTabela = "select distinct it.* "
                + "from permissao_tabela ta,"
                + "     sistema_interface it"
                + " where ta.table_name like '%' || upper( ? ) ||'%'"
                + " and it.id_interface = ta.id_interface";



        this.selectExportarArquivosExternos = "select NOME_ARQUIVO,"
                + "PATH_RELATIVO,"
                + "DESCRICAO,"
                + "CONTEUDO"
                + " from arquivo_externo"
                + " where arquivo_externo.nome_arquivo like '%'|| ? || '%'";

        // Obtendo condições do select para encontrar o as interfaces existentes
        StringBuilder sbAllInterfaces = new StringBuilder();
        sbAllInterfaces.append("select * from (");
        sbAllInterfaces.append("select replace( replace( replace( substr(interfaces.executavel, instr(interfaces.executavel, '\\')+1, length(interfaces.executavel)), '#IDENT#INTERFACES\\SAP\\', ''), '#IDENT#INTERFACES\\', '') , '#IDENT#INTEGRACAO\\', '') executavel,");
        sbAllInterfaces.append("interfaces.id_interface,");
        sbAllInterfaces.append("interfaces.tipo_interface,");
        sbAllInterfaces.append("nvl(sistema_interface.id_sistema, 'sfw') id_sistema,");
        sbAllInterfaces.append("interfaces.descricao,");
        sbAllInterfaces.append("interfaces.username,");
        sbAllInterfaces.append("interfaces.tempo_medio,");
        sbAllInterfaces.append("interfaces.executavelCompl executavelCompl,");
        sbAllInterfaces.append("interfaces.interfere_proc_dir");
        sbAllInterfaces.append(" from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, ");
        sbAllInterfaces.append("id_interface, ");
        sbAllInterfaces.append("tipo_interface, ");
        sbAllInterfaces.append("descricao, ");
        sbAllInterfaces.append("username, ");
        sbAllInterfaces.append("tempo_medio, ");
        sbAllInterfaces.append("executavel executavelCompl, interfere_proc_dir from interfaces ");
        sbAllInterfaces.append("where executavel like '%.BAT%' ");
        sbAllInterfaces.append("or executavel like '%.bat%') interfaces,");
        sbAllInterfaces.append(" sistema_interface");
        sbAllInterfaces.append(" where interfaces.id_interface = sistema_interface.id_interface (+)");
        //sbAllInterfaces.append(" order by descricao");
        sbAllInterfaces.append("union all ");
        sbAllInterfaces.append("select ");
        sbAllInterfaces.append("  executavel, ");
        sbAllInterfaces.append("  interfaces.id_interface, ");
        sbAllInterfaces.append("  interfaces.tipo_interface, ");
        sbAllInterfaces.append("  nvl(sistema_interface.id_sistema, 'sfw') id_sistema, ");
        sbAllInterfaces.append("  interfaces.descricao, ");
        sbAllInterfaces.append("  interfaces.username, ");
        sbAllInterfaces.append("  interfaces.tempo_medio, ");
        sbAllInterfaces.append("  executavel executavelCompl, ");
        sbAllInterfaces.append("  interfaces.interfere_proc_dir ");
        sbAllInterfaces.append(" from interfaces interfaces, sistema_interface ");
        sbAllInterfaces.append(" where interfaces.executavel like '%IMPORTADOR_IN_OUT_NEW.EXE%' ");
        sbAllInterfaces.append(" and interfaces.id_interface = sistema_interface.id_interface (+)");
        sbAllInterfaces.append(" ) order by descricao");





        psUsersUser = ConnectionInout.getConnection().prepareStatement(this.selectUsers);
        psExportarArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectExportarArquivosExternos);

        
        

        psInterfaces = ConnectionInout.getConnection().prepareStatement(sbAllInterfaces.toString());




        psInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(this.interfaceDaTabela);


    }

    public ArrayList readInterfaces() throws SQLException, IOException {
        StringBuilder sbAllInterfaces = new StringBuilder();
        sbAllInterfaces.append("select * from interfaces order by descricao");
        PreparedStatement psInterfacesRead = ConnectionInout.getConnection().prepareStatement(sbAllInterfaces.toString());

        ArrayList arrInterfaces = new ArrayList();
        ResultSet rsInterfacesRead = psInterfacesRead.executeQuery();
        while (rsInterfacesRead.next()) {
            arrInterfaces.add(rsInterfacesRead.getString("DESCRICAO"));
        }
        return arrInterfaces;
    }

    public void spoolCVSStruture(ArrayList pTipo, JFrameCVS jframe) throws SQLException, IOException {
        CVSStructure.jframe = (JFrameCVS) jframe;
        SfwLogger.debug("Iniciando validação dos parâmetros ...");
        intialize();
        try {
            rsUsersUser = psUsersUser.executeQuery();
            rsUsersUser.next();
            Cliente.userNameSys = rsUsersUser.getString("USERNAME");

            id_sistema_it = "";
            psSistema = ConnectionInout.getConnection().prepareStatement(this.sistema);
            s_ItUser2 = s_ItUser;
            psSistema.setString(1, s_ItUser.toUpperCase());
            rsSistema = psSistema.executeQuery();
            while (rsSistema.next()) {
                id_sistema_it = rsSistema.getString("ID_SISTEMA");
            }
            rsSistema.close();
            psSistema.close();

            Diretorio diretorios = new Diretorio();
            if (pTipo.contains("D")) {
                SfwLogger.saveLog(QUEBRA_LINHA + "## Criando Diretórios Comuns ##" + QUEBRA_LINHA);
                try {
                    diretorios.criarDiretoriosComuns();
                } catch (Exception ex) {
                    Logger.getLogger(CVSStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Criando Diretórios Comuns ##" + QUEBRA_LINHA);
            }

            if (pTipo.contains("ArquivosExternos")
                    || pTipo.contains("Interfaces")
                    || pTipo.contains("TabelasTemporarias")) {
                rsInterfaces = psInterfaces.executeQuery();
                while (rsInterfaces.next()) {
                    try {
                        boolean flagSelectInterface = false;
                        // Quando  for seleciona pelo menos uma interface
                        if (getSSelectInterfaces().length != 0) {
                            for (int j = 0; j < getSSelectInterfaces().length; j++) {
                                if (rsInterfaces.getString("DESCRICAO").equals(getSSelectInterfaces()[j])) {
                                    flagSelectInterface = true;
                                    break;
                                }
                            }
                        } else {
                            // Quando nehuma interface for seleciona, gerar de todas
                            flagSelectInterface = true;
                        }

                        if (flagSelectInterface) {
                            int nTotInter = new Interface().getCountSistemaPorInterface(rsInterfaces.getString("ID_INTERFACE")).getInt("TOTAL");

                            boolean sisFlag = true;
                            if (nTotInter >= 2) {
                                if (!id_sistema_it.equals("")) {
                                    if (id_sistema_it.equals(rsInterfaces.getString("ID_SISTEMA"))) {
                                        sisFlag = false;
                                    }
                                }
                            }

                            if (sisFlag) {

                                logMessage("*** Building Interfaces select found " + rsInterfaces.getString("ID_INTERFACE") + " - " + rsInterfaces.getString("DESCRICAO") + "...");
                                Interface interfaces = new Interface();
                                interfaces.setIdInterface(rsInterfaces.getString("ID_INTERFACE"));
                                interfaces.setExecutavel(rsInterfaces.getString("EXECUTAVEL"));
                                interfaces.setTipoInterface(rsInterfaces.getString("TIPO_INTERFACE"));
                                interfaces.setIdSistema(rsInterfaces.getString("ID_SISTEMA").toLowerCase());
                                interfaces.setDescricao(rsInterfaces.getString("DESCRICAO"));
                                //sUserNameApp = rsInterfaces.getString("USERNAME");
                                interfaces.setTempoMedio(rsInterfaces.getString("TEMPO_MEDIO"));
                                interfaces.setExecutavelCompl(rsInterfaces.getString("EXECUTAVELCOMPL"));


                                if (rsInterfaces.getString("INTERFERE_PROC_DIR") != null) {
                                    interfaces.setInterfereProcessamentoDireto( rsInterfaces.getString("INTERFERE_PROC_DIR") );
                                }
                                
                                if (pTipo.contains("D")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Criando Diretórios ##" + QUEBRA_LINHA);
                                    diretorios.setTipoInterface(interfaces.getTipoInterface());
                                    diretorios.criarDiretorio();
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Criando Diretórios ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("Interfaces")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Interfaces ##" + QUEBRA_LINHA);
                                    new Interfaces(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Interfaces ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                    new ExportarArquivosExternos(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                                    new ArquivosExternos(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Arquivos Externos  ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("TabelasTemporarias")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Tabelas da Interface ##" + QUEBRA_LINHA);
                                    //this.exportarTabInterface(); //ok
                                    rsPermissaoTabela = new cvsstructure.model.ArquivosExternos().getPermissaoTabelaByIdInterface(interfaces.getIdInterface());
                                    while (rsPermissaoTabela.next()) {
                                        new TabInterfaces(rsPermissaoTabela.getString("TABLE_NAME"), 
                                                          getSSelectInterfaces());
                                    }
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Tabelas da Interface ##" + QUEBRA_LINHA);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logMessage(e.getLocalizedMessage());
                        SfwLogger.saveLog(e.getClass().toString(), e.getStackTrace());
                    }
                }
            }


            try {
                if (pTipo.contains(
                        "ArquivosExternos") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                    new ArquivosExternosNaoGerados(); // ok
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Arquivos Externos ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Synonyms") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Synonyms ##" + QUEBRA_LINHA);
                    Synonyms synonyms = new Synonyms("INOUT", this);
                    synonyms.setName("Synonyms");
                    synonyms.start();
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Synonyms ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "TabelasTemporarias") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Tabelas Sem Permissão##" + QUEBRA_LINHA);
                    new TabInterfaceSemPermissao();
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Tabelas Sem Permissão ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Sistemas") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Sistemas ##" + QUEBRA_LINHA);
                    Sistemas sistemas = new Sistemas(this);
                    sistemas.setName("Sistemas");
                    sistemas.start();
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Sistemas ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Sequences") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Sequences ##" + QUEBRA_LINHA);
                    new Sequence("INOUT");
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Sequences ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Views") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Views ##" + QUEBRA_LINHA);
                    new Views("INOUT");
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Views ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "IntMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando IntMapeamento ##" + QUEBRA_LINHA);
                    new IntMapeamento("INOUT", this);
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando IntMapeamento ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "SapMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando SapMapeamento ##" + QUEBRA_LINHA);
                    new SapMapeamento("INOUT", this);
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando SapMapeamento ##" + QUEBRA_LINHA);
                }
            } catch (Exception ex) {
                logMessage(ex.getLocalizedMessage());
                SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            // Base de Integracao
            if (s_ItUser != null && !s_ItUser.equals("") && !s_ItPass.equals("") && s_ItPass != null) {
                try {
                    if (pTipo.contains("Synonyms") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Synonyms Integração ##" + QUEBRA_LINHA);
                        new Synonyms("INTEGRACAO", this);
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Synonyms Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("IntMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando IntMapeamento Integração ##" + QUEBRA_LINHA);
                        new IntMapeamento("INTEGRACAO", this);
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando IntMapeamento Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("SapMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando SapMapeamento Integração ##" + QUEBRA_LINHA);
                        new SapMapeamento("INTEGRACAO", this);
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando SapMapeamento Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Objetos") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Objetos Integração ##" + QUEBRA_LINHA);
                        new ObjetosIntegracao();
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Objetos Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Sequences") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Sequences Integração ##" + QUEBRA_LINHA);
                        new Sequence("INTEGRACAO");
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Sequences Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Views") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Views Integração ##" + QUEBRA_LINHA);
                        new Views("INTEGRACAO");
                        SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Views Integração ##" + QUEBRA_LINHA);
                    }
                } catch (Exception ex) {
                    logMessage(ex.getLocalizedMessage());
                    SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
                }
            }

            // Deretórios
            try {
                this.removeDiretorio();
                this.validaDiretorio();
            } catch (Exception ex) {
                SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

        } catch (SQLException e) {
            SfwLogger.saveLog(e.getClass().toString(), e.getStackTrace());
            logMessage("Error in the implementation of the interface with Id_Importação ");
        } finally {
            //ConnectionInout.getConnection().close();
            //rsSistema.close();
            //psSistema.close();
            //psCountInterface2.close();
            //rsCountInterface.close();
            //rsInterfaces.close();
        }
    }

    private void validaDiretorio() throws Exception {
        StringBuilder strOutScripts = new StringBuilder();
        SfwValidaScripts valid = new SfwValidaScripts();
        valid.setArqsInstala("N");
        //strOutScripts.append("SPOOL SCRIPT_STATUS.LOG" + QUEBRA_LINHA);
        //strOutScripts.append("@\".\\define.sql\"" + QUEBRA_LINHA);

        // comum em primeiro lugar
        //valid.executar(path + "\\"+Cliente.userNameSys+"\\Scripts\\comum");
        //strOutScripts.append("@\"" + (path + "\\"+Cliente.userNameSys+"\\Scripts\\").replace(path + "\\"+Cliente.userNameSys+"\\Scripts", ".") + "\\comum\\ordem_instalacao.sql\"" + QUEBRA_LINHA);

        strOutScripts.append("--spool instalacao_v01r01p00_txt.log  -- esse comando passou a ser executado no script dispara_script_instalacao.sql" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@define.sql" + QUEBRA_LINHA);
        strOutScripts.append("-- Conectar na base do INOUT para obter a data do inicio do processamento" + QUEBRA_LINHA);
        strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("column \"DATA INICIO\" format A23" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS') AS \"DATA INICIO\" FROM DUAL;" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt INICIO do Processamento" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);

        for (int i = 0; i < Diretorio.dirScriptsValida.size(); i++) {

            // iginora o comum pq ele já foi valido em primeiro lugar
            //if( dirScriptsValida.get(i).toString().indexOf("\\comum\\") == 0 ){
            valid.executar(Diretorio.dirScriptsValida.get(i).toString());
            strOutScripts.append("@\"" + Diretorio.dirScriptsValida.get(i).toString().replace(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts", ".") + "\\ordem_instalacao.sql\"" + QUEBRA_LINHA);
            //}
        }

        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@\".\\processa_grants.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@\".\\processa_grants_sistema.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append("connect_io.sql" + QUEBRA_LINHA);
        strOutScripts.append("@\".\\compila_invalidos.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA);
        strOutScripts.append("connect_it.sql" + QUEBRA_LINHA);
        strOutScripts.append("@\".\\compila_invalidos.sql\"" + QUEBRA_LINHA);

        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("@define.sql" + QUEBRA_LINHA);
        //strOutScripts.append("-- Conectar na base do INOUT para obter a data de fim do processamento" + QUEBRA_LINHA);
        //strOutScripts.append("-- utilizar a mesma base utilizada no inicio do processamento" + QUEBRA_LINHA);
        //strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("SELECT 'Finalizado em: '||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS') AS \"DATA FIM\" FROM DUAL;" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt Fim do Processamento" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("@\"limpa_definicoes.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);

        strOutScripts.append("spool off" + QUEBRA_LINHA);
        strOutScripts.append("exit" + QUEBRA_LINHA);

        valid.copy(new File(".\\definicoes\\define.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "define.sql"));
        valid.copy(new File(".\\definicoes\\Instrucoes.txt"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "Instrucoes.txt"));
        valid.copy(new File(".\\definicoes\\limpa_definicoes.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "limpa_definicoes.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "processa_grants.sql"));
        valid.copy(new File(".\\definicoes\\compila_invalidos.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "compila_invalidos.sql"));
        valid.copy(new File(".\\definicoes\\instala_linux.sh"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "instala_linux.sh"));
        valid.copy(new File(".\\definicoes\\Instala_win.bat"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "Instala_win.bat"));
        valid.copy(new File(".\\definicoes\\dispara_script_instalacao.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "dispara_script_instalacao.sql"));
        valid.copy(new File(".\\definicoes\\connect_io.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "connect_io.sql"));
        valid.copy(new File(".\\definicoes\\connect_it.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "connect_it.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants_sistema.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "processa_grants_sistema.sql"));

        File fileScripts = new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\ordem_instalacao.sql");
        if (!fileScripts.exists()) {
            fileScripts.createNewFile();
        }

        FileWriter fwScripts = new FileWriter(fileScripts, false);
        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
        fwScripts.close();
        logMessage("File .\\" + Cliente.userNameSys + "\\Scripts\\ordem_instalacao.sql was succesfull generated.");

    }

    /**************************************************************************
     * <b>Remover diretórios</b>
     **************************************************************************/
    private void removeDiretorio() throws Exception {
        File diretorio = new File(Diretorio.path + "\\" + Cliente.userNameSys);
        File[] subdiretorios = diretorio.listFiles();
        SfwLogger.saveLog("Removendo diretórios! ");
        for (File subdir : subdiretorios) {
            if (subdir.isDirectory()) {
                //SfwLogger.saveLog(subdir.getName());
                int nArqs = listaSubDir(subdir);
                if (nArqs == 0) {
                    if (subdir.delete()) {
                        //SfwLogger.saveLog("Diretório deletado: " + subdir.getName());
                    }
                }
            }
        }
    }

    /**************************************************************************
     * <b>Listar subDiretórios</b>
     * @param subDir
     **************************************************************************/
    private int listaSubDir(File subDir) throws IOException {
        int nArqs = 0;
        File[] subdiretorios = subDir.listFiles();
        for (File subdir : subdiretorios) {
            if (subdir.isDirectory()) {
                //System.out.println(subdir.getName());
                if (listaSubDir(subdir) == 0) {
                    if (subdir.delete()) {
                        SfwLogger.saveLog("Diretório deletado: " + subdir.getName());
                    }
                }
            } else {
                nArqs += 1;
            }
        }
        return nArqs;
    }

    /**
     * @return the selectInterfaces
     */
    public Object[] getSSelectInterfaces() {
        return selectInterfaces;
    }

    /**
     * @param selectInterfaces the selectInterfaces to set
     */
    public void setSSelectInterfaces(Object[] sSelectInterfaces) {
        this.selectInterfaces = sSelectInterfaces;
    }

    /**
     * @return the caminhoGeracao
     */
    public String getCaminhoGeracao() {
        return caminhoGeracao;
    }

    /**
     * @param caminhoGeracao the caminhoGeracao to set
     */
    public void setCaminhoGeracao(String caminhoGeracao) {
        this.caminhoGeracao = caminhoGeracao;
    }
}
