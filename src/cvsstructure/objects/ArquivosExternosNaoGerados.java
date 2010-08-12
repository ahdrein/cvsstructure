/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import static cvsstructure.CVSStructure.chNomePasta;
import cvsstructure.database.ConnectionInout;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.model.Interface;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.Diretorio;
import cvsstructure.util.PrepararConsultas;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ahdrein
 */
public class ArquivosExternosNaoGerados extends Thread{
    Cliente cliente;
    Interface interfaces;

    private ResultSet rsGerarArquivosExternosNaoGerados;
    private PreparedStatement psCountArquivosExternosNaoGerados;
    private ResultSet rsCountArquivosExternosNaoGerados;
    private ResultSet rsSqlInterface;
    private ResultSet rsBatInterface;
    private ResultSet rsDadosInterface;
    private PreparedStatement psGerarArquivosExternos;
    private ResultSet rsArquivosExternos;
    private Clob clob;

    public ArquivosExternosNaoGerados() {

    }

    public ArquivosExternosNaoGerados(Cliente cliente){
        this.cliente = cliente;
    }
    /**************************************************************************
     * <b>Gerar scripts dos arquivos externos</b>
     **************************************************************************/
    @Override
    public void run() {
        CvsStructureFile fileScripts;
        StringBuilder strOutScripts;
        String fileName = null;
        String fileNameScripts = null;
        BufferedReader brScripts;
        

        try {

            try {
                SfwLogger.log("*** Drop table TMP_CVS_STRUCTURE ");
                cvsstructure.model.ArquivosExternos.getInstance().dropTableTmpCvsStructure();
            } catch (Exception ex) {
                SfwLogger.log("Error drop table TMP_CVS_STRUCTURE ");
                SfwLogger.log(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            try {
                SfwLogger.log("*** Create table TMP_CVS_STRUCTURE ");
                cvsstructure.model.ArquivosExternosNaoGerados.getInstance().createTableCvsStructure();
                cvsstructure.model.ArquivosExternosNaoGerados.getInstance().insertTableTmpCvsStructure(cliente.getIoUser().getUser());
            } catch (Exception ex) {
                SfwLogger.log("Error create table TMP_CVS_STRUCTURE ");
                SfwLogger.log(ex.getLocalizedMessage());
                //SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            }

            rsGerarArquivosExternosNaoGerados = cvsstructure.model.ArquivosExternosNaoGerados.getInstance().getArquivosExternosNaoGerados();
            while (rsGerarArquivosExternosNaoGerados.next()) {
                interfaces = new Interface();
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
                                interfaces.setIdInterface( rsSqlInterface.getString("ID_INTERFACE") );
                            }
                        } else {
                            rsBatInterface = PrepararConsultas.getBatInterface(rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO"));
                            while (rsBatInterface.next()) {
                                interfaces.setIdInterface( rsBatInterface.getString("ID_INTERFACE") );
                            }
                        }

                        rsDadosInterface = PrepararConsultas.getDadosInterface(interfaces.getIdInterface());
                        while (rsDadosInterface.next()) {
                            interfaces.setExecutavel(rsDadosInterface.getString("EXECUTAVEL"));
                            interfaces.setTipoInterface(rsDadosInterface.getString("TIPO_INTERFACE"));
                            interfaces.setIdSistema(rsDadosInterface.getString("ID_SISTEMA").toLowerCase());
                            interfaces.setDescricao(rsDadosInterface.getString("DESCRICAO"));
                            //sUserNameApp = rsDadosInterface.getString("USERNAME");
                            interfaces.setTempoMedio(rsDadosInterface.getString("TEMPO_MEDIO"));
                            interfaces.setExecutavelCompl(rsDadosInterface.getString("EXECUTAVELCOMPL"));
                        }

                        String nomeArquivo = "";
                        if (i == 1) {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("CONTEM").toLowerCase();
                        } else {
                            nomeArquivo = rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").toLowerCase().substring(0, rsGerarArquivosExternosNaoGerados.getString("NOME_ARQUIVO").indexOf(".") - 1);
                        }

                        if (interfaces.getTipoInterface().trim().equals("S")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INTEGRACAO\\Package\\" + fileName;
                            }
                        } else if (interfaces.getTipoInterface().trim().equals("E")) {
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Package\\" + fileName;
                            }
                        } else {
                            //fileNameScripts = ".\\"+sUserName+"\\Scripts\\" + this.getIDInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            if (tipoArquivo.equals("bat")) {
                                fileName = nomeArquivo + "." + tipoArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("sql")) {
                                fileName = nomeArquivo + "." + tipoArquivo;
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\ArquivosExternos\\" + fileName;
                            } else if (tipoArquivo.equals("function")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\Function\\" + fileName;
                            } else if (tipoArquivo.equals("procedure")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\Procedure\\" + fileName;
                            } else if (tipoArquivo.equals("package")) {
                                fileName = nomeArquivo + ".sql";
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\Package\\" + fileName;
                            }
                        }
                    }

                    SfwLogger.log("Creating or appending to file " + fileNameScripts);

                    strOutScripts = new StringBuilder();
                    String auxScripts;

                    if (tipoArquivo.equals("bat") || tipoArquivo.equals("sql")) {
                        psGerarArquivosExternos.setString(1, rsGerarArquivosExternosNaoGerados.getString("CONTEM") + "." + rsGerarArquivosExternosNaoGerados.getString("TIPO"));
                        rsArquivosExternos = psGerarArquivosExternos.executeQuery();

                        try {
                            fileScripts = new CvsStructureFile(fileNameScripts);
                            if (!fileScripts.exists()) {

                                while (rsArquivosExternos.next()) {
                                    clob = rsArquivosExternos.getClob("CONTEUDO");

                                    if (clob != null) {
                                        /******************************************
                                         * Gerando arquivos na pasta de Scripts
                                         ******************************************/
                                        if (chConexaoPorArquivos.equals("S")) {
                                            strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                        }

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

                                            auxScripts = auxScripts.replaceAll("'", "' || chr(27) || '");
                                            auxScripts = auxScripts.replaceAll("@", "' || chr(64) || '");
                                            auxScripts = auxScripts.replaceAll("\t", "' '");
                                            //auxScripts = auxScripts.replaceAll(sQuebraLinha,  "' || chr(13) || '");
                                            auxScripts = auxScripts.replaceAll(QUEBRA_LINHA, "' || chr(13) || CHR(10) ||'");
                                            //auxScripts = auxScripts.replaceAll("|| '' ||",  "||");

                                            if (contador == 0) {
                                                strOutScripts.append("('").append(rsArquivosExternos.getString("NOME_ARQUIVO")).append("'");
                                                strOutScripts.append(",");
                                                if (rsArquivosExternos.getString("NOME_ARQUIVO") == null) {
                                                    strOutScripts.append("'").append(interfaces.getDescricao().length() > 50 ? interfaces.getDescricao().substring(0, 50) : interfaces.getDescricao().trim()).append("'");
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
                                                //if(!auxScripts.isEmpty()){
                                                strOutScripts.append(QUEBRA_LINHA + "").append(QUEBRA_LINHA);
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
                                        strOutScripts.append(QUEBRA_LINHA + "").append(QUEBRA_LINHA);
                                        strOutScripts.append("commit;");
                                        strOutScripts.append(QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                                        strOutScripts.append("set define on");
                                        strOutScripts.append(QUEBRA_LINHA + "").append(QUEBRA_LINHA);
                                        strOutScripts.append("-- //////").append(QUEBRA_LINHA);
                                        strOutScripts.append("-- //////  Fim do Script").append(QUEBRA_LINHA);
                                        strOutScripts.append("-- //////");
                                    } else {
                                        SfwLogger.log("File " + fileName + " wasn't generated.");
                                    }
                                }

                                if (strOutScripts != null) {
                                    fileScripts.saveArquivo(strOutScripts);
                                }
                                

                                SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                            }
                        } catch (IOException ioex) {
                            SfwLogger.log("File " + fileNameScripts + " was error generated.");
                            SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
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
            SfwLogger.log("Error generating " + fileName);
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        } finally {
            try {
                cvsstructure.model.ArquivosExternos.getInstance().dropTableTmpCvsStructure();
            } catch (SQLException ex) {
                ex.printStackTrace();
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

}
