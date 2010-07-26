/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.cvsStructure.objects;

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
public class Packages {
	private PreparedStatement psUserSource = null;
    private ResultSet rsUserSource = null;

    public Packages(String system,
                    String referencedName,
                    String fileName,
                    String fileNameScripts) throws SQLException{

		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        String auxScripts;

        for(int i=0; i <= 1; i++){
            try{

                if(system.equals("INOUT")){
                    psUserSource = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }else{
                    psUserSource = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }

                if(i==0){
                    psUserSource.setString(1, "PACKAGE" );
                }else{
                    psUserSource.setString(1, "PACKAGE BODY" );
                    fileNameScripts = fileNameScripts.replace("Package", "PackageBody");
                }

                fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists()){


                    psUserSource.setString(2, referencedName.toUpperCase() );
                    rsUserSource = psUserSource.executeQuery();

                    CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);


                    strOutScripts = new StringBuffer();

                    if(CVSStructure.chConexaoPorArquivos.equals("S")){
                        if(system.equals("INOUT")){
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }else{
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }
                    }

                    while(rsUserSource.next()){
                        if(rsUserSource.getString("TEXT").toLowerCase().contains("package")){
                            auxScripts=rsUserSource.getString("TEXT").toLowerCase().replace("package", "create or replace package");
                            auxScripts=auxScripts.replace("\"", "");
                            strOutScripts.append(auxScripts);
                        }else{
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                    }
                    strOutScripts.append(CVSStructure.quebraLinha);
                    strOutScripts.append("/");

                    if(strOutScripts != null && !strOutScripts.equals("")){
                        fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();

                        CVSStructure.nTotalPackages++;
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }

                }
            }catch(IOException ioex){
                CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                ioex.printStackTrace();
            }catch(Exception ex){
                CVSStructure.logMessage("Error generating " + fileName);
                SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
                ex.printStackTrace();
            }
        }

    }

}
