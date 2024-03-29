/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.util.PrepararConsultas;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.CvsStructureFile;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            String fileNameScripts,
            Cliente cliente) throws IOException {

        CvsStructureFile fileScripts;

        StringBuilder strOutScripts = null;
        BufferedReader brScripts;

        try {
            fileScripts = new CvsStructureFile(fileNameScripts);
            if (!fileScripts.exists()) {

                strOutScripts = new StringBuilder();

                if (system.equals("INOUT")) {
                    psUserSource = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                } else {
                    psUserSource = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }


                psUserSource.setString(1, tipo.toUpperCase());
                psUserSource.setString(2, referenceName.toUpperCase());
                rsUserSource = psUserSource.executeQuery();

                if (chConexaoPorArquivos.equals("S")) {
                    if (system.equals("INOUT")) {
                        strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                    } else {
                        strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                    }
                }

                while (rsUserSource.next()) {
                    if (rsUserSource.getString("TEXT").toLowerCase().contains("procedure") || rsUserSource.getString("TEXT").toLowerCase().contains("function")) {
                        strOutScripts.append(rsUserSource.getString("TEXT").toLowerCase().replace("procedure", "create or replace procedure").replace("function", "create or replace function"));
                    } else {
                        strOutScripts.append(rsUserSource.getString("TEXT"));
                    }
                }

                strOutScripts.append(QUEBRA_LINHA);
                strOutScripts.append("/");

                if (strOutScripts != null && !strOutScripts.toString().isEmpty()) {
                    fileScripts.saveArquivo(strOutScripts);
                    Estatisticas.nTotalFunctionsProcedures++;
                    SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                }

            }
        } catch (IOException ioex) {
            SfwLogger.log("File " + fileNameScripts + " was error generated.");
            SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        }finally{
            try {
                if(psUserSource != null){
                    psUserSource.close();
                    psUserSource = null;
                }

                if(rsUserSource != null){
                    rsUserSource.close();
                    rsUserSource = null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(FunctionProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
