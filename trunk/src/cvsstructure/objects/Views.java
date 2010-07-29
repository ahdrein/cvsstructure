/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sfw.structure.cvsStructure.CVSStructure;
import sfw.structure.database.ConnectionInout;
import sfw.structure.database.ConnectionIntegracao;
import sfw.structure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class Views {

    private PreparedStatement psView = null;
    private ResultSet rsView = null;

	public Views(String system){
        String fileNameScripts = "";
        String fileName = "";

        File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        StringBuffer sbView = new StringBuffer();
        sbView.append("select * from user_views");

        try{
            if(system.equals("INOUT")){
                psView = ConnectionInout.getConnection().prepareStatement(sbView.toString());
            }else{
                psView = ConnectionIntegracao.getConnection().prepareStatement(sbView.toString());
            }
            rsView = psView.executeQuery();
            while(rsView.next()){

                fileName = rsView.getString("VIEW_NAME").toLowerCase() + ".sql";
                if(system.equals("INOUT")){
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INOUT\\View\\" + fileName;
                }else{
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\View\\" + fileName;
                }

                String longText = rsView.getString("TEXT");

                if(longText != null && !longText.toString().equals("")){
                    CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    if(CVSStructure.chConexaoPorArquivos.equals("S")){
                        if(system.equals("INOUT")){
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }else{
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }
                    }
                    strOutScripts.append("create or replace force view " + rsView.getString("VIEW_NAME") + " as " + CVSStructure.quebraLinha);
                    strOutScripts.append(longText.trim().replace((CVSStructure.s_ItUser2+".").toUpperCase(), "").replace((CVSStructure.userNameSys+".").toUpperCase(), "") + CVSStructure.quebraLinha);
                    strOutScripts.append(";" + CVSStructure.quebraLinha);

                    try{
                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();

                        CVSStructure.nTotalViews++;
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }catch(IOException ioex){
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
				}
            }
        }catch(Exception ex){
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        }finally{
            try{
                if(psView != null) psView.close();
                if(psView != null) rsView.close();
            }catch(SQLException sqlex){
                SfwLogger.log(sqlex);
                sqlex.printStackTrace();
            }
        }
    }
}
