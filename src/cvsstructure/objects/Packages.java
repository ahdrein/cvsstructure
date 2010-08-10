package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.util.PrepararConsultas;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import cvsstructure.log.SfwLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            String fileNameScripts) throws SQLException {

        File fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;
        String auxScripts;

        for (int i = 0; i <= 1; i++) {
            try {

                if (system.equals("INOUT")) {
                    psUserSource = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                } else {
                    psUserSource = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getUserSurce().toString());
                }

                if (i == 0) {
                    psUserSource.setString(1, "PACKAGE");
                } else {
                    psUserSource.setString(1, "PACKAGE BODY");
                    fileNameScripts = fileNameScripts.replace("Package", "PackageBody");
                }

                fileScripts = new File(fileNameScripts);
                if (!fileScripts.exists()) {


                    psUserSource.setString(2, referencedName.toUpperCase());
                    rsUserSource = psUserSource.executeQuery();

                    SfwLogger.log("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuilder();

                    if (chConexaoPorArquivos.equals("S")) {
                        if (system.equals("INOUT")) {
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        } else {
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        }
                    }

                    while (rsUserSource.next()) {
                        if (rsUserSource.getString("TEXT").toLowerCase().contains("package")) {
                            auxScripts = rsUserSource.getString("TEXT").toLowerCase().replace("package", "create or replace package");
                            auxScripts = auxScripts.replace("\"", "");
                            strOutScripts.append(auxScripts);
                        } else {
                            strOutScripts.append(rsUserSource.getString("TEXT"));
                        }
                    }
                    strOutScripts.append(QUEBRA_LINHA);
                    strOutScripts.append("/");

                    if (strOutScripts != null && !strOutScripts.toString().equals("")) {
                        fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        fwScripts.close();

                        Estatisticas.nTotalPackages++;
                        SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                    }

                }
            } catch (IOException ioex) {
                SfwLogger.log("File " + fileNameScripts + " was error generated.");
                SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                ioex.printStackTrace();
            } catch (Exception ex) {
                SfwLogger.log("Error generating ");
                SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
                ex.printStackTrace();
            }
        }

    }
}
