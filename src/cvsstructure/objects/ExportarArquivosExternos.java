/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

/**
 *
 * @author ahdrein
 */
public class ExportarArquivosExternos {
    /**************************************************************************
     * <b>Gerar arquivos externos</b>
     **************************************************************************/
    private void exportarArquivosExternos() throws Exception {

        //psArquivosExternos = ConnectionInout.getConnection().prepareStatement(this.selectExportarArquivosExternos);
        try {
            psExportarArquivosExternos.setString(1, executavel);
            rsArquivosExternos = psExportarArquivosExternos.executeQuery();

            while (rsArquivosExternos.next()) {

                fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                if (tipoInterface.trim().equals("S")) {
                    fileName = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else if (tipoInterface.trim().equals("E")) {
                    fileName = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                } else {
                    fileName = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                }
                logMessage("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if (clob != null) {
                    /******************************************
                     * Gerando arquivos na pasta de Arquivos
                     ******************************************/
                    StringBuffer strOut = new StringBuffer();
                    String aux;

                    // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                    BufferedReader br = new BufferedReader(clob.getCharacterStream());

                    while ((aux = br.readLine()) != null) {
                        strOut.append(aux);
                        strOut.append(QUEBRA_LINHA);
                    }

                    try {
                        File file = new File(fileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        FileWriter fw = new FileWriter(file, false);

                        fw.write(strOut.toString(), 0, strOut.length());
                        fw.close();

                        logMessage("File " + fileName + " was succesfull generated.");

                    } catch (IOException ioex) {
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                } else {
                    logMessage("No data are being generated");
                    logMessage("File " + fileName + " wasn't generated.");
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

}
