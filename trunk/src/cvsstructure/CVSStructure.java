package cvsstructure;

import cvsstructure.model.Cliente;
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
import cvsstructure.gui.SfwValidaScriptsFrame;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
public class CVSStructure {

    // public String sQuebraLinha = System.getProperty("line.separator");
    public static final String QUEBRA_LINHA = "\r\n";
    private Cliente cliente;

    public static String chNomePasta;
    public static String chConexaoPorArquivos;
    public static String chScriptsSemVinculoInterface;
    private Object[] selectInterfaces;
    private ArrayList pTipo;

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

    public CVSStructure(){
        
    }
    
    public CVSStructure(Cliente cliente){
        this.cliente = cliente;
    }

    // Imprime Mensagens na Tela e coloca mensagem no log
    /*
    public static void SfwLogger.log(String p_msg) {
        //System.out.println(p_msg);
        if (CVSStructure.jframe != null) {
            CVSStructure.jframe.setTextArea(p_msg + QUEBRA_LINHA);
        }
    }
    */
    private void intialize() throws SQLException, IOException {

        //System.getProperty("user.dir");
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
        
        StringBuilder sbInterfaceDaTabela = new StringBuilder();
        sbInterfaceDaTabela.append("select distinct it.* ");
        sbInterfaceDaTabela.append("from permissao_tabela ta,");
        sbInterfaceDaTabela.append("     sistema_interface it");
        sbInterfaceDaTabela.append(" where ta.table_name like '%' || upper( ? ) ||'%'");
        sbInterfaceDaTabela.append(" and it.id_interface = ta.id_interface");

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
        psInterfaces = ConnectionInout.getConnection().prepareStatement(sbAllInterfaces.toString());
        psInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(sbInterfaceDaTabela.toString());
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
    
    /*
        public void spoolCVSStruture(ArrayList pTipo, CvsStructureFrame jframe, Cliente cliente){
            this.pTipo = pTipo;
            CVSStructure.jframe = jframe;
            this.cliente = cliente;
        }
    */
    public void spoolCVSStruture(ArrayList pTipo) throws SQLException, IOException {
        SfwLogger.debug("Iniciando validação dos parâmetros ...");
        intialize();
        try {
            rsUsersUser = psUsersUser.executeQuery();
            rsUsersUser.next();
            Cliente.userNameSys = rsUsersUser.getString("USERNAME");

            id_sistema_it = "";
            psSistema = ConnectionInout.getConnection().prepareStatement(this.sistema);
            psSistema.setString(1, cliente.getItUser().getUser().toUpperCase());
            rsSistema = psSistema.executeQuery();
            while (rsSistema.next()) {
                id_sistema_it = rsSistema.getString("ID_SISTEMA");
            }
            rsSistema.close();
            psSistema.close();

            Diretorio diretorios = new Diretorio();
            if (pTipo.contains("D")) {
                SfwLogger.debug(QUEBRA_LINHA + "## Criando Diretórios Comuns ##" + QUEBRA_LINHA);
                try {
                    diretorios.criarDiretoriosComuns();
                } catch (Exception ex) {
                    Logger.getLogger(CVSStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Criando Diretórios Comuns ##" + QUEBRA_LINHA);
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
                            int nTotInter = Interface.getInstance().getCountSistemaPorInterface(rsInterfaces.getString("ID_INTERFACE")).getInt("TOTAL");

                            boolean sisFlag = true;
                            if (nTotInter >= 2) {
                                if (!id_sistema_it.isEmpty()) {
                                    if (id_sistema_it.equals(rsInterfaces.getString("ID_SISTEMA"))) {
                                        sisFlag = false;
                                    }
                                }
                            }

                            if (sisFlag) {

                                SfwLogger.log("*** Building Interfaces " + rsInterfaces.getString("ID_INTERFACE") + " - " + rsInterfaces.getString("DESCRICAO") + "...");
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
                                    SfwLogger.debug(QUEBRA_LINHA + "## Criando Diretórios ##" + QUEBRA_LINHA);
                                    diretorios.setTipoInterface(interfaces.getTipoInterface());
                                    diretorios.setInterfaces(interfaces);
                                    diretorios.criarDiretorio();
                                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Criando Diretórios ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("Interfaces")) {
                                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Interfaces ##" + QUEBRA_LINHA);
                                    Interfaces interfacess = new Interfaces(interfaces);
                                    interfacess.start();
                                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Interfaces ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.debug(QUEBRA_LINHA + "## Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                    ExportarArquivosExternos exportarArquivosExternos= new ExportarArquivosExternos();
                                    exportarArquivosExternos.start();
                                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                                    new ArquivosExternos(); // ok
                                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Arquivos Externos  ##" + QUEBRA_LINHA);
                                }
                                if (pTipo.contains("TabelasTemporarias")) {
                                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Tabelas da Interface ##" + QUEBRA_LINHA);
                                    //this.exportarTabInterface(); //ok
                                    rsPermissaoTabela = cvsstructure.model.ArquivosExternos.getInstance().getPermissaoTabelaByIdInterface(interfaces.getIdInterface());
                                    while (rsPermissaoTabela.next()) {
                                        new TabInterfaces(rsPermissaoTabela.getString("TABLE_NAME"), 
                                                          getSSelectInterfaces());
                                    }
                                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Tabelas da Interface ##" + QUEBRA_LINHA);
                                }
                            }
                        }
                    } catch (Exception e) {
                        SfwLogger.log(e.getLocalizedMessage());
                        SfwLogger.debug(e.getClass().toString(), e.getStackTrace());
                    }
                }
            }


            try {
                if (pTipo.contains(
                        "ArquivosExternos") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                    ArquivosExternosNaoGerados arquivosExternosNaoGerados = new ArquivosExternosNaoGerados();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Arquivos Externos ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Synonyms") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Synonyms ##" + QUEBRA_LINHA);
                    Synonyms synonyms = new Synonyms("INOUT", getCliente());
                    synonyms.setName("Synonyms");
                    synonyms.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Synonyms ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "TabelasTemporarias") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Tabelas Sem Permissão##" + QUEBRA_LINHA);
                    TabInterfaceSemPermissao tabInterfaceSemPermissao = new TabInterfaceSemPermissao();
                    tabInterfaceSemPermissao.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Tabelas Sem Permissão ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Sistemas") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Sistemas ##" + QUEBRA_LINHA);
                    Sistemas sistemas = new Sistemas(getCliente());
                    sistemas.setName("Sistemas");
                    sistemas.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Sistemas ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Sequences") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Sequences ##" + QUEBRA_LINHA);
                    Sequence sequence = new Sequence("INOUT");
                    sequence.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Sequences ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "Views") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando Views ##" + QUEBRA_LINHA);
                    Views views = new Views("INOUT");
                    views.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Views ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains(
                        "IntMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando IntMapeamento ##" + QUEBRA_LINHA);
                    IntMapeamento intMapeamento = new IntMapeamento("INOUT");
                    intMapeamento.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando IntMapeamento ##" + QUEBRA_LINHA);
                }
                if (pTipo.contains("SapMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.debug(QUEBRA_LINHA + "## Gerando SapMapeamento ##" + QUEBRA_LINHA);
                    SapMapeamento sapMapeamento = new SapMapeamento("INOUT");
                    sapMapeamento.start();
                    SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando SapMapeamento ##" + QUEBRA_LINHA);
                }
            } catch (Exception ex) {
                SfwLogger.log(ex.getLocalizedMessage());
                SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            }

            // Base de Integracao
            if (cliente.getItUser().getUser() != null && !cliente.getItUser().getUser().isEmpty()) {
                try {
                    if (pTipo.contains("Synonyms") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando Synonyms Integração ##" + QUEBRA_LINHA);
                        Synonyms synonyms= new Synonyms("INTEGRACAO", getCliente());
                        synonyms.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Synonyms Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("IntMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando IntMapeamento Integração ##" + QUEBRA_LINHA);
                        IntMapeamento intMapeamento = new IntMapeamento("INTEGRACAO");
                        intMapeamento.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando IntMapeamento Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("SapMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando SapMapeamento Integração ##" + QUEBRA_LINHA);
                        SapMapeamento sapMapeamento = new SapMapeamento("INTEGRACAO");
                        sapMapeamento.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando SapMapeamento Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Objetos") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando Objetos Integração ##" + QUEBRA_LINHA);
                        ObjetosIntegracao objetosIntegracao = new ObjetosIntegracao();
                        objetosIntegracao.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Objetos Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Sequences") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando Sequences Integração ##" + QUEBRA_LINHA);
                        Sequence sequence = new Sequence("INTEGRACAO");
                        sequence.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Sequences Integração ##" + QUEBRA_LINHA);
                    }

                    if (pTipo.contains("Views") && chScriptsSemVinculoInterface.equals("S")) {
                        SfwLogger.debug(QUEBRA_LINHA + "## Gerando Views Integração ##" + QUEBRA_LINHA);
                        Views views = new Views("INTEGRACAO");
                        views.start();
                        SfwLogger.debug(QUEBRA_LINHA + "## Finalizando - Gerando Views Integração ##" + QUEBRA_LINHA);
                    }
                } catch (Exception ex) {
                    SfwLogger.log(ex.getLocalizedMessage());
                    SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
                }
            }

            // Deretórios
            try {
                diretorios.removeDiretorio();
                diretorios.validaDiretorio();
            } catch (Exception ex) {
                SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            }

        } catch (SQLException e) {
            SfwLogger.debug(e.getClass().toString(), e.getStackTrace());
            SfwLogger.log("Error in the implementation of the interface with Id_Importação ");
        } finally {
            //ConnectionInout.getConnection().close();
            //rsSistema.close();
            //psSistema.close();
            //psCountInterface2.close();
            //rsCountInterface.close();
            //rsInterfaces.close();
        }
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

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
