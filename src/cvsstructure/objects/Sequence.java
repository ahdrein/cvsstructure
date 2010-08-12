package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.util.Diretorio;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;

/**
 *
 * @author andrein
 */
public class Sequence extends Thread{

    private String system;

    private PreparedStatement psSequences = null;
    private ResultSet rsSequences = null;

    public Sequence(){

    }
    
    public Sequence(String system){
        this.system = system;
    }
    
    @Override
    public void run() {
        String fileNameScripts = "";
        String fileName = "";

        File fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;

        StringBuilder sbSequence = new StringBuilder();
        sbSequence.append("select * from user_sequences");

        try {
            if (system.toUpperCase().equals("INOUT")) {
                psSequences = ConnectionInout.getConnection().prepareStatement(sbSequence.toString());
            } else {
                psSequences = ConnectionIntegracao.getConnection().prepareStatement(sbSequence.toString());
            }
            rsSequences = psSequences.executeQuery();
            while (rsSequences.next()) {

                fileName = rsSequences.getString("SEQUENCE_NAME").toLowerCase() + ".sql";
                if (system.toUpperCase().equals("INOUT")) {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Sequence\\" + fileName;
                } else {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\Sequence\\" + fileName;
                }

                try {
                    fileScripts = new File(fileNameScripts);
                    if (!fileScripts.exists()) {
                        SfwLogger.log("Creating or appending to file " + fileNameScripts);
                    }

                    strOutScripts = new StringBuilder();

                    /******************************************
                     * Gerando arquivos na pasta de Scripts
                     ******************************************/
                    if (chConexaoPorArquivos.equals("S")) {
                        if (system.toUpperCase().equals("INOUT")) {
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        } else {
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        }
                    }
                    strOutScripts.append("-- Create sequence ").append(QUEBRA_LINHA);
                    strOutScripts.append("create sequence ").append(rsSequences.getString("SEQUENCE_NAME")).append(QUEBRA_LINHA);
                    strOutScripts.append("  minvalue 1").append(rsSequences.getString("MIN_VALUE")).append(QUEBRA_LINHA);
                    strOutScripts.append("  maxvalue ").append(rsSequences.getString("MAX_VALUE")).append(QUEBRA_LINHA);
                    strOutScripts.append("  start with ").append(rsSequences.getString("LAST_NUMBER")).append(QUEBRA_LINHA);
                    strOutScripts.append("  increment by ").append(rsSequences.getString("INCREMENT_BY")).append(QUEBRA_LINHA);
                    strOutScripts.append("  cache ").append(rsSequences.getString("CACHE_SIZE")).append(";").append(QUEBRA_LINHA);

                    if (strOutScripts != null && !strOutScripts.toString().isEmpty()) {
                        fileScripts.createNewFile();
                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        fwScripts.close();

                        Estatisticas.nTotalSequences++;
                        SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                    }
                } catch (IOException ioex) {
                    SfwLogger.log("File " + fileNameScripts + " was error generated.");
                    SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                    ioex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            try {
                if (rsSequences != null) {
                    rsSequences.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.debug(sqlex.getClass().toString(), sqlex.getStackTrace());
                sqlex.printStackTrace();
            }
        }
    }
}
