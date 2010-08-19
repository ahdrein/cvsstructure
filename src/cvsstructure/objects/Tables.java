package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.Estatisticas;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrein
 */
public class Tables {

    private Clob clob;
    private static PreparedStatement psTableSource = null;
    private ResultSet rsTableSource = null;
    private Cliente cliente;

    public Tables(Cliente cliente){
        this.cliente = cliente;
    }

    public Tables(String system,
            String referencedName,
            String fileName,
            String fileNameScripts,
            Cliente cliente) {

        CvsStructureFile fileScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;
        String auxScripts;

        this.cliente = cliente;
        boolean pcFree = false;

        try {

            fileScripts = new CvsStructureFile(fileNameScripts);
            if (!fileScripts.exists()) {

                strOutScripts = new StringBuilder();

                if (system.equals("INOUT")) {
                    psTableSource = ConnectionInout.getConnection().prepareStatement("SELECT dbms_metadata.get_ddl('TABLE', ?) conteudo FROM DUAL");
                } else {
                    psTableSource = ConnectionIntegracao.getConnection().prepareStatement("SELECT dbms_metadata.get_ddl('TABLE', ?) conteudo FROM DUAL");
                }

                /*
                if(sbTablespace == null ){
                sbTablespace = new StringBuilder();
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
                while (rsTableSource.next()) {
                    clob = rsTableSource.getClob("CONTEUDO");

                    if (clob != null) {
                        if (chConexaoPorArquivos.equals("S")) {
                            if (system.equals("INOUT")) {
                                strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                            } else {
                                strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                            }
                        }
                        pcFree = false;
                        // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                        brScripts = new BufferedReader(clob.getCharacterStream());
                        while ((auxScripts = brScripts.readLine()) != null) {
                            String tableOwner = "";
                            if (!auxScripts.trim().isEmpty()) {
                                if (auxScripts.contains("PCTFREE") && !pcFree) {
                                    pcFree = true;
                                    strOutScripts.append(")");
                                }

                                if (!pcFree) {
                                    //for(String tablespace: arrTablespace){
                                    auxScripts = auxScripts.trim().toUpperCase().contains("CREATE GLOBAL TEMPORARY TABLE") ? auxScripts.replace("\"", "") : auxScripts;
                                    auxScripts = auxScripts.trim().toUpperCase().contains("CREATE TABLE") ? auxScripts.replace("\"", "") : auxScripts;
                                    auxScripts = auxScripts.trim().replace(("\"" + cliente.getItUser().getUser() + "\".").toUpperCase(), "");
                                    auxScripts = auxScripts.trim().toUpperCase().replace(cliente.getItUser().getUser().toUpperCase() + ".", "");
                                    //.replace(("\""+s_ItUser2+"\"").toUpperCase(), "&&INTEGRACAO_USER.")
                                    //.replace(s_ItUser2.toUpperCase(), "&&INTEGRACAO_USER");
                                    //.replace(tablespace, "&&SFW_DATA_1M");
                                    //.substring(0, (auxScripts.indexOf("PCFREE") == -1 ? auxScripts.length() -1 : auxScripts.indexOf("PCFREE")));
                                    //}
                                    strOutScripts.append(auxScripts).append(QUEBRA_LINHA);
                                }
                            }
                        }
                        //strOutScripts.append(QUEBRA_LINHA );
                        //strOutScripts.append(QUEBRA_LINHA);
                        strOutScripts.append(";").append(QUEBRA_LINHA);
                        //strOutScripts.append("/");
                    } else {
                        SfwLogger.log("No data are being generated");
                        SfwLogger.log("File " + fileName + " wasn't generated.");
                        SfwLogger.log("Error in the implementation of the interface with Id_Importação ");

                    }
                }

                if (strOutScripts != null && !strOutScripts.toString().isEmpty()) {
                    fileScripts.saveArquivo(strOutScripts);

                    Estatisticas.nTotalTabelas++;
                    SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                }
            }
        } catch (IOException ioex) {
            SfwLogger.log("File " + fileNameScripts + " was error generated.");
            SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        } catch (Exception ex) {
            SfwLogger.log("File " + fileNameScripts + " was error generated.");
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        } finally{
            try {
                if (psTableSource != null) {
                    psTableSource.close();
                    psTableSource = null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Tables.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
