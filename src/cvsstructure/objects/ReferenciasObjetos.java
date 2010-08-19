package cvsstructure.objects;


import cvsstructure.util.Diretorio;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;

/**
 *
 * @author andrein
 */
public class ReferenciasObjetos {

    private static PreparedStatement psGerarReferenciasObjetos = null;
    private ResultSet rsGerarReferenciasObjetos = null;
    private static StringBuilder sbReferenciasObjetos;

    public ReferenciasObjetos(String system, 
                              String objectName,
                              Cliente cliente) {
        String fileNameScripts = "";
        String fileName = "";

        File fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;

        try {
            if (psGerarReferenciasObjetos == null) {
                sbReferenciasObjetos = new StringBuilder();
                sbReferenciasObjetos.append("select distinct ");
                sbReferenciasObjetos.append(" name, type, referenced_owner, referenced_name, referenced_type");
                sbReferenciasObjetos.append(" from TMP_CVS_STRUCTURE ");
                sbReferenciasObjetos.append(" where name like '%' || ? || '%'");

                if (system.equals("INOUT")) {
                    psGerarReferenciasObjetos = ConnectionInout.getConnection().prepareStatement(sbReferenciasObjetos.toString());
                } else {
                    psGerarReferenciasObjetos = ConnectionIntegracao.getConnection().prepareStatement(sbReferenciasObjetos.toString());
                }
            }

            psGerarReferenciasObjetos.setString(1, objectName.toUpperCase());

            rsGerarReferenciasObjetos = psGerarReferenciasObjetos.executeQuery();
            while (rsGerarReferenciasObjetos.next()) {

                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function")) {
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                } else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")) {
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                } else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package")) {
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                } else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")) {
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
                }

                SfwLogger.log("Creating or appending to file " + fileNameScripts);

                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function")
                        || rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")) {

                    //
                    new FunctionProcedure(system,
                            rsGerarReferenciasObjetos.getString("TIPO"),
                            rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                            fileName,
                            fileNameScripts,
                            cliente);

                } else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")) {

                    //
                    new Tables(system,
                            rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                            fileName,
                            fileNameScripts,
                            cliente);

                } else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package")
                        || rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package body")) {

                    //
                    new Packages(system,
                            rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                            fileName,
                            fileNameScripts);
                }

            } // fim rsGerarReferenciasObjetos
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        }
    }
}
