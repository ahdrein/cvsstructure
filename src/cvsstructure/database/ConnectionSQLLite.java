package cvsstructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class ConnectionSQLLite {

    private static Connection conn;

    private ConnectionSQLLite() throws Exception {
    }

    public static void initialize(String dataBase, String userName, String passWord, String port, String service) throws SQLException {

        SfwLogger.debug("Conectando ao banco de dados...");

        ConnectionIntegracao.getConnection();

        SfwLogger.debug("Conexão com o banco de dados realizada com sucesso.");
    }

    public static Connection getConnection() throws SQLException {

        if (conn == null || conn.isClosed()) {
            SfwLogger.debug("Conexão não encontrada. Connectando ...");

            try {
                Class.forName("org.sqlite.JDBC");
            } catch (Exception e) {
                SfwLogger.log("ERRO ao localizar o Driver 'org.sqlite.JDBC' da base de dados!");
                throw new RuntimeException(e);
            }

            SfwLogger.debug("Driver encontrado com sucesso");

            try {
                conn = DriverManager.getConnection("jdbc:sqlite:./database/test.db");
                conn.setAutoCommit(false);
            } catch (Exception e) {
                conn = null;
            }

            SfwLogger.debug("Conectado com sucesso");
        }
        return conn;
    }

    public static void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
