package cvsstructure.cvsStructure;

import cvsstructure.Usuario;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import cvsstructure.objects.DataTableLayout;
import cvsstructure.objects.FunctionProcedure;
import cvsstructure.objects.IntMapeamento;
import cvsstructure.objects.Packages;
import cvsstructure.objects.PrepararConsultas;
import cvsstructure.objects.SapMapeamento;
import cvsstructure.objects.Sequence;
import cvsstructure.objects.Sistemas;
import cvsstructure.objects.Synonyms;
import cvsstructure.objects.TabInterfaces;
import cvsstructure.objects.Tables;
import cvsstructure.objects.Views;
import cvsstructure.log.SfwLogger;
import cvsstructure.validaScripts.SfwValidaScripts;

public class CVSStructure {

    private Usuario ioUser;
    private Usuario itUser;
    private Usuario bgUser;
    private Usuario bsUser;
    private Usuario isUser;
    private Usuario ceUser;
    private Usuario ciUser;
    private Usuario exUser;
    private Usuario dbUser;
    private Usuario caiUser;
    private Usuario appsUser;
    private String dbLinkCai;
    private String dbLinkApps;
    public String s_User;
    public String s_Pass;
    public String s_Conn;
    public String s_ItUser;
    public static String s_ItUser2;
    public String s_ItPass;
    private ArrayList dirScriptsValida = new ArrayList();
    public static String chNomePasta;
    public static String chConexaoPorArquivos;
    public static String chScriptsSemVinculoInterface;
    private Object[] selectInterfaces;

    /* Selects Utilizados */
    private String selectGerarArquivosExternos;
    private String selectExportarArquivosExternos;
    private String selectPermissaoTabela;
    private String selectUsers;
    private String sistema;
    private String interfaceDaTabela;
    private String sessionSchema;
    private String role;
    // Variaveis
    public String idInterface;
    public static String userNameSys = "";
    public static String id_sistema_it = "";
    private String fileName = "";
    private String fileNameScripts = "";
    public String executavel = "";
    public String tipoInterface = "";
    public String idSistema = "";
    public String descricao = "";
    public String interfereProcessamentoDireto = null;
    public String tempoMedio = "";
    public String executavelCompl = "";
    // Contadores
    public static int nTotalSistemas = 0;
    public static int nTotalTabelas = 0;
    public static int nTotalInterfaces = 0;
    public static int nTotalViews = 0;
    public static int nTotalSynonyms = 0;
    public static int nTotalSequences = 0;
    public static int nTotalPackages = 0;
    public static int nTotalSapMapeamento = 0;
    public static int nTotalIntMapeamento = 0;
    public static int nTotalFunctionsProcedures = 0;
    // public String sQuebraLinha = System.getProperty("line.separator");
    public static final String QUEBRA_LINHA = "\r\n";
    public static String path;
    private Clob clob;
    public static String sDebug = "N";
    private boolean flag = true;
    private String sCaminhaGeracao;
    private String sSynonymsAll = "";
    // PreparedStatement
    private PreparedStatement psInterfaces = null;
    private ResultSet rsInterfaces = null;
    private PreparedStatement psExportarArquivosExternos = null;
    private PreparedStatement psUsersUser = null;
    private ResultSet rsUsersUser = null;
    private PreparedStatement psGerarArquivosExternos = null;
    private ResultSet rsArquivosExternos = null;
    private PreparedStatement psGerarArquivosExternosNaoGerados;
    private ResultSet rsGerarArquivosExternosNaoGerados;
    private PreparedStatement psCountArquivosExternosNaoGerados;
    private ResultSet rsCountArquivosExternosNaoGerados;
    private PreparedStatement psPermissaoTabela = null;
    private ResultSet rsPermissaoTabela = null;
    private PreparedStatement psCountInterface = null;
    private PreparedStatement psCountInterface2 = null;
    private ResultSet rsCountInterface = null;
    private PreparedStatement psFoundObjectsIT = null;
    private ResultSet rsFoundObjectsIT = null;
    private PreparedStatement psSistema = null;
    private ResultSet rsSistema = null;
    private PreparedStatement psInterfaceDaTabela = null;
    private ResultSet rsInterfaceDaTabela = null;
    private PreparedStatement psCreateTable = null;
    private PreparedStatement psInsertReferencesObjects = null;
    private PreparedStatement psCreateTableIT = null;
    private PreparedStatement psInsertReferencesObjectsIT = null;
    private PreparedStatement psDropTableIT = null;
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

        sCaminhaGeracao = CVSStructure.jframe.getTxCaminhaGeracao();
        if (sCaminhaGeracao.equals(".\\")) {
            path = new File(".").getCanonicalPath();
        } else {
            if (sCaminhaGeracao.substring(sCaminhaGeracao.length() - 1).equals("\\")) {
                path = sCaminhaGeracao.substring(0, sCaminhaGeracao.length() - 1);
            } else {
                path = sCaminhaGeracao;
            }
        }

        this.selectUsers = "select username from user_users";



        this.selectPermissaoTabela = "select id_interface, table_name from permissao_tabela where lower(table_name) != 'tmp_cvs_structure' and id_interface = ?";



        this.sistema = "select * from sistema where user_oracle like '%'|| ? || '%'";


        this.interfaceDaTabela = "select distinct it.* "
                + "from permissao_tabela ta,"
                + "     sistema_interface it"
                + " where ta.table_name like '%' || upper( ? ) ||'%'"
                + " and it.id_interface = ta.id_interface";

        this.selectGerarArquivosExternos = "select NOME_ARQUIVO,"
                + "PATH_RELATIVO,"
                + "DESCRICAO,"
                + "CONTEUDO"
                + " from arquivo_externo"
                + " where arquivo_externo.nome_arquivo like '%'|| ? || '%'";

        this.selectExportarArquivosExternos = "select NOME_ARQUIVO,"
                + "PATH_RELATIVO,"
                + "DESCRICAO,"
                + "CONTEUDO"
                + " from arquivo_externo"
                + " where arquivo_externo.nome_arquivo like '%'|| ? || '%'";

        // Obtendo condições do select para encontrar o as interfaces existentes
        StringBuffer sbAllInterfaces = new StringBuffer();
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


        StringBuffer sbCountInterface = new StringBuffer();
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



        StringBuffer sbArquivosExternosNaoGerados = new StringBuffer();
        sbArquivosExternosNaoGerados.append("select distinct nome_arquivo, contem, tipo from (");
        sbArquivosExternosNaoGerados.append("        select a.nome_arquivo, substr(v1.nome_arquivo, 1, instr(v1.nome_arquivo, '.')-1) contem, substr(v1.nome_arquivo, instr(v1.nome_arquivo, '.')+1, length(v1.nome_arquivo)) tipo");
        sbArquivosExternosNaoGerados.append("          from   arquivo_externo a, ");
        sbArquivosExternosNaoGerados.append("                (select nome_arquivo from arquivo_externo) v1");
        sbArquivosExternosNaoGerados.append("         where upper(a.conteudo) like '%' || upper(v1.nome_arquivo) || '%'");
        sbArquivosExternosNaoGerados.append("         union");
        sbArquivosExternosNaoGerados.append("        select a.nome_arquivo, v2.object_name contem, v2.tipo");
        sbArquivosExternosNaoGerados.append("        from arquivo_externo a,");
        sbArquivosExternosNaoGerados.append("                (       ");
        sbArquivosExternosNaoGerados.append("                    select * from ");
        sbArquivosExternosNaoGerados.append("                    (");
        sbArquivosExternosNaoGerados.append("                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PROCEDURE' ");
        sbArquivosExternosNaoGerados.append("                    union all ");
        sbArquivosExternosNaoGerados.append("                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'FUNCTION' ");
        sbArquivosExternosNaoGerados.append("                    union all ");
        sbArquivosExternosNaoGerados.append("                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PACKAGE' ");
        sbArquivosExternosNaoGerados.append("                    ) my_all_objects");
        sbArquivosExternosNaoGerados.append("                    where my_all_objects.object_name not in ");
        sbArquivosExternosNaoGerados.append("                    ( 'SET_CONTEUDO_ARQ' ,");
        sbArquivosExternosNaoGerados.append("                    'PROC_CTL_FIXO' ,");
        sbArquivosExternosNaoGerados.append("                    'PROC_CONTEUDO_ARQ' ,");
        sbArquivosExternosNaoGerados.append("                    'PROCESSA_INTERFACES_IN' ,");
        sbArquivosExternosNaoGerados.append("                    'PROCESSA_ID_SISTEMA' ,");
        sbArquivosExternosNaoGerados.append("                    'PROCESSA_GRANTS_SISTEMA' ,");
        sbArquivosExternosNaoGerados.append("                    'PROCESSA_ERRO_INTERFACE' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_UPDATE_CONCATENA_LONG' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_SINCRONIZA_TABELA' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_PROCESSA_DIRETO' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_POPULA_SAP_COLUMNS' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_INSERE_LOG_GERAL' ,");
        sbArquivosExternosNaoGerados.append("                    'PRC_INDEXA_HEADERS' ,");
        sbArquivosExternosNaoGerados.append("                    'NOTIFICA_RESUMO_INTERFACE' ,");
        sbArquivosExternosNaoGerados.append("                    'INTERFACE_EXCLUI_REGISTROS' ,");
        sbArquivosExternosNaoGerados.append("                    'INSERE_NOTIFICACAO_TABELA' ,");
        sbArquivosExternosNaoGerados.append("                    'INSERE_INFO_ODBC' ,");
        sbArquivosExternosNaoGerados.append("                    'INSERE_HISTORICO_SAIDA_ID_IMP' ,");
        sbArquivosExternosNaoGerados.append("                    'INSERE_HISTORICO_SAIDA' ,");
        sbArquivosExternosNaoGerados.append("                    'INSERE_ERRO_IMPORTACAO' ,");
        sbArquivosExternosNaoGerados.append("                    'INFORMA_SUCESSO_ERRO' ,");
        sbArquivosExternosNaoGerados.append("                    'INFORMA_QTDE_REGISTROS_IMPORT' ,");
        sbArquivosExternosNaoGerados.append("                    'INFORMA_OBS_IMPORTACAO' ,");
        sbArquivosExternosNaoGerados.append("                    'INFORMA_OBS_ID_IMPORTACAO' ,");
        sbArquivosExternosNaoGerados.append("                    'GET_CONTEUDO_ARQ' ,");
        sbArquivosExternosNaoGerados.append("                    'FINALIZA_INTERFACE' ,");
        sbArquivosExternosNaoGerados.append("                    'EXCLUI_ERRO_IMPORTACAO' ,");
        sbArquivosExternosNaoGerados.append("                    'ELIMINA_REGISTROS_ANTIGOS' ,");
        sbArquivosExternosNaoGerados.append("                    'ELIMINA_INTERFACE' ,");
        sbArquivosExternosNaoGerados.append("                    'CONCATENA_CONTEUDO' ,");
        sbArquivosExternosNaoGerados.append("                    'COMPILE_INVALID' ,");
        sbArquivosExternosNaoGerados.append("                    'ATUALIZA_IMPORTACAO_EXECUTADA' )");
        sbArquivosExternosNaoGerados.append("                )  v2 ");
        sbArquivosExternosNaoGerados.append("          where upper(a.conteudo) like '%' || upper(v2.object_name) || '%'");
        sbArquivosExternosNaoGerados.append("          ) p1");
        sbArquivosExternosNaoGerados.append(" where not exists (");
        sbArquivosExternosNaoGerados.append("select 1 ");
        sbArquivosExternosNaoGerados.append("from interfaces i ");
        sbArquivosExternosNaoGerados.append("where i.executavel like '%' || p1.contem || '%' )");

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

        psUsersUser = ConnectionInout.getConnection().prepareStatement(this.selectUsers);
        psExportarArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectExportarArquivosExternos);
        psGerarArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectGerarArquivosExternos);
        psGerarArquivosExternosNaoGerados = ConnectionInout.getConnection().prepareStatement(sbArquivosExternosNaoGerados.toString());
        psCountInterface = ConnectionInout.getConnection().prepareStatement(sbCountInterface.toString());
        psPermissaoTabela = ConnectionInout.getConnection().prepareStatement(selectPermissaoTabela);

        psInterfaces = ConnectionInout.getConnection().prepareStatement(sbAllInterfaces.toString());

        //
        StringBuffer sbCountSistemaPorInterface = new StringBuffer();
        sbCountSistemaPorInterface.append("select count(*) TOTAL");
        sbCountSistemaPorInterface.append(" from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,");
        sbCountSistemaPorInterface.append(" sistema_interface");
        sbCountSistemaPorInterface.append(" where interfaces.id_interface = sistema_interface.id_interface (+)");
        sbCountSistemaPorInterface.append(" and interfaces.id_interface like '%'||?||'%'");

        psCountInterface2 = ConnectionInout.getConnection().prepareStatement(sbCountSistemaPorInterface.toString());

        if (ConnectionIntegracao.getConnection() != null) {

            psFoundObjectsIT = ConnectionIntegracao.getConnection().prepareStatement(sbAllObjectsSystem.toString());

            psCreateTableIT = ConnectionIntegracao.getConnection().prepareCall(sbCreateTableCvsStructure.toString());
            psInsertReferencesObjectsIT = ConnectionIntegracao.getConnection().prepareCall(sbInsertTableCvsStructure.toString());
        }

        psInterfaceDaTabela = ConnectionInout.getConnection().prepareStatement(this.interfaceDaTabela);

        psCreateTable = ConnectionInout.getConnection().prepareCall(sbCreateTableCvsStructure.toString());

        psInsertReferencesObjects = ConnectionInout.getConnection().prepareCall(sbInsertTableCvsStructure.toString());
    }

    public ArrayList readInterfaces() throws SQLException, IOException {
        StringBuffer sbAllInterfaces = new StringBuffer();
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
            userNameSys = rsUsersUser.getString("USERNAME");

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

            if (pTipo.contains("D")) {
                SfwLogger.saveLog(QUEBRA_LINHA + "## Criando Diretórios Comuns ##" + QUEBRA_LINHA);
                try {
                    criarDiretoriosComuns();
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
                            String sistema = "";

                            psCountInterface2.setString(1, rsInterfaces.getString("ID_INTERFACE"));
                            rsCountInterface = psCountInterface2.executeQuery();
                            rsCountInterface.next();
                            int nTotInter = rsCountInterface.getInt("TOTAL");

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
                                idInterface = rsInterfaces.getString("ID_INTERFACE");
                                executavel = rsInterfaces.getString("EXECUTAVEL");
                                tipoInterface = rsInterfaces.getString("TIPO_INTERFACE");
                                idSistema = rsInterfaces.getString("ID_SISTEMA").toLowerCase();
                                descricao = rsInterfaces.getString("DESCRICAO");
                                //sUserNameApp = rsInterfaces.getString("USERNAME");
                                tempoMedio = rsInterfaces.getString("TEMPO_MEDIO");
                                executavelCompl = rsInterfaces.getString("EXECUTAVELCOMPL");
                                if (rsInterfaces.getString("INTERFERE_PROC_DIR") != null) {
                                    interfereProcessamentoDireto = rsInterfaces.getString("INTERFERE_PROC_DIR");
                                }

                                if (pTipo.contains("D")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Criando Diretórios ##" + QUEBRA_LINHA);
                                    criarDiretorio();
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Criando Diretórios ##" + QUEBRA_LINHA);
                                }

                                if (pTipo.contains("Interfaces")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Interfaces ##" + QUEBRA_LINHA);
                                    this.interfaces(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Interfaces ##" + QUEBRA_LINHA);
                                }

                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                    this.exportarArquivosExternos(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Exportando Arquivos Externos ##" + QUEBRA_LINHA);
                                }

                                if (pTipo.contains("ArquivosExternos")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                                    this.arquivosExternos(); // ok
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Arquivos Externos  ##" + QUEBRA_LINHA);
                                }

                                if (pTipo.contains("TabelasTemporarias")) {
                                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Tabelas da Interface ##" + QUEBRA_LINHA);
                                    //this.exportarTabInterface(); //ok
                                    psPermissaoTabela.setString(1, idInterface);
                                    rsPermissaoTabela = psPermissaoTabela.executeQuery();

                                    while (rsPermissaoTabela.next()) {
                                        new TabInterfaces(rsPermissaoTabela.getString("TABLE_NAME"), idInterface, idSistema, this);
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
                if (pTipo.contains("ArquivosExternos") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Arquivos Externos ##" + QUEBRA_LINHA);
                    this.arquivosExternosNaoGerados(); // ok
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Arquivos Externos ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("Synonyms") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Synonyms ##" + QUEBRA_LINHA);
                    new Synonyms("INOUT", this);
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Synonyms ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("TabelasTemporarias") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Tabelas Sem Permissão##" + QUEBRA_LINHA);
                    this.tabInterfaceSemPermissao();
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Tabelas Sem Permissão ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("Sistemas") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Sistemas ##" + QUEBRA_LINHA);
                    new Sistemas(this);
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Sistemas ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("Sequences") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Sequences ##" + QUEBRA_LINHA);
                    new Sequence("INOUT");
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Sequences ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("Views") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando Views ##" + QUEBRA_LINHA);
                    new Views("INOUT");
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando Views ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("IntMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Gerando IntMapeamento ##" + QUEBRA_LINHA);
                    new IntMapeamento("INOUT", this);
                    SfwLogger.saveLog(QUEBRA_LINHA + "## Finalizando - Gerando IntMapeamento ##" + QUEBRA_LINHA);
                }

                if (pTipo.contains("SapMapeamento") && chScriptsSemVinculoInterface.equals("S")) {
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
                        this.gerarObjetosIntegracao();
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
            logMessage("Error in the implementation of the interface with Id_Importação " + idInterface);
        } finally {
            //ConnectionInout.getConnection().close();
            //rsSistema.close();
            //psSistema.close();
            //psCountInterface2.close();
            //rsCountInterface.close();
            //rsInterfaces.close();
        }
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

    /**************************************************************************
     * <b>Criar diretorio</b>
     **************************************************************************/
    private void criarDiretoriosComuns() throws Exception {
        ArrayList dirScripts = new ArrayList();

        dirScriptsValida.add(path + "\\" + userNameSys + "\\Scripts\\comum");

        // Arquivos Inout
        dirScripts.add(path + "\\" + userNameSys);
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\Interfaces");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\ArquivosExternos");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\Tabelas");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\Sistemas");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\Synonyms");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\View");
        dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\comum\\INOUT\\Sequence");

        // Scripts Inout
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Interfaces");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Tabelas");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Sistemas");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Synonyms");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\View");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Sequence");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Package");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\PackageBody");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Procedure");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Function");

        // Scripts Integracao
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Function");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Procedure");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Package");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\PackageBody");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Table");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Synonyms");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\View");
        dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Sequence");

        for (int i = 0; i < dirScripts.size(); i++) {
            File file = new File(dirScripts.get(i).toString());
            if (file.mkdir()) {
                SfwLogger.saveLog("Diretorio criado com sucesso! " + dirScripts.get(i));
            } else {
                SfwLogger.saveLog("Erro ao criar diretorio! " + dirScripts.get(i));
            }
        }
    }

    /**************************************************************************
     * <b>Criar diretorio</b>
     **************************************************************************/
    @SuppressWarnings("unchecked")
    private void criarDiretorio() throws Exception {
        ArrayList dirScripts = new ArrayList();

        if (tipoInterface.trim().equals("S")) {
            dirScriptsValida.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT"));

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT"));
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT"));

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\View\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\View\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\Sequence\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Package\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\PackageBody\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Procedure\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Function\\");

            /*Integracao*/
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Sequence");

        } else if (tipoInterface.trim().equals("E")) {
            dirScriptsValida.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN"));

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN"));
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN"));

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\View\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\View\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\Sequence\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Package\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\PackageBody\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Procedure\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Function\\");

            /*Integracao*/
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INTEGRACAO\\Sequence");
        } else {
            dirScriptsValida.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface());

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface());
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface());

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\View\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\View\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Sequence\\");

            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Package\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\PackageBody\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Procedure\\");
            dirScripts.add(path + "\\" + userNameSys + "\\Arquivos\\" + getIdInterface() + "\\INOUT\\Function\\");

            /*Integracao*/
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\" + userNameSys + "\\Scripts\\" + getIdInterface() + "\\INTEGRACAO\\Sequence");
        }

        for (int i = 0; i < dirScripts.size(); i++) {
            File file = new File(dirScripts.get(i).toString());
            if (file.mkdir()) {
                SfwLogger.saveLog("Diretorio criado com sucesso! " + dirScripts.get(i));
            } else {
                SfwLogger.saveLog("Erro ao criar diretorio! " + dirScripts.get(i));
            }
        }
    }

    public String getNomePasta(String tipo) {
        String pasta = "";

        if (tipo.equals("") || chNomePasta.equals("N")) {
            pasta = getIdInterface();
        } else if (tipo.equals("IN")) {
            pasta = getIdSistema() + "_in_" + getIdInterface();
        } else if (tipo.equals("OUT")) {
            pasta = getIdSistema() + "_out_" + getIdInterface();
        }

        return pasta;
    }

    private void validaDiretorio() throws Exception {
        StringBuffer strOutScripts = new StringBuffer();
        SfwValidaScripts valid = new SfwValidaScripts();
        valid.setArqsInstala("N");
        //strOutScripts.append("SPOOL SCRIPT_STATUS.LOG" + QUEBRA_LINHA);
        //strOutScripts.append("@\".\\define.sql\"" + QUEBRA_LINHA);

        // comum em primeiro lugar
        //valid.executar(path + "\\"+userNameSys+"\\Scripts\\comum");
        //strOutScripts.append("@\"" + (path + "\\"+userNameSys+"\\Scripts\\").replace(path + "\\"+userNameSys+"\\Scripts", ".") + "\\comum\\ordem_instalacao.sql\"" + QUEBRA_LINHA);

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

        for (int i = 0; i < dirScriptsValida.size(); i++) {

            // iginora o comum pq ele já foi valido em primeiro lugar
            //if( dirScriptsValida.get(i).toString().indexOf("\\comum\\") == 0 ){
            valid.executar(dirScriptsValida.get(i).toString());
            strOutScripts.append("@\"" + dirScriptsValida.get(i).toString().replace(path + "\\" + userNameSys + "\\Scripts", ".") + "\\ordem_instalacao.sql\"" + QUEBRA_LINHA);
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

        valid.copy(new File(".\\definicoes\\define.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "define.sql"));
        valid.copy(new File(".\\definicoes\\Instrucoes.txt"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "Instrucoes.txt"));
        valid.copy(new File(".\\definicoes\\limpa_definicoes.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "limpa_definicoes.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "processa_grants.sql"));
        valid.copy(new File(".\\definicoes\\compila_invalidos.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "compila_invalidos.sql"));
        valid.copy(new File(".\\definicoes\\instala_linux.sh"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "instala_linux.sh"));
        valid.copy(new File(".\\definicoes\\Instala_win.bat"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "Instala_win.bat"));
        valid.copy(new File(".\\definicoes\\dispara_script_instalacao.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "dispara_script_instalacao.sql"));
        valid.copy(new File(".\\definicoes\\connect_io.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "connect_io.sql"));
        valid.copy(new File(".\\definicoes\\connect_it.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "connect_it.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants_sistema.sql"), new File(path + "\\" + userNameSys + "\\Scripts\\" + "processa_grants_sistema.sql"));

        File fileScripts = new File(path + "\\" + userNameSys + "\\Scripts\\ordem_instalacao.sql");
        if (!fileScripts.exists()) {
            fileScripts.createNewFile();
        }

        FileWriter fwScripts = new FileWriter(fileScripts, false);
        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
        fwScripts.close();
        logMessage("File .\\" + userNameSys + "\\Scripts\\ordem_instalacao.sql was succesfull generated.");

    }

    /**************************************************************************
     * <b>Remover diretórios</b>
     **************************************************************************/
    private void removeDiretorio() throws Exception {
        File diretorio = new File(path + "\\" + userNameSys);
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

    /**************************************************************************
     * <b>Gerar scripts das interfaces</b>
     **************************************************************************/
    private void interfaces() throws Exception {

        try {
            fileName = "interface.sql";
            if (tipoInterface.trim().equals("S")) {
                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Interface\\" + fileName;
            } else if (tipoInterface.trim().equals("E")) {
                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Interface\\" + fileName;
            } else {
                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Interface\\" + fileName;
            }

            // Criando arquivo na pasta de scripts
            File fileScripts = new File(fileNameScripts);
            if (!fileScripts.exists()) {
                fileScripts.createNewFile();

                FileWriter fwScripts = new FileWriter(fileScripts, false);
                StringBuffer strOut = new StringBuffer();

                logMessage("Creating or appending to file " + fileNameScripts);

                if (CVSStructure.chConexaoPorArquivos.equals("S")) {
                    strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                }
                strOut.append("--  ///////" + QUEBRA_LINHA);
                strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                strOut.append("--  ///////     Interface: " + descricao + QUEBRA_LINHA);
                strOut.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from PERMISSAO_TABELA where ID_INTERFACE = '" + idInterface + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from SISTEMA_INTERFACE where ID_INTERFACE = '" + idInterface + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("begin" + QUEBRA_LINHA);
                strOut.append("   insert into    INTERFACES" + QUEBRA_LINHA);
                strOut.append("      (ID_INTERFACE," + QUEBRA_LINHA);
                strOut.append("      DESCRICAO," + QUEBRA_LINHA);
                strOut.append("      NOME_MAQUINA," + QUEBRA_LINHA);
                strOut.append("      EXECUTAVEL," + QUEBRA_LINHA);
                strOut.append("      USERNAME," + QUEBRA_LINHA);
                strOut.append("      TEMPO_MEDIO," + QUEBRA_LINHA);
                strOut.append("      TIPO_INTERFACE");

                if (interfereProcessamentoDireto != null && !interfereProcessamentoDireto.equals("")) {
                    strOut.append("," + QUEBRA_LINHA + "     INTERFERE_PROC_DIR)" + QUEBRA_LINHA);
                } else {
                    strOut.append(")" + QUEBRA_LINHA);
                }

                strOut.append("      values" + QUEBRA_LINHA);
                strOut.append("      ('" + idInterface + "'," + QUEBRA_LINHA);
                strOut.append("      '" + descricao + "'," + QUEBRA_LINHA);
                strOut.append("   	  '&&USER_MACHINE'," + QUEBRA_LINHA);
                strOut.append("	  '" + executavelCompl + "'," + QUEBRA_LINHA);
                strOut.append("	  'ADM'," + QUEBRA_LINHA);
                strOut.append("	  '0'," + QUEBRA_LINHA);
                strOut.append("   '" + tipoInterface + "'");
                if (interfereProcessamentoDireto != null && !interfereProcessamentoDireto.equals("")) {
                    strOut.append(QUEBRA_LINHA);
                    strOut.append("      ,'" + interfereProcessamentoDireto + "'" + QUEBRA_LINHA);
                }
                strOut.append(");" + QUEBRA_LINHA);
                strOut.append("exception" + QUEBRA_LINHA);
                strOut.append("    when dup_val_on_index then" + QUEBRA_LINHA);
                strOut.append("        update INTERFACES" + QUEBRA_LINHA);
                strOut.append("        set" + QUEBRA_LINHA);
                strOut.append("                DESCRICAO = '" + descricao + "'," + QUEBRA_LINHA);
                strOut.append("                NOME_MAQUINA = '&&USER_MACHINE'," + QUEBRA_LINHA);
                strOut.append("                EXECUTAVEL = '" + executavelCompl + "'," + QUEBRA_LINHA);
                strOut.append("                USERNAME = 'ADM'," + QUEBRA_LINHA);
                strOut.append("                TIPO_INTERFACE = '" + tipoInterface + "'" + QUEBRA_LINHA);
                strOut.append("        where id_interface  = '" + idInterface + "';" + QUEBRA_LINHA);
                strOut.append("end;" + QUEBRA_LINHA);
                strOut.append("/" + QUEBRA_LINHA + QUEBRA_LINHA);

                psPermissaoTabela.setString(1, idInterface);
                strOut.append(new DataTableLayout("INOUT",
                        "permissao_tabela",
                        psPermissaoTabela).create());

                strOut.append("delete from INTERFACE_SAIDA where ID_INTERFACE =  '" + idInterface + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('" + idInterface + "' , '" + (idSistema.toUpperCase().equals("SFW") ? "BG" : idSistema.toUpperCase()) + "');" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("commit;" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("-- //////" + QUEBRA_LINHA);
                strOut.append("-- //////  FIM DO SCRIPT" + QUEBRA_LINHA);
                strOut.append("-- //////");

                if (strOut != null && !strOut.toString().equals("")) {
                    fwScripts.write(strOut.toString(), 0, strOut.length());
                }

                fwScripts.close();
                CVSStructure.nTotalInterfaces++;
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileNameScripts);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    private void arquivosExternos() throws Exception {
        File fileScripts;
        FileWriter fwScripts;
        StringBuffer strOutScripts;
        BufferedReader brScripts;

        try {
            psCountInterface.setString(1, executavel);
            rsCountInterface = psCountInterface.executeQuery();
            rsCountInterface.next();
            int nTotalArquivos = rsCountInterface.getInt("TOTAL_ARQUIVOS");

            //psArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectGerarArquivosExternos);
            psGerarArquivosExternos.setString(1, executavel);
            rsArquivosExternos = psGerarArquivosExternos.executeQuery();

            while (rsArquivosExternos.next()) {

                if (rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase().substring(rsArquivosExternos.getString("NOME_ARQUIVO").indexOf(".") + 1, rsArquivosExternos.getString("NOME_ARQUIVO").length()).equals("sql")) {
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                } else {
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                }

                if (nTotalArquivos == 1) {
                    if (tipoInterface.trim().equals("S")) {
                        fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                    } else if (tipoInterface.trim().equals("E")) {
                        fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                    } else {
                        fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                    }
                } else if (nTotalArquivos >= 2) {
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else {
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                }
                logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if (clob != null) {
                    try {
                        fileScripts = new File(fileNameScripts);
                        if (!fileScripts.exists()) {
                            fileScripts.createNewFile();

                            fwScripts = new FileWriter(fileScripts, false);

                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/
                            logMessage("Creating or appending to file " + fileNameScripts);

                            strOutScripts = new StringBuffer();
                            String auxScripts;

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("--  ///////" + QUEBRA_LINHA);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                            strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + QUEBRA_LINHA);
                            strOutScripts.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("set define off" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO" + QUEBRA_LINHA);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + QUEBRA_LINHA);
                            strOutScripts.append("values" + QUEBRA_LINHA);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts = brScripts.readLine()) != null) {

                                auxScripts = auxScripts.replaceAll("'", "' || chr(39) || '");
                                auxScripts = auxScripts.replaceAll("@", "' || chr(64) || '");
                                auxScripts = auxScripts.replaceAll("\t", "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(QUEBRA_LINHA, "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0) {
                                    strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                    strOutScripts.append(",");
                                    if (rsArquivosExternos.getString("NOME_ARQUIVO") == null) {
                                        strOutScripts.append("'" + (descricao.length() > 50 ? descricao.substring(0, 50) : descricao.trim()) + "'");
                                    } else {
                                        strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                    }
                                    strOutScripts.append(",");
                                    strOutScripts.append("'" + rsArquivosExternos.getString("PATH_RELATIVO") + "'");
                                    strOutScripts.append(",");
                                    strOutScripts.append("'" + auxScripts + "'");
                                    strOutScripts.append(" || CHR(13) || CHR(10)");
                                    strOutScripts.append(");");
                                } else {
                                    //if(maxLin == 0){
                                    //strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                    //strOutScripts.append(sQuebraLinha);
                                    //	strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "',");
                                    //}

                                    //if( (maxLin + auxScripts.length()) >= 350 ){
                                    //	strOutScripts.append("'" + auxScripts + "');");
                                    //	maxLin = 0;
                                    //}else{
                                    //	strOutScripts.append("'"+ auxScripts + "' || chr(13) || chr(10) ||");
                                    //	maxLin += auxScripts.length();
                                    //}
                                    //if(!auxScripts.equals("")){
                                    strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                                    strOutScripts.append("exec CONCATENA_CONTEUDO (");
                                    strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                    strOutScripts.append(",");
                                    strOutScripts.append("'" + auxScripts + "'");
                                    strOutScripts.append(" || CHR(13) || CHR(10)");
                                    strOutScripts.append(");");

                                    //}

                                }
                                contador += 1;
                            }


                            //if(maxLin < 350){
                            //	strOutScripts.append("');");
                            //}

                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("commit;");
                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("set define on");
                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("-- //////" + QUEBRA_LINHA);
                            strOutScripts.append("-- //////  Fim do Script" + QUEBRA_LINHA);
                            strOutScripts.append("-- //////");

                            if (strOutScripts != null) {
                                fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                            }
                            fwScripts.close();
                        }

                        logMessage("File " + fileNameScripts + " was succesfull generated.");
                    } catch (IOException ioex) {
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                } else {
                    logMessage("No data are being generated");
                    logMessage("File " + fileName + " wasn't generated.");
                    logMessage("Error in the implementation of the interface with Id_Importação " + idInterface);
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            rsCountInterface.close();
        }
    }

    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    private void arquivosExternosNaoGerados() throws Exception {
        File fileScripts;
        FileWriter fwScripts;
        StringBuffer strOutScripts;
        BufferedReader brScripts;

        try {

            try {
                logMessage("*** Drop table TMP_CVS_STRUCTURE ");
                psDropTableIT = ConnectionInout.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE");
                psDropTableIT.executeQuery();
            } catch (Exception ex) {
                //logMessage("Error drop table TMP_CVS_STRUCTURE ");
                //logMessage(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            try {
                logMessage("*** Create table TMP_CVS_STRUCTURE ");
                psCreateTable.executeQuery();
                psInsertReferencesObjects.setString(1, s_User.toUpperCase());
                psInsertReferencesObjects.executeUpdate();
                ConnectionInout.getConnection().commit();
            } catch (Exception ex) {
                //logMessage("Error create table TMP_CVS_STRUCTURE ");
                //logMessage(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            rsGerarArquivosExternosNaoGerados = psGerarArquivosExternosNaoGerados.executeQuery();
            while (rsGerarArquivosExternosNaoGerados.next()) {

                for (int i = 1; i <= 2; i++) {

                    psCountArquivosExternosNaoGerados = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                    String tipoArquivo;
                    if (i == 1) {
                        psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                        tipoArquivo = rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                    } else {
                        psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        tipoArquivo = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") + 1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length());
                    }
                    //psCountArquivosExternosNaoGerados.setString(2, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                    rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                    rsCountArquivosExternosNaoGerados.next();
                    int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                    //String a = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase();
                    //System.out.println(a);
                    //if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().contains("igi")){
                    //    String b = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase();
                    //}

                    if (nTotalArquivos > 1 || nTotalArquivos == 0) {
                        if (i == 1) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Package\\" + fileName;
                            }
                        } else {

                            if (tipoArquivo.equals("bat")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\Package\\" + fileName;
                            }
                        }
                    } else {
                        if (rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") + 1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL")) {
                            rsSqlInterface = PrepararConsultas.getSqlInterface(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                            while (rsSqlInterface.next()) {
                                idInterface = rsSqlInterface.getString("ID_INTERFACE");
                            }
                        } else {
                            rsBatInterface = PrepararConsultas.getBatInterface(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                            while (rsBatInterface.next()) {
                                idInterface = rsBatInterface.getString("ID_INTERFACE");
                            }
                        }

                        rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface);
                        while (rsDadosInterface.next()) {
                            executavel = rsDadosInterface.getString("EXECUTAVEL");
                            tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                            idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            descricao = rsDadosInterface.getString("DESCRICAO");
                            //sUserNameApp = rsDadosInterface.getString("USERNAME");
                            tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                            executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        String nomeArquivo = "";
                        if (i == 1) {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase();
                        } else {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().substring(0, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") - 1);
                        }

                        if (tipoInterface.trim().equals("S")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Package\\" + fileName;
                            }
                        } else if (tipoInterface.trim().equals("E")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Package\\" + fileName;
                            }
                        } else {
                            //fileNameScripts = ".\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Package\\" + fileName;
                            }
                        }
                    }

                    logMessage("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    if (tipoArquivo.equals("bat") || tipoArquivo.equals("sql")) {
                        psGerarArquivosExternos.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM") + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO"));
                        rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                        try {
                            fileScripts = new File(fileNameScripts);
                            if (!fileScripts.exists()) {
                                fileScripts.createNewFile();

                                fwScripts = new FileWriter(fileScripts, false);

                                while (rsArquivosExternos.next()) {
                                    clob = rsArquivosExternos.getClob("CONTEUDO");

                                    if (clob != null) {
                                        /******************************************
                                         * Gerando arquivos na pasta de Scripts
                                         ******************************************/
                                        if (chConexaoPorArquivos.equals("S")) {
                                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        }

                                        strOutScripts.append("--  ///////" + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("set define off" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("insert into ARQUIVO_EXTERNO" + QUEBRA_LINHA);
                                        strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + QUEBRA_LINHA);
                                        strOutScripts.append("values" + QUEBRA_LINHA);

                                        // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                                        brScripts = new BufferedReader(clob.getCharacterStream());
                                        int contador = 0;
                                        int maxLin = 0;
                                        while ((auxScripts = brScripts.readLine()) != null) {

                                            auxScripts = auxScripts.replaceAll("'", "' || chr(27) || '");
                                            auxScripts = auxScripts.replaceAll("@", "' || chr(64) || '");
                                            auxScripts = auxScripts.replaceAll("\t", "' '");
                                            //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                            auxScripts = auxScripts.replaceAll(QUEBRA_LINHA, "' || chr(13) || CHR(10) ||'");
                                            //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                            if (contador == 0) {
                                                strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                strOutScripts.append(",");
                                                if (rsArquivosExternos.getString("NOME_ARQUIVO") == null) {
                                                    strOutScripts.append("'" + (descricao.length() > 50 ? descricao.substring(0, 50) : descricao.trim()) + "'");
                                                } else {
                                                    strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                }
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + rsArquivosExternos.getString("PATH_RELATIVO") + "'");
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + auxScripts + "'");
                                                strOutScripts.append(" || CHR(13) || CHR(10)");
                                                strOutScripts.append(");");
                                            } else {
                                                //if(!auxScripts.equals("")){
                                                strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                                strOutScripts.append("exec CONCATENA_CONTEUDO (");
                                                strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + auxScripts + "'");
                                                strOutScripts.append(" || CHR(13) || CHR(10)");
                                                strOutScripts.append(");");
                                                //}

                                            }
                                            contador += 1;
                                        }
                                        strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                        strOutScripts.append("commit;");
                                        strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("set define on");
                                        strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////  Fim do Script" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////");
                                    } else {
                                        logMessage("No data are being generated");
                                        logMessage("File " + fileName + " wasn't generated.");
                                        logMessage("Error in the implementation of the interface with Id_Importação " + idInterface);

                                    }
                                }

                                if (strOutScripts != null) {
                                    fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                                }
                                fwScripts.close();

                                logMessage("File " + fileNameScripts + " was succesfull generated.");
                            }
                        } catch (IOException ioex) {
                            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                            ioex.printStackTrace();
                        }

                    } else if (tipoArquivo.equals("function")
                            || tipoArquivo.equals("procedure")) {

                        //
                        new FunctionProcedure("INOUT",
                                rsGerarArquivosExternosNaoGerados.getString("TIPO"),
                                rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"),
                                fileName,
                                fileNameScripts);

                    } else if (tipoArquivo.equals("package")
                            || tipoArquivo.equals("package body")) {

                        //
                        new Packages("INOUT",
                                rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"),
                                fileName,
                                fileNameScripts);
                    }
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            psDropTableIT = ConnectionInout.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE");
            psDropTableIT.executeQuery();
        }
    }

    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    private void gerarObjetosIntegracao() throws Exception {
        File fileScripts;
        FileWriter fwScripts;
        StringBuffer strOutScripts;
        BufferedReader brScripts;

        try {
            try {
                psCreateTableIT.executeQuery();
                ConnectionIntegracao.getConnection().commit();
            } catch (Exception ex) {
                logMessage("Creando tabela caso não exista");
                logMessage(ex.getLocalizedMessage());
                SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            psInsertReferencesObjectsIT.setString(1, s_ItUser.toUpperCase());
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
                if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("bat")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase() + ".sql";
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("sql")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase();
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("function")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("procedure")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                } else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("table")) {
                    fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                    fileNameScripts = path + "\\" + userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
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
                    fileScripts = new File(fileNameScripts);
                    if (!fileScripts.exists()) {

                        logMessage("Creating or appending to file " + fileNameScripts);

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
                    CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                    SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                    ioex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            psDropTableIT = ConnectionIntegracao.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE");
            psDropTableIT.execute();
        }

    }

    /**************************************************************************
     * <b>Gerar arquivos externos</b>
     **************************************************************************/
    private void exportarArquivosExternos() throws Exception {

        //psArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectExportarArquivosExternos);
        try {
            psExportarArquivosExternos.setString(1, executavel);
            rsArquivosExternos = psExportarArquivosExternos.executeQuery();

            while (rsArquivosExternos.next()) {

                fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                if (tipoInterface.trim().equals("S")) {
                    fileName = path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (tipoInterface.trim().equals("E")) {
                    fileName = path + "\\" + userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else {
                    fileName = path + "\\" + userNameSys + "\\Arquivos\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }
                logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if (clob != null) {
                    /******************************************
                     * Gerando arquivos na pasta de Arquivos
                     ******************************************/
                    StringBuffer strOut = new StringBuffer();
                    String aux;

                    // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                    BufferedReader br = new BufferedReader(clob.getCharacterStream());

                    while ((aux = br.readLine()) != null) {
                        strOut.append(aux);
                        strOut.append(QUEBRA_LINHA);
                    }

                    try {
                        File file = new File(fileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        FileWriter fw = new FileWriter(file, false);

                        fw.write(strOut.toString(), 0, strOut.length());
                        fw.close();

                        logMessage("File " + fileName + " was succesfull generated.");

                    } catch (IOException ioex) {
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                } else {
                    logMessage("No data are being generated");
                    logMessage("File " + fileName + " wasn't generated.");
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

    /**************************************************************************
     * <b>Exportar Tabelas sem vinculo com alguma interface</b>
     **************************************************************************/
    private void tabInterfaceSemPermissao() throws Exception {
        String sSelectTabsSemPermissao = "select table_name from tab_interface tab where tab.table_name not in (select table_name from permissao_tabela)";
        PreparedStatement psTabsSemPermissao = ConnectionInout.getConnection().prepareStatement(sSelectTabsSemPermissao);
        ResultSet rsTabsSemPermissao = psTabsSemPermissao.executeQuery();

        try {
            while (rsTabsSemPermissao.next()) {
                new TabInterfaces(rsTabsSemPermissao.getString("TABLE_NAME"), idInterface, idSistema, this);
            }
        } catch (Exception ex) {
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

    // Valida linha de commando
    public void validateCommandLine(String args[]) throws Exception {
        String sChave = null;

        for (int i = 0; i < args.length; i++) {
            // valida parâmetro importacao
            //armazena o usuário e senha a utilizar durante o processamento
            sChave = "-user=";
            if (args[i].startsWith(sChave)) {
                String s_Usuario_Senha = args[i].substring(sChave.length(), args[i].length());
                s_User = s_Usuario_Senha.substring(0, s_Usuario_Senha.indexOf("/"));
                s_Pass = s_Usuario_Senha.substring(s_Usuario_Senha.indexOf("/") + 1);
            }
            // Valida parametro CONN
            //armazena dados sobre o TNS
            sChave = "-conn=";
            if (args[i].startsWith(sChave)) {
                s_Conn = args[i].substring(sChave.length(), args[i].length());
            }

            //armazena o schema a utilizar durante o processamento corrente
            sChave = "-sessionschema=";
            if (args[i].startsWith(sChave)) {
                sessionSchema = args[i].substring(sChave.length(), args[i].length());
            }

            //armazena a ROLE utilizada durante o processamento
            sChave = "-role=";
            if (args[i].startsWith(sChave)) {
                role = args[i].substring(sChave.length(), args[i].length());
            }
        }

        // Parametros recusados
        if ((s_User == null) || (s_User.equals(""))) {
            throw new Exception("ERROR: Parameter missing [-user] ex: -user=USER/PASSWORD");
        } else if ((s_Conn == null) || (s_Conn.equals(""))) {
            throw new Exception("ERROR: Parameter missing [-conn] ex: -conn=jdbc:oracle:thin:@HOST:PORT:SERVICE_NAME");
        }

    }

    public static void main(String[] args) {
        // foi utilizado somente para teste
        try {
            //System.setErr(System.out);
            ArrayList arr = new ArrayList();
            arr.add("T");

            CVSStructure spool = new CVSStructure();
            spool.validateCommandLine(args);
            //spool.connectOracle(args[0], args[1]);
            spool.spoolCVSStruture(arr, null);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            e.printStackTrace(System.err);
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
     * @return the ioUser
     */
    public Usuario getIoUser() {
        return ioUser;
    }

    /**
     * @return the itUser
     */
    public Usuario getItUser() {
        return itUser;
    }

    /**
     * @param itUser the itUser to set
     */
    public void setItUser(Usuario itUser) {
        this.itUser = itUser;
    }

    /**
     * @return the bgUser
     */
    public Usuario getBgUser() {
        return bgUser;
    }

    /**
     * @param bgUser the bgUser to set
     */
    public void setBgUser(Usuario bgUser) {
        this.bgUser = bgUser;
    }

    /**
     * @return the bsUser
     */
    public Usuario getBsUser() {
        return bsUser;
    }

    /**
     * @param bsUser the bsUser to set
     */
    public void setBsUser(Usuario bsUser) {
        this.bsUser = bsUser;
    }

    /**
     * @return the isUser
     */
    public Usuario getIsUser() {
        return isUser;
    }

    /**
     * @param isUser the isUser to set
     */
    public void setIsUser(Usuario isUser) {
        this.isUser = isUser;
    }

    /**
     * @return the ceUser
     */
    public Usuario getCeUser() {
        return ceUser;
    }

    /**
     * @param ceUser the ceUser to set
     */
    public void setCeUser(Usuario ceUser) {
        this.ceUser = ceUser;
    }

    /**
     * @return the ciUser
     */
    public Usuario getCiUser() {
        return ciUser;
    }

    /**
     * @param ciUser the ciUser to set
     */
    public void setCiUser(Usuario ciUser) {
        this.ciUser = ciUser;
    }

    /**
     * @return the exUser
     */
    public Usuario getExUser() {
        return exUser;
    }

    /**
     * @param exUser the exUser to set
     */
    public void setExUser(Usuario exUser) {
        this.exUser = exUser;
    }

    /**
     * @return the dbUser
     */
    public Usuario getDbUser() {
        return dbUser;
    }

    /**
     * @param dbUser the dbUser to set
     */
    public void setDbUser(Usuario dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * @param ioUser the ioUser to set
     */
    public void setIoUser(Usuario ioUser) {
        this.ioUser = ioUser;
    }

    /**
     * @return the caiUser
     */
    public Usuario getCaiUser() {
        return caiUser;
    }

    /**
     * @param caiUser the caiUser to set
     */
    public void setCaiUser(Usuario caiUser) {
        this.caiUser = caiUser;
    }

    /**
     * @return the appsUser
     */
    public Usuario getAppsUser() {
        return appsUser;
    }

    /**
     * @param appsUser the appsUser to set
     */
    public void setAppsUser(Usuario appsUser) {
        this.appsUser = appsUser;
    }

    /**
     * @return the dbLinkCai
     */
    public String getDbLinkCai() {
        return dbLinkCai;
    }

    /**
     * @param dbLinkCai the dbLinkCai to set
     */
    public void setDbLinkCai(String dbLinkCai) {
        this.dbLinkCai = dbLinkCai;
    }

    /**
     * @return the dbLinkApps
     */
    public String getDbLinkApps() {
        return dbLinkApps;
    }

    /**
     * @param dbLinkApps the dbLinkApps to set
     */
    public void setDbLinkApps(String dbLinkApps) {
        this.dbLinkApps = dbLinkApps;
    }
}
