/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import cvsstructure.database.ConnectionInout;
import cvsstructure.model.Cliente;
import cvsstructure.util.Arquivo;
import cvsstructure.util.Diretorio;
import cvsstructure.util.PrepararConsultas;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ahdrein
 */
public class ArquivosExternosNaoGerados {
    Cliente cliente;
    private ResultSet rsGerarArquivosExternosNaoGerados;
    private PreparedStatement psCountArquivosExternosNaoGerados;

    public ArquivosExternosNaoGerados(Cliente cliente){
        this.cliente = cliente;
    }
    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    private void arquivosExternosNaoGerados() throws Exception {
        Arquivo fileScripts;
        StringBuilder strOutScripts;
        String fileName;
        String fileNameScripts;

        try {

            try {
                //logMessage("*** Drop table TMP_CVS_STRUCTURE ");
                new cvsstructure.model.ArquivosExternos().dropTableTmpCvsStructure();
            } catch (Exception ex) {
                //logMessage("Error drop table TMP_CVS_STRUCTURE ");
                //logMessage(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            try {
                //logMessage("*** Create table TMP_CVS_STRUCTURE ");
                new cvsstructure.model.ArquivosExternosNaoGerados().createTableCvsStructure();
                new cvsstructure.model.ArquivosExternosNaoGerados().insertTableTmpCvsStructure(cliente.getIoUser().getUser());
            } catch (Exception ex) {
                //logMessage("Error create table TMP_CVS_STRUCTURE ");
                //logMessage(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            rsGerarArquivosExternosNaoGerados = new cvsstructure.model.ArquivosExternosNaoGerados().getArquivosExternosNaoGerados();
            while (rsGerarArquivosExternosNaoGerados.next()) {

                for (int i = 1; i <= 2; i++) {

                    psCountArquivosExternosNaoGerados = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                    String tipoArquivo;
                    if (i == 1) {
                        psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                        tipoArquivo = rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase();
                    } else {
                        psCountArquivosExternosNaoGerados.setString(1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                        tipoArquivo = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") + 1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length());
                    }
                    //psCountArquivosExternosNaoGerados.setString(2, rsGerarArquivosExternosNaoGerados.getString("CONTEM"));
                    rsCountArquivosExternosNaoGerados = psCountArquivosExternosNaoGerados.executeQuery();
                    rsCountArquivosExternosNaoGerados.next();
                    int nTotalArquivos = rsCountArquivosExternosNaoGerados.getInt("TOTAL_ARQUIVOS");

                    //String a = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase();
                    //System.out.println(a);
                    //if(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().contains("igi")){
                    //    String b = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase();
                    //}

                    if (nTotalArquivos > 1 || nTotalArquivos == 0) {
                        if (i == 1) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + "." + tipoArquivo;
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Package\\" + fileName;
                            }
                        } else {

                            if (tipoArquivo.equals("bat")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + "." + tipoArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + "." + tipoArquivo;
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase() + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\INOUT\\Package\\" + fileName;
                            }
                        }
                    } else {
                        if (rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").substring(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") + 1, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").length()).equals("SQL")) {
                            rsSqlInterface = PrepararConsultas.getSqlInterface(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                            while (rsSqlInterface.next()) {
                                idInterface = rsSqlInterface.getString("ID_INTERFACE");
                            }
                        } else {
                            rsBatInterface = PrepararConsultas.getBatInterface(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                            while (rsBatInterface.next()) {
                                idInterface = rsBatInterface.getString("ID_INTERFACE");
                            }
                        }

                        rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface);
                        while (rsDadosInterface.next()) {
                            executavel = rsDadosInterface.getString("EXECUTAVEL");
                            tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                            idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                            descricao = rsDadosInterface.getString("DESCRICAO");
                            //sUserNameApp = rsDadosInterface.getString("USERNAME");
                            tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                            executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");
                        }

                        String nomeArquivo = "";
                        if (i == 1) {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase();
                        } else {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().substring(0, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") - 1);
                        }

                        if (tipoInterface.trim().equals("S")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Package\\" + fileName;
                            }
                        } else if (tipoInterface.trim().equals("E")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Package\\" + fileName;
                            }
                        } else {
                            //fileNameScripts = ".\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + this.getIdInterface() + "\\INOUT\\Package\\" + fileName;
                            }
                        }
                    }

                    logMessage("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuffer();
                    String auxScripts;

                    if (tipoArquivo.equals("bat") || tipoArquivo.equals("sql")) {
                        psGerarArquivosExternos.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM") + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO"));
                        rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                        try {
                            fileScripts = new File(fileNameScripts);
                            if (!fileScripts.exists()) {
                                fileScripts.createNewFile();

                                fwScripts = new FileWriter(fileScripts, false);

                                while (rsArquivosExternos.next()) {
                                    clob = rsArquivosExternos.getClob("CONTEUDO");

                                    if (clob != null) {
                                        /******************************************
                                         * Gerando arquivos na pasta de Scripts
                                         ******************************************/
                                        if (chConexaoPorArquivos.equals("S")) {
                                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        }

                                        strOutScripts.append("--  ///////" + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////     Arquivo Externo: " + rsArquivosExternos.getString("NOME_ARQUIVO") + QUEBRA_LINHA);
                                        strOutScripts.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("set define off" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = '" + rsArquivosExternos.getString("NOME_ARQUIVO") + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("insert into ARQUIVO_EXTERNO" + QUEBRA_LINHA);
                                        strOutScripts.append("(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)" + QUEBRA_LINHA);
                                        strOutScripts.append("values" + QUEBRA_LINHA);

                                        // We access to stream, as this way we don't have to use the CLOB.length() which is slower...
                                        brScripts = new BufferedReader(clob.getCharacterStream());
                                        int contador = 0;
                                        int maxLin = 0;
                                        while ((auxScripts = brScripts.readLine()) != null) {

                                            auxScripts = auxScripts.replaceAll("'", "' || chr(27) || '");
                                            auxScripts = auxScripts.replaceAll("@", "' || chr(64) || '");
                                            auxScripts = auxScripts.replaceAll("\t", "' '");
                                            //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                            auxScripts = auxScripts.replaceAll(QUEBRA_LINHA, "' || chr(13) || CHR(10) ||'");
                                            //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                            if (contador == 0) {
                                                strOutScripts.append("('" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                strOutScripts.append(",");
                                                if (rsArquivosExternos.getString("NOME_ARQUIVO") == null) {
                                                    strOutScripts.append("'" + (descricao.length() > 50 ? descricao.substring(0, 50) : descricao.trim()) + "'");
                                                } else {
                                                    strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                }
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + rsArquivosExternos.getString("PATH_RELATIVO") + "'");
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + auxScripts + "'");
                                                strOutScripts.append(" || CHR(13) || CHR(10)");
                                                strOutScripts.append(");");
                                            } else {
                                                //if(!auxScripts.equals("")){
                                                strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                                strOutScripts.append("exec CONCATENA_CONTEUDO (");
                                                strOutScripts.append("'" + rsArquivosExternos.getString("NOME_ARQUIVO") + "'");
                                                strOutScripts.append(",");
                                                strOutScripts.append("'" + auxScripts + "'");
                                                strOutScripts.append(" || CHR(13) || CHR(10)");
                                                strOutScripts.append(");");
                                                //}

                                            }
                                            contador += 1;
                                        }
                                        strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                        strOutScripts.append("commit;");
                                        strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("set define on");
                                        strOutScripts.append(QUEBRA_LINHA + "" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////  Fim do Script" + QUEBRA_LINHA);
                                        strOutScripts.append("-- //////");
                                    } else {
                                        logMessage("No data are being generated");
                                        logMessage("File " + fileName + " wasn't generated.");
                                        logMessage("Error in the implementation of the interface with Id_Importação " + idInterface);

                                    }
                                }

                                if (strOutScripts != null) {
                                    fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                                }
                                fwScripts.close();

                                logMessage("File " + fileNameScripts + " was succesfull generated.");
                            }
                        } catch (IOException ioex) {
                            CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                            SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                            ioex.printStackTrace();
                        }

                    } else if (tipoArquivo.equals("function")
                            || tipoArquivo.equals("procedure")) {

                        //
                        new FunctionProcedure("INOUT",
                                rsGerarArquivosExternosNaoGerados.getString("TIPO"),
                                rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"),
                                fileName,
                                fileNameScripts);

                    } else if (tipoArquivo.equals("package")
                            || tipoArquivo.equals("package body")) {

                        //
                        new Packages("INOUT",
                                rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"),
                                fileName,
                                fileNameScripts);
                    }
                }
            }
        } catch (Exception ex) {
            logMessage("Error generating " + fileName);
            logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            psDropTableIT = ConnectionInout.getConnection().prepareStatement("drop table TMP_CVS_STRUCTURE");
            psDropTableIT.executeQuery();
        }
    }

}
