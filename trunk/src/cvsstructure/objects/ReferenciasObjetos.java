package cvsstructure.objects;

import cvsstructure.cvsStructure.CVSStructure;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cvsstructure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class ReferenciasObjetos {

    private static PreparedStatement psGerarReferenciasObjetos = null;
    private ResultSet rsGerarReferenciasObjetos = null;

    private static StringBuffer sbReferenciasObjetos;

    public ReferenciasObjetos(String system, String objectName){
        String fileNameScripts = "";
        String fileName = "";

        File fileScripts;
        FileWriter fwScripts;
        StringBuffer strOutScripts;
        BufferedReader brScripts;

		try{
            if(psGerarReferenciasObjetos == null){
                sbReferenciasObjetos = new StringBuffer();
                sbReferenciasObjetos.append("select distinct ");
                sbReferenciasObjetos.append(" name, type, referenced_owner, referenced_name, referenced_type");
                sbReferenciasObjetos.append(" from TMP_CVS_STRUCTURE ");
                sbReferenciasObjetos.append(" where name like '%' || ? || '%'");

                if(system.equals("INOUT")){
                    psGerarReferenciasObjetos = ConnectionInout.getConnection().prepareStatement(sbReferenciasObjetos.toString());
                }else{
                    psGerarReferenciasObjetos = ConnectionIntegracao.getConnection().prepareStatement(sbReferenciasObjetos.toString());
                }
            }

            psGerarReferenciasObjetos.setString(1, objectName.toUpperCase());
            
            rsGerarReferenciasObjetos = psGerarReferenciasObjetos.executeQuery();
            while(rsGerarReferenciasObjetos.next()){
 
                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function")){
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Function\\" + fileName;
                }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Procedure\\" + fileName;
                }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package")){
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Package\\" + fileName;
                }else if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")){
                    fileName = rsGerarReferenciasObjetos.getString("REFERENCED_NAME").toLowerCase() + ".sql";
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Table\\" + fileName;
                }

                CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);

                if (rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("function") ||
                        rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("procedure")){

                    //
                    new FunctionProcedure(system,
                                          rsGerarReferenciasObjetos.getString("TIPO"),
                                          rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                                          fileName,
                                          fileNameScripts);

                }else if(rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("table")){

                    //
                    new Tables(system, 
                               rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                               fileName,
                               fileNameScripts);

                }else if ( rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package") ||
                          rsGerarReferenciasObjetos.getString("REFERENCED_TYPE").toLowerCase().equals("package body")){

                    //
                    new Packages(system, 
                                 rsGerarReferenciasObjetos.getString("REFERENCED_NAME"),
                                 fileName,
                                 fileNameScripts);
                }

            } // fim rsGerarReferenciasObjetos
        }catch(Exception ex){
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }



}
