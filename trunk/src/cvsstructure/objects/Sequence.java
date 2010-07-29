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
public class Sequence {

    private PreparedStatement psSequences = null;
    private ResultSet rsSequences = null;

	public Sequence(String system){
        String fileNameScripts = "";
        String fileName = "";

		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        StringBuffer sbSequence = new StringBuffer();
        sbSequence.append("select * from user_sequences");

        try{
            if(system.toUpperCase().equals("INOUT")){
                psSequences = ConnectionInout.getConnection().prepareStatement(sbSequence.toString());
            }else{
                psSequences = ConnectionIntegracao.getConnection().prepareStatement(sbSequence.toString());
            }
            rsSequences = psSequences.executeQuery();
            while(rsSequences.next()){

                fileName = rsSequences.getString("SEQUENCE_NAME").toLowerCase() + ".sql";
                if(system.toUpperCase().equals("INOUT")){
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INOUT\\Sequence\\" + fileName;
                }else{
                    fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INTEGRACAO\\Sequence\\" + fileName;
                }

                try{
                    fileScripts = new File(fileNameScripts);
                    if(!fileScripts.exists())
                        

                    CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);


                    strOutScripts = new StringBuffer();

                    /******************************************
                    * Gerando arquivos na pasta de Scripts
                    ******************************************/

                    if(CVSStructure.chConexaoPorArquivos.equals("S")){
                        if(system.toUpperCase().equals("INOUT")){
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }else{
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                        }
                    }
                    strOutScripts.append("-- Create sequence " + CVSStructure.quebraLinha);
                    strOutScripts.append("create sequence " + rsSequences.getString("SEQUENCE_NAME") + CVSStructure.quebraLinha);
                    strOutScripts.append("  minvalue 1" + rsSequences.getString("MIN_VALUE") + CVSStructure.quebraLinha);
                    strOutScripts.append("  maxvalue " + rsSequences.getString("MAX_VALUE") + CVSStructure.quebraLinha);
                    strOutScripts.append("  start with " + rsSequences.getString("LAST_NUMBER") + CVSStructure.quebraLinha);
                    strOutScripts.append("  increment by "+ rsSequences.getString("INCREMENT_BY") + CVSStructure.quebraLinha);
                    strOutScripts.append("  cache " + rsSequences.getString("CACHE_SIZE") + ";" + CVSStructure.quebraLinha );

                    if(strOutScripts != null && !strOutScripts.toString().equals("")){
                        fileScripts.createNewFile();
                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();

                        CVSStructure.nTotalSequences++;
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                }catch(IOException ioex){
                    CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                    SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                    ioex.printStackTrace();
                }
            }
        }catch(Exception ex){
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            try{
                if(rsSequences != null) rsSequences.close();
            } catch (SQLException sqlex){
                SfwLogger.saveLog(sqlex.getClass().toString(), sqlex.getStackTrace());
                sqlex.printStackTrace();
            }
        }
	}
}
