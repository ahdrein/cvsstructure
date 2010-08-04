package cvsstructure.objects;

import cvsstructure.cvsStructure.CVSStructure;
import cvsstructure.database.ConnectionInout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class Sistemas {

    public Sistemas(CVSStructure cvsStructure) throws SQLException{
        String fileNameScripts = "";
        String fileName = "";

        try{
            String sSelectSistemas = "select * from sistema";
            PreparedStatement psSistemas = ConnectionInout.getConnection().prepareStatement(sSelectSistemas);
            ResultSet rsSistemas = psSistemas.executeQuery();
        
            while(rsSistemas.next()){

                String oracleOwnerUser = "";
                String oracleOwnerPass = "";
                if(rsSistemas.getString("USER_ORACLE") == null){
                    fileName = "sistema.sql";
                    oracleOwnerUser = "SOFT_USER";
                    oracleOwnerPass = "SOFT_PASS";
                }else if(cvsStructure.getIoUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IO")){
                    fileName = "inout.sql";
                    oracleOwnerUser = "INOUT_USER";
                    oracleOwnerPass = "INOUT_PASS";
                }else if(cvsStructure.getCeUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("CE")){
                    fileName = "cambio_exportacao.sql";
                    oracleOwnerUser = "CAMBIO_EXP_USER";
                    oracleOwnerPass = "CAMBIO_EXP_PASS";
                }else if(cvsStructure.getCiUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("CI")){
                    fileName = "cambio_importacao.sql";
                    oracleOwnerUser = "CAMBIO_IMP_USER";
                    oracleOwnerPass = "CAMBIO_IMP_PASS";
                }else if(cvsStructure.getIsUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IS")){
                    fileName = "import.sql";
                    oracleOwnerUser = "IMPORT_USER";
                    oracleOwnerPass = "IMPORT_PASS";
                }else if(cvsStructure.getExUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("EX")){
                    fileName = "export.sql";
                    oracleOwnerUser = "EXPORT_USER";
                    oracleOwnerPass = "EXPORT_PASS";
                }else if(cvsStructure.getDbUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("DB")){
                    fileName = "drawback.sql";
                    oracleOwnerUser = "DRAWBACK_USER";
                    oracleOwnerPass = "DRAWBACK_PASS";
                }else if(cvsStructure.getBsUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("BS")){
                    fileName = "broker.sql";
                    oracleOwnerUser = "BROKER_USER";
                    oracleOwnerPass = "BROKER_PASS";
                }else if(cvsStructure.getItUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IT")){
                    fileName = "integracao.sql";
                    oracleOwnerUser = "INTEGRACAO_USER";
                    oracleOwnerPass = "INTEGRACAO_PASS";
                }else{
                    fileName = "base_generica.sql";
                    oracleOwnerUser = "SOFT_USER";
                    oracleOwnerPass = "SOFT_PASS";
                }
                
                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\INOUT\\Sistemas\\" + fileName;

                File fileScripts = new File(fileNameScripts);
                if(!fileScripts.exists()){


                    StringBuffer strOut = new StringBuffer();
                    CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);

                    if(CVSStructure.chConexaoPorArquivos.equals("S")){
                        strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                    }

                    strOut.append("--  ///////" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("--  ///////     Sistema: "+rsSistemas.getString("DESCRICAO")+CVSStructure.QUEBRA_LINHA);
                    strOut.append("--  ///////" + CVSStructure.QUEBRA_LINHA);
                    strOut.append(CVSStructure.QUEBRA_LINHA);
                    strOut.append("begin" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("   insert into		SISTEMA" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		(ID_SISTEMA," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		DESCRICAO," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		USER_ORACLE," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		PROC_NAME," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		PASSWORD," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		PROC_ATUALIZACAO)" + CVSStructure.QUEBRA_LINHA);
                    strOut.append( "values		('"+rsSistemas.getString("ID_SISTEMA")+"'," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		'"+rsSistemas.getString("DESCRICAO")+"'," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		'&&"+oracleOwnerUser+"'," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		''," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		get_Crypto('&&"+oracleOwnerPass+"')," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("		'');" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("  commit;" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("exception" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("  when dup_val_on_index then" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("    update	SISTEMA" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("    set	DESCRICAO = '"+rsSistemas.getString("DESCRICAO")+"'," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("	USER_ORACLE = '&&"+oracleOwnerUser+"'," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("	PROC_NAME = ''," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("	PASSWORD = get_Crypto('&&"+oracleOwnerPass+"')," + CVSStructure.QUEBRA_LINHA);
                    strOut.append("	PROC_ATUALIZACAO = ''" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("    where	ID_SISTEMA = '"+rsSistemas.getString("ID_SISTEMA")+"';" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("    commit;" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("end;" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("/" + CVSStructure.QUEBRA_LINHA);
                    strOut.append(CVSStructure.QUEBRA_LINHA);
                    strOut.append("-- //////" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("-- //////  Fim do script do sistema Broker Sys" + CVSStructure.QUEBRA_LINHA);
                    strOut.append("-- //////" + CVSStructure.QUEBRA_LINHA);

                    if(strOut != null && !strOut.toString().equals("")){
                        fileScripts.createNewFile();

                        FileWriter fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOut.toString(),0,strOut.length());
                        fwScripts.close();

                        CVSStructure.nTotalSistemas++;
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
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
            ex.printStackTrace();
        }

    }
}
