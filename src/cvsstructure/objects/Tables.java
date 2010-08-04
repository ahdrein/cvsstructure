package cvsstructure.objects;

import cvsstructure.cvsStructure.CVSStructure;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import cvsstructure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class Tables {
    private Clob clob;

    private static PreparedStatement psTableSource = null;
	private ResultSet rsTableSource = null;

    //private static PreparedStatement psTablespace = null;
    //private static ResultSet rsTablespace = null;

    //StringBuffer sbTablespace = null;
    //ArrayList<String> arrTablespace = new ArrayList();

    public Tables(String system, 
                  String referencedName,
                  String fileName,
                  String fileNameScripts){

		File fileScripts;
		FileWriter fwScripts;
		StringBuffer strOutScripts;
		BufferedReader brScripts;
        String auxScripts;

        boolean pcFree = false;

        try{

            fileScripts = new File(fileNameScripts);
            if(!fileScripts.exists()){

                strOutScripts = new StringBuffer();

                if(system.equals("INOUT")){
                    psTableSource = ConnectionInout.getConnection().prepareStatement("SELECT dbms_metadata.get_ddl('TABLE', ?) conteudo FROM DUAL");
                }else{
                    psTableSource = ConnectionIntegracao.getConnection().prepareStatement("SELECT dbms_metadata.get_ddl('TABLE', ?) conteudo FROM DUAL");
                }
                
                /*
                if(sbTablespace == null ){
                    sbTablespace = new StringBuffer();
                    sbTablespace.append("select  TABLESPACE_NAME,");
                    sbTablespace.append("        INITIAL_EXTENT,");
                    sbTablespace.append("        NEXT_EXTENT,");
                    sbTablespace.append("        MIN_EXTENTS,");
                    sbTablespace.append("        MAX_EXTENTS,");
                    sbTablespace.append("        PCT_INCREASE,");
                    sbTablespace.append("        STATUS,");
                    sbTablespace.append("        CONTENTS");
                    sbTablespace.append(" from 	dba_tablespaces");
                    sbTablespace.append(" order by TABLESPACE_NAME ");

                    psTablespace = ConnectionIntegracao.getConnection().prepareStatement(sbTablespace.toString());

                    rsTablespace = psTablespace.executeQuery();
                    while(rsTablespace.next()){
                        arrTablespace.add(rsTablespace.getString("TABLESPACE_NAME"));
                    }
                }
                 */

                psTableSource.setString(1, referencedName);
                rsTableSource = psTableSource.executeQuery();
                while(rsTableSource.next()){
                    clob = rsTableSource.getClob("CONTEUDO");

                    if(clob!=null){
                        if(CVSStructure.chConexaoPorArquivos.equals("S")){
                            if(system.equals("INOUT")){
                                strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                            }else{
                                strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                            }
                        }
                        pcFree = false;
                        // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                        brScripts = new BufferedReader(clob.getCharacterStream());
                        while ((auxScripts=brScripts.readLine())!=null){
                            String tableOwner = "";
                            if(!auxScripts.trim().equals("")){
                                if(auxScripts.contains("PCTFREE") && !pcFree){
                                    pcFree = true;
                                    strOutScripts.append(")");
                                }

                                if(!pcFree){
                                    //for(String tablespace: arrTablespace){
                                    auxScripts=auxScripts.trim().toUpperCase().contains("CREATE GLOBAL TEMPORARY TABLE") ? auxScripts.replace("\"", "") : auxScripts;
                                    auxScripts=auxScripts.trim().toUpperCase().contains("CREATE TABLE") ? auxScripts.replace("\"", "") : auxScripts;
                                    auxScripts=auxScripts.trim().replace(("\""+CVSStructure.s_ItUser2+"\".").toUpperCase(), "");
                                    auxScripts=auxScripts.trim().toUpperCase().replace(CVSStructure.s_ItUser2.toUpperCase()+".", "");
                                                                //.replace(("\""+CVSStructure.s_ItUser2+"\"").toUpperCase(), "&&INTEGRACAO_USER.")
                                                                //.replace(CVSStructure.s_ItUser2.toUpperCase(), "&&INTEGRACAO_USER");
                                                                //.replace(tablespace, "&&SFW_DATA_1M");
                                                                //.substring(0, (auxScripts.indexOf("PCFREE") == -1 ? auxScripts.length() -1 : auxScripts.indexOf("PCFREE")));
                                    //}
                                    strOutScripts.append(auxScripts + CVSStructure.QUEBRA_LINHA);
                                }
                            }
                        }
                        //strOutScripts.append(CVSStructure.QUEBRA_LINHA );
                        //strOutScripts.append(CVSStructure.QUEBRA_LINHA);
                        strOutScripts.append(";" + CVSStructure.QUEBRA_LINHA);
                        //strOutScripts.append("/");
                    }else{
                        CVSStructure.logMessage("No data are being generated");
                        CVSStructure.logMessage("File " + fileName + " wasn't generated.");
                        CVSStructure.logMessage("Error in the implementation of the interface with Id_Importação ");

                    }
                }

                if(strOutScripts != null && !strOutScripts.toString().equals("")){
                    fileScripts.createNewFile();
                    fwScripts = new FileWriter(fileScripts, false);
                    fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                    fwScripts.close();
                    CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        }catch(IOException ioex){
            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        }catch(Exception ex){
            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        }

    }
}
