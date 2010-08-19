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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrein
 */
public class ArquivosExternos implements Runnable{
    private Interface interfaces;
    private Clob clob;

    public ArquivosExternos(Interface interfaces) throws Exception {
        this.interfaces = interfaces;
    }

    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    @Override
    public void run() {
        CvsStructureFile fileScripts;
        StringBuilder strOutScripts = null;
        BufferedReader brScripts;

        String fileName = null;
        String fileNameScripts = null;

        ResultSet rsArquivosExternos = null;

        try {

            int nTotalArquivos = this.interfaces.getCountInterfaceByExecutavel().getInt("TOTAL_ARQUIVOS");

            rsArquivosExternos = cvsstructure.model.ArquivosExternos.getInstance().getArquivosExternosByNomeArquivo(this.interfaces.getExecutavel());

            while (rsArquivosExternos.next()) {

                if (rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase().substring(rsArquivosExternos.getString("NOME_ARQUIVO").indexOf(".") + 1, rsArquivosExternos.getString("NOME_ARQUIVO").length()).equals("sql")) {
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase();
                } else {
                    fileName = rsArquivosExternos.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                }

                if (nTotalArquivos == 1) {
                    if (this.interfaces.getTipoInterface().equals("S")) {
                        fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                    } else if (this.interfaces.getTipoInterface().equals("E")) {
                        fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                    } else {
                        fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("") + "\\INOUT\\ArquivosExternos\\" + fileName;
                    }
                } else if (nTotalArquivos >= 2) {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                } else {
                    fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                }
                SfwLogger.log("Creating or appending to file " + fileName);
                //fileName = ".\\"+sUserName+"\\Scripts\\" + IDInterface + " \\" fileName;

                clob = rsArquivosExternos.getClob("CONTEUDO");
                if (clob != null) {
                    try {
                        fileScripts = new CvsStructureFile(fileNameScripts);
                        if (!fileScripts.exists()) {

                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/
                            SfwLogger.log("Creating or appending to file " + fileNameScripts);

                            strOutScripts = new StringBuilder();
                            String auxScripts;

                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("--  ///////").append(QUEBRA_LINHA);
                            strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT").append(QUEBRA_LINHA);
                            strOutScripts.append("--  ///////     Arquivo Externo: ").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append(QUEBRA_LINHA);
                            strOutScripts.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("set define off" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append("';" + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("insert into ARQUIVO_EXTERNO").append(QUEBRA_LINHA);
                            strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)").append(QUEBRA_LINHA);
                            strOutScripts.append("values").append(QUEBRA_LINHA);

                            // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                            brScripts = new BufferedReader(clob.getCharacterStream());
                            int contador = 0;
                            int maxLin = 0;
                            while ((auxScripts = brScripts.readLine()) != null) {

                                auxScripts = auxScripts.replaceAll("'", "' || chr(39) || '");
                                auxScripts = auxScripts.replaceAll("@", "' || chr(64) || '");
                                auxScripts = auxScripts.replaceAll("\t", "' '");
                                //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                auxScripts = auxScripts.replaceAll(QUEBRA_LINHA, "' || chr(13) || CHR(10) ||'");
                                //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                if (contador == 0) {
                                    strOutScripts.append("('").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append("'");
                                    strOutScripts.append(",");
                                    if (rsArquivosExternos.getString("NOME_ARQUIVO") == null) {
                                        strOutScripts.append("'").append(this.interfaces.getDescricao().length() > 50 ? this.interfaces.getDescricao().substring(0, 50) : this.interfaces.getDescricao().trim()).append("'");
                                    } else {
                                        strOutScripts.append("'").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append("'");
                                    }
                                    strOutScripts.append(",");
                                    strOutScripts.append("'").append(rsArquivosExternos.getString("PATH_RELATIVO")).append("'");
                                    strOutScripts.append(",");
                                    strOutScripts.append("'").append(auxScripts).append("'");
                                    strOutScripts.append(" || CHR(13) || CHR(10)");
                                    strOutScripts.append(");");
                                } else {
                                    //if(maxLin == 0){
                                    //strOutScripts.append(sQuebraLinha + "" + sQuebraLinha);
                                    //strOutScripts.append(sQuebraLinha);
                                    //	strOutScripts.append("exec CONCATENA_CONTEUDO ('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "',");
                                    //}

                                    //if( (maxLin + auxScripts.length()) >= 350 ){
                                    //	strOutScripts.append("'" + auxScripts + "');");
                                    //	maxLin = 0;
                                    //}else{
                                    //	strOutScripts.append("'"+ auxScripts + "' || chr(13) || chr(10) ||");
                                    //	maxLin += auxScripts.length();
                                    //}
                                    //if(!auxScripts.isEmpty()){
                                    strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                                    strOutScripts.append("exec CONCATENA_CONTEUDO (");
                                    strOutScripts.append("'").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append("'");
                                    strOutScripts.append(",");
                                    strOutScripts.append("'").append(auxScripts).append("'");
                                    strOutScripts.append(" || CHR(13) || CHR(10)");
                                    strOutScripts.append(");");

                                    //}

                                }
                                contador += 1;
                            }


                            //if(maxLin < 350){
                            //	strOutScripts.append("');");
                            //}

                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("commit;");
                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("set define on");
                            strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA);
                            strOutScripts.append("-- //////").append(QUEBRA_LINHA);
                            strOutScripts.append("-- //////  Fim do Script").append(QUEBRA_LINHA);
                            strOutScripts.append("-- //////");

                            if (strOutScripts != null) {
                                fileScripts.saveArquivo(strOutScripts);
                                SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                            }
                        }
                    } catch (IOException ioex) {
                        SfwLogger.log("File " + fileNameScripts + " was error generated.");
                        SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                } else {
                    SfwLogger.log("No data are being generated");
                    SfwLogger.log("File " + fileName + " wasn't generated.");
                    SfwLogger.log("Error in the implementation of the interface with Id_Importação ");
                }
            }
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            cvsstructure.model.ArquivosExternos.getInstance().close();
            if(rsArquivosExternos != null){
                try {
                    rsArquivosExternos.close();
                    rsArquivosExternos = null;
                } catch (SQLException ex) {
                    Logger.getLogger(ArquivosExternos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String getNomePasta(String tipo) {
        if (tipo.isEmpty() || chNomePasta.equals("N")) {
            return interfaces.getIdInterface();
        } else if (tipo.equals("IN")) {
            return interfaces.getIdSistema() + "_in_" + interfaces.getIdInterface();
        } else if (tipo.equals("OUT")) {
            return interfaces.getIdSistema() + "_out_" + interfaces.getIdInterface();
        }
        return "";
    }

    /**
     * @return the intefaces
     */
    public Interface getInterfaces() {
        return interfaces;
    }

    /**
     * @param intefaces the intefaces to set
     */
    public void setInterfaces(Interface interfaces) {
        this.interfaces = interfaces;
    }
}
