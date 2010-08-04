/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import cvsstructure.cvsStructure.CVSStructure;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cvsstructure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class FunctionProcedure {
	private PreparedStatement psUserSource = null;
	private ResultSet rsUserSource = null;

    public FunctionProcedure(String system,
                             String tipo,
                             String referenceName,
                             String fileName,
                             String fileNameScripts) throws IOException{

		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts = null;
		BufferedReader brScripts;

        try{
            fileScripts = new File(fileNameScripts);
            if(!fileScripts.exists()){

                strOutScripts = new StringBuffer();

                if(system.equals("INOUT")){
                    psUserSource = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }else{
                    psUserSource = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }

                
                psUserSource.setString(1, tipo.toUpperCase() );
                psUserSource.setString(2, referenceName.toUpperCase() );
                rsUserSource = psUserSource.executeQuery();

                if(CVSStructure.chConexaoPorArquivos.equals("S")){
                    if(system.equals("INOUT")){
                        strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                    }else{
                        strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                    }
                }

                while(rsUserSource.next()){
                    if(rsUserSource.getString("TEXT").toLowerCase().contains("procedure") || rsUserSource.getString("TEXT").toLowerCase().contains("function")){
                        strOutScripts.append(rsUserSource.getString("TEXT").toLowerCase().replace("procedure", "create or replace procedure").replace("function", "create or replace function"));
                    }else{
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }
                }

                strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                strOutScripts.append("/");

                if(strOutScripts != null && !strOutScripts.toString().equals("")){
                    fileScripts.createNewFile();

                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();

                    CVSStructure.nTotalFunctionsProcedures++;
                    CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                }

            }
        }catch(IOException ioex){
            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        }catch(Exception ex){
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }

    }


}
