/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.model;

import cvsstructure.database.ConnectionInout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author andrein
 */
public class ArquivosExternosNaoGerados {

    private PreparedStatement psInsertReferencesObjects = null;
    private PreparedStatement psArquivosExternosNaoGerados = null;

    private StringBuilder sbCreateTableCvsStructure = null;
    private StringBuilder sbInsertTableCvsStructure = null;
    private StringBuilder sbArquivosExternosNaoGerados = null;

    private static ArquivosExternosNaoGerados instance;

    static {
            instance = new ArquivosExternosNaoGerados();
    }

    private ArquivosExternosNaoGerados(){
    }

    public static ArquivosExternosNaoGerados getInstance(){
            return instance;
    }

    public void createTableCvsStructure() throws SQLException{
        if(sbCreateTableCvsStructure == null){
            sbCreateTableCvsStructure = new StringBuilder();
            sbCreateTableCvsStructure.append("create table TMP_CVS_STRUCTURE");
            sbCreateTableCvsStructure.append("(");
            //sbCreateTableCvsStructure.append(" LEVEL_OBJ        number,");
            sbCreateTableCvsStructure.append(" NAME             varchar2(100),");
            sbCreateTableCvsStructure.append(" TYPE             varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_OWNER varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_NAME  varchar2(100),");
            sbCreateTableCvsStructure.append(" REFERENCED_TYPE  varchar2(100)");
            sbCreateTableCvsStructure.append(")");
        }

        ConnectionInout.getConnection().prepareCall(sbCreateTableCvsStructure.toString()).executeQuery();
    }

    public void insertTableTmpCvsStructure(String user) throws SQLException{
        if(sbInsertTableCvsStructure == null){
            sbInsertTableCvsStructure = new StringBuilder();
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
        }

        if(psInsertReferencesObjects == null){
            psInsertReferencesObjects = ConnectionInout.getConnection().prepareCall(sbInsertTableCvsStructure.toString());
        }

        psInsertReferencesObjects.setString(1, user.toUpperCase());
        psInsertReferencesObjects.executeUpdate();
        ConnectionInout.getConnection().commit();
    }

    public ResultSet getArquivosExternosNaoGerados() throws SQLException {

        if(sbArquivosExternosNaoGerados == null){
            sbArquivosExternosNaoGerados = new StringBuilder();
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
        }

        if(psArquivosExternosNaoGerados == null){
            psArquivosExternosNaoGerados = ConnectionInout.getConnection().prepareStatement(sbArquivosExternosNaoGerados.toString());
        }

        return psArquivosExternosNaoGerados.executeQuery();
    }
}
