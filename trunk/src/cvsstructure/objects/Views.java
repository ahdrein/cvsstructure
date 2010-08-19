package cvsstructure.objects;

import cvsstructure.util.Diretorio;
import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.CvsStructureFile;

/**
 *
 * @author andrein
 */
public class Views implements Runnable {

    private String system;
    private PreparedStatement psView = null;
    private ResultSet rsView = null;
    private Cliente cliente;

    public Views() {
    }

    public Views(String system, Cliente cliente) {
        this.system = system;
        this.cliente = cliente;
    }

    @Override
    public void run() {
        String fileNameScripts = "";
        String fileName = "";

        CvsStructureFile fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;

        StringBuilder sbView = new StringBuilder();
        sbView.append("select * from user_views");

        try {
            if (system.equals("INOUT")) {
                psView = ConnectionInout.getConnection().prepareStatement(sbView.toString());
            } else {
                psView = ConnectionIntegracao.getConnection().prepareStatement(sbView.toString());
            }
            rsView = psView.executeQuery();
            while (rsView.next()) {

                fileName = this.getNameSynonym( rsView.getString("VIEW_NAME").toLowerCase()) + ".sql" ;
                if (system.equals("INOUT")) {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\View\\" + fileName;
                } else {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INTEGRACAO\\View\\" + fileName;
                }

                String longText = rsView.getString("TEXT");

                if (longText != null && !longText.toString().isEmpty()) {
                    SfwLogger.log("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuilder();
                    String auxScripts;

                    if (chConexaoPorArquivos.equals("S")) {
                        if (system.equals("INOUT")) {
                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        } else {
                            strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                        }
                    }
                    StringBuilder append = strOutScripts.append("create or replace force view ").append(rsView.getString("VIEW_NAME")).append(" as ").append(QUEBRA_LINHA);
                    strOutScripts.append(longText.trim().replace((cliente.getItUser().getUser() + ".").toUpperCase(), "").replace((Cliente.userNameSys + ".").toUpperCase(), "")).append(QUEBRA_LINHA);
                    strOutScripts.append(";").append(QUEBRA_LINHA);

                    try {
                        fileScripts = new CvsStructureFile(fileNameScripts);
                        if (!fileScripts.exists()) {
                            fileScripts.saveArquivo(strOutScripts);

                            Estatisticas.nTotalViews++;
                            SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                        }
                    } catch (IOException ioex) {
                        SfwLogger.log("File " + fileNameScripts + " was error generated.");
                        SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                    }
                }
            }
        } catch (Exception ex) {
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        } finally {
            try {
                if (psView != null) {
                    psView.close();
                }
                if (psView != null) {
                    rsView.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.log(sqlex.getMessage());
                sqlex.printStackTrace();
            }
        }
    }

    /*
     * Obtem o IDInterface
     */
    private String getNameSynonym(String nameSynonym) {
        return nameSynonym.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").replace("$", "S#").trim().toLowerCase();
    }
}
