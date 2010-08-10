package cvsstructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;

//Singleton for use just one connection
public class ConnectionInout {

    private static Connection conn;
    private static String dataBase;
    private static String userName;
    private static String passWord;
    private static String port;
    private static String service;

    private ConnectionInout() throws Exception {
    }

    public static void initialize(String dataBase, String userName, String passWord, String port, String service) throws SQLException {

        SfwLogger.debug("Conectando ao banco de dados...");

        ConnectionInout.dataBase = dataBase;
        ConnectionInout.userName = userName;
        ConnectionInout.passWord = passWord;
        ConnectionInout.port = port;
        ConnectionInout.service = service;

        ConnectionInout.getConnection();

        SfwLogger.debug("Conexão com o banco de dados realizada com sucesso.");
    }

    public static Connection getConnection() throws SQLException {

        if (conn == null || conn.isClosed()) {
            SfwLogger.debug("Conexão não encontrada. Connectando ...");

            try {
                Class.forName("oracle.jdbc.OracleDriver");
                DriverManager.registerDriver((java.sql.Driver) Class.forName("oracle.jdbc.OracleDriver").newInstance());
            } catch (Exception e) {
                SfwLogger.log("ERRO ao localizar o Driver 'oracle.jdbc.OracleDriver' da base de dados!");
                throw new RuntimeException(e);
            }

            ConnectionInout.dataBase = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" + ConnectionInout.dataBase + ")(PORT=" + ConnectionInout.port + ")))(CONNECT_DATA=(SERVICE_NAME=" + ConnectionInout.service + ")))";

            SfwLogger.debug("Driver encontrado com sucesso");

            try {
                conn = DriverManager.getConnection(dataBase, userName, passWord);
                conn.setAutoCommit(false);
            } catch (Exception e) {
                conn = null;
            }

            SfwLogger.debug("Conectado com sucesso");
        }
        return conn;
    }

    public static void setSchemaRole(String sSessionSchema, String sRole, String sRolePassword) throws SQLException {

        if (sSessionSchema != null) {
            SfwLogger.debug("Alterando SESSION SCHEMA da conexão...:" + sSessionSchema);
            PreparedStatement psAlteraSession = conn.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = " + sSessionSchema);
            psAlteraSession.executeUpdate();
            SfwLogger.debug("SESSION SCHEMA da conexão alterado.");
        }

        if (sRole != null) {
            SfwLogger.debug("Alterando ROLE da conexão...:" + sRole);
            PreparedStatement psDefineRole;
            if (sRolePassword != null) {
                psDefineRole = conn.prepareStatement("set role " + sRole + " IDENTIFIED BY " + sRolePassword);
            } else {
                psDefineRole = conn.prepareStatement("set role " + sRole);
            }
            psDefineRole.execute();
            SfwLogger.debug("ROLE da conexão alterado.");
        }
    }

    public static void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
