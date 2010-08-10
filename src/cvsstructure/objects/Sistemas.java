package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.Diretorio;

/**
 *
 * @author andrein
 */
public class Sistemas extends Thread {

    private Cliente cliente;

    public Sistemas(Cliente cliente) throws SQLException {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        String fileNameScripts = "";
        String fileName = "";

        try {
            String sSelectSistemas = "select * from sistema";
            PreparedStatement psSistemas = ConnectionInout.getConnection().prepareStatement(sSelectSistemas);
            ResultSet rsSistemas = psSistemas.executeQuery();

            while (rsSistemas.next()) {

                String oracleOwnerUser = "";
                String oracleOwnerPass = "";
                if (rsSistemas.getString("USER_ORACLE") == null) {
                    fileName = "sistema.sql";
                    oracleOwnerUser = "SOFT_USER";
                    oracleOwnerPass = "SOFT_PASS";
                } else if (cliente.getIoUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IO")) {
                    fileName = "inout.sql";
                    oracleOwnerUser = "INOUT_USER";
                    oracleOwnerPass = "INOUT_PASS";
                } else if (cliente.getCeUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("CE")) {
                    fileName = "cambio_exportacao.sql";
                    oracleOwnerUser = "CAMBIO_EXP_USER";
                    oracleOwnerPass = "CAMBIO_EXP_PASS";
                } else if (cliente.getCiUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("CI")) {
                    fileName = "cambio_importacao.sql";
                    oracleOwnerUser = "CAMBIO_IMP_USER";
                    oracleOwnerPass = "CAMBIO_IMP_PASS";
                } else if (cliente.getIsUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IS")) {
                    fileName = "import.sql";
                    oracleOwnerUser = "IMPORT_USER";
                    oracleOwnerPass = "IMPORT_PASS";
                } else if (cliente.getExUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("EX")) {
                    fileName = "export.sql";
                    oracleOwnerUser = "EXPORT_USER";
                    oracleOwnerPass = "EXPORT_PASS";
                } else if (cliente.getDbUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("DB")) {
                    fileName = "drawback.sql";
                    oracleOwnerUser = "DRAWBACK_USER";
                    oracleOwnerPass = "DRAWBACK_PASS";
                } else if (cliente.getBsUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("BS")) {
                    fileName = "broker.sql";
                    oracleOwnerUser = "BROKER_USER";
                    oracleOwnerPass = "BROKER_PASS";
                } else if (cliente.getItUser().getUser().toUpperCase().equals(rsSistemas.getString("USER_ORACLE").toUpperCase()) || rsSistemas.getString("USER_ORACLE").toUpperCase().contains("IT")) {
                    fileName = "integracao.sql";
                    oracleOwnerUser = "INTEGRACAO_USER";
                    oracleOwnerPass = "INTEGRACAO_PASS";
                } else {
                    fileName = "base_generica.sql";
                    oracleOwnerUser = "SOFT_USER";
                    oracleOwnerPass = "SOFT_PASS";
                }

                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Sistemas\\" + fileName;

                CvsStructureFile fileScripts = new CvsStructureFile(fileNameScripts);
                if (!fileScripts.exists()) {

                    StringBuilder strOut = new StringBuilder();
                    SfwLogger.log("Creating or appending to file " + fileNameScripts);

                    if (chConexaoPorArquivos.equals("S")) {
                        strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                    }

                    strOut.append("--  ///////" + QUEBRA_LINHA);
                    strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                    strOut.append("--  ///////     Sistema: " + rsSistemas.getString("DESCRICAO") + QUEBRA_LINHA);
                    strOut.append("--  ///////" + QUEBRA_LINHA);
                    strOut.append(QUEBRA_LINHA);
                    strOut.append("begin" + QUEBRA_LINHA);
                    strOut.append("   insert into		SISTEMA" + QUEBRA_LINHA);
                    strOut.append("		(ID_SISTEMA," + QUEBRA_LINHA);
                    strOut.append("		DESCRICAO," + QUEBRA_LINHA);
                    strOut.append("		USER_ORACLE," + QUEBRA_LINHA);
                    strOut.append("		PROC_NAME," + QUEBRA_LINHA);
                    strOut.append("		PASSWORD," + QUEBRA_LINHA);
                    strOut.append("		PROC_ATUALIZACAO)" + QUEBRA_LINHA);
                    strOut.append("values		('" + rsSistemas.getString("ID_SISTEMA") + "'," + QUEBRA_LINHA);
                    strOut.append("		'" + rsSistemas.getString("DESCRICAO") + "'," + QUEBRA_LINHA);
                    strOut.append("		'&&" + oracleOwnerUser + "'," + QUEBRA_LINHA);
                    strOut.append("		''," + QUEBRA_LINHA);
                    strOut.append("		get_Crypto('&&" + oracleOwnerPass + "')," + QUEBRA_LINHA);
                    strOut.append("		'');" + QUEBRA_LINHA);
                    strOut.append("  commit;" + QUEBRA_LINHA);
                    strOut.append("exception" + QUEBRA_LINHA);
                    strOut.append("  when dup_val_on_index then" + QUEBRA_LINHA);
                    strOut.append("    update	SISTEMA" + QUEBRA_LINHA);
                    strOut.append("    set	DESCRICAO = '" + rsSistemas.getString("DESCRICAO") + "'," + QUEBRA_LINHA);
                    strOut.append("	USER_ORACLE = '&&" + oracleOwnerUser + "'," + QUEBRA_LINHA);
                    strOut.append("	PROC_NAME = ''," + QUEBRA_LINHA);
                    strOut.append("	PASSWORD = get_Crypto('&&" + oracleOwnerPass + "')," + QUEBRA_LINHA);
                    strOut.append("	PROC_ATUALIZACAO = ''" + QUEBRA_LINHA);
                    strOut.append("    where	ID_SISTEMA = '" + rsSistemas.getString("ID_SISTEMA") + "';" + QUEBRA_LINHA);
                    strOut.append("    commit;" + QUEBRA_LINHA);
                    strOut.append("end;" + QUEBRA_LINHA);
                    strOut.append("/" + QUEBRA_LINHA);
                    strOut.append(QUEBRA_LINHA);
                    strOut.append("-- //////" + QUEBRA_LINHA);
                    strOut.append("-- //////  Fim do script do sistema Broker Sys" + QUEBRA_LINHA);
                    strOut.append("-- //////" + QUEBRA_LINHA);

                    if (strOut != null && !strOut.toString().equals("")) {
                        fileScripts.saveArquivo(strOut);

                        Estatisticas.nTotalSistemas++;
                        SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                    }
                }
            }
        } catch (IOException ioex) {
            SfwLogger.log("File " + fileNameScripts + " was error generated.");
            SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
            ioex.printStackTrace();
        } catch (Exception ex) {
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        }

    }
}
