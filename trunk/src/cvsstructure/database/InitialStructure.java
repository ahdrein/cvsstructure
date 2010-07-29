package cvsstructure.database;
import java.sql.*;

/**
 *
 * @author andrein
 */
public class InitialStructure {

    public static void execute() throws Exception {
        Statement stat = ConnectionSQLLite.getConnection().createStatement();

        stat.executeUpdate("create table LayoutInterfaces (interface text, objeto text, ordem numeric);");
        stat.executeUpdate("create table ObjetosAcessados (objeto text, objeto_acessado text);");
        stat.executeUpdate("create table SfwUsuarios (user text, pass text, itUser text, itPass text, port text, host);");
        stat.executeUpdate("create table VersaoIntalacao (versao text, dataInstalacao text);");

        stat.close();
    }
}
