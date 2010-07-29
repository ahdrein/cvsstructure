package sfw.structure.cvsStructure;

import sfw.structure.main.*;
import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import sfw.structure.validaScripts.SfwValidaScripts;

public class CVSStructureBackup {
	private Connection conn;
    private Connection connIt;
	public String s_User;
	public String s_Pass;
	public String s_Conn;
	public String s_ItUser;
	public String s_ItPass;
 	private String s_File;
 	private String sSessionSchema;
 	private String sRole;
    private ArrayList dirScriptsValida = new ArrayList();

    /* Selects Utilizados */
    private String sSelect;
	private String sSelectGerarArquivosExternos;
    private String sSelectArquivosExternosNaoGerados;
    private String sSelectCountArquivosExternosNaoGerados;
	private String sSelectExportarArquivosExternos;
	private String sSelectTabInterface;
	private String sSelectPermissaoTabela;
	private String sSelectColunasTabInterface;
    private String sSelectCountInterface;
    private String sSelectCountPermissaoTabela;
    private String sSelectUserUsers;
    private String sSelectArquivosSynonymos;
    private String sSelectSequences;
    private String sSynonyms;
    private String sCountArquivosSynonyms;
    private String sFoundSQLInterface;
    private String sFoundBATInterface;
    private String sFoundDadosInterface;
    private String sSelectIntMapeamento;
    private String sSelectIntMapeamentoColuna;
    private String sSelectSapMapeamento;
    private String sSelectSapMapeamentoColuna;
    private String sSelectPermissaoTabelaTab;
    private String sCountPermissaoTabelaTab;
    private String sCountSelect;
    private String sFoundObjects;
    private String sSistema;
    private String sInterfaceDaTabela;
    private String sView;
    private String sReferenciasObjetos;
    private String sInsert;
    private String sTable;

    /* Variaveis */
    public static String IDInterface;
	private String fileName = "";
	public static String fileNameScripts = "";
	public static String sExecutavel = "";
	public static String sTipoInterface = "";
	public static String sIdSistema = "";
	public static String sDescricao = "";
	public static String sUserName = "";
	public static String sTempoMedio = "";
    //public String sQuebraLinha = System.getProperty("line.separator");
    public static String sQuebraLinha = "\r\n";
	public static String sExecutavelCompl = "";
	public static String path;
	private Clob clob;
    public String sDebug = "N";
    private boolean flag = true;
    private String sCaminhaGeracao;
    public static String id_sistema_it = "";
    private String sSynonymsAll = "";

    /* Filtros */
	private PreparedStatement psInterfaces = null;
	private ResultSet rsInterfaces = null;
	private PreparedStatement psExportarArquivosExternos = null;
    private PreparedStatement psUsersUser = null;
    private ResultSet rsUsersUser = null;
	private PreparedStatement psGerarArquivosExternos = null;
	private ResultSet rsArquivosExternos = null;

	private PreparedStatement psUserSource = null;
    private PreparedStatement psUserSourceIt = null;
	private ResultSet rsUserSource = null;

    private PreparedStatement psArquivosSynonyms = null;
    private PreparedStatement psArquivosSynonymsIT = null;
	private ResultSet rsArquivosSynonyms = null;

    private PreparedStatement psSynonyms = null;
    private PreparedStatement psSynonymsIT = null;
	private ResultSet rsSynonyms = null;

    private PreparedStatement psCountArquivosSynonyms = null;
    private PreparedStatement psCountArquivosSynonymsIT = null;
	private ResultSet rsCountArquivosSynonyms = null;

    private PreparedStatement psFoundSQLInterface = null;
	private ResultSet rsFoundSQLInterface = null;

    private PreparedStatement psFoundBATInterface = null;
	private ResultSet rsFoundBATInterface = null;

    private PreparedStatement psFoundDadosInterface = null;
	private ResultSet rsFoundDadosInterface = null;

    private PreparedStatement psGerarArquivosExternosNaoGerados;
    private ResultSet rsGerarArquivosExternosNaoGerados;
    
    private PreparedStatement psCountArquivosExternosNaoGerados;
    private ResultSet rsCountArquivosExternosNaoGerados;

    private PreparedStatement psIntMapeamento = null;
    private PreparedStatement psIntMapeamentoIT = null;
	private ResultSet rsIntMapeamento = null;

    private PreparedStatement psIntMapeamentoColuna = null;
    private PreparedStatement psIntMapeamentoColunaIT = null;
	private ResultSet rsIntMapeamentoColuna = null;

    private PreparedStatement psSapMapeamento = null;
    private PreparedStatement psSapMapeamentoIT = null;
	private ResultSet rsSapMapeamento = null;

    private PreparedStatement psSapMapeamentoColuna = null;
    private PreparedStatement psSapMapeamentoColunaIT = null;
	private ResultSet rsSapMapeamentoColuna = null;

	private PreparedStatement psPermissaoTabela = null;
	private ResultSet rsPermissaoTabela = null;

	private PreparedStatement psPermissaoTabelaTab = null;
	private ResultSet rsPermissaoTabelaTab = null;

	private PreparedStatement psCountPermissaoTabelaTab = null;
	private ResultSet rsCountPermissaoTabelaTab = null;

	private PreparedStatement psCountInterface = null;
    private PreparedStatement psCountInterface2 = null;
	private ResultSet rsCountInterface = null;

	private PreparedStatement psColunasTabInterface = null;
	private ResultSet rsColunasTabInterface = null;

	private PreparedStatement psTabInterface = null;
	private ResultSet rsTabInterface = null;

	private PreparedStatement psCountPermissaoTabela = null;
	private ResultSet rsCountPermissaoTabela = null;

	private PreparedStatement psFoundObjectsIT = null;
	private ResultSet rsFoundObjectsIT = null;

    private PreparedStatement psTableSourceIT = null;
	private ResultSet rsTableSourceIT = null;

    private PreparedStatement psGerarReferenciasObjetos = null;
    private PreparedStatement psGerarReferenciasObjetosIT = null;
    private ResultSet rsGerarReferenciasObjetos = null;

    private PreparedStatement psGerarSequences = null;
    private PreparedStatement psGerarSequencesIT = null;
    private ResultSet rsGerarSequences = null;

    private PreparedStatement psSistema = null;
    private ResultSet rsSistema = null;

    private PreparedStatement psInterfaceDaTabela = null;
    private ResultSet rsInterfaceDaTabela = null;

    private PreparedStatement psView = null;
    private PreparedStatement psViewIT = null;
    private ResultSet rsView = null;

    private PreparedStatement psCreateTable = null;
    private PreparedStatement psInsertReferencesObjects = null;

    private PreparedStatement psCreateTableIT = null;
    private PreparedStatement psInsertReferencesObjectsIT = null;
    private PreparedStatement psDropTableIT = null;

    static JTextArea textAreaCVS;
    static JFrameCVS jframe;


   // Imprime Mensagens na Tela e coloca mensagem no log
	public static void logMessage(String p_msg)
	{
		System.out.println(p_msg);
        if(jframe != null){
            jframe.setTextArea(p_msg + sQuebraLinha);
        }
	}

    private void intialize() throws SQLException, IOException{
        
        //System.getProperty("user.dir");

        sCaminhaGeracao = jframe.getTxCaminhaGeracao();
        if (sCaminhaGeracao.equals(".\\")){
            this.path = new File(".").getCanonicalPath();
        }else{
            if(sCaminhaGeracao.substring(sCaminhaGeracao.length()-1).equals("\\")){
                this.path = sCaminhaGeracao.substring(0, sCaminhaGeracao.length()-1);
            }else{
                this.path = sCaminhaGeracao;
            }
        }

        this.sSelectUserUsers = "select username from user_users";

        this.sSelectTabInterface = "select table_name table_name, nvl(ctl_name, '') ctl_name, nvl(prefix_file, '') prefix_file, ctl_fixo ctl_fixo, nvl(gerar_ctl, '') gerar_ctl, nvl(prioridade, '') prioridade, nvl(descricao, '') descricao, nvl(tipo_interface, '') tipo_interface, nvl(odbc_source_name, '') odbc_source_name, nvl(odbc_user, '') odbc_user, nvl(odbc_password, '') odbc_password, nvl(odbc_table_name, '') odbc_table_name, nvl(oracle_initial_extent, '') oracle_initial_extent, nvl(oracle_next_extent, '') oracle_next_extent, nvl(oracle_index_tablespace, '') oracle_index_tablespace, nvl(odbc_where, '') odbc_where, nvl(odbc_select_especifico, '') odbc_select_especifico, nvl(eliminar_reg_execucao, '') eliminar_reg_execucao, nvl(comando_extra_loader, '') comando_extra_loader, nvl(trigger1, '') trigger1, nvl(trigger2, '') trigger2, nvl(id_sistema, '') id_sistema, nvl(procedure_name, '') procedure_name, nvl(separador, '') separador from tab_interface where table_name = ?";

        this.sSelectPermissaoTabela = "select id_interface, table_name from permissao_tabela where id_interface = ?";

		this.sSelectColunasTabInterface = "select * from colunas_tab_interface where table_name = ?";

        this.sSistema = "select * from sistema where user_oracle like '%'|| ? || '%'";

        this.sView = "select * from user_views";

        this.sInterfaceDaTabela = "select distinct it.* "+
                "from permissao_tabela ta,"+
                "     sistema_interface it"+
                " where ta.table_name like '%' || upper( ? ) ||'%'"+
                " and it.id_interface = ta.id_interface";

		this.sSelectGerarArquivosExternos = "select NOME_ARQUIVO," +
		  "PATH_RELATIVO," +
		  "DESCRICAO," +
		  "CONTEUDO" +
		  " from arquivo_externo" +
		  " where arquivo_externo.nome_arquivo like '%'|| ? || '%'";

		this.sSelectExportarArquivosExternos = "select NOME_ARQUIVO," +
		  "PATH_RELATIVO," +
		  "DESCRICAO," +
		  "CONTEUDO" +
		  " from arquivo_externo" +
		  " where arquivo_externo.nome_arquivo like '%'|| ? || '%'";

        // Obtendo condições do select para encontrar o as interfaces existentes
		this.sSelect = "select replace( replace( replace( substr(interfaces.executavel, instr(interfaces.executavel, '\\')+1, length(interfaces.executavel)), '#IDENT#INTERFACES\\SAP\\', ''), '#IDENT#INTERFACES\\', '') , '#IDENT#INTEGRACAO\\', '') executavel," +
    			  "interfaces.id_interface," +
    			  "interfaces.tipo_interface,"+
    			  "nvl(sistema_interface.id_sistema, 'sfw') id_sistema,"+
    			  "interfaces.descricao,"+
    			  "interfaces.username,"+
    			  "interfaces.tempo_medio,"+
    			  "interfaces.executavelCompl executavelCompl"+
    			  " from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,"+
    			  " sistema_interface"+
    			  " where interfaces.id_interface = sistema_interface.id_interface (+)";

         this.sCountSelect = "select count(*) TOTAL"+
                    " from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,"+
                    " sistema_interface"+
                    " where interfaces.id_interface = sistema_interface.id_interface (+)"+
                    " and interfaces.id_interface like '%'||?||'%'";

         this.sSelectCountInterface  = "select count(*) total_arquivos"+
              " from (select replace(replace(replace(substr(interfaces.executavel,"+
              " instr(interfaces.executavel, '\\') + 1,"+
              "length(interfaces.executavel)),"+
              "'#IDENT#INTERFACES\\SAP\\',"+
              "''),"+
              "'#IDENT#INTERFACES\\',"+
              "''),"+
              "'#IDENT#INTEGRACAO\\',"+
              "'') executavel"+
              " from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces)"+
              " where executavel like '%'||?||'%'";

        this.sSelectCountPermissaoTabela = "select count(*) total_interfaces from permissao_tabela where table_name = ?";

        this.sSelectPermissaoTabelaTab = "select * from permissao_tabela where table_name = ?";

        this.sCountPermissaoTabelaTab = "select count(*) TOTAL from permissao_tabela where table_name = ?";

        this.sSelectArquivosExternosNaoGerados = "select distinct nome_arquivo, contem, tipo from (" +
            "        select a.nome_arquivo, substr(v1.nome_arquivo, 1, instr(v1.nome_arquivo, '.')-1) contem, substr(v1.nome_arquivo, instr(v1.nome_arquivo, '.')+1, length(v1.nome_arquivo)) tipo" +
            "          from   arquivo_externo a, " +
            "                (select nome_arquivo from arquivo_externo) v1" +
            "         where upper(a.conteudo) like '%' || upper(v1.nome_arquivo) || '%'" +
            "         union" +
            "        select a.nome_arquivo, v2.object_name contem, v2.tipo" +
            "        from arquivo_externo a," +
            "                (       " +
            "                    select * from " +
            "                    (" +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PROCEDURE' " +
            "                    union all " +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'FUNCTION' " +
            "                    union all " +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PACKAGE' " +
            "                    ) my_all_objects" +
            "                    where my_all_objects.object_name not in " +
            "                    ( 'SET_CONTEUDO_ARQ' ," +
            "                    'PROC_CTL_FIXO' ," +
            "                    'PROC_CONTEUDO_ARQ' ," +
            "                    'PROCESSA_INTERFACES_IN' ," +
            "                    'PROCESSA_ID_SISTEMA' ," +
            "                    'PROCESSA_GRANTS_SISTEMA' ," +
            "                    'PROCESSA_ERRO_INTERFACE' ," +
            "                    'PRC_UPDATE_CONCATENA_LONG' ," +
            "                    'PRC_SINCRONIZA_TABELA' ," +
            "                    'PRC_PROCESSA_DIRETO' ," +
            "                    'PRC_POPULA_SAP_COLUMNS' ," +
            "                    'PRC_INSERE_LOG_GERAL' ," +
            "                    'PRC_INDEXA_HEADERS' ," +
            "                    'NOTIFICA_RESUMO_INTERFACE' ," +
            "                    'INTERFACE_EXCLUI_REGISTROS' ," +
            "                    'INSERE_NOTIFICACAO_TABELA' ," +
            "                    'INSERE_INFO_ODBC' ," +
            "                    'INSERE_HISTORICO_SAIDA_ID_IMP' ," +
            "                    'INSERE_HISTORICO_SAIDA' ," +
            "                    'INSERE_ERRO_IMPORTACAO' ," +
            "                    'INFORMA_SUCESSO_ERRO' ," +
            "                    'INFORMA_QTDE_REGISTROS_IMPORT' ," +
            "                    'INFORMA_OBS_IMPORTACAO' ," +
            "                    'INFORMA_OBS_ID_IMPORTACAO' ," +
            "                    'GET_CONTEUDO_ARQ' ," +
            "                    'FINALIZA_INTERFACE' ," +
            "                    'EXCLUI_ERRO_IMPORTACAO' ," +
            "                    'ELIMINA_REGISTROS_ANTIGOS' ," +
            "                    'ELIMINA_INTERFACE' ," +
            "                    'CONCATENA_CONTEUDO' ," +
            "                    'COMPILE_INVALID' ," +
            "                    'ATUALIZA_IMPORTACAO_EXECUTADA' )" +
            "                )  v2 " +
            "          where upper(a.conteudo) like '%' || upper(v2.object_name) || '%'" +
            "          ) p1" +
            " where not exists (" +
            "select 1 " +
            "from interfaces i " +
            "where i.executavel like '%' || p1.contem || '%' )";

        this.sSelectCountArquivosExternosNaoGerados = "select distinct count(*) TOTAL_ARQUIVOS from (" +
            "        select a.nome_arquivo" +
            "          from   arquivo_externo a " +
                        "         where upper(a.conteudo) like '%' || upper(?) || '%'" +
            "         union" +
            "        select a.nome_arquivo " +
            "        from arquivo_externo a," +
            "                (       " +
            "                    select * from " +
            "                    (" +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PROCEDURE' " +
            "                    union all " +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'FUNCTION' " +
            "                    union all " +
            "                    select object_name, OBJECT_TYPE tipo from user_objects a where object_type = 'PACKAGE' " +
            "                    ) my_all_objects" +
            "                    where my_all_objects.object_name not in " +
            "                    ( 'SET_CONTEUDO_ARQ' ," +
            "                    'PROC_CTL_FIXO' ," +
            "                    'PROC_CONTEUDO_ARQ' ," +
            "                    'PROCESSA_INTERFACES_IN' ," +
            "                    'PROCESSA_ID_SISTEMA' ," +
            "                    'PROCESSA_GRANTS_SISTEMA' ," +
            "                    'PROCESSA_ERRO_INTERFACE' ," +
            "                    'PRC_UPDATE_CONCATENA_LONG' ," +
            "                    'PRC_SINCRONIZA_TABELA' ," +
            "                    'PRC_PROCESSA_DIRETO' ," +
            "                    'PRC_POPULA_SAP_COLUMNS' ," +
            "                    'PRC_INSERE_LOG_GERAL' ," +
            "                    'PRC_INDEXA_HEADERS' ," +
            "                    'NOTIFICA_RESUMO_INTERFACE' ," +
            "                    'INTERFACE_EXCLUI_REGISTROS' ," +
            "                    'INSERE_NOTIFICACAO_TABELA' ," +
            "                    'INSERE_INFO_ODBC' ," +
            "                    'INSERE_HISTORICO_SAIDA_ID_IMP' ," +
            "                    'INSERE_HISTORICO_SAIDA' ," +
            "                    'INSERE_ERRO_IMPORTACAO' ," +
            "                    'INFORMA_SUCESSO_ERRO' ," +
            "                    'INFORMA_QTDE_REGISTROS_IMPORT' ," +
            "                    'INFORMA_OBS_IMPORTACAO' ," +
            "                    'INFORMA_OBS_ID_IMPORTACAO' ," +
            "                    'GET_CONTEUDO_ARQ' ," +
            "                    'FINALIZA_INTERFACE' ," +
            "                    'EXCLUI_ERRO_IMPORTACAO' ," +
            "                    'ELIMINA_REGISTROS_ANTIGOS' ," +
            "                    'ELIMINA_INTERFACE' ," +
            "                    'CONCATENA_CONTEUDO' ," +
            "                    'COMPILE_INVALID' ," +
            "                    'ATUALIZA_IMPORTACAO_EXECUTADA' )" +
            "                )  v2 " +
            "          where upper(a.conteudo) like '%' || upper(?) || '%'" +
            "          ) p1" +
            " where not exists (" +
            "select 1 " +
            "from interfaces i " +
            "where i.executavel like '%' || p1.contem || '%' )";

          // Exibe todos os sysnonymos o arquivos que o utilizam
        this.sSelectArquivosSynonymos = "select distinct a.nome_arquivo, v1.synonym_name" +
            " from arquivo_externo a, " +
            "     (select synonym_name from user_synonyms) v1 " +
            "where upper(a.conteudo) like '%' || upper(v1.synonym_name) ||'%'";


        // Obetem os dados do synonymo
        this.sSynonyms = "select * from user_synonyms where synonym_name = ?";

        this.sSynonymsAll = "select * from user_synonyms";

        // Contagem de arquivos onde o synonimo é utilizado
        this.sCountArquivosSynonyms = "select distinct count(*) TOTAL_ARQUIVOS " +
        "from arquivo_externo a " +
        "where upper(a.conteudo) like '%' || upper( ? ) ||'%'";

        // Encontrar a Interface do SQL
        this.sFoundSQLInterface = "select * from interfaces i," +
        "              (  " +
        "                 select a.nome_arquivo" +
        "                  from   arquivo_externo a " +
        "                 where upper(a.conteudo) like '%' || upper( ? ) || '%'" +
        "                   and exists ( select 1" +
        "                                from interfaces i " +
        "                                where i.executavel like '%' || a.nome_arquivo || '%' ) " +
        "               ) a " +
        " where i.executavel like '%' || a.nome_arquivo || '%'";


        // Encontrar a interface do BAT
        this.sFoundBATInterface = "select * from interfaces i " +
        "where i.executavel like '%' || ? || '%'";


        // Obtendo condições do select para encontrar o as interfaces existentes
		this.sFoundDadosInterface = "select replace( replace( replace( substr(interfaces.executavel, instr(interfaces.executavel, '\\')+1, length(interfaces.executavel)), '#IDENT#INTERFACES\\SAP\\', ''), '#IDENT#INTERFACES\\', '') , '#IDENT#INTEGRACAO\\', '') executavel," +
    			  "interfaces.id_interface," +
    			  "interfaces.tipo_interface,"+
    			  "nvl(sistema_interface.id_sistema, 'sfw') id_sistema,"+
    			  "interfaces.descricao,"+
    			  "interfaces.username,"+
    			  "interfaces.tempo_medio,"+
    			  "interfaces.executavelCompl executavelCompl"+
    			  " from ( select substr(executavel, instr(executavel, '\\')+1, (instr(executavel, '.BAT'))-(instr(executavel, '\\')+1)) executavel, id_interface, tipo_interface, descricao, username, tempo_medio, executavel executavelCompl from interfaces where executavel like '%.BAT%') interfaces,"+
    			  " sistema_interface"+
    			  " where interfaces.id_interface = sistema_interface.id_interface (+)" +
                  "and sistema_interface.id_interface = ?";

        this.sFoundObjects = "select object_name, OBJECT_TYPE tipo from user_objects a "+
                    "where object_type in ('PROCEDURE','FUNCTION','PACKAGE','TABLE')"+
                    "and object_name not in "+
                    "( 'SET_CONTEUDO_ARQ' ,"+
                    "'PROC_CTL_FIXO' ,"+
                    "'PROC_CONTEUDO_ARQ' ,"+
                    "'PROCESSA_INTERFACES_IN' ,"+
                    "'PROCESSA_ID_SISTEMA' ,"+
                    "'PROCESSA_GRANTS_SISTEMA' ,"+
                    "'PROCESSA_ERRO_INTERFACE' ,"+
                    "'PRC_UPDATE_CONCATENA_LONG' ,"+
                    "'PRC_SINCRONIZA_TABELA' ,"+
                    "'PRC_PROCESSA_DIRETO' ,"+
                    "'PRC_POPULA_SAP_COLUMNS' ,"+
                    "'PRC_INSERE_LOG_GERAL' ,"+
                    "'PRC_INDEXA_HEADERS' ,"+
                    "'NOTIFICA_RESUMO_INTERFACE' ,"+
                    "'INTERFACE_EXCLUI_REGISTROS' ,"+
                    "'INSERE_NOTIFICACAO_TABELA' ,"+
                    "'INSERE_INFO_ODBC' ,"+
                    "'INSERE_HISTORICO_SAIDA_ID_IMP' ,"+
                    "'INSERE_HISTORICO_SAIDA' ,"+
                    "'INSERE_ERRO_IMPORTACAO' ,"+
                    "'INFORMA_SUCESSO_ERRO' ,"+
                    "'INFORMA_QTDE_REGISTROS_IMPORT' ,"+
                    "'INFORMA_OBS_IMPORTACAO' ,"+
                    "'INFORMA_OBS_ID_IMPORTACAO' ,"+
                    "'GET_CONTEUDO_ARQ' ,"+
                    "'FINALIZA_INTERFACE' ,"+
                    "'EXCLUI_ERRO_IMPORTACAO' ,"+
                    "'ELIMINA_REGISTROS_ANTIGOS' ,"+
                    "'ELIMINA_INTERFACE' ,"+
                    "'CONCATENA_CONTEUDO' ,"+
                    "'COMPILE_INVALID' ,"+
                    "'ATUALIZA_IMPORTACAO_EXECUTADA',"+
                    "'INT_MAPEAMENTO_LAYOUT',"+
                    "'INT_MAPEAMENTO_COLUNA',"+
                    "'PKG_IT_GEN',"+
                    "'PKG_INT_IT_SFW',"+
                    "'SFWXMLCONCAT',"+
                    "'SFWXMLELEMENT',"+
                    "'SFWXMLFOREST' "+
                    ")";

                sInsert = "insert into TMP_CVS_STRCTURE ("+
        "select *"+
        "  from (select distinct LEVEL,"+
        "                        NAME,"+
        "                        TYPE,"+
        "                        u.referenced_owner,"+
        "                        u.referenced_name,"+
        "                        u.referenced_type"+
        "          from user_dependencies u"+
        "         where REFERENCED_OWNER = ?  and "+
        "           name <> referenced_name"+
        "        connect by prior referenced_name = name)"+
        " where (TYPE in ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))"+
        "   and (REFERENCED_TYPE in"+
        "       ('FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE'))"+
        ")";

         sTable = "create table TMP_CVS_STRCTURE"+
                            "("+
                            " LEVEL_OBJ        number,"+
                            " NAME             varchar2(100),"+
                            " TYPE             varchar2(100),"+
                            " REFERENCED_OWNER varchar2(100),"+
                            " REFERENCED_NAME  varchar2(100),"+
                            " REFERENCED_TYPE  varchar2(100)"+
                            ")";

        /*
        this.sReferenciasObjetos = "select *"+
                " from "+
                " (select distinct"+
                "       LEVEL,"+
                "       NAME,"+
                "       TYPE,"+
                "       u.referenced_owner,"+
                "       u.referenced_name,"+
                "       u.referenced_type"+
                "  from user_dependencies u"+
                " where REFERENCED_OWNER =  ? "+
                "   and name <> referenced_name"+
                " START WITH name like '%' || ? || '%'"+
                //" -- connect by prior name = referenced_name -- mostra referencias das referencias (n indireções)"+
                " connect by prior referenced_name= name"+ //   -- mostra quem é chamado (somente as referencias) (uma indireção)
                " )"+
                " where (TYPE in ('FUNCTION','PACKAGE','PACKAGE BODY','PROCEDURE'))"+
                "  and (REFERENCED_TYPE in ('FUNCTION','PACKAGE','PACKAGE BODY','PROCEDURE'))"+
                "  and REFERENCED_NAME != ? ";
        */

        this.sReferenciasObjetos = "select distinct "+
                " name, type, referenced_owner, referenced_name, referenced_type"+
                " from TMP_CVS_STRCTURE "+
                " where name like '%' || ? || '%'";

        this.sSelectSequences = "select * from user_sequences";

        this.sSelectIntMapeamento = "select * from int_mapeamento_layout";
        this.sSelectIntMapeamentoColuna = "select * from int_mapeamento_coluna where id = ?";

        this.sSelectSapMapeamento = "select * from sap_interface_tables";
        this.sSelectSapMapeamentoColuna = "select * from sap_interface_columns where table_id = ?";

        psColunasTabInterface = this.conn.prepareStatement(sSelectColunasTabInterface);
        psTabInterface = this.conn.prepareStatement(sSelectTabInterface);
        psUsersUser = this.conn.prepareStatement(this.sSelectUserUsers);
        psExportarArquivosExternos = this.conn.prepareStatement(this.sSelectExportarArquivosExternos);
        psGerarArquivosExternos = this.conn.prepareStatement(this.sSelectGerarArquivosExternos);
        psGerarArquivosExternosNaoGerados = this.conn.prepareStatement(this.sSelectArquivosExternosNaoGerados);
        psCountInterface = this.conn.prepareStatement(this.sSelectCountInterface);
        psPermissaoTabela = this.conn.prepareStatement(sSelectPermissaoTabela);
        psCountPermissaoTabela = this.conn.prepareStatement(this.sSelectCountPermissaoTabela);

        psInterfaces = this.conn.prepareStatement(this.sSelect);
        psCountInterface2 = this.conn.prepareStatement(this.sCountSelect);

        psUserSource = this.conn.prepareStatement("select * from user_source where type = ? and name = ?");



        if(connIt != null){
            psUserSourceIt = this.connIt.prepareStatement("select * from user_source where type = ? and name = ?");
            psTableSourceIT = this.connIt.prepareStatement("SELECT dbms_metadata.get_ddl('TABLE', ?) conteudo FROM DUAL");
            psFoundObjectsIT = this.connIt.prepareStatement(sFoundObjects);

            psIntMapeamentoIT = this.connIt.prepareStatement(this.sSelectIntMapeamento);
            psIntMapeamentoColunaIT = this.connIt.prepareStatement(this.sSelectIntMapeamentoColuna);

            psSapMapeamentoIT = this.connIt.prepareStatement(this.sSelectSapMapeamento);
            psSapMapeamentoColunaIT = this.connIt.prepareStatement(this.sSelectSapMapeamentoColuna);

            psGerarSequencesIT = this.connIt.prepareStatement(this.sSelectSequences);

            psViewIT = this.connIt.prepareStatement(this.sView);

            psGerarReferenciasObjetosIT = this.connIt.prepareStatement(this.sReferenciasObjetos);

            psCreateTableIT = this.connIt.prepareCall(sTable);

            psInsertReferencesObjectsIT = this.connIt.prepareCall(sInsert);
        }

        psFoundSQLInterface = this.conn.prepareStatement(this.sFoundSQLInterface);
        psFoundBATInterface = this.conn.prepareStatement(this.sFoundBATInterface);
        psFoundDadosInterface = this.conn.prepareStatement(this.sFoundDadosInterface);

        psIntMapeamento = this.conn.prepareStatement(this.sSelectIntMapeamento);
        psIntMapeamentoColuna = this.conn.prepareStatement(this.sSelectIntMapeamentoColuna);

        psSapMapeamento = this.conn.prepareStatement(this.sSelectSapMapeamento);
        psSapMapeamentoColuna = this.conn.prepareStatement(this.sSelectSapMapeamentoColuna);

        psGerarSequences = this.conn.prepareStatement(this.sSelectSequences);

        psInterfaceDaTabela = this.conn.prepareStatement(this.sInterfaceDaTabela);

        psView = this.conn.prepareStatement(this.sView);
        
        psGerarReferenciasObjetos = this.conn.prepareStatement(this.sReferenciasObjetos);

        psCreateTable = this.conn.prepareCall(sTable);

        psInsertReferencesObjects = this.conn.prepareCall(sInsert);
    }

   	/**************************************************************************
	 * <b>Connect Oracle</b>
	 **************************************************************************/
    public void connectOracle(String s_User, String s_Pass) throws SQLException{
        // registra driver oracle
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        // connecta no banco de dados
        logMessage("Connecting to database...");
        conn = DriverManager.getConnection(s_Conn, s_User, s_Pass);
        if(s_ItUser != null && !s_ItUser.equals("") && !s_ItPass.equals("") && s_ItPass != null){
            connIt = DriverManager.getConnection(s_Conn, s_ItUser, s_ItPass);
        }

        System.out.println("connected.");

        System.out.println("Alterando configurações da conexão...");
       	//changeConnection();
       	System.out.println("Configurações da conexão alteradas.");
       	conn.setAutoCommit(false);
    }

	private void changeConnection(){
		try{
            /*
	     	if ( sSessionSchema != null ){
                saveLog("Alterando SESSION SCHEMA da conexão...");
	     		PreparedStatement psAlteraSession = this.conn.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = " + sSessionSchema );
	    		psAlteraSession.executeUpdate();
                saveLog("SESSION SCHEMA da conexão alterado.");
	     	}

			if ( sRole != null ){
                saveLog("Alterando ROLE da conexão...");
	     		PreparedStatement psDefineRole = this.conn.prepareStatement("begin dbms_session.set_role('" + sRole + "'); end;");
	    		psDefineRole.execute();
                saveLog("ROLE da conexão alterado.");
	    	}
            */
	     	//if ( sSessionSchema != null ){
                saveLog("Alterando SESSION SCHEMA da conexão...");
	     		PreparedStatement psAlteraSession = this.conn.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = " + "sfwioelx" );
	    		psAlteraSession.executeUpdate();
                saveLog("SESSION SCHEMA da conexão alterado.");
	     	//}

			//if ( sRole != null ){
                saveLog("Alterando ROLE da conexão...");
	     		PreparedStatement psDefineRole = this.conn.prepareStatement("begin dbms_session.set_role('" + "rsfwioelx" + "'); end;");
	    		psDefineRole.execute();
                saveLog("ROLE da conexão alterado.");
	    	//}
		}catch (SQLException e){
    		String erro = "";
			saveLog("Error connecting " + this.s_File);
			System.out.println(e.getLocalizedMessage());

			for(int i=0; i<e.getStackTrace().length; i++){
				erro += "\t" + e.getStackTrace()[i].getLineNumber() + " " + e.getStackTrace()[i].toString() + sQuebraLinha;
			}
			System.out.println(erro);

            saveLog(e.getClass().toString(), e.getStackTrace());
		}
	}

	/**************************************************************************
	 * <b>Save Logs</b>
	 * @param sLog
	 * @param stackTrace
     * @param sUserName
	 **************************************************************************/
	public void saveLog(String sLog, StackTraceElement[] stackTrace){
		File arquivo;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentTime = sdf.format(cal.getTime());

		String nameArq = path + "\\log_cvs_structure_" + this.sUserName + currentTime + ".log";
		try{
			arquivo = new File(nameArq);
			if (!arquivo.exists()){
				arquivo.createNewFile();
			}

			PrintWriter arqLog = new PrintWriter(new FileOutputStream(arquivo, true), true);

			arqLog.println(currentTime);
			arqLog.println(sLog);
			if(stackTrace.length != 0){
				for(int y = 1; y < stackTrace.length; y++){
					arqLog.println(stackTrace[y]);
				}
			}

            arqLog.close();
		}catch(FileNotFoundException e) {
			System.out.println("Arquivo de Log não existe");
		}catch (IOException e) {
			System.out.println("Impossível abrir arquivo de log");
		}
     }

	/**************************************************************************
	 * <b>Save Logs</b>
	 * @param sLog
	 **************************************************************************/
	public void saveLog(String sLog){
		File arquivo;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentTime = sdf.format(cal.getTime());

        String nameArq = path + "\\log_cvs_structure_" + CVSStructureBackup.sUserName + currentTime + ".log";
        //String nameArq = "\\log_cvs_structure_" + this.sUserName + currentTime + ".log";
		try{
            if(sDebug.equals("S")){
                arquivo = new File(nameArq);
                if (!arquivo.exists()){
                    arquivo.createNewFile();
                }

                PrintWriter arqLog = new PrintWriter(new FileOutputStream(arquivo, true), true);

                arqLog.println(currentTime);
                arqLog.println(sLog);

                System.out.println(sLog);
                this.logMessage(sLog);

                arqLog.close();
            }
		}catch(FileNotFoundException e) {
			System.out.println("Arquivo de Log não existe");
		}catch (IOException e) {
			System.out.println("Impossível abrir arquivo de log");
		}
     }

	public void spoolCVSStruture(ArrayList pTipo, JFrameCVS jframe) throws SQLException, IOException{
        this.jframe = (JFrameCVS) jframe;
        intialize();
		try{
            rsUsersUser = psUsersUser.executeQuery();
            rsUsersUser.next();
            this.sUserName = rsUsersUser.getString("USERNAME");
            flag = true;

            psSistema = this.conn.prepareStatement(this.sSistema);
            psSistema.setString(1, s_ItUser.toUpperCase());
            rsSistema = psSistema.executeQuery();
            rsSistema.next();
            id_sistema_it = rsSistema.getString("ID_SISTEMA");

            rsInterfaces = psInterfaces.executeQuery();
			while(rsInterfaces.next()){
				try{

                    String sistema = "";
                    psCountInterface2 = this.conn.prepareStatement(this.sCountSelect);
                    psCountInterface2.setString(1, rsInterfaces.getString("ID_INTERFACE"));
                    rsCountInterface = psCountInterface2.executeQuery();
                    rsCountInterface.next();
                    int nTotInter = rsCountInterface.getInt("TOTAL");

                    boolean sisFlag = true;
                    if (!id_sistema_it.equals("")){
                        if (id_sistema_it.equals(rsInterfaces.getString("ID_SISTEMA"))){
                            sisFlag = false;
                        }
                    }
                    
                    if(sisFlag){

                        logMessage("Param file name param select found " + rsInterfaces.getString("EXECUTAVEL") + "...");
                        IDInterface = rsInterfaces.getString("ID_INTERFACE");
                        sExecutavel = rsInterfaces.getString("EXECUTAVEL");
                        sTipoInterface = rsInterfaces.getString("TIPO_INTERFACE");
                        sIdSistema = rsInterfaces.getString("ID_SISTEMA").toLowerCase();
                        sDescricao = rsInterfaces.getString("DESCRICAO");
                        sUserName = rsInterfaces.getString("USERNAME");
                        sTempoMedio = rsInterfaces.getString("TEMPO_MEDIO");
                        sExecutavelCompl = rsInterfaces.getString("EXECUTAVELCOMPL");

                        if(pTipo.contains("T") || pTipo.contains("D")){
                            saveLog( sQuebraLinha + "## Criando Diretórios ##" + sQuebraLinha);
                            criarDiretorio();
                            saveLog(sQuebraLinha + "## Finalizando - Criando Diretórios ##" + sQuebraLinha);
                        }

                        if(pTipo.contains("T") || pTipo.contains("I")){
                            saveLog(sQuebraLinha + "## Gerando Interfaces ##" + sQuebraLinha);
                            this.gerarInterfaces();
                            saveLog(sQuebraLinha + "## Finalizando - Gerando Interfaces ##" + sQuebraLinha);
                        }

                        if(pTipo.contains("T") || pTipo.contains("E")){
                            saveLog(sQuebraLinha + "## Exportando Arquivos Externos ##" + sQuebraLinha);
                            this.exportarArquivosExternos();
                            saveLog(sQuebraLinha + "## Finalizando - Exportando Arquivos Externos ##" + sQuebraLinha);
                        }

                        if(pTipo.contains("T") || pTipo.contains("G")){
                            saveLog(sQuebraLinha + "## Gerando Arquivos Externos ##" + sQuebraLinha);
                            this.gerarArquivosExternos();
                            saveLog(sQuebraLinha + "## Finalizando - Gerando Arquivos Externos  ##" + sQuebraLinha);
                        }

                        if(sTipoInterface.trim().equals("E")){
                            saveLog(sQuebraLinha + "## Gerando Tabelas da Interface ##" + sQuebraLinha);
                            this.exportarTabInterface();
                            saveLog(sQuebraLinha + "## Finalizando - Gerando Tabelas da Interface ##" + sQuebraLinha);
                        }
                    }
				}catch (Exception e) {
					e.printStackTrace();
                   
					String erro = "";
                    saveLog("Error generating " + fileName);
					logMessage(e.getLocalizedMessage());
					for(int i=0; i<e.getStackTrace().length; i++){
						erro += "\t" + e.getStackTrace()[i].getLineNumber() + " " + e.getStackTrace()[i].toString() + sQuebraLinha;
					}
					logMessage(erro);
                    saveLog(e.getClass().toString(), e.getStackTrace());
				}
			}

            try {
                this.gerarArquivosExternosNaoGerados();
                this.gerarSynonyms();
                this.exportarTabInterfaceSemPermissao();
                this.exportarSistemas();
                this.gerarSequence();
                this.gerarView();
                if(jframe.getChIntMapeamento().isSelected()){
                    this.gerarIntMapeamento();
                    //IntMapeamento.SequenceInOut();
                }
                this.gerarSAPMapeamento();
            } catch (Exception ex) {
                Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

                String erro = "";
                logMessage("Error ");
                logMessage(ex.getLocalizedMessage());
                for(int i=0; i < ex.getStackTrace().length; i++)
                {
                    erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
                }
                saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            // Base de Integracao
            if(s_ItUser != null && !s_ItUser.equals("") && !s_ItPass.equals("") && s_ItPass != null){
                try {
                    this.gerarSynonymsIT(); // Não está funcionando
                    this.gerarIntMapeamentoIT();
                    this.gerarSAPMapeamentoIT();
                    this.gerarObjetosIntegracao();
                    this.gerarSequenceIT();
                    this.gerarViewIT();
                } catch (Exception ex) {
                    Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

                    String erro = "";
                    logMessage("Error generating " + fileName);
                    logMessage(ex.getLocalizedMessage());
                    for(int i=0; i < ex.getStackTrace().length; i++)
                    {
                        erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
                    }
                    saveLog(ex.getClass().toString(), ex.getStackTrace());
                }
            }

            // Deretórios
            try {
                this.removeDiretorio();
                this.ValidaDiretorio();
            } catch (Exception ex) {
                Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

                String erro = "";
                logMessage("Error generating " + fileName);
                logMessage(ex.getLocalizedMessage());
                for(int i=0; i < ex.getStackTrace().length; i++)
                {
                    erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
                }
                saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

		}catch (SQLException e) {
			String erro = "";
			logMessage(e.getLocalizedMessage());
			for(int i=0; i<e.getStackTrace().length; i++){
				erro += "\t" + e.getStackTrace()[i].getLineNumber() + " " + e.getStackTrace()[i].toString() + sQuebraLinha;
			}
			logMessage(erro);
			logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);
		}finally{
			this.conn.close();
		}
	}

    /*
     * Obtem o IDInterface
     */
    public static String getIDInterface(){
            return IDInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    /*
     * Obtem o IDSistema
     */
    public static String getIDSistema(){
            return sIdSistema.trim().toLowerCase();
    }

	/**************************************************************************
	 * <b>Criar diretorio</b>
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void criarDiretorio() throws Exception{
        ArrayList dirScripts = new ArrayList();

        if(flag){
            flag = false;
            dirScriptsValida.add(path + "\\"+sUserName+"\\Scripts\\comum");

            // Arquivos Inout
            dirScripts.add(path + "\\" + sUserName);
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Interfaces");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\ArquivosExternos");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Tabelas");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Sistemas");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Synonyms");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Sequence");

            // Scripts Inout
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Interfaces");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Tabelas");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Sistemas");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Synonyms");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Sequence");

            // Scripts Integracao
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Synonyms");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Sequence");
        }

        if(sTipoInterface.trim().equals("S")){
            dirScriptsValida.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() );
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\View\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\View\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Sequence\\");

            /*Integracao*/
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Sequence");

        }else if(sTipoInterface.trim().equals("E")){
            dirScriptsValida.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() );
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\View\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\View\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Sequence\\");

            /*Integracao*/
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Sequence");
        }else{
            dirScriptsValida.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() );
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() );

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Interface\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\Interface\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Tabelas\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\Tabelas\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Synonyms\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\Synonyms\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\View\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\View\\");

            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Sequence\\");
            dirScripts.add(path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\Sequence\\");

            /*Integracao*/
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Function");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Procedure");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Package");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\PackageBody");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Table");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\View");
            dirScripts.add(path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Sequence");
        }

        for(int i = 0; i < dirScripts.size(); i++){
            File file = new File(dirScripts.get(i).toString());
            if(file.mkdir()){
                saveLog("Diretorio criado com sucesso! " + dirScripts.get(i));
            }else{
                saveLog("Erro ao criar diretorio! "  + dirScripts.get(i));
            }
        }
	}

  	private void ValidaDiretorio() throws Exception{
        StringBuffer strOutScripts = new StringBuffer();
        SfwValidaScripts valid = new SfwValidaScripts();
        valid.setArqsInstala("N");
        strOutScripts.append("SPOOL SCRIPT_STATUS.LOG" + sQuebraLinha);
        strOutScripts.append("@\".\\define.sql\"" + sQuebraLinha);
        for(int i = 0; i < dirScriptsValida.size(); i++){
            valid.executar(dirScriptsValida.get(i).toString());
            strOutScripts.append("@\"" + dirScriptsValida.get(i).toString().replace(path + "\\"+sUserName+"\\Scripts", ".") + "\\ordem_instalacao.sql\"" + sQuebraLinha);
        }
        strOutScripts.append("SPOOL OFF" + sQuebraLinha);
        strOutScripts.append("EXIT" + sQuebraLinha);

        valid.copy(new File(".\\definicoes\\define.sql"), new File(path + "\\"+sUserName + "\\Scripts\\" + "define.sql" ));
        valid.copy(new File(".\\definicoes\\Readme.txt"), new File(path + "\\"+sUserName + "\\Scripts\\" + "Readme.txt" ));
        valid.copy(new File(".\\definicoes\\limpa_definicoes.sql"), new File(path + "\\"+sUserName + "\\Scripts\\" + "limpa_definicoes.sql" ));

        File fileScripts = new File(path + "\\"+sUserName+"\\Scripts\\ordem_instalacao.sql");
        if(!fileScripts.exists())
            fileScripts.createNewFile();

        FileWriter fwScripts = new FileWriter(fileScripts, false);
        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
        fwScripts.close();
        logMessage("File .\\"+sUserName+"\\Scripts\\ordem_instalacao.sql was succesfull generated.");

    }

	/**************************************************************************
	 * <b>Gerar scripts dos Sistemas</b>
	 **************************************************************************/
	private void exportarSistemas() throws Exception{
        String sSelectSistemas = "select * from sistema";
        PreparedStatement psSistemas = this.conn.prepareStatement(sSelectSistemas);
        ResultSet rsSistemas = psSistemas.executeQuery();

        try{
            while(rsSistemas.next()){
                fileName = rsSistemas.getString("USER_ORACLE").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Sistemas\\" + fileName;
                fileName = path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Sistemas\\" + fileName;

                StringBuffer strOut = new StringBuffer();
                logMessage("Creating or appending to file " + fileNameScripts);

                strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                strOut.append("--  ///////" + sQuebraLinha);
                strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                strOut.append("--  ///////     Sistema: "+rsSistemas.getString("DESCRICAO")+sQuebraLinha);
                strOut.append("--  ///////" + sQuebraLinha);
                strOut.append(sQuebraLinha);
                strOut.append("begin" + sQuebraLinha);
                strOut.append("   insert into		SISTEMA" + sQuebraLinha);
                strOut.append("		(ID_SISTEMA," + sQuebraLinha);
                strOut.append("		DESCRICAO," + sQuebraLinha);
                strOut.append("		USER_ORACLE," + sQuebraLinha);
                strOut.append("		PROC_NAME," + sQuebraLinha);
                strOut.append("		PASSWORD," + sQuebraLinha);
                strOut.append("		PROC_ATUALIZACAO)" + sQuebraLinha);
                strOut.append( "values		('"+rsSistemas.getString("ID_SISTEMA")+"'," + sQuebraLinha);
                strOut.append("		'"+rsSistemas.getString("DESCRICAO")+"'," + sQuebraLinha);
                strOut.append("		'&&"+rsSistemas.getString("USER_ORACLE").toUpperCase()+"_USER'," + sQuebraLinha);
                strOut.append("		''," + sQuebraLinha);
                strOut.append("		get_Crypto('&&"+rsSistemas.getString("USER_ORACLE").toUpperCase()+"_PASS')," + sQuebraLinha);
                strOut.append("		'');" + sQuebraLinha);
                strOut.append("  commit;" + sQuebraLinha);
                strOut.append("exception" + sQuebraLinha);
                strOut.append("  when dup_val_on_index then" + sQuebraLinha);
                strOut.append("    update	SISTEMA" + sQuebraLinha);
                strOut.append("    set	DESCRICAO = '"+rsSistemas.getString("DESCRICAO")+"'," + sQuebraLinha);
                strOut.append("	USER_ORACLE = '&&"+rsSistemas.getString("USER_ORACLE").toUpperCase()+"_USER'," + sQuebraLinha);
                strOut.append("	PROC_NAME = ''," + sQuebraLinha);
                strOut.append("	PASSWORD = get_Crypto('&&"+rsSistemas.getString("USER_ORACLE").toUpperCase()+"_PASS')," + sQuebraLinha);
                strOut.append("	PROC_ATUALIZACAO = ''" + sQuebraLinha);
                strOut.append("    where	ID_SISTEMA = '"+rsSistemas.getString("ID_SISTEMA")+"';" + sQuebraLinha);
                strOut.append("    commit;" + sQuebraLinha);
                strOut.append("end;" + sQuebraLinha);
                strOut.append("/" + sQuebraLinha);
                strOut.append(sQuebraLinha);
                strOut.append("-- //////" + sQuebraLinha);
                strOut.append("-- //////  Fim do script do sistema Broker Sys" + sQuebraLinha);
                strOut.append("-- //////" + sQuebraLinha);

                File file = new File(fileName);
                if(!file.exists())
                    file.createNewFile();

                FileWriter fw = new FileWriter(file, false);
                fw.write(strOut.toString(),0,strOut.length());
                fw.close();

                File fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                FileWriter fwScripts = new FileWriter(fileScripts, false);
                fwScripts.write(strOut.toString(),0,strOut.length());
                fwScripts.close();

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

	/**************************************************************************
	 * <b>Remover diretórios</b>
	 **************************************************************************/
	private void removeDiretorio() throws Exception{
        File diretorio = new File(path + "\\" + sUserName);
        File[] subdiretorios = diretorio.listFiles();
        for(File subdir : subdiretorios){
            if(subdir.isDirectory()){
                saveLog(subdir.getName());
                int nArqs = listaSubDir(subdir);
                if(nArqs == 0){
                    if(subdir.delete()){
                        saveLog("Diretório deletado: " + subdir.getName());
                    }
                }
            }
        }
	}

	/**************************************************************************
	 * <b>Listar subDiretórios</b>
	 * @param subDir
	 **************************************************************************/
    private int listaSubDir(File subDir){
        int nArqs = 0;
        File[] subdiretorios = subDir.listFiles();
        for(File subdir : subdiretorios){
            if(subdir.isDirectory()){
                System.out.println(subdir.getName());
                if(listaSubDir(subdir) == 0){
                    if(subdir.delete()){
                        saveLog("Diretório deletado: " + subdir.getName());
                    }
                }
            }else{
                nArqs += 1;
            }
        }
        return nArqs;
    }
    
	/**************************************************************************
	 * <b>Gerar scripts das interfaces</b>
	 **************************************************************************/
	private void gerarInterfaces() throws Exception{

        try{
            fileName = "interface.sql";
            if(sTipoInterface.trim().equals("S")){
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
                fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
            }else if(sTipoInterface.trim().equals("E")){
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
                fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
            }else{
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
                fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\Interface\\" + fileName;
            }

            StringBuffer strOut = new StringBuffer();
            logMessage("Creating or appending to file " + fileNameScripts);

            strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
            strOut.append("--  ///////" + sQuebraLinha);
            strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
            strOut.append("--  ///////     Interface: " + sDescricao + sQuebraLinha);
            strOut.append("--  ///////" + sQuebraLinha + sQuebraLinha + sQuebraLinha);
            strOut.append("delete from PERMISSAO_TABELA where ID_INTERFACE = '" + IDInterface + "';" + sQuebraLinha + sQuebraLinha);
            strOut.append("begin" + sQuebraLinha);
            strOut.append("   insert into    INTERFACES" + sQuebraLinha);
            strOut.append("      (ID_INTERFACE," + sQuebraLinha);
            strOut.append("      DESCRICAO," + sQuebraLinha);
            strOut.append("      NOME_MAQUINA," + sQuebraLinha);
            strOut.append("      EXECUTAVEL," + sQuebraLinha);
            strOut.append("      USERNAME," + sQuebraLinha);
            strOut.append("      TEMPO_MEDIO," + sQuebraLinha);
            strOut.append("      TIPO_INTERFACE)" + sQuebraLinha);
            strOut.append("      values" + sQuebraLinha);
            strOut.append("      ('" + IDInterface + "'," + sQuebraLinha);
            strOut.append("      '" + sDescricao + "'," + sQuebraLinha);
            strOut.append("   	  '&&USER_MACHINE'," + sQuebraLinha);
            strOut.append("	  '" + sExecutavelCompl + "'," + sQuebraLinha);
            strOut.append("	  '" + sUserName + "'," + sQuebraLinha);
            strOut.append("	  '0'," + sQuebraLinha);
            strOut.append("   '" + sTipoInterface + "');" + sQuebraLinha);
            strOut.append("exception" + sQuebraLinha);
            strOut.append("    when dup_val_on_index then" + sQuebraLinha);
            strOut.append("        update INTERFACES" + sQuebraLinha);
            strOut.append("        set" + sQuebraLinha);
            strOut.append("                DESCRICAO = '" + sDescricao + "'," + sQuebraLinha);
            strOut.append("                NOME_MAQUINA = '&&USER_MACHINE'," + sQuebraLinha);
            strOut.append("                EXECUTAVEL = '" + sExecutavelCompl + "'," + sQuebraLinha);
            strOut.append("                USERNAME = '" + sUserName + "'," + sQuebraLinha);
            strOut.append("                TIPO_INTERFACE = '" + sTipoInterface + "'" + sQuebraLinha);
            strOut.append("        where id_interface  = '" + IDInterface + "';" + sQuebraLinha);
            strOut.append("end;" + sQuebraLinha);
            strOut.append("/" + sQuebraLinha + sQuebraLinha);
            strOut.append("delete from INTERFACE_SAIDA where ID_INTERFACE =  '" + IDInterface + "';" + sQuebraLinha + sQuebraLinha);
            strOut.append("insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('" + IDInterface + "' , '" + (sIdSistema.toUpperCase().equals("SFW") ? "SW" : sIdSistema.toUpperCase())+ "');" + sQuebraLinha + sQuebraLinha);
            strOut.append("commit;" + sQuebraLinha + sQuebraLinha + sQuebraLinha);
            strOut.append("-- //////" + sQuebraLinha);
            strOut.append("-- //////  FIM DO SCRIPT" + sQuebraLinha);
            strOut.append("-- //////");

            // Criando arquivo na pasta de arquivos
            File file = new File(fileName);
            if(!file.exists())
                file.createNewFile();

            FileWriter fw = new FileWriter(file, false);
            fw.write(strOut.toString(),0,strOut.length());
            fw.close();

            // Criando arquivo na pasta de scripts
            File fileScripts = new File(fileNameScripts);
            if(!fileScripts.exists())
                fileScripts.createNewFile();

            FileWriter fwScripts = new FileWriter(fileScripts, false);
            fwScripts.write(strOut.toString(),0,strOut.length());
            fwScripts.close();
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}

	/**************************************************************************
	 * <b>Gerar scripts dos arquivos externos</b>
	 **************************************************************************/
	private void gerarArquivosExternos() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{
            psCountInterface.setString(1, sExecutavel);
            rsCountInterface = psCountInterface.executeQuery();
            rsCountInterface.next();
            int nTotalArquivos = rsCountInterface.getInt("TOTAL_ARQUIVOS");

            //psArquivosExternos = this.conn.prepareStatement(this.sSelectGerarArquivosExternos);
            psGerarArquivosExternos.setString(1, sExecutavel);
            rsArquivosExternos = psGerarArquivosExternos.executeQuery();

            while(rsArquivosExternos.next()){

                if(rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase().substring(rsArquivosExternos.getString("NOME_ARQUIVO").indexOf(".")+1,rsArquivosExternos.getString("NOME_ARQUIVO").length()).equals("sql")){
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                }else{
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                }

                if(nTotalArquivos == 1){
                    if(sTipoInterface.trim().equals("S")){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if(sTipoInterface.trim().equals("E")){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                    }else{
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                    }
                }else if(nTotalArquivos >= 2){
                    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                }else{
                    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                }
                logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if(clob!=null){
                    /******************************************
                     * Gerando arquivos na pasta de Scripts
                     ******************************************/
                    logMessage("Creating or appending to file " + fileNameScripts);
                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("--  ///////" + sQuebraLinha);
                    strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                    strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + sQuebraLinha );
                    strOutScripts.append("--  ///////" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("set define off" + sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("insert into ARQUIVO_EXTERNO" + sQuebraLinha);
                    strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + sQuebraLinha);
                    strOutScripts.append("values" + sQuebraLinha);

                    // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                    brScripts = new BufferedReader(clob.getCharacterStream());
                    int contador = 0;
                    int maxLin = 0;
                    while ((auxScripts=brScripts.readLine())!=null){

                        auxScripts = auxScripts.replaceAll("'",  "' || chr(27) || '");
                        auxScripts = auxScripts.replaceAll("@",  "' || chr(40) || '");
                        auxScripts = auxScripts.replaceAll("\t",  "' '");
                        //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                        auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || CHR(10) ||'");
                        //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                        if (contador == 0){
                            strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + sDescricao + "','" + rsArquivosExternos.getString("PATH_RELATIVO") + "','" + auxScripts + "');");
                        }else{
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
                            if(!auxScripts.equals("")){
                                strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + auxScripts + "');");
                            }

                        }
                        contador += 1;
                    }


                    //if(maxLin < 350){
                    //	strOutScripts.append("');");
                    //}

                    strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                    strOutScripts.append("commit;");
                    strOutScripts.append(sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("set define on");
                    strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                    strOutScripts.append("-- //////" + sQuebraLinha);
                    strOutScripts.append("-- //////  Fim do Script" + sQuebraLinha);
                    strOutScripts.append("-- //////");

                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else{
                    logMessage("No data are being generated");
                    logMessage("File " + fileName + " wasn't generated.");
                    logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);
                }
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsCountInterface.close();
        }
	}

    /**************************************************************************
	 * <b>Gerar scripts dos arquivos externos</b>
	 **************************************************************************/
	private void gerarArquivosExternosNaoGerados() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{

            psCreateTable.executeQuery();
            psInsertReferencesObjects.executeUpdate();
            psInsertReferencesObjectsIT.setString(1, s_User.toUpperCase());
            conn.commit();

            rsGerarArquivosExternosNaoGerados = psGerarArquivosExternosNaoGerados.executeQuery();
            while(rsGerarArquivosExternosNaoGerados.next()){
                psCountArquivosExternosNaoGerados = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                //psCountArquivosExternosNaoGerados.setString(2, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                rsCountArquivosExternosNaoGerados.next();
                int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                if(nTotalArquivos > 1){
                    if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                        fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                        fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                        fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                    }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                        fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                    }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                        fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                    }
                }else{
                    if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".")+1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                        psFoundSQLInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                        while(rsFoundSQLInterface.next()){
                            IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                        }
                    }else{
                        psFoundBATInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundBATInterface = psFoundBATInterface.executeQuery();
                        while(rsFoundBATInterface.next()){
                            IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                        }
                    }

                    psFoundDadosInterface.setString(1, IDInterface);
                    rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                    while(rsFoundDadosInterface.next()){
                        sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                        sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                        sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                        sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                        sUserName = rsFoundDadosInterface.getString("USERNAME");
                        sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                        sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                    }

                    if(sTipoInterface.trim().equals("S")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }else if(sTipoInterface.trim().equals("E")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
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

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();
                String auxScripts;

                if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat") || rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                    psGerarArquivosExternos.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM") + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO"));
                    rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                    while(rsArquivosExternos.next()){
                        clob = rsArquivosExternos.getClob("CONTEUDO");

                        if(clob!=null){
                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("--  ///////" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + sQuebraLinha );
                            strOutScripts.append("--  ///////" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define off" + sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO" + sQuebraLinha);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + sQuebraLinha);
                            strOutScripts.append("values" + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts=brScripts.readLine())!=null){

                                auxScripts = auxScripts.replaceAll("'",  "' || chr(27) || '");
                                auxScripts = auxScripts.replaceAll("@",  "' || chr(40) || '");
                                auxScripts = auxScripts.replaceAll("\t",  "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0){
                                    strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + sDescricao + "','" + rsArquivosExternos.getString("PATH_RELATIVO") + "','" + auxScripts + "');");
                                }else{
                                    if(!auxScripts.equals("")){
                                        strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                        strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + auxScripts + "');");
                                    }

                                }
                                contador += 1;
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("commit;");
                            strOutScripts.append(sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define on");
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("-- //////" + sQuebraLinha);
                            strOutScripts.append("-- //////  Fim do Script" + sQuebraLinha);
                            strOutScripts.append("-- //////");
                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function") ||
                        rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                    psUserSource.setString(1, rsGerarArquivosExternosNaoGerados.getString("TIPO").toUpperCase() );
                    psUserSource.setString(2, rsGerarArquivosExternosNaoGerados.getString("CONTEM").toUpperCase() );
                    rsUserSource = psUserSource.executeQuery();

                    // Geranco Referencias do Objeto
                    this.gerarReferenciasObjetos(rsGerarArquivosExternosNaoGerados.getString("CONTEM").toUpperCase());

                    strOutScripts.append("conn &&INOU_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                    while(rsUserSource.next()){
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }
                    strOutScripts.append("/");

                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if ( rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package") ||
                          rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package body")){

                    for(int i=0; i <= 1; i++){

                        if(i==0){
                            psUserSource.setString(1, "PACKAGE" );
                            this.gerarReferenciasObjetos(rsGerarArquivosExternosNaoGerados.getString("CONTEM").toUpperCase());
                        }else{
                            psUserSource.setString(1, "PACKAGE BODY" );
                            fileNameScripts.replace("Package", "PackageBody");
                        }
                        psUserSource.setString(2, rsGerarArquivosExternosNaoGerados.getString("CONTEM").toUpperCase() );
                        rsUserSource = psUserSource.executeQuery();

                        logMessage("Creating or appending to file " + fileNameScripts);

                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        strOutScripts = new StringBuffer();

                        strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                        while(rsUserSource.next()){
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                        strOutScripts.append("/");

                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();
                        logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                }

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            psDropTableIT = this.conn.prepareStatement("drop table TMP_CVS_STRCTURE");
            psDropTableIT.executeQuery();
        }
	}


    /**************************************************************************
	 * <b>Gerar scripts das Referencias dos Objetos do InOut</b>
	 **************************************************************************/
	private void gerarReferenciasObjetos(String objectName) throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{
            psGerarReferenciasObjetos.setString(1, s_User.toUpperCase());
            //psGerarReferenciasObjetos.setString(2, objectName);
            //psGerarReferenciasObjetos.setString(3, objectName);
            rsGerarReferenciasObjetos = psGerarReferenciasObjetos.executeQuery();
            while(rsGerarReferenciasObjetos.next()){
                //psCountArquivosExternosNaoGerados = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                //psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                //rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                //rsCountArquivosExternosNaoGerados.next();
                //int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                //if(nTotalArquivos > 1){
                    //if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat")){
                    //    fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase() + ".sql";
                    //    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    //}else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                    //    fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase();
                    //    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    //}else
                    if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                    }
                /*
                }else{
                    if(rsGerarReferenciasObjetos.getString("NOME_ARQUIVO").substring(rsGerarReferenciasObjetos.getString("NOME_ARQUIVO").indexOf(".")+1, rsGerarReferenciasObjetos.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                        psFoundSQLInterface.setString(1, rsGerarReferenciasObjetos.getString("NOME_ARQUIVO"));
                        rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                        while(rsFoundSQLInterface.next()){
                            IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                        }
                    }else{
                        psFoundBATInterface.setString(1, rsGerarReferenciasObjetos.getString("NOME_ARQUIVO"));
                        rsFoundBATInterface = psFoundBATInterface.executeQuery();
                        while(rsFoundBATInterface.next()){
                            IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                        }
                    }

                    psFoundDadosInterface.setString(1, IDInterface);
                    rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                    while(rsFoundDadosInterface.next()){
                        sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                        sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                        sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                        sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                        sUserName = rsFoundDadosInterface.getString("USERNAME");
                        sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                        sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                    }

                    if(sTipoInterface.trim().equals("S")){
                        if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }else if(sTipoInterface.trim().equals("E")){
                        if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }else{
                        //fileNameScripts = ".\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarReferenciasObjetos.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }
                }
                */

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();
                String auxScripts;
/*

                if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat") || rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                    psGerarArquivosExternos.setString(1, rsGerarReferenciasObjetos.getString("CONTEM") + "." + rsGerarReferenciasObjetos.getString("TIPO"));
                    rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                    while(rsArquivosExternos.next()){
                        clob = rsArquivosExternos.getClob("CONTEUDO");

                        if(clob!=null){

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("--  ///////" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + sQuebraLinha );
                            strOutScripts.append("--  ///////" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define off" + sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO" + sQuebraLinha);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + sQuebraLinha);
                            strOutScripts.append("values" + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts=brScripts.readLine())!=null){

                                auxScripts = auxScripts.replaceAll("'",  "' || chr(27) || '");
                                auxScripts = auxScripts.replaceAll("@",  "' || chr(40) || '");
                                auxScripts = auxScripts.replaceAll("\t",  "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0){
                                    strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + sDescricao + "','" + rsArquivosExternos.getString("PATH_RELATIVO") + "','" + auxScripts + "');");
                                }else{
                                    if(!auxScripts.equals("")){
                                        strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                        strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + auxScripts + "');");
                                    }

                                }
                                contador += 1;
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("commit;");
                            strOutScripts.append(sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define on");
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("-- //////" + sQuebraLinha);
                            strOutScripts.append("-- //////  Fim do Script" + sQuebraLinha);
                            strOutScripts.append("-- //////");
                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else
    */
                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function") ||
                        rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){
                    psUserSource.setString(1, rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toUpperCase() );
                    psUserSource.setString(2, rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toUpperCase() );
                    rsUserSource = psUserSource.executeQuery();

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                    while(rsUserSource.next()){
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }
                    strOutScripts.append("/");

                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if ( rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package") ||
                          rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package body")){

                    for(int i=0; i <= 1; i++){

                        if(i==0){
                            psUserSource.setString(1, "PACKAGE" );
                        }else{
                            psUserSource.setString(1, "PACKAGE BODY" );
                            fileNameScripts.replace("Package", "PackageBody");
                        }
                        psUserSource.setString(2, rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toUpperCase() );
                        rsUserSource = psUserSource.executeQuery();

                        logMessage("Creating or appending to file " + fileNameScripts);

                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        strOutScripts = new StringBuffer();

                        strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                        while(rsUserSource.next()){
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                        strOutScripts.append("/");

                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();
                        logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                }

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}



    /**************************************************************************
	 * <b>Gerar scripts dos arquivos externos</b>
	 **************************************************************************/
	private void gerarObjetosIntegracao() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

		try{

            psCreateTableIT.executeQuery();
            connIt.commit();
            psInsertReferencesObjectsIT.setString(1, s_ItUser.toUpperCase());
            psInsertReferencesObjectsIT.executeUpdate();

            connIt.commit();

            rsFoundObjectsIT = psFoundObjectsIT.executeQuery();
            while(rsFoundObjectsIT.next()){
                //psCountArquivosExternosNaoGerados = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                //psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                //rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                //rsCountArquivosExternosNaoGerados.next();
                //int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                //if(nTotalArquivos > 1){
                    if(rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("bat")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("sql")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + "." + rsFoundObjectsIT.getString("TIPO").toLowerCase();
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("function")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                    }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("procedure")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                    }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                    }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("table")){
                        fileName = rsFoundObjectsIT.getString("OBJECT_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
                    }
                 /*
                }else{
                    if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".")+1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                        psFoundSQLInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                        while(rsFoundSQLInterface.next()){
                            IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                        }
                    }else{
                        psFoundBATInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundBATInterface = psFoundBATInterface.executeQuery();
                        while(rsFoundBATInterface.next()){
                            IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                        }
                    }

                    psFoundDadosInterface.setString(1, IDInterface);
                    rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                    while(rsFoundDadosInterface.next()){
                        sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                        sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                        sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                        sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                        sUserName = rsFoundDadosInterface.getString("USERNAME");
                        sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                        sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                    }

                    if(sTipoInterface.trim().equals("S")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }else if(sTipoInterface.trim().equals("E")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
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

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();
                String auxScripts;

                if(rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("bat") || rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("sql")){
                    psGerarArquivosExternos.setString(1, rsFoundObjectsIT.getString("OBJECT_NAME") + "." + rsFoundObjectsIT.getString("TIPO"));
                    rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                    while(rsArquivosExternos.next()){
                        clob = rsArquivosExternos.getClob("CONTEUDO");

                        if(clob!=null){
                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("--  ///////" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + sQuebraLinha );
                            strOutScripts.append("--  ///////" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define off" + sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO" + sQuebraLinha);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + sQuebraLinha);
                            strOutScripts.append("values" + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts=brScripts.readLine())!=null){

                                auxScripts = auxScripts.replaceAll("'",  "' || chr(27) || '");
                                auxScripts = auxScripts.replaceAll("@",  "' || chr(40) || '");
                                auxScripts = auxScripts.replaceAll("\t",  "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0){
                                    strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + sDescricao + "','" + rsArquivosExternos.getString("PATH_RELATIVO") + "','" + auxScripts + "');");
                                }else{
                                    if(!auxScripts.equals("")){
                                        strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                        strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + auxScripts + "');");
                                    }

                                }
                                contador += 1;
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("commit;");
                            strOutScripts.append(sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define on");
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("-- //////" + sQuebraLinha);
                            strOutScripts.append("-- //////  Fim do Script" + sQuebraLinha);
                            strOutScripts.append("-- //////");
                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if (rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("function") ||
                        rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("procedure")){

                    psUserSourceIt.setString(1, rsFoundObjectsIT.getString("TIPO").toUpperCase() );
                    psUserSourceIt.setString(2, rsFoundObjectsIT.getString("OBJECT_NAME").toUpperCase() );
                    rsUserSource = psUserSourceIt.executeQuery();

                    this.gerarReferenciasObjetosIT(rsFoundObjectsIT.getString("OBJECT_NAME").toUpperCase());
                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                    while(rsUserSource.next()){
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }

                    strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if(rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("table")){
                    psTableSourceIT.setString(1, rsFoundObjectsIT.getString("OBJECT_NAME"));
                    rsTableSourceIT = psTableSourceIT.executeQuery();

                    while(rsTableSourceIT.next()){
                        clob = rsTableSourceIT.getClob("CONTEUDO");

                        if(clob!=null){
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            while ((auxScripts=brScripts.readLine())!=null){
                                strOutScripts.append(auxScripts + sQuebraLinha);
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append(";" + sQuebraLinha);
                            strOutScripts.append("/");

                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    //strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if ( rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package") ||
                          rsFoundObjectsIT.getString("TIPO").toLowerCase().equals("package body")){

                    for(int i=0; i <= 1; i++){

                        if(i==0){
                            psUserSourceIt.setString(1, "PACKAGE" );
                            this.gerarReferenciasObjetosIT(rsFoundObjectsIT.getString("OBJECT_NAME").toUpperCase());
                        }else{
                            psUserSourceIt.setString(1, "PACKAGE BODY" );
                            fileNameScripts = fileNameScripts.replace("\\Package\\", "\\PackageBody\\");
                        }
                        psUserSourceIt.setString(2, rsFoundObjectsIT.getString("OBJECT_NAME").toUpperCase() );
                        rsUserSource = psUserSourceIt.executeQuery();

                        logMessage("Creating or appending to file " + fileNameScripts);

                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        strOutScripts = new StringBuffer();

                        strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                        while(rsUserSource.next()){
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                        strOutScripts.append("/");

                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();
                        logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                }

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            psDropTableIT = this.connIt.prepareStatement("drop table TMP_CVS_STRCTURE");
            psDropTableIT.execute();
        }

	}

        /**************************************************************************
	 * <b>Gerar scripts dos arquivos externos</b>
	 **************************************************************************/
	private void gerarReferenciasObjetosIT(String objectName) throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

		try{
            psGerarReferenciasObjetosIT.setString(1, s_ItUser.toUpperCase());
            //psGerarReferenciasObjetosIT.setString(2, objectName);
            //psGerarReferenciasObjetosIT.setString(3, objectName);
            rsGerarReferenciasObjetos = psGerarReferenciasObjetosIT.executeQuery();
            while(rsGerarReferenciasObjetos.next()){
                //psCountArquivosExternosNaoGerados = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                //psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                //rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                //rsCountArquivosExternosNaoGerados.next();
                //int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                //if(nTotalArquivos > 1){
                /*
                    if(rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("bat")){
                        fileName = rsGerarReferenciasObjetos.getString("OBJECT_NAME").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("TIPO").toLowerCase().equals("sql")){
                        fileName = rsGerarReferenciasObjetos.getString("OBJECT_NAME").toLowerCase() + "." + rsGerarReferenciasObjetos.getString("TIPO").toLowerCase();
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                    }else
                 */
                    if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                    }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")){
                        fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
                    }
                 /*
                }else{
                    if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".")+1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                        psFoundSQLInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                        while(rsFoundSQLInterface.next()){
                            IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                        }
                    }else{
                        psFoundBATInterface.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        rsFoundBATInterface = psFoundBATInterface.executeQuery();
                        while(rsFoundBATInterface.next()){
                            IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                        }
                    }

                    psFoundDadosInterface.setString(1, IDInterface);
                    rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                    while(rsFoundDadosInterface.next()){
                        sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                        sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                        sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                        sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                        sUserName = rsFoundDadosInterface.getString("USERNAME");
                        sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                        sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                    }

                    if(sTipoInterface.trim().equals("S")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
                        }
                    }else if(sTipoInterface.trim().equals("E")){
                        if(rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("bat")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("sql")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("function")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Function\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("procedure")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Procedure\\" + fileName;
                        }else if (rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase().equals("package")){
                            fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Package\\" + fileName;
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

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();
                String auxScripts;

                /*
                if(rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("bat") || rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("sql")){
                    psGerarArquivosExternos.setString(1, rsGerarReferenciasObjetos.getString("REFERENCED_NAME") + "." + rsGerarReferenciasObjetos.getString("REFERENCED_TYPE"));
                    rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                    while(rsArquivosExternos.next()){
                        clob = rsArquivosExternos.getClob("CONTEUDO");

                        if(clob!=null){

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("--  ///////" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
                            strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + sQuebraLinha );
                            strOutScripts.append("--  ///////" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define off" + sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO" + sQuebraLinha);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + sQuebraLinha);
                            strOutScripts.append("values" + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts=brScripts.readLine())!=null){

                                auxScripts = auxScripts.replaceAll("'",  "' || chr(27) || '");
                                auxScripts = auxScripts.replaceAll("@",  "' || chr(40) || '");
                                auxScripts = auxScripts.replaceAll("\t",  "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0){
                                    strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + sDescricao + "','" + rsArquivosExternos.getString("PATH_RELATIVO") + "','" + auxScripts + "');");
                                }else{
                                    if(!auxScripts.equals("")){
                                        strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                        strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "','" + auxScripts + "');");
                                    }

                                }
                                contador += 1;
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("commit;");
                            strOutScripts.append(sQuebraLinha + sQuebraLinha + sQuebraLinha + sQuebraLinha);
                            strOutScripts.append("set define on");
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append("-- //////" + sQuebraLinha);
                            strOutScripts.append("-- //////  Fim do Script" + sQuebraLinha);
                            strOutScripts.append("-- //////");
                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else
                */
                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function") ||
                        rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){

                    psUserSourceIt.setString(1, rsGerarReferenciasObjetos.getString("TIPO").toUpperCase() );
                    psUserSourceIt.setString(2, rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toUpperCase() );
                    rsUserSource = psUserSourceIt.executeQuery();

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                    while(rsUserSource.next()){
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }

                    strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if(rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")){
                    psTableSourceIT.setString(1, rsGerarReferenciasObjetos.getString("REFERENCED_NAME"));
                    rsTableSourceIT = psTableSourceIT.executeQuery();

                    while(rsTableSourceIT.next()){
                        clob = rsTableSourceIT.getClob("CONTEUDO");

                        if(clob!=null){
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            while ((auxScripts=brScripts.readLine())!=null){
                                strOutScripts.append(auxScripts + sQuebraLinha);
                            }
                            strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                            strOutScripts.append(";" + sQuebraLinha);
                            strOutScripts.append("/");

                        }else{
                            logMessage("No data are being generated");
                            logMessage("File " + fileName + " wasn't generated.");
                            logMessage("Error in the implementation of the interface with Id_Importação " + IDInterface);

                        }
                    }
                    //strOutScripts.append("/");
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }else if ( rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package") ||
                          rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package body")){

                    for(int i=0; i <= 1; i++){

                        if(i==0){
                            psUserSourceIt.setString(1, "PACKAGE" );

                        }else{
                            psUserSourceIt.setString(1, "PACKAGE BODY" );
                            fileNameScripts = fileNameScripts.replace("\\Package\\", "\\PackageBody\\");
                        }
                        psUserSourceIt.setString(2, rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toUpperCase() );
                        rsUserSource = psUserSourceIt.executeQuery();

                        logMessage("Creating or appending to file " + fileNameScripts);

                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        strOutScripts = new StringBuffer();

                        strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                        while(rsUserSource.next()){
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                        strOutScripts.append("/");

                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();
                        logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                }

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}


   /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarSynonyms() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        IDInterface = "";

        try{
            psArquivosSynonyms = this.conn.prepareStatement(this.sSelectArquivosSynonymos);
            //psGerarArquivosExternos.setString(1, sExecutavel);
            rsArquivosSynonyms = psArquivosSynonyms.executeQuery();

            while(rsArquivosSynonyms.next()){

                psCountArquivosSynonyms = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                psCountArquivosSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                rsCountArquivosSynonyms = psCountArquivosSynonyms.executeQuery();
                rsCountArquivosSynonyms.next();
                int nTotalArquivos = rsCountArquivosSynonyms.getInt("TOTAL_ARQUIVOS");

                psSynonyms = this.conn.prepareStatement(this.sSynonyms);
                psSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                rsSynonyms = psSynonyms.executeQuery();
                while(rsSynonyms.next()){
                    if(nTotalArquivos > 1){
                        fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Synonyms\\" + fileName;
                    }else{

                        if(rsArquivosSynonyms.getString("NOME_ARQUIVO").substring(rsArquivosSynonyms.getString("NOME_ARQUIVO").indexOf(".")+1, rsArquivosSynonyms.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                            psFoundSQLInterface.setString(1, rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                            while(rsFoundSQLInterface.next()){
                                IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                            }
                        }else{
                            psFoundBATInterface.setString(1, rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            rsFoundBATInterface = psFoundBATInterface.executeQuery();
                            while(rsFoundBATInterface.next()){
                                IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                            }
                        }

                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\Synonyms\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Synonyms\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\Synonyms\\"  + fileName;
                        }
                    }

                    logMessage("Creating or appending to file " + fileNameScripts);
                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);

                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("-- Create the synonym" + sQuebraLinha);
                    strOutScripts.append("create or replace synonym " + rsSynonyms.getString("SYNONYM_NAME") + sQuebraLinha);
                    strOutScripts.append("  for " + rsSynonyms.getString("TABLE_OWNER") + "." + rsSynonyms.getString("TABLE_NAME") + ";" + sQuebraLinha);

                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}

   /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarSynonymsIT() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        IDInterface = "";

        try{
            psArquivosSynonymsIT = this.connIt.prepareStatement(this.sSynonymsAll);
            //psGerarArquivosExternos.setString(1, sExecutavel);
            rsArquivosSynonyms = psArquivosSynonymsIT.executeQuery();

            while(rsArquivosSynonyms.next()){

                //psCountArquivosSynonymsIT = this.conn.prepareStatement(this.sCountArquivosSynonyms);
                //psCountArquivosSynonymsIT.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                //rsCountArquivosSynonyms = psCountArquivosSynonymsIT.executeQuery();
                //rsCountArquivosSynonyms.next();
                //int nTotalArquivos = rsCountArquivosSynonyms.getInt("TOTAL_ARQUIVOS");

                //psSynonymsIT = this.connIt.prepareStatement(this.sSynonyms);
                //psSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                //rsSynonyms = psSynonymsIT.executeQuery();
                //while(rsSynonyms.next()){
                    //if(nTotalArquivos > 1){
                        fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Synonyms\\" + fileName;
                    /*
                    }else{

                        if(rsArquivosSynonyms.getString("NOME_ARQUIVO").substring(rsArquivosSynonyms.getString("NOME_ARQUIVO").indexOf(".")+1, rsArquivosSynonyms.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                            psFoundSQLInterface.setString(1, rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            rsFoundSQLInterface = psFoundSQLInterface.executeQuery();
                            while(rsFoundSQLInterface.next()){
                                IDInterface = rsFoundSQLInterface.getString("ID_INTERFACE");
                            }
                        }else{
                            psFoundBATInterface.setString(1, rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            rsFoundBATInterface = psFoundBATInterface.executeQuery();
                            while(rsFoundBATInterface.next()){
                                IDInterface = rsFoundBATInterface.getString("ID_INTERFACE");
                            }
                        }

                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Synonyms\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Synonyms\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Synonyms\\"  + fileName;
                        }

                    }
                    */
                    logMessage("Creating or appending to file " + fileNameScripts);

                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);

                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("-- Create the synonym" + sQuebraLinha);
                    strOutScripts.append("create or replace synonym " + rsArquivosSynonyms.getString("SYNONYM_NAME") + sQuebraLinha);
                    strOutScripts.append("  for " + rsArquivosSynonyms.getString("TABLE_OWNER") + "." + rsArquivosSynonyms.getString("TABLE_NAME") + ";" + sQuebraLinha);

                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                //}
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsArquivosSynonyms.close();
        }
	}

   /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarSequence() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{

            rsGerarSequences = psGerarSequences.executeQuery();
            while(rsGerarSequences.next()){

                fileName = rsGerarSequences.getString("SEQUENCE_NAME").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Sequence\\" + fileName;

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();

                /******************************************
                * Gerando arquivos na pasta de Scripts
                ******************************************/

                strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                strOutScripts.append("-- Create sequence " + sQuebraLinha);
                strOutScripts.append("create sequence " + rsGerarSequences.getString("SEQUENCE_NAME") + sQuebraLinha);
                strOutScripts.append("  minvalue 1" + rsGerarSequences.getString("MIN_VALUE") + sQuebraLinha);
                strOutScripts.append("  maxvalue " + rsGerarSequences.getString("MAX_VALUE") + sQuebraLinha);
                strOutScripts.append("  start with " + rsGerarSequences.getString("LAST_NUMBER") + sQuebraLinha);
                strOutScripts.append("  increment by "+ rsGerarSequences.getString("INCREMENT_BY") + sQuebraLinha);
                strOutScripts.append("  cache " + rsGerarSequences.getString("CACHE_SIZE") + ";" + sQuebraLinha );

                fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                fwScripts.close();
                logMessage("File " + fileNameScripts + " was succesfull generated.");
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsGerarSequences.close();
        }
	}

       /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarSequenceIT() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{

            rsGerarSequences = psGerarSequencesIT.executeQuery();
            while(rsGerarSequences.next()){

                fileName = rsGerarSequences.getString("SEQUENCE_NAME").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Sequence\\" + fileName;

                logMessage("Creating or appending to file " + fileNameScripts);
                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists())
                    fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);

                strOutScripts = new StringBuffer();

                /******************************************
                * Gerando arquivos na pasta de Scripts
                ******************************************/

                strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                strOutScripts.append("-- Create sequence " + sQuebraLinha);
                strOutScripts.append("create sequence " + rsGerarSequences.getString("SEQUENCE_NAME") + sQuebraLinha);
                strOutScripts.append("  minvalue 1" + rsGerarSequences.getString("MIN_VALUE") + sQuebraLinha);
                strOutScripts.append("  maxvalue " + rsGerarSequences.getString("MAX_VALUE") + sQuebraLinha);
                strOutScripts.append("  start with " + rsGerarSequences.getString("LAST_NUMBER") + sQuebraLinha);
                strOutScripts.append("  increment by "+ rsGerarSequences.getString("INCREMENT_BY") + sQuebraLinha);
                strOutScripts.append("  cache " + rsGerarSequences.getString("CACHE_SIZE") + ";" + sQuebraLinha );
                
                fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                fwScripts.close();
                logMessage("File " + fileNameScripts + " was succesfull generated.");
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsGerarSequences.close();
        }
	}

   /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarView() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{

            rsView = psView.executeQuery();
            while(rsView.next()){

                fileName = rsView.getString("VIEW_NAME").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\View\\" + fileName;

                String longText = rsView.getString("TEXT");

                if(longText != null && !longText.toString().equals("")){
                    logMessage("Creating or appending to file " + fileNameScripts);
                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("create or replace force view " + rsView.getString("VIEW_NAME") + sQuebraLinha);
                    strOutScripts.append(longText.trim() + sQuebraLinha);
                    strOutScripts.append(";" + sQuebraLinha);
					fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
					fwScripts.close();
					logMessage("File " + fileNameScripts + " was succesfull generated.");
				}

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsView.close();
        }
	}

   /**************************************************************************
	 * <b>Gerar scripts dos synonyms</b>
	 **************************************************************************/
	private void gerarViewIT() throws Exception{
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        try{

            rsView = psViewIT.executeQuery();
            while(rsView.next()){

                fileName = rsView.getString("VIEW_NAME").toLowerCase() + ".sql";
                fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\View\\" + fileName;

                String longText = rsView.getString("TEXT");

                if(longText != null && !longText.toString().equals("")){
                    logMessage("Creating or appending to file " + fileNameScripts);
                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    strOutScripts.append("create or replace force view " + rsView.getString("VIEW_NAME") + sQuebraLinha);
                    strOutScripts.append(longText.trim() + sQuebraLinha);
                    strOutScripts.append(";" + sQuebraLinha);
					fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
					fwScripts.close();
					logMessage("File " + fileNameScripts + " was succesfull generated.");
				}

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            rsView.close();
        }
	}

    private void gerarIntMapeamento() {
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        IDInterface = "";

        try{
            rsIntMapeamento = psIntMapeamento.executeQuery();
            while(rsIntMapeamento.next()){

                psPermissaoTabelaTab = this.conn.prepareStatement(this.sSelectPermissaoTabelaTab);
                psPermissaoTabelaTab.setString(1, rsIntMapeamento.getString("LAYOUT"));
                rsPermissaoTabelaTab = psPermissaoTabelaTab.executeQuery();

                while(rsPermissaoTabelaTab.next()){
                    IDInterface = rsPermissaoTabelaTab.getString("ID_INTERFACE");

                    psCountPermissaoTabelaTab = this.conn.prepareStatement(this.sCountPermissaoTabelaTab);
                    psCountPermissaoTabelaTab.setString(1, rsIntMapeamento.getString("LAYOUT"));
                    rsCountPermissaoTabelaTab = psCountPermissaoTabelaTab.executeQuery();

                    int nTotPer = rsCountPermissaoTabelaTab.getInt("TOTAL");

                    boolean sisFlag = true;
                    if (nTotPer > 1){
                        psInterfaceDaTabela = this.conn.prepareStatement(this.sInterfaceDaTabela);
                        psInterfaceDaTabela.setString(1, rsIntMapeamento.getString("LAYOUT"));
                        rsInterfaceDaTabela = psInterfaceDaTabela.executeQuery();
                        rsInterfaceDaTabela.next();

                        if (!id_sistema_it.equals("")){
                            if (id_sistema_it.equals(rsInterfaceDaTabela.getString("ID_SISTEMA"))){
                                sisFlag = false;
                            }
                        }
                    }              

                    fileName = rsIntMapeamento.getString("LAYOUT").toLowerCase() + ".sql";
                    if(IDInterface == null || IDInterface.equals("") || sisFlag){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento\\" + fileName;
                    }else{
                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\"  + fileName;
                        }

                    }

                    logMessage("Creating or appending to file " + fileNameScripts);
                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    //strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&INTEGRACAO_TNS" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("/*###############################" + sQuebraLinha);
                    strOutScripts.append(rsIntMapeamento.getString("LAYOUT") + sQuebraLinha);
                    strOutScripts.append("###############################*/" + sQuebraLinha);
                    strOutScripts.append("--garante que não existirão registros duplicados" + sQuebraLinha);
                    strOutScripts.append("declare" + sQuebraLinha);
                    strOutScripts.append("    n_id   number := null;" + sQuebraLinha);
                    strOutScripts.append("begin" + sQuebraLinha);
                    strOutScripts.append("    select l.id into n_id" + sQuebraLinha);
                    strOutScripts.append("      from int_mapeamento_layout l " + sQuebraLinha);
                    strOutScripts.append("     where layout = '" + rsIntMapeamento.getString("LAYOUT") + "' " + sQuebraLinha);
                    strOutScripts.append("       and api = '" + rsIntMapeamento.getString("API") + "'; " + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    delete from int_mapeamento_coluna c where c.id = n_id;" + sQuebraLinha);
                    strOutScripts.append("    delete from int_mapeamento_layout l where l.id = n_id;" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    EXCEPTION" + sQuebraLinha);
                    strOutScripts.append("        WHEN NO_DATA_FOUND THEN" + sQuebraLinha);
                    strOutScripts.append("             null;" + sQuebraLinha);
                    strOutScripts.append("end;" + sQuebraLinha);
                    strOutScripts.append("/" + sQuebraLinha);
                    strOutScripts.append("insert into int_mapeamento_layout (id, layout, api, processa, ordem_processamento, tipo)" + sQuebraLinha);
                    strOutScripts.append("values ( seq_int_mapeamento_layout.nextval, '" + rsIntMapeamento.getString("LAYOUT") + "', '" + rsIntMapeamento.getString("API") + "', '" + rsIntMapeamento.getString("PROCESSA") + "', 1, 'LOADER');" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsIntMapeamento.getString("API") + sQuebraLinha);
                    strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + sQuebraLinha);

                    psIntMapeamentoColuna.setString(1, rsIntMapeamento.getString("ID"));
                    rsIntMapeamentoColuna = psIntMapeamentoColuna.executeQuery();
                    while(rsIntMapeamentoColuna.next()){
                        strOutScripts.append(sQuebraLinha);
                        strOutScripts.append("insert into int_mapeamento_coluna (id, api_coluna, layout_coluna,layout_formula)" + sQuebraLinha);
                        if(rsIntMapeamentoColuna.getString("LAYOUT_FORMULA")==null || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("") || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("null")){
                            strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '');      " + sQuebraLinha);
                        }else{
                            strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_FORMULA") + "');      " + sQuebraLinha);
                        }
                    }
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("commit;" + sQuebraLinha);

                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }catch(IOException ioex){
            ioex.printStackTrace();
        }
   }

    private void gerarIntMapeamentoIT() {
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        IDInterface = "";

        try{
            rsIntMapeamento = psIntMapeamentoIT.executeQuery();
            while(rsIntMapeamento.next()){

                psPermissaoTabelaTab = this.conn.prepareStatement(this.sSelectPermissaoTabelaTab);
                psPermissaoTabelaTab.setString(1, rsIntMapeamento.getString("LAYOUT"));
                rsPermissaoTabelaTab = psPermissaoTabelaTab.executeQuery();

                while(rsPermissaoTabelaTab.next()){
                    IDInterface = rsPermissaoTabelaTab.getString("ID_INTERFACE");

                    psCountPermissaoTabelaTab = this.conn.prepareStatement(this.sCountPermissaoTabelaTab);
                    psCountPermissaoTabelaTab.setString(1, rsIntMapeamento.getString("LAYOUT"));
                    rsCountPermissaoTabelaTab = psCountPermissaoTabelaTab.executeQuery();
                    rsCountPermissaoTabelaTab.next();
                    int nTotPer = rsCountPermissaoTabelaTab.getInt("TOTAL");

                    boolean sisFlag = true;
                    if (nTotPer > 1){
                        psInterfaceDaTabela = this.conn.prepareStatement(this.sInterfaceDaTabela);
                        psInterfaceDaTabela.setString(1, rsIntMapeamento.getString("LAYOUT"));
                        rsInterfaceDaTabela = psInterfaceDaTabela.executeQuery();
                        rsInterfaceDaTabela.next();

                        if (!id_sistema_it.equals("")){
                            if (id_sistema_it.equals(rsInterfaceDaTabela.getString("ID_SISTEMA"))){
                                sisFlag = false;
                            }
                        }
                    }

                    fileName = rsIntMapeamento.getString("LAYOUT").toLowerCase() + ".sql";
                    if(IDInterface == null || IDInterface.equals("") || sisFlag){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento\\" + fileName;
                    }else{
                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento\\"  + fileName;
                        }

                    }

                    logMessage("Creating or appending to file " + fileNameScripts);
                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);

                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("/*###############################" + sQuebraLinha);
                    strOutScripts.append(rsIntMapeamento.getString("LAYOUT") + sQuebraLinha);
                    strOutScripts.append("###############################*/" + sQuebraLinha);
                    strOutScripts.append("--garante que não existirão registros duplicados" + sQuebraLinha);
                    strOutScripts.append("declare" + sQuebraLinha);
                    strOutScripts.append("    n_id   number := null;" + sQuebraLinha);
                    strOutScripts.append("begin" + sQuebraLinha);
                    strOutScripts.append("    select l.id into n_id" + sQuebraLinha);
                    strOutScripts.append("      from int_mapeamento_layout l " + sQuebraLinha);
                    strOutScripts.append("     where layout = '" + rsIntMapeamento.getString("LAYOUT") + "' " + sQuebraLinha);
                    strOutScripts.append("       and api = '" + rsIntMapeamento.getString("API") + "'; " + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    delete from int_mapeamento_coluna c where c.id = n_id;" + sQuebraLinha);
                    strOutScripts.append("    delete from int_mapeamento_layout l where l.id = n_id;" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    EXCEPTION" + sQuebraLinha);
                    strOutScripts.append("        WHEN NO_DATA_FOUND THEN" + sQuebraLinha);
                    strOutScripts.append("             null;" + sQuebraLinha);
                    strOutScripts.append("end;" + sQuebraLinha);
                    strOutScripts.append("/" + sQuebraLinha);
                    strOutScripts.append("insert into int_mapeamento_layout (id, layout, api, processa, ordem_processamento, tipo)" + sQuebraLinha);
                    strOutScripts.append("values ( seq_int_mapeamento_layout.nextval, '" + rsIntMapeamento.getString("LAYOUT") + "', '" + rsIntMapeamento.getString("API") + "', '" + rsIntMapeamento.getString("PROCESSA") + "', 1, 'LOADER');" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsIntMapeamento.getString("API") + sQuebraLinha);
                    strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + sQuebraLinha);

                    psIntMapeamentoColunaIT.setString(1, rsIntMapeamento.getString("ID"));
                    rsIntMapeamentoColuna = psIntMapeamentoColunaIT.executeQuery();
                    while(rsIntMapeamentoColuna.next()){
                        strOutScripts.append(sQuebraLinha);
                        strOutScripts.append("insert into int_mapeamento_coluna (id, api_coluna, layout_coluna,layout_formula)" + sQuebraLinha);
                        if(rsIntMapeamentoColuna.getString("LAYOUT_FORMULA")==null || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("") || rsIntMapeamentoColuna.getString("LAYOUT_FORMULA").equals("null")){
                            strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '');      " + sQuebraLinha);
                        }else{
                            strOutScripts.append("values (seq_int_mapeamento_layout.currval, '" + rsIntMapeamentoColuna.getString("API_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_COLUNA") + "', '" + rsIntMapeamentoColuna.getString("LAYOUT_FORMULA") + "');      " + sQuebraLinha);
                        }
                    }
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("commit;" + sQuebraLinha);

                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }catch(IOException ioex){
            ioex.printStackTrace();
        }
   }

    private void gerarSAPMapeamento() {
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        this.IDInterface = "";

        try{
            rsSapMapeamento = psSapMapeamento.executeQuery();
            while(rsSapMapeamento.next()){

                psPermissaoTabelaTab = this.conn.prepareStatement(this.sSelectPermissaoTabelaTab);
                psPermissaoTabelaTab.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                rsPermissaoTabelaTab = psPermissaoTabelaTab.executeQuery();

                while(rsPermissaoTabelaTab.next()){
                    IDInterface = rsPermissaoTabelaTab.getString("ID_INTERFACE");

                    psCountPermissaoTabelaTab = this.conn.prepareStatement(this.sCountPermissaoTabelaTab);
                    psCountPermissaoTabelaTab.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                    rsCountPermissaoTabelaTab = psCountPermissaoTabelaTab.executeQuery();
                    rsCountPermissaoTabelaTab.next();
                    int nTotPer = rsCountPermissaoTabelaTab.getInt("TOTAL");

                    boolean sisFlag = true;
                    if (nTotPer > 1){
                        psInterfaceDaTabela = this.conn.prepareStatement(this.sInterfaceDaTabela);
                        psInterfaceDaTabela.setString(1, rsSapMapeamento.getString("TABLE_NAME"));
                        rsInterfaceDaTabela = psInterfaceDaTabela.executeQuery();
                        rsInterfaceDaTabela.next();

                        if (!id_sistema_it.equals("")){
                            if (id_sistema_it.equals(rsInterfaceDaTabela.getString("ID_SISTEMA"))){
                                sisFlag = false;
                            }
                        }
                    }

                    fileName = rsSapMapeamento.getString("IO_TABLE").toLowerCase() + ".sql";
                    if(IDInterface == null || IDInterface.equals("") || !sisFlag){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                    }else{
                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\"  + fileName;
                        }

                    }

                    logMessage("Creating or appending to file " + fileNameScripts);
                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    //strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&INTEGRACAO_TNS" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("/*###############################" + sQuebraLinha);
                    strOutScripts.append(rsSapMapeamento.getString("IO_TABLE") + "" + sQuebraLinha);
                    strOutScripts.append("###############################*/" + sQuebraLinha);
                    strOutScripts.append("--garante que não existirão registros duplicados" + sQuebraLinha);
                    strOutScripts.append("declare" + sQuebraLinha);
                    strOutScripts.append("    n_id   number := null;" + sQuebraLinha);
                    strOutScripts.append("begin" + sQuebraLinha);
                    strOutScripts.append("    select l.table_id into n_id" + sQuebraLinha);
                    strOutScripts.append("      from sap_interface_tables l " + sQuebraLinha);
                    strOutScripts.append("     where io_table = '" + rsSapMapeamento.getString("IO_TABLE") + "'" + sQuebraLinha);
                    strOutScripts.append("       and block_name = '" + rsSapMapeamento.getString("BLOCK_NAME") + "'; " + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    delete from sap_interface_columns c where c.table_id = n_id;" + sQuebraLinha);
                    strOutScripts.append("    delete from sap_interface_tables l where l.table_id = n_id;" + sQuebraLinha);
                    strOutScripts.append( sQuebraLinha);
                    strOutScripts.append("    EXCEPTION" + sQuebraLinha);
                    strOutScripts.append("        WHEN NO_DATA_FOUND THEN" + sQuebraLinha);
                    strOutScripts.append("             null;" + sQuebraLinha);
                    strOutScripts.append("end;" + sQuebraLinha);
                    strOutScripts.append("/" + sQuebraLinha);
                    strOutScripts.append("insert into sap_interface_tables" + sQuebraLinha);
                    strOutScripts.append("  (table_id, sap_table, io_table, description, block_name, block_order)" + sQuebraLinha);
                    strOutScripts.append("values" + sQuebraLinha);
                    strOutScripts.append("  ('" + rsSapMapeamento.getString("TABLE_ID") + "', '" + rsSapMapeamento.getString("SAP_TABLE") + "', '" + rsSapMapeamento.getString("IO_TABLE") + "', '" + rsSapMapeamento.getString("DESCRIPTION") + "', '" + rsSapMapeamento.getString("BLOCK_NAME") + "', '" + rsSapMapeamento.getString("BLOCK_ORDER") +"');" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsSapMapeamento.getString("BLOCK_NAME")  + sQuebraLinha );
                    strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + sQuebraLinha);

                    psSapMapeamentoColuna.setString(1, rsSapMapeamento.getString("TABLE_ID"));
                    rsSapMapeamentoColuna = psSapMapeamentoColuna.executeQuery();
                    while(rsSapMapeamentoColuna.next()){
                        strOutScripts.append( sQuebraLinha);
                        strOutScripts.append("  insert into sap_interface_columns (column_id, table_id, column_position, column_size, function, sap_column, io_column, description)" + sQuebraLinha);
                        strOutScripts.append("  values ('" + rsSapMapeamentoColuna.getString("COLUMN_ID") + "', '" + rsSapMapeamentoColuna.getString("TABLE_ID") + "', '" + rsSapMapeamentoColuna.getString("COLUMN_POSITION") + "', '" + rsSapMapeamentoColuna.getString("COLUMN_SIZE") + "', '" + rsSapMapeamentoColuna.getString("FUNCTION") + "', '" + rsSapMapeamentoColuna.getString("SAP_COLUMN") + "', '" + rsSapMapeamentoColuna.getString("IO_COLUMN") + "', '" + rsSapMapeamentoColuna.getString("DESCRIPTION") + "') ';" + sQuebraLinha);
                    }
                    
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("commit;" + sQuebraLinha);

                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }catch(IOException ioex){
            ioex.printStackTrace();
        }finally{
            try {
                this.rsSapMapeamento.close();
                this.rsPermissaoTabelaTab.close();
            } catch (SQLException ex) {
                Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
   }


    private void gerarSAPMapeamentoIT() {
		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        this.IDInterface = "";

        try{
            rsSapMapeamento = psSapMapeamentoIT.executeQuery();
            while(rsSapMapeamento.next()){

                psPermissaoTabelaTab = this.conn.prepareStatement(this.sSelectPermissaoTabelaTab);
                psPermissaoTabelaTab.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                rsPermissaoTabelaTab = psPermissaoTabelaTab.executeQuery();

                while(rsPermissaoTabelaTab.next()){
                    IDInterface = rsPermissaoTabelaTab.getString("ID_INTERFACE");

                    psCountPermissaoTabelaTab = this.conn.prepareStatement(this.sCountPermissaoTabelaTab);
                    psCountPermissaoTabelaTab.setString(1, rsSapMapeamento.getString("IO_TABLE"));
                    rsCountPermissaoTabelaTab = psCountPermissaoTabelaTab.executeQuery();
                    rsCountPermissaoTabelaTab.next();
                    int nTotPer = rsCountPermissaoTabelaTab.getInt("TOTAL");

                    boolean sisFlag = true;
                    if (nTotPer > 1){
                        psInterfaceDaTabela = this.conn.prepareStatement(this.sInterfaceDaTabela);
                        psInterfaceDaTabela.setString(1, rsSapMapeamento.getString("TABLE_NAME"));
                        rsInterfaceDaTabela = psInterfaceDaTabela.executeQuery();
                        rsInterfaceDaTabela.next();

                        if (!id_sistema_it.equals("")){
                            if (id_sistema_it.equals(rsInterfaceDaTabela.getString("ID_SISTEMA"))){
                                sisFlag = false;
                            }
                        }
                    }

                    fileName = rsSapMapeamento.getString("IO_TABLE").toLowerCase() + ".sql";
                    if(IDInterface == null || IDInterface.equals("") || sisFlag){
                        fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                    }else{
                        psFoundDadosInterface.setString(1, IDInterface);
                        rsFoundDadosInterface = psFoundDadosInterface.executeQuery();
                        while(rsFoundDadosInterface.next()){
                            sExecutavel = rsFoundDadosInterface.getString("EXECUTAVEL");
                            sTipoInterface = rsFoundDadosInterface.getString("TIPO_INTERFACE");
                            sIdSistema = rsFoundDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            sDescricao = rsFoundDadosInterface.getString("DESCRICAO");
                            sUserName = rsFoundDadosInterface.getString("USERNAME");
                            sTempoMedio = rsFoundDadosInterface.getString("TEMPO_MEDIO");
                            sExecutavelCompl = rsFoundDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        if(sTipoInterface.trim().equals("S")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        }else if(sTipoInterface.trim().equals("E")){
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\" + fileName;
                        }else{
                            fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INTEGRACAO\\Mapeamento_SAP\\"  + fileName;
                        }

                    }

                    logMessage("Creating or appending to file " + fileNameScripts);
                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + sQuebraLinha + sQuebraLinha);
                    //strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&INTEGRACAO_TNS" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("/*###############################" + sQuebraLinha);
                    strOutScripts.append(rsSapMapeamento.getString("IO_TABLE") + "" + sQuebraLinha);
                    strOutScripts.append("###############################*/" + sQuebraLinha);
                    strOutScripts.append("--garante que não existirão registros duplicados" + sQuebraLinha);
                    strOutScripts.append("declare" + sQuebraLinha);
                    strOutScripts.append("    n_id   number := null;" + sQuebraLinha);
                    strOutScripts.append("begin" + sQuebraLinha);
                    strOutScripts.append("    select l.table_id into n_id" + sQuebraLinha);
                    strOutScripts.append("      from sap_interface_tables l " + sQuebraLinha);
                    strOutScripts.append("     where io_table = '" + rsSapMapeamento.getString("IO_TABLE") + "'" + sQuebraLinha);
                    strOutScripts.append("       and block_name = '" + rsSapMapeamento.getString("BLOCK_NAME") + "'; " + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("    delete from sap_interface_columns c where c.table_id = n_id;" + sQuebraLinha);
                    strOutScripts.append("    delete from sap_interface_tables l where l.table_id = n_id;" + sQuebraLinha);
                    strOutScripts.append( sQuebraLinha);
                    strOutScripts.append("    EXCEPTION" + sQuebraLinha);
                    strOutScripts.append("        WHEN NO_DATA_FOUND THEN" + sQuebraLinha);
                    strOutScripts.append("             null;" + sQuebraLinha);
                    strOutScripts.append("end;" + sQuebraLinha);
                    strOutScripts.append("/" + sQuebraLinha);
                    strOutScripts.append("insert into sap_interface_tables" + sQuebraLinha);
                    strOutScripts.append("  (table_id, sap_table, io_table, description, block_name, block_order)" + sQuebraLinha);
                    strOutScripts.append("values" + sQuebraLinha);
                    strOutScripts.append("  ('" + rsSapMapeamento.getString("TABLE_ID") + "', '" + rsSapMapeamento.getString("SAP_TABLE") + "', '" + rsSapMapeamento.getString("IO_TABLE") + "', '" + rsSapMapeamento.getString("DESCRIPTION") + "', '" + rsSapMapeamento.getString("BLOCK_NAME") + "', '" + rsSapMapeamento.getString("BLOCK_ORDER") +"');" + sQuebraLinha);
                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("--inserções de relacionamento das tags contidas dentro da tag " + rsSapMapeamento.getString("BLOCK_NAME")  + sQuebraLinha );
                    strOutScripts.append("--relacionam a tag com uma coluna da tabela temporário do INOUT" + sQuebraLinha);

                    psSapMapeamentoColunaIT.setString(1, rsSapMapeamento.getString("TABLE_ID"));
                    rsSapMapeamentoColuna = psSapMapeamentoColunaIT.executeQuery();
                    while(rsSapMapeamentoColuna.next()){
                        strOutScripts.append( sQuebraLinha);
                        strOutScripts.append("  insert into sap_interface_columns (column_id, table_id, column_position, column_size, function, sap_column, io_column, description)" + sQuebraLinha);
                        strOutScripts.append("  values ('" + rsSapMapeamentoColuna.getString("COLUMN_ID") + "', '" + rsSapMapeamentoColuna.getString("TABLE_ID") + "', '" + rsSapMapeamentoColuna.getString("COLUMN_POSITION") + "', '" + rsSapMapeamentoColuna.getString("COLUMN_SIZE") + "', '" + rsSapMapeamentoColuna.getString("FUNCTION") + "', '" + rsSapMapeamentoColuna.getString("SAP_COLUMN") + "', '" + rsSapMapeamentoColuna.getString("IO_COLUMN") + "', '" + rsSapMapeamentoColuna.getString("DESCRIPTION") + "') ';" + sQuebraLinha);
                    }

                    strOutScripts.append(sQuebraLinha);
                    strOutScripts.append("commit;" + sQuebraLinha);

                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }catch(IOException ioex){
            ioex.printStackTrace();
        }finally{
            try {
                rsSapMapeamento.close();
                rsPermissaoTabelaTab.close();
            } catch (SQLException ex) {
                Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

   }

	/**************************************************************************
	 * <b>Gerar arquivos externos</b>
	 **************************************************************************/
	private void exportarArquivosExternos() throws Exception{

        //psArquivosExternos = this.conn.prepareStatement(this.sSelectExportarArquivosExternos);
        try{
            psExportarArquivosExternos.setString(1, sExecutavel);
            rsArquivosExternos = psExportarArquivosExternos.executeQuery();

            while(rsArquivosExternos.next()){

                fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                if(sTipoInterface.trim().equals("S")){
                    fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_out_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else if(sTipoInterface.trim().equals("E")){
                    fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }else{
                    fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }
                logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if(clob!=null){
                    /******************************************
                     * Gerando arquivos na pasta de Arquivos
                     ******************************************/
                    File file = new File(fileName);
                    if(!file.exists())
                        file.createNewFile();

                    FileWriter fw = new FileWriter(file, false);

                    StringBuffer strOut = new StringBuffer();
                    String aux;

                    // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                    BufferedReader br = new BufferedReader(clob.getCharacterStream());

                    while ((aux=br.readLine())!=null){
                        strOut.append(aux);
                        strOut.append(sQuebraLinha);
                    }

                    fw.write(strOut.toString(),0,strOut.length());
                    fw.close();

                    logMessage("File " + fileName + " was succesfull generated.");
                }else{
                    logMessage("No data are being generated");
                    logMessage("File " + fileName + " wasn't generated.");
                }
            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}

	/**************************************************************************
	 * <b>Exportar Tabelas</b>
	 **************************************************************************/
	private void exportarTabInterface() throws Exception{

		psPermissaoTabela.setString(1, IDInterface);
		rsPermissaoTabela = psPermissaoTabela.executeQuery();

		while(rsPermissaoTabela.next()){

			fileName = rsPermissaoTabela.getString("TABLE_NAME").toLowerCase() + ".sql";

			//psTabInterface = this.conn.prepareStatement(sSelectTabInterface);
			psTabInterface.setString(1, rsPermissaoTabela.getString("TABLE_NAME"));
			rsTabInterface = psTabInterface.executeQuery();

			logMessage("Creating or appending to file " + fileName);

			while(rsTabInterface.next()){
				StringBuffer strOut = new StringBuffer();
                StringBuffer strOutCltFixo = new StringBuffer();

                if(rsTabInterface.getCharacterStream("CTL_FIXO") != null){
                    BufferedReader brCtlFixo = new BufferedReader(rsTabInterface.getCharacterStream("CTL_FIXO"));

                    if (brCtlFixo != null){
                        String aux;

                        while ((aux=brCtlFixo.readLine())!=null){
                            strOutCltFixo.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','CTL_FIXO','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                            strOutCltFixo.append( sQuebraLinha + sQuebraLinha);
                        }
                    }
                }


                strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS");
                strOut.append( sQuebraLinha + sQuebraLinha);
				strOut.append("--  ///////" + sQuebraLinha);
				strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + sQuebraLinha);
				strOut.append("--  ///////     TABELA: " + rsTabInterface.getString("TABLE_NAME")  + sQuebraLinha);
				strOut.append("--  ///////" + sQuebraLinha + sQuebraLinha);
				strOut.append("delete from DEPEND_HEADER_ITEM_TMP;" + sQuebraLinha + sQuebraLinha);
				strOut.append("delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "';" + sQuebraLinha + sQuebraLinha);
				strOut.append("begin" + sQuebraLinha + sQuebraLinha);
				strOut.append("  update    TAB_INTERFACE" + sQuebraLinha);
				if(rsTabInterface.getString("DESCRICAO") == null){
					strOut.append("  set       DESCRICAO = '',");
				}else{
					strOut.append("  set       DESCRICAO = '" + rsTabInterface.getString("DESCRICAO") + "',");
				}
                strOut.append(sQuebraLinha);
				strOut.append("         CTL_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "', + sQuebraLinha");
				strOut.append("        PREFIX_FILE = '" + rsTabInterface.getString("PREFIX_FILE") + "', + sQuebraLinha");
				strOut.append("        GERAR_CTL = '" + rsTabInterface.getString("GERAR_CTL") + "', + sQuebraLinha");
				strOut.append("        PRIORIDADE = " + rsTabInterface.getString("PRIORIDADE") + ", + sQuebraLinha");

				if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
					strOut.append("        ODBC_SOURCE_NAME = '',");
				}else{
					strOut.append("        ODBC_SOURCE_NAME = '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
					strOut.append("        ODBC_USER = '',");
				}else{
					strOut.append("        ODBC_USER = '" + rsTabInterface.getString("ODBC_USER") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ODBC_PASSWORD") == null){
					strOut.append("        ODBC_PASSWORD = '',");
				}else{
					strOut.append("        ODBC_PASSWORD = '" + rsTabInterface.getString("ODBC_PASSWORD") + "',");
				}
				strOut.append( sQuebraLinha);

				if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
					strOut.append("        ODBC_TABLE_NAME = '',");
				}else{
					strOut.append("        ODBC_TABLE_NAME = '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ODBC_WHERE") == null){
					strOut.append("        ODBC_WHERE = '',");
				}else{
					strOut.append("        ODBC_WHERE = '" + rsTabInterface.getString("ODBC_WHERE") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
					strOut.append("        ODBC_SELECT_ESPECIFICO = '',");
				}else{
					strOut.append("        ODBC_SELECT_ESPECIFICO = '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("TIPO_INTERFACE") == null){
					strOut.append("        TIPO_INTERFACE = '',");
				}else{
					strOut.append("        TIPO_INTERFACE = '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
					strOut.append("        ORACLE_INITIAL_EXTENT = '',");
				}else{
					strOut.append("        ORACLE_INITIAL_EXTENT = '"+rsTabInterface.getString("ORACLE_INITIAL_EXTENT")+"',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
					strOut.append("        ORACLE_NEXT_EXTENT = '',");
				}else{
					strOut.append("        ORACLE_NEXT_EXTENT = '"+rsTabInterface.getString("ORACLE_NEXT_EXTENT")+"',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
					strOut.append("        ORACLE_INDEX_TABLESPACE = '',");
				}else{
					strOut.append("        ORACLE_INDEX_TABLESPACE = '"+rsTabInterface.getString("ORACLE_INDEX_TABLESPACE")+"',");
				}
				strOut.append( sQuebraLinha);
				if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
					strOut.append("        ELIMINAR_REG_EXECUCAO = '',");
				}else{
					strOut.append("        ELIMINAR_REG_EXECUCAO = '"+rsTabInterface.getString("ELIMINAR_REG_EXECUCAO")+"',");
				}
				strOut.append(sQuebraLinha);
				if(rsTabInterface.getString("ID_SISTEMA") == null){
					strOut.append("        ID_SISTEMA = '',");
				}else{
					strOut.append("        ID_SISTEMA = '"+rsTabInterface.getString("ID_SISTEMA")+"',");
				}
				strOut.append(sQuebraLinha);
				if(rsTabInterface.getString("PROCEDURE_NAME") == null){
					strOut.append("        PROCEDURE_NAME = '',");
				}else{
					strOut.append("        PROCEDURE_NAME = '"+rsTabInterface.getString("PROCEDURE_NAME")+"',");
				}
				strOut.append(sQuebraLinha);
				if(rsTabInterface.getString("SEPARADOR") == null){
					strOut.append("        SEPARADOR = '',");
				}else{
					strOut.append("        SEPARADOR = '"+rsTabInterface.getString("SEPARADOR")+"',");
				}
				strOut.append(sQuebraLinha);

				strOut.append("        CTL_FIXO = null,");
				strOut.append(sQuebraLinha);
				strOut.append("        TRIGGER1 = null,");
				strOut.append(sQuebraLinha);
				strOut.append("        TRIGGER2 = null,");
				strOut.append(sQuebraLinha);
				if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
					strOut.append("        COMANDO_EXTRA_LOADER = ''");
				}else{
					strOut.append("        COMANDO_EXTRA_LOADER = '"+rsTabInterface.getString("COMANDO_EXTRA_LOADER")+"'");
				}
				strOut.append(sQuebraLinha);
				strOut.append("  where TABLE_NAME = '"+rsTabInterface.getString("TABLE_NAME")+"';" + sQuebraLinha + sQuebraLinha);
				strOut.append("  if SQL%notfound then" + sQuebraLinha);
				strOut.append("        insert into TAB_INTERFACE" + sQuebraLinha);
				strOut.append("        (TABLE_NAME, " + sQuebraLinha + "        DESCRICAO, " + sQuebraLinha + "        CTL_NAME, " + sQuebraLinha + "        PREFIX_FILE, " + sQuebraLinha + "        GERAR_CTL, " + sQuebraLinha + "        PRIORIDADE, " + sQuebraLinha + "        ODBC_SOURCE_NAME, " + sQuebraLinha + "        ODBC_USER, " + sQuebraLinha + "        ODBC_PASSWORD, " + sQuebraLinha + "        ODBC_TABLE_NAME, " + sQuebraLinha + "        ODBC_WHERE, " + sQuebraLinha + "        ODBC_SELECT_ESPECIFICO, " + sQuebraLinha + "        TIPO_INTERFACE," + sQuebraLinha);
				strOut.append("        ORACLE_INITIAL_EXTENT, " + sQuebraLinha + "        ORACLE_NEXT_EXTENT, " + sQuebraLinha + "        ORACLE_INDEX_TABLESPACE, " + sQuebraLinha + "        ELIMINAR_REG_EXECUCAO, " + sQuebraLinha + "        COMANDO_EXTRA_LOADER, " + sQuebraLinha + "        ID_SISTEMA, " + sQuebraLinha + "        PROCEDURE_NAME, " + sQuebraLinha + "        SEPARADOR)" + sQuebraLinha);
				strOut.append("        values" + sQuebraLinha);
				//strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', '" + rsTabInterface.getString("CTL_NAME") + "', '" + rsTabInterface.getString("PREFIX_FILE") + "', '" + rsTabInterface.getString("GERAR_CTL") + "', " + rsTabInterface.getString("PRIORIDADE") + ", '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "', '" + rsTabInterface.getString("ODBC_USER") +"', '" + rsTabInterface.getString("ODBC_PASSWORD") + "', '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "', '" + rsTabInterface.getString("ODBC_WHERE") + "', '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
				if(rsTabInterface.getString("TABLE_NAME") == null){
					strOut.append("        ('', " + sQuebraLinha + "        '");
				}else{
					strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("DESCRICAO") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("DESCRICAO") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("CTL_NAME") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("CTL_NAME") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("PREFIX_FILE") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("PREFIX_FILE") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("GERAR_CTL") == null){
					strOut.append("', " + sQuebraLinha + "        ");
				}else{
					strOut.append(rsTabInterface.getString("GERAR_CTL") + "', " + sQuebraLinha + "        ");
				}

				strOut.append(rsTabInterface.getString("PRIORIDADE") + ", " + sQuebraLinha + "        '");

				if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_SOURCE_NAME") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ODBC_USER") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_USER") +"', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ODBC_PASSWORD") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_PASSWORD") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_TABLE_NAME") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ODBC_WHERE") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_WHERE") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("TIPO_INTERFACE") == null){
					strOut.append("'," + sQuebraLinha + "        ");
				}else{
					strOut.append(rsTabInterface.getString("TIPO_INTERFACE") + "'," + sQuebraLinha + "        ");
				}
				//strOut.append(sQuebraLinha);

				//strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "','" + rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "','" + rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "',");
				if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
					strOut.append("        '', " + sQuebraLinha + "        '");
				}else{
					strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
					strOut.append("', " + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', " + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
					strOut.append("'," + sQuebraLinha + "        '" );
				}else{
					strOut.append(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "'," + sQuebraLinha + "        '" );
				}

				if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
					strOut.append("'," + sQuebraLinha + "        '");
				}else{
					strOut.append(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "'," + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
					strOut.append("'," + sQuebraLinha + "        ");
				}else{
					strOut.append(rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "'," + sQuebraLinha + "        ");
				}

				//strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "','" + rsTabInterface.getString("PROCEDURE_NAME") + "','" + rsTabInterface.getString("SEPARADOR") +"');");

				if(rsTabInterface.getString("ID_SISTEMA") == null){
					strOut.append("        ''," + sQuebraLinha + "        '");
				}else{
					strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "'," + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("PROCEDURE_NAME") == null){
					strOut.append("'," + sQuebraLinha + "        '");
				}else{
					strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "'," + sQuebraLinha + "        '");
				}

				if(rsTabInterface.getString("SEPARADOR") == null){
					strOut.append("');");
				}else{
					strOut.append(rsTabInterface.getString("SEPARADOR") + "')");
				}

				strOut.append(sQuebraLinha);
				strOut.append("  end if;");
				strOut.append(sQuebraLinha);
				strOut.append("end;");
				strOut.append(sQuebraLinha);
				strOut.append("/");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);

                strOut.append(strOutCltFixo);

				if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
					String aux;
					BufferedReader br2 = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

					while ((aux=br2.readLine())!=null){
						strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER1','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
						strOut.append(sQuebraLinha + "" + sQuebraLinha);
					}
				}


				if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
					String aux;
					BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

					while ((aux=br.readLine())!=null){
						strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER2','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
						strOut.append(sQuebraLinha + "" + sQuebraLinha);
					}
				}

				//psColunasTabInterface = this.conn.prepareStatement(sSelectColunasTabInterface);
				psColunasTabInterface.setString(1, rsPermissaoTabela.getString("TABLE_NAME"));
				rsColunasTabInterface = psColunasTabInterface.executeQuery();

				while(rsColunasTabInterface.next()){
					strOut.append("insert into COLUNAS_TAB_INTERFACE");
					strOut.append("(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)");
					strOut.append("values");
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


					strOut.append(sQuebraLinha + "" + sQuebraLinha);
				}

				strOut.append("commit;");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("set serveroutput on");
				strOut.append(sQuebraLinha);
				strOut.append("exec PRC_SINCRONIZA_TABELA('"+rsPermissaoTabela.getString("TABLE_NAME")+"');");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);


				if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
					String aux;
					BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

					while ((aux=br.readLine())!=null){
						strOut.append(aux);
						strOut.append(sQuebraLinha);
					}
					strOut.append("/" + sQuebraLinha + sQuebraLinha);
				}


				if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
					String aux;
					BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

					while ((aux=br.readLine())!=null){
						strOut.append(aux);
						strOut.append(sQuebraLinha);
					}
					strOut.append("/" + sQuebraLinha);
				}

				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("CREATE OR REPLACE");
				strOut.append(sQuebraLinha);
				strOut.append("TRIGGER TI_" + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("BEFORE INSERT ON " + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("REFERENCING OLD AS old NEW AS new");
				strOut.append(sQuebraLinha);
				strOut.append("FOR EACH ROW");
				strOut.append(sQuebraLinha);
				strOut.append("begin");
				strOut.append(sQuebraLinha);
				strOut.append("    select seq_id_interface.nextval into :new.id from dual;");
			    strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("end;");
                strOut.append(sQuebraLinha);
				strOut.append("/");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("CREATE OR REPLACE");
				strOut.append(sQuebraLinha);
				strOut.append("TRIGGER TIA_" + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("AFTER INSERT ON " + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("REFERENCING OLD AS old NEW AS new");
				strOut.append(sQuebraLinha);
				strOut.append("FOR EACH ROW");
				strOut.append(sQuebraLinha);
				strOut.append("begin");
				strOut.append(sQuebraLinha);
				strOut.append("    insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)");
				strOut.append(sQuebraLinha);
				strOut.append("    values (:new.id, :new.id_importacao, '" + rsPermissaoTabela.getString("TABLE_NAME") + "', :new.id_ref);");
				strOut.append(sQuebraLinha);
				strOut.append("end;");
				strOut.append(sQuebraLinha);
				strOut.append("/");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("CREATE OR REPLACE");
				strOut.append(sQuebraLinha);
				strOut.append("TRIGGER TD_" + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("BEFORE DELETE ON " + rsPermissaoTabela.getString("TABLE_NAME"));
				strOut.append(sQuebraLinha);
				strOut.append("REFERENCING OLD AS old NEW AS new");
				strOut.append(sQuebraLinha);
				strOut.append("FOR EACH ROW");
				strOut.append(sQuebraLinha);
				strOut.append("begin");
				strOut.append(sQuebraLinha);
				strOut.append("    delete from REGISTROS_INTERFACES where id = :old.id;");
				strOut.append(sQuebraLinha);
				strOut.append("end;");
				strOut.append(sQuebraLinha);
				strOut.append("/");
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append(sQuebraLinha + "" + sQuebraLinha);
				strOut.append("-- //////");
				strOut.append(sQuebraLinha);
				strOut.append("-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas");
				strOut.append(sQuebraLinha);
				strOut.append("-- //////");

                psCountPermissaoTabela.setString(1, rsPermissaoTabela.getString("TABLE_NAME"));
                rsCountPermissaoTabela = psCountPermissaoTabela.executeQuery();
                rsCountPermissaoTabela.next();

                if(rsCountPermissaoTabela.getInt("TOTAL_INTERFACES") == 1){
                    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Tabelas\\" + fileName;
                    fileName = path + "\\"+sUserName+"\\Arquivos\\" + this.getIDSistema() + "_in_" + this.getIDInterface() + "\\INOUT\\Tabelas\\" + fileName;
                }else{
                    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Tabelas\\" + fileName;
                    fileName = path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Tabelas\\" + fileName;
                }

				File file = new File(fileName);
				if(!file.exists())
					file.createNewFile();

				FileWriter fw = new FileWriter(file, false);

				fw.write(strOut.toString(),0,strOut.length());
				fw.close();

				File fileScripts = new File(fileNameScripts);
				if(!fileScripts.exists())
					fileScripts.createNewFile();

				FileWriter fwScripts = new FileWriter(fileScripts, false);

				fwScripts.write(strOut.toString(),0,strOut.length());
				fwScripts.close();
			}

			logMessage("File " + fileName + " was succesfull generated.");

		}
	}

	/**************************************************************************
	 * <b>Exportar Tabelas sem vinculo com alguma interface</b>
	 **************************************************************************/
	private void exportarTabInterfaceSemPermissao() throws Exception{
        String sSelectTabsSemPermissao = "select table_name from tab_interface tab where tab.table_name not in (select table_name from permissao_tabela)";
        PreparedStatement psTabsSemPermissao = this.conn.prepareStatement(sSelectTabsSemPermissao);
		ResultSet rsTabsSemPermissao = psTabsSemPermissao.executeQuery();

        try{
            while(rsTabsSemPermissao.next()){

                fileName = rsTabsSemPermissao.getString("TABLE_NAME").toLowerCase() + ".sql";

                //psTabInterface = this.conn.prepareStatement(sSelectTabInterface);
                psTabInterface.setString(1, rsTabsSemPermissao.getString("TABLE_NAME"));
                rsTabInterface = psTabInterface.executeQuery();

                logMessage("Creating or appending to file " + fileName);

                while(rsTabInterface.next()){
                    StringBuffer strOut = new StringBuffer();
                    StringBuffer strOutCltFixo = new StringBuffer();

                    if(rsTabInterface.getCharacterStream("CTL_FIXO") != null){
                        BufferedReader brCtlFixo = new BufferedReader(rsTabInterface.getCharacterStream("CTL_FIXO"));

                        if (brCtlFixo != null){
                            String aux;

                            while ((aux=brCtlFixo.readLine())!=null){
                                strOutCltFixo.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','CTL_FIXO','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                                strOutCltFixo.append(sQuebraLinha + "" + sQuebraLinha);
                            }
                        }
                    }

                    strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("--  ///////");
                    strOut.append(sQuebraLinha);
                    strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT");
                    strOut.append(sQuebraLinha);
                    strOut.append("--  ///////     TABELA: " + rsTabInterface.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("--  ///////");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("delete from DEPEND_HEADER_ITEM_TMP;");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "';");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("begin");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("  update    TAB_INTERFACE");
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("DESCRICAO") == null){
                        strOut.append("  set       DESCRICAO = '',");
                    }else{
                        strOut.append("  set       DESCRICAO = '" + rsTabInterface.getString("DESCRICAO") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    strOut.append("         CTL_NAME = '" + rsTabInterface.getString("TABLE_NAME") + "',");
                    strOut.append(sQuebraLinha);
                    strOut.append("        PREFIX_FILE = '" + rsTabInterface.getString("PREFIX_FILE") + "',");
                    strOut.append(sQuebraLinha);
                    strOut.append("        GERAR_CTL = '" + rsTabInterface.getString("GERAR_CTL") + "',");
                    strOut.append(sQuebraLinha);
                    strOut.append("        PRIORIDADE = " + rsTabInterface.getString("PRIORIDADE") + ",");

                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("        ODBC_SOURCE_NAME = '',");
                    }else{
                        strOut.append("        ODBC_SOURCE_NAME = '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("        ODBC_USER = '',");
                    }else{
                        strOut.append("        ODBC_USER = '" + rsTabInterface.getString("ODBC_USER") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ODBC_PASSWORD") == null){
                        strOut.append("        ODBC_PASSWORD = '',");
                    }else{
                        strOut.append("        ODBC_PASSWORD = '" + rsTabInterface.getString("ODBC_PASSWORD") + "',");
                    }
                    strOut.append(sQuebraLinha);

                    if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
                        strOut.append("        ODBC_TABLE_NAME = '',");
                    }else{
                        strOut.append("        ODBC_TABLE_NAME = '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ODBC_WHERE") == null){
                        strOut.append("        ODBC_WHERE = '',");
                    }else{
                        strOut.append("        ODBC_WHERE = '" + rsTabInterface.getString("ODBC_WHERE") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
                        strOut.append("        ODBC_SELECT_ESPECIFICO = '',");
                    }else{
                        strOut.append("        ODBC_SELECT_ESPECIFICO = '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("TIPO_INTERFACE") == null){
                        strOut.append("        TIPO_INTERFACE = '',");
                    }else{
                        strOut.append("        TIPO_INTERFACE = '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
                        strOut.append("        ORACLE_INITIAL_EXTENT = '',");
                    }else{
                        strOut.append("        ORACLE_INITIAL_EXTENT = '"+rsTabInterface.getString("ORACLE_INITIAL_EXTENT")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
                        strOut.append("        ORACLE_NEXT_EXTENT = '',");
                    }else{
                        strOut.append("        ORACLE_NEXT_EXTENT = '"+rsTabInterface.getString("ORACLE_NEXT_EXTENT")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
                        strOut.append("        ORACLE_INDEX_TABLESPACE = '',");
                    }else{
                        strOut.append("        ORACLE_INDEX_TABLESPACE = '"+rsTabInterface.getString("ORACLE_INDEX_TABLESPACE")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
                        strOut.append("        ELIMINAR_REG_EXECUCAO = '',");
                    }else{
                        strOut.append("        ELIMINAR_REG_EXECUCAO = '"+rsTabInterface.getString("ELIMINAR_REG_EXECUCAO")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("ID_SISTEMA") == null){
                        strOut.append("        ID_SISTEMA = '',");
                    }else{
                        strOut.append("        ID_SISTEMA = '"+rsTabInterface.getString("ID_SISTEMA")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("PROCEDURE_NAME") == null){
                        strOut.append("        PROCEDURE_NAME = '',");
                    }else{
                        strOut.append("        PROCEDURE_NAME = '"+rsTabInterface.getString("PROCEDURE_NAME")+"',");
                    }
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("SEPARADOR") == null){
                        strOut.append("        SEPARADOR = '',");
                    }else{
                        strOut.append("        SEPARADOR = '"+rsTabInterface.getString("SEPARADOR")+"',");
                    }
                    strOut.append(sQuebraLinha);

                    strOut.append("        CTL_FIXO = null,");
                    strOut.append(sQuebraLinha);
                    strOut.append("        TRIGGER1 = null,");
                    strOut.append(sQuebraLinha);
                    strOut.append("        TRIGGER2 = null,");
                    strOut.append(sQuebraLinha);
                    if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
                        strOut.append("        COMANDO_EXTRA_LOADER = ''");
                    }else{
                        strOut.append("        COMANDO_EXTRA_LOADER = '"+rsTabInterface.getString("COMANDO_EXTRA_LOADER")+"'");
                    }
                    strOut.append(sQuebraLinha);
                    strOut.append("  where TABLE_NAME = '"+rsTabInterface.getString("TABLE_NAME")+"';");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha );
                    strOut.append("  if SQL%notfound then");
                    strOut.append(sQuebraLinha);
                    strOut.append("        insert into TAB_INTERFACE");
                    strOut.append(sQuebraLinha);
                    strOut.append("        (TABLE_NAME, " + sQuebraLinha + "        DESCRICAO, " + sQuebraLinha + "        CTL_NAME, " + sQuebraLinha + "        PREFIX_FILE, " + sQuebraLinha + "        GERAR_CTL, " + sQuebraLinha + "        PRIORIDADE, " + sQuebraLinha + "        ODBC_SOURCE_NAME, " + sQuebraLinha + "        ODBC_USER, " + sQuebraLinha + "        ODBC_PASSWORD, " + sQuebraLinha + "        ODBC_TABLE_NAME, " + sQuebraLinha + "        ODBC_WHERE, " + sQuebraLinha + "        ODBC_SELECT_ESPECIFICO, " + sQuebraLinha + "        TIPO_INTERFACE,");
                    strOut.append(sQuebraLinha);
                    strOut.append("        ORACLE_INITIAL_EXTENT, " + sQuebraLinha + "        ORACLE_NEXT_EXTENT, " + sQuebraLinha + "        ORACLE_INDEX_TABLESPACE, " + sQuebraLinha + "        ELIMINAR_REG_EXECUCAO, " + sQuebraLinha + "        COMANDO_EXTRA_LOADER, " + sQuebraLinha + "        ID_SISTEMA, " + sQuebraLinha + "        PROCEDURE_NAME, " + sQuebraLinha + "        SEPARADOR)");
                    strOut.append(sQuebraLinha);
                    strOut.append("        values");
                    strOut.append(sQuebraLinha);
                    //strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', '" + rsTabInterface.getString("CTL_NAME") + "', '" + rsTabInterface.getString("PREFIX_FILE") + "', '" + rsTabInterface.getString("GERAR_CTL") + "', " + rsTabInterface.getString("PRIORIDADE") + ", '" + rsTabInterface.getString("ODBC_SOURCE_NAME") + "', '" + rsTabInterface.getString("ODBC_USER") +"', '" + rsTabInterface.getString("ODBC_PASSWORD") + "', '" + rsTabInterface.getString("ODBC_TABLE_NAME") + "', '" + rsTabInterface.getString("ODBC_WHERE") + "', '" + rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', '" + rsTabInterface.getString("TIPO_INTERFACE") + "',");
                    if(rsTabInterface.getString("TABLE_NAME") == null){
                        strOut.append("        ('', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append("        ('" + rsTabInterface.getString("TABLE_NAME") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("DESCRICAO") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("DESCRICAO") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("CTL_NAME") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("CTL_NAME") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("PREFIX_FILE") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("PREFIX_FILE") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("GERAR_CTL") == null){
                        strOut.append("', " + sQuebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("GERAR_CTL") + "', " + sQuebraLinha + "        ");
                    }

                    strOut.append(rsTabInterface.getString("PRIORIDADE") + ", " + sQuebraLinha + "        '");

                    if(rsTabInterface.getString("ODBC_SOURCE_NAME") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_SOURCE_NAME") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_USER") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_USER") +"', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_PASSWORD") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_PASSWORD") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_TABLE_NAME") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_TABLE_NAME") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_WHERE") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_WHERE") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ODBC_SELECT_ESPECIFICO") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("TIPO_INTERFACE") == null){
                        strOut.append("'," + sQuebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("TIPO_INTERFACE") + "'," + sQuebraLinha + "        ");
                    }
                    //strOut.append(sQuebraLinha);

                    //strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', '" + rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "','" + rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "','" + rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "',");
                    if(rsTabInterface.getString("ORACLE_INITIAL_EXTENT") == null){
                        strOut.append("        '', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append("        '" + rsTabInterface.getString("ORACLE_INITIAL_EXTENT") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ORACLE_NEXT_EXTENT") == null){
                        strOut.append("', " + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ORACLE_NEXT_EXTENT") + "', " + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") == null){
                        strOut.append("'," + sQuebraLinha + "        '" );
                    }else{
                        strOut.append(rsTabInterface.getString("ORACLE_INDEX_TABLESPACE") + "'," + sQuebraLinha + "        '" );
                    }

                    if(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") == null){
                        strOut.append("'," + sQuebraLinha + "        '");
                    }else{
                        strOut.append(rsTabInterface.getString("ELIMINAR_REG_EXECUCAO") + "'," + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("COMANDO_EXTRA_LOADER") == null){
                        strOut.append("'," + sQuebraLinha + "        ");
                    }else{
                        strOut.append(rsTabInterface.getString("COMANDO_EXTRA_LOADER") + "'," + sQuebraLinha + "        ");
                    }

                    //strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "','" + rsTabInterface.getString("PROCEDURE_NAME") + "','" + rsTabInterface.getString("SEPARADOR") +"');");

                    if(rsTabInterface.getString("ID_SISTEMA") == null){
                        strOut.append("        ''," + sQuebraLinha + "        '");
                    }else{
                        strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "'," + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("PROCEDURE_NAME") == null){
                        strOut.append("'," + sQuebraLinha + "        '");
                    }else{
                        strOut.append("        '" + rsTabInterface.getString("ID_SISTEMA") + "'," + sQuebraLinha + "        '");
                    }

                    if(rsTabInterface.getString("SEPARADOR") == null){
                        strOut.append("');");
                    }else{
                        strOut.append(rsTabInterface.getString("SEPARADOR") + "')");
                    }

                    strOut.append(sQuebraLinha);
                    strOut.append("  end if;");
                    strOut.append(sQuebraLinha);
                    strOut.append("end;");
                    strOut.append(sQuebraLinha);
                    strOut.append("/");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);

                    strOut.append(strOutCltFixo);

                    if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
                        String aux;
                        BufferedReader br2 = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

                        while ((aux=br2.readLine())!=null){
                            strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER1','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                            strOut.append(sQuebraLinha + "" + sQuebraLinha);
                        }
                    }


                    if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

                        while ((aux=br.readLine())!=null){
                            strOut.append("exec PRC_UPDATE_CONCATENA_LONG('TAB_INTERFACE','TRIGGER2','TABLE_NAME','" + rsTabInterface.getString("TABLE_NAME") + "','" + aux.replace("'", "''") + "');");
                            strOut.append(sQuebraLinha + "" + sQuebraLinha);
                        }
                    }

                    //psColunasTabInterface = this.conn.prepareStatement(sSelectColunasTabInterface);
                    psColunasTabInterface.setString(1, rsTabsSemPermissao.getString("TABLE_NAME"));
                    rsColunasTabInterface = psColunasTabInterface.executeQuery();

                    while(rsColunasTabInterface.next()){
                        strOut.append("insert into COLUNAS_TAB_INTERFACE");
                        strOut.append("(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)");
                        strOut.append("values");
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


                        strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    }

                    strOut.append("commit;");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("set serveroutput on");
                    strOut.append(sQuebraLinha);
                    strOut.append("exec PRC_SINCRONIZA_TABELA('"+rsTabsSemPermissao.getString("TABLE_NAME")+"');");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);


                    if (rsTabInterface.getCharacterStream("TRIGGER1") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER1"));

                        while ((aux=br.readLine())!=null){
                            strOut.append(aux);
                            strOut.append(sQuebraLinha);
                        }
                        strOut.append("/" + sQuebraLinha + sQuebraLinha);
                    }


                    if (rsTabInterface.getCharacterStream("TRIGGER2") != null){
                        String aux;
                        BufferedReader br = new BufferedReader(rsTabInterface.getCharacterStream("TRIGGER2"));

                        while ((aux=br.readLine())!=null){
                            strOut.append(aux);
                            strOut.append(sQuebraLinha);
                        }
                        strOut.append("/" + sQuebraLinha);
                    }

                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(sQuebraLinha);
                    strOut.append("TRIGGER TI_" + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("BEFORE INSERT ON " + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(sQuebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(sQuebraLinha);
                    strOut.append("begin");
                    strOut.append(sQuebraLinha);
                    strOut.append("    select seq_id_interface.nextval into :new.id from dual;");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("end;");
                    strOut.append(sQuebraLinha);
                    strOut.append("/");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(sQuebraLinha);
                    strOut.append("TRIGGER TIA_" + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("AFTER INSERT ON " + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(sQuebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(sQuebraLinha);
                    strOut.append("begin");
                    strOut.append(sQuebraLinha);
                    strOut.append("    insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)");
                    strOut.append(sQuebraLinha);
                    strOut.append("    values (:new.id, :new.id_importacao, '" + rsTabsSemPermissao.getString("TABLE_NAME") + "', :new.id_ref);");
                    strOut.append(sQuebraLinha);
                    strOut.append("end;");
                    strOut.append(sQuebraLinha);
                    strOut.append("/");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("CREATE OR REPLACE");
                    strOut.append(sQuebraLinha);
                    strOut.append("TRIGGER TD_" + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("BEFORE DELETE ON " + rsTabsSemPermissao.getString("TABLE_NAME"));
                    strOut.append(sQuebraLinha);
                    strOut.append("REFERENCING OLD AS old NEW AS new");
                    strOut.append(sQuebraLinha);
                    strOut.append("FOR EACH ROW");
                    strOut.append(sQuebraLinha);
                    strOut.append("begin");
                    strOut.append(sQuebraLinha);
                    strOut.append("    delete from REGISTROS_INTERFACES where id = :old.id;");
                    strOut.append(sQuebraLinha);
                    strOut.append("end;");
                    strOut.append(sQuebraLinha);
                    strOut.append("/");
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append(sQuebraLinha + "" + sQuebraLinha);
                    strOut.append("-- //////");
                    strOut.append(sQuebraLinha);
                    strOut.append("-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas");
                    strOut.append(sQuebraLinha);
                    strOut.append("-- //////");

                    fileNameScripts = path + "\\"+sUserName+"\\Scripts\\comum\\INOUT\\Tabelas\\" + fileName;
                    fileName = path + "\\"+sUserName+"\\Arquivos\\comum\\INOUT\\Tabelas\\" + fileName;

                    File file = new File(fileName);
                    if(!file.exists())
                        file.createNewFile();

                    FileWriter fw = new FileWriter(file, false);

                    fw.write(strOut.toString(),0,strOut.length());
                    fw.close();

                    File fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        fileScripts.createNewFile();

                    FileWriter fwScripts = new FileWriter(fileScripts, false);

                    fwScripts.write(strOut.toString(),0,strOut.length());
                    fwScripts.close();
                }

                logMessage("File " + fileName + " was succesfull generated.");

            }
        }catch(Exception ex){
            Logger.getLogger(CVSStructureBackup.class.getName()).log(Level.SEVERE, null, ex);

            String erro = "";
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            for(int i=0; i < ex.getStackTrace().length; i++)
            {
                erro += "\t" + ex.getStackTrace()[i].getLineNumber() + " " + ex.getStackTrace()[i].toString() + sQuebraLinha;
            }
            saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
	}

    // Valida linha de commando
    public void validateCommandLine(String args[]) throws Exception{
      String sChave = null;

      for (int i=0; i<args.length ;i++)
      {
        // valida parâmetro importacao
        //armazena o usuário e senha a utilizar durante o processamento
        sChave = "-user=";
        if (args[i].startsWith(sChave)) {
            String s_Usuario_Senha = args[i].substring(sChave.length(),args[i].length());
            s_User = s_Usuario_Senha.substring(0, s_Usuario_Senha.indexOf("/"));
            s_Pass = s_Usuario_Senha.substring(s_Usuario_Senha.indexOf("/")+1);
        }
        // Valida parametro CONN
        //armazena dados sobre o TNS
        sChave = "-conn=";
        if (args[i].startsWith(sChave))
            s_Conn = args[i].substring(sChave.length(),args[i].length());

        //armazena o schema a utilizar durante o processamento corrente
        sChave = "-sessionschema=";
        if (args[i].startsWith(sChave))
        	sSessionSchema = args[i].substring(sChave.length(),args[i].length());

        //armazena a ROLE utilizada durante o processamento
        sChave = "-role=";
        if (args[i].startsWith(sChave))
        	sRole = args[i].substring(sChave.length(),args[i].length());
        }

      // Parametros recusados
      if ((s_User == null ) || (s_User.equals("")))
    	  throw new Exception("ERROR: Parameter missing [-user] ex: -user=USER/PASSWORD");
      else if ((s_Conn == null ) || (s_Conn.equals("")))
    	  throw new Exception("ERROR: Parameter missing [-conn] ex: -conn=jdbc:oracle:thin:@HOST:PORT:SERVICE_NAME");

    }

	public static void main(String[] args){
		try{
			System.setErr(System.out);
			ArrayList arr = new ArrayList();
			arr.add("T");

			CVSStructureBackup spool = new CVSStructureBackup();
			spool.validateCommandLine(args);
			spool.connectOracle(args[0], args[1]);
			spool.spoolCVSStruture(arr, null);
		}catch(Exception e){
			e.printStackTrace(System.out);
			e.printStackTrace(System.err);
		}
	}

}
