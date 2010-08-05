package cvsstructure.objects;

import cvsstructure.util.Diretorio;
import cvsstructure.CVSStructure;
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
public class Sequence {

    private PreparedStatement psSequences = null;
    private ResultSet rsSequences = null;

    public Sequence(String system) {
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
                        CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);
                    }


                    strOutScripts = new StringBuilder();

                    /******************************************
                     * Gerando arquivos na pasta de Scripts
                     ******************************************/
                    if (CVSStructure.chConexaoPorArquivos.equals("S")) {
                        if (system.toUpperCase().equals("INOUT")) {
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                        } else {
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                        }
                    }
                    strOutScripts.append("-- Create sequence " + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("create sequence " + rsSequences.getString("SEQUENCE_NAME") + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("  minvalue 1" + rsSequences.getString("MIN_VALUE") + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("  maxvalue " + rsSequences.getString("MAX_VALUE") + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("  start with " + rsSequences.getString("LAST_NUMBER") + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("  increment by " + rsSequences.getString("INCREMENT_BY") + CVSStructure.QUEBRA_LINHA);
                    strOutScripts.append("  cache " + rsSequences.getString("CACHE_SIZE") + ";" + CVSStructure.QUEBRA_LINHA);

                    if (strOutScripts != null && !strOutScripts.toString().equals("")) {
                        fileScripts.createNewFile();
                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        fwScripts.close();

                        Estatisticas.nTotalSequences++;
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }
                } catch (IOException ioex) {
                    CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                    SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                    ioex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            try {
                if (rsSequences != null) {
                    rsSequences.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.saveLog(sqlex.getClass().toString(), sqlex.getStackTrace());
                sqlex.printStackTrace();
            }
        }
    }
}
