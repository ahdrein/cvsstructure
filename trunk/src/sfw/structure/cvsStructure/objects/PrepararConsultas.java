/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.cvsStructure.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sfw.structure.database.ConnectionInout;
import sfw.structure.database.ConnectionIntegracao;

/**
 *
 * @author andrein
 */
public class PrepararConsultas {
    
    private static StringBuffer sbSqlInterface;
    private static StringBuffer sbBatInterface;
    private static StringBuffer sbDadosInterface;
    private static StringBuffer sbCountArquivosExternos;
    private static StringBuffer sbUserTabColumns;
    private static StringBuffer sbUserSoucer;
    private static StringBuffer sbExistObject;

    private static PreparedStatement psSqlInterface = null;
    private static PreparedStatement psBatInterface = null;
    private static PreparedStatement psDadosInterface = null;
    private static PreparedStatement psExistObject = null;

    private static ResultSet rsExistObject;

    public PrepararConsultas(){

    }

    public static int existObject(String nomeObjeto, String system) throws SQLException{
        int numberObj = 0;
        if(sbExistObject == null ){
            sbExistObject = new StringBuffer();
            sbExistObject.append("select count(*) NUMBER_OBJ from user_objects where object_name like ?");

        }
        
        if(system.equals("INOUT")){
            psExistObject = ConnectionInout.getConnection().prepareStatement(sbExistObject.toString());
        }else{
            psExistObject = ConnectionIntegracao.getConnection().prepareStatement(sbExistObject.toString());
        }

        psExistObject.setString(1, nomeObjeto);
        rsExistObject = psExistObject.executeQuery();
        rsExistObject.next();
        numberObj = rsExistObject.getInt("NUMBER_OBJ");

        return numberObj;
    }

    public static StringBuffer getUserSurce(){
        if(sbUserSoucer == null ){
            sbUserSoucer = new StringBuffer();
            sbUserSoucer.append("select * from user_source where type = ? and name = ?");
        }

        return sbUserSoucer;
    }


    public static StringBuffer getUserTabColumns(){
        if(sbUserTabColumns == null ){
            sbUserTabColumns = new StringBuffer();
            sbUserTabColumns.append("SELECT");
            sbUserTabColumns.append(" column_name,");
            sbUserTabColumns.append(" nullable,");
            sbUserTabColumns.append(" concat(concat(concat(data_type,'('),data_length),')') Type,");
            sbUserTabColumns.append(" data_type TypeName");
            sbUserTabColumns.append(" FROM user_tab_columns");
            sbUserTabColumns.append(" WHERE table_name=?");
        }
        
        return sbUserTabColumns;
    }

    public static StringBuffer getCountArquivosExternos(){
        // Contagem de arquivos onde
        if(sbCountArquivosExternos == null ){
            sbCountArquivosExternos = new StringBuffer();
            sbCountArquivosExternos.append("select distinct count(*) TOTAL_ARQUIVOS ");
            sbCountArquivosExternos.append(" from arquivo_externo a ");
            sbCountArquivosExternos.append(" where upper(a.conteudo) like '%' || upper( ? ) ||'%'");
        }

        return sbCountArquivosExternos;
}

    // Encontrar a Interface do SQL
    public static ResultSet getSqlInterface(String nomeArquivo) throws SQLException{
        if(psSqlInterface == null){
            sbSqlInterface = new StringBuffer();
            sbSqlInterface.append("select * from interfaces i,");
            sbSqlInterface.append(" (  ");
            sbSqlInterface.append(" select a.nome_arquivo");
            sbSqlInterface.append(" from   arquivo_externo a ");
            sbSqlInterface.append(" where upper(a.conteudo) like '%' || upper( ? ) || '%'");
            sbSqlInterface.append("   and exists ( select 1");
            sbSqlInterface.append("  from interfaces i ");
            sbSqlInterface.append(" where i.executavel like '%' || a.nome_arquivo || '%' ) ");
            sbSqlInterface.append(" ) a ");
            sbSqlInterface.append(" where i.executavel like '%' || a.nome_arquivo || '%'");

            psSqlInterface = ConnectionInout.getConnection().prepareStatement(sbSqlInterface.toString());
        }
        psSqlInterface.setString(1, nomeArquivo);
        return psSqlInterface.executeQuery();
    }

    // Encontrar a interface do BAT
    public static ResultSet getBatInterface(String nomeExecutavel) throws SQLException{
        if(psBatInterface == null){
            sbBatInterface = new StringBuffer();
            sbBatInterface.append("select * from interfaces i ");
            sbBatInterface.append("where i.executavel like '%' || ? || '%'");

            psBatInterface = ConnectionInout.getConnection().prepareStatement(sbBatInterface.toString());
        }
        psBatInterface.setString(1, nomeExecutavel);
        return psBatInterface.executeQuery();
    }

    // Obtendo condições do select para encontrar o as interfaces existentes
    public static ResultSet getDadosInterface(String idInterface) throws SQLException{
        if(psDadosInterface == null){
            sbDadosInterface = new StringBuffer();
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

            psDadosInterface = ConnectionInout.getConnection().prepareStatement(sbDadosInterface.toString());
        }
        psDadosInterface.setString(1, idInterface);
        return psDadosInterface.executeQuery();
    }
}
