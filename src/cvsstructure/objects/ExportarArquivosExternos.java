/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chNomePasta;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.model.Interface;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.Diretorio;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ahdrein
 */
public class ExportarArquivosExternos extends Thread {

    Interface interfaces;
    private PreparedStatement psExportarArquivosExternos;
    private ResultSet rsArquivosExternos;

    public ExportarArquivosExternos() {
    }

    public ExportarArquivosExternos(Interface interfaces) {
        this.interfaces = interfaces;
    }

    /**************************************************************************
     * <b>Gerar arquivos externos</b>
     **************************************************************************/
    @Override
    public void run() {
        String fileName = null;
        String fileNameScripts = null;
        Clob clob;

        //psArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectExportarArquivosExternos);
        try {
            psExportarArquivosExternos = cvsstructure.model.ArquivosExternos.getInstance().getExportarArquivosExternosByNomeArquivo();
            psExportarArquivosExternos.setString(1, interfaces.getExecutavel());
            rsArquivosExternos = psExportarArquivosExternos.executeQuery();

            while (rsArquivosExternos.next()) {

                fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                if (interfaces.getTipoInterface().trim().equals("S")) {
                    fileName = Diretorio.path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (interfaces.getTipoInterface().trim().equals("E")) {
                    fileName = Diretorio.path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else {
                    fileName = Diretorio.path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + interfaces.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }
                //logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if (clob != null) {
                    /******************************************
                     * Gerando arquivos na pasta de Arquivos
                     ******************************************/
                    StringBuilder strOut = new StringBuilder();
                    String aux;

                    // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                    BufferedReader br = new BufferedReader(clob.getCharacterStream());

                    while ((aux = br.readLine()) != null) {
                        strOut.append(aux);
                        strOut.append(QUEBRA_LINHA);
                    }

                    try {
                        CvsStructureFile file = new CvsStructureFile(fileName);
                        if (!file.exists()) {
                            if (strOut != null) {
                                file.saveArquivo(strOut);
                            }
                        }

                        //logMessage("File " + fileName + " was succesfull generated.");

                    } catch (IOException ioex) {
                        SfwLogger.log("File " + fileNameScripts + " was error generated.");
                        SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                } else {
                    //logMessage("No data are being generated");
                    //logMessage("File " + fileName + " wasn't generated.");
                }
            }
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        }
    }

    public String getNomePasta(String tipo) {
        if (tipo.equals("") || chNomePasta.equals("N")) {
            return interfaces.getIdInterface();
        } else if (tipo.equals("IN")) {
            return interfaces.getIdSistema() + "_in_" + interfaces.getIdInterface();
        } else if (tipo.equals("OUT")) {
            return interfaces.getIdSistema() + "_out_" + interfaces.getIdInterface();
        }
        return "";
    }
}
