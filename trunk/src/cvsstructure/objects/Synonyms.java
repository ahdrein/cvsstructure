package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import static cvsstructure.CVSStructure.chNomePasta;
import cvsstructure.util.Diretorio;
import cvsstructure.util.Define;
import cvsstructure.util.PrepararConsultas;
import cvsstructure.util.Estatisticas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;

/**
 *
 * @author andrein
 */
public class Synonyms extends Thread {

    private String idInterface = "";
    private String idSistema = "";
    private Cliente cliente;
    private PreparedStatement psArquivosSynonyms = null;
    private ResultSet rsArquivosSynonyms = null;
    private PreparedStatement psCountArquivosSynonyms = null;
    private ResultSet rsCountArquivosSynonyms = null;
    private PreparedStatement psSynonyms = null;
    private ResultSet rsSynonyms = null;
    private ResultSet rsSqlInterface = null;
    private ResultSet rsBatInterface = null;
    private ResultSet rsDadosInterface = null;
    private String system;

    public Synonyms(String system, Cliente cliente) {
        this.system = system;
        this.cliente = cliente;
    }

    @Override
    public void run() {
        // Informações InOut
        String executavel = "";
        String tipoInterface = "";

        String descricao = "";
        String userName = "";
        String tempoMedio = "";
        String executavelCompl = "";

        String fileNameScripts = "";
        String fileName = "";

        File fileScripts;
        FileWriter fwScripts;
        StringBuilder strOutScripts;
        BufferedReader brScripts;

        // Exibe todos os sysnonymos o arquivos que o utilizam
        StringBuilder sbArquivosSynonymos = new StringBuilder();
        sbArquivosSynonymos.append("select distinct a.nome_arquivo, v1.synonym_name");
        sbArquivosSynonymos.append("  from arquivo_externo a, (select synonym_name from user_synonyms) v1");
        sbArquivosSynonymos.append(" where upper(a.conteudo) like '%' || upper(v1.synonym_name) || '%'");
        sbArquivosSynonymos.append(" union all");
        sbArquivosSynonymos.append(" select '' nome_arquivo, synonym_name");
        sbArquivosSynonymos.append("  from user_synonyms");
        sbArquivosSynonymos.append(" where synonym_name not in");
        sbArquivosSynonymos.append("       (select distinct v1.synonym_name");
        sbArquivosSynonymos.append("          from arquivo_externo a,");
        sbArquivosSynonymos.append("               (select synonym_name from user_synonyms) v1");
        sbArquivosSynonymos.append("         where upper(a.conteudo) like '%' || upper(v1.synonym_name) || '%')");


        StringBuilder sbSynonymsAll = new StringBuilder();
        sbSynonymsAll.append("select * from user_synonyms");

        // Obetem os dados do synonymo
        StringBuilder sbSynonyms = new StringBuilder();
        sbSynonyms.append("select * from user_synonyms where synonym_name = ?");

        try {
            if (psArquivosSynonyms == null) {
                if (getSystem().equals("INOUT")) {
                    psArquivosSynonyms = ConnectionInout.getConnection().prepareStatement(sbArquivosSynonymos.toString());
                } else {
                    psArquivosSynonyms = ConnectionIntegracao.getConnection().prepareStatement(sbSynonymsAll.toString());
                }
            }
            //psGerarArquivosExternos.setString(1, sExecutavel);
            rsArquivosSynonyms = psArquivosSynonyms.executeQuery();

            while (rsArquivosSynonyms.next()) {
                int totalArquivos = 0;
                if (getSystem().equals("INOUT")) {
                    if (psCountArquivosSynonyms == null) {
                        psCountArquivosSynonyms = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                    }

                    // Contagem de interfaces que utilizando o synonymo
                    psCountArquivosSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                    rsCountArquivosSynonyms = psCountArquivosSynonyms.executeQuery();
                    rsCountArquivosSynonyms.next();
                    totalArquivos = rsCountArquivosSynonyms.getInt("TOTAL_ARQUIVOS");
                } else {
                    psCountArquivosSynonyms = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                }

                if (psSynonyms == null) {
                    if (getSystem().equals("INOUT")) {
                        psSynonyms = ConnectionInout.getConnection().prepareStatement(sbSynonyms.toString());
                    } else {
                        psSynonyms = ConnectionIntegracao.getConnection().prepareStatement(sbSynonyms.toString());
                    }
                }
                psSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                rsSynonyms = psSynonyms.executeQuery();
                while (rsSynonyms.next()) {
                    int totalInterfacesBySql = 0;
                    fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                    if (totalArquivos > 1 || totalArquivos == 0) {
                        fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\" + getSystem().toUpperCase() + "\\Synonyms\\" + fileName;
                    } else {
                        // Qual interface utiliza o synonymo
                        if (rsArquivosSynonyms.getString("NOME_ARQUIVO").substring(rsArquivosSynonyms.getString("NOME_ARQUIVO").indexOf(".") + 1, rsArquivosSynonyms.getString("NOME_ARQUIVO").length()).equals("SQL")) {
                            rsSqlInterface = PrepararConsultas.getSqlInterface(rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            while (rsSqlInterface.next()) {
                                idInterface = rsSqlInterface.getString("ID_INTERFACE").toLowerCase();
                                totalInterfacesBySql += 1;
                            }

                        } else {
                            rsBatInterface = PrepararConsultas.getBatInterface(rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            while (rsBatInterface.next()) {
                                idInterface = rsBatInterface.getString("ID_INTERFACE").toLowerCase();
                                totalInterfacesBySql += 1;
                            }
                        }

                        int totalSistemasByInterface = 0;
                        if (totalInterfacesBySql == 1) {
                            rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface.toUpperCase());
                            while (rsDadosInterface.next()) {
                                executavel = rsDadosInterface.getString("EXECUTAVEL");
                                tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                                idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                                descricao = rsDadosInterface.getString("DESCRICAO");
                                userName = rsDadosInterface.getString("USERNAME");
                                tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                                executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");

                                totalSistemasByInterface += 1;
                            }

                            if (tipoInterface.trim().equals("S")) {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\" + getSystem().toUpperCase() + "\\Synonyms\\" + fileName;
                            } else if (tipoInterface.trim().equals("E")) {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\" + getSystem().toUpperCase() + "\\Synonyms\\" + fileName;
                            } else {
                                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + idInterface + "\\" + getSystem().toUpperCase() + "\\Synonyms\\" + fileName;
                            }
                        } else {
                            fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\comum\\" + getSystem().toUpperCase() + "\\Synonyms\\" + fileName;
                        }
                    }

                    try {
                        fileScripts = new File(fileNameScripts);
                        if (!fileScripts.exists()) {
                            fileScripts.createNewFile();

                            SfwLogger.log("Creating or appending to file " + fileNameScripts);

                            strOutScripts = new StringBuilder();

                            /******************************************
                             * Gerando arquivos na pasta de Scripts
                             ******************************************/
                            if (chConexaoPorArquivos.equals("S")) {
                                if (getSystem().equals("INOUT")) {
                                    strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                } else {
                                    strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                                }
                            }

                            strOutScripts.append("-- Create the synonym").append(QUEBRA_LINHA);
                            strOutScripts.append("create or replace synonym ").append(rsSynonyms.getString("SYNONYM_NAME")).append(QUEBRA_LINHA);

                            String tableOwner = "";
                            if (rsSynonyms.getString("TABLE_OWNER") == null && rsSynonyms.getString("DB_LINK") == null) {
                                tableOwner = "";
                            } else if (rsSynonyms.getString("TABLE_OWNER") == null && rsSynonyms.getString("DB_LINK") != null) {
                                tableOwner = "";
                            } else if (cliente.getIoUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IO")) {
                                tableOwner = Define.INOUT_USER;
                            } else if (cliente.getCeUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CE")) {
                                tableOwner = Define.CAMBIO_EXP_USER;
                            } else if (cliente.getCiUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CI")) {
                                tableOwner = Define.CAMBIO_IMP_USER;
                            } else if (cliente.getIsUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IS")) {
                                tableOwner = Define.IMPORT_USER;
                            } else if (cliente.getExUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("EX")) {
                                tableOwner = Define.EXPORT_USER;
                            } else if (cliente.getDbUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("DB")) {
                                tableOwner = Define.DRAWBACK_USER;
                            } else if (cliente.getBsUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("BS")) {
                                tableOwner = Define.BROKER_USER;
                            } else if (cliente.getItUser().getUser().toUpperCase().equals(rsSynonyms.getString("TABLE_OWNER").toUpperCase()) || rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IT")) {
                                tableOwner = Define.INTEGRACAO_USER;
                            } else if (rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("APPS")) {
                                tableOwner = Define.APPS_USER;
                            } else if (rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CAI")) {
                                tableOwner = Define.CAI_USER;
                            } else {
                                tableOwner = "";
                            }

                            //strOutScripts.append("  for &&" + rsSynonyms.getString("TABLE_OWNER") );
                            strOutScripts.append("  for ");
                            if (!tableOwner.isEmpty()) {
                                strOutScripts.append("&&").append(tableOwner).append("..");
                            }

                            strOutScripts.append(rsSynonyms.getString("TABLE_NAME"));

                            if (rsSynonyms.getString("DB_LINK") != null) {
                                if (rsSynonyms.getString("DB_LINK").toUpperCase().contains("CAI")) {
                                    strOutScripts.append("@&&" + Define.DBLINK_CAI);
                                } else if (rsSynonyms.getString("DB_LINK").toUpperCase().contains("APPS")) {
                                    strOutScripts.append("@&&" + Define.DBLINK_APPS);
                                }

                            }
                            //strOutScripts.append(rsSynonyms.getString("DB_LINK") != null ? "@" + rsSynonyms.getString("DB_LINK") + QUEBRA_LINHA : "");
                            strOutScripts.append(";").append(QUEBRA_LINHA);

                            fwScripts = new FileWriter(fileScripts, false);
                            fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                            fwScripts.close();

                            Estatisticas.nTotalSynonyms++;
                            SfwLogger.log("File " + fileNameScripts + " was succesfull generated.");
                        }
                    } catch (IOException ioex) {
                        SfwLogger.log("File " + fileNameScripts + " was error generated.");
                        SfwLogger.debug(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            SfwLogger.log(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } finally {
            try {
                SfwLogger.log("Fechando conexões Synonyms");
                if (psArquivosSynonyms != null) {
                    psArquivosSynonyms.close();
                }
                if (rsArquivosSynonyms != null) {
                    rsArquivosSynonyms.close();
                }
                if (psCountArquivosSynonyms != null) {
                    psCountArquivosSynonyms.close();
                }
                if (rsCountArquivosSynonyms != null) {
                    rsCountArquivosSynonyms.close();
                }
                if (psSynonyms != null) {
                    psSynonyms.close();
                }
                if (rsSynonyms != null) {
                    rsSynonyms.close();
                }
                if (rsSqlInterface != null) {
                    rsSqlInterface.close();
                }
                if (rsBatInterface != null) {
                    rsBatInterface.close();
                }
                if (rsDadosInterface != null) {
                    rsDadosInterface.close();
                }
            } catch (SQLException sqlex) {
                SfwLogger.log(sqlex.getLocalizedMessage());
                sqlex.printStackTrace();
            }
        }
    }

    public String getIdInterface() {
        return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    public String getNomePasta(String tipo) {
        String pasta = "";

        if (tipo.isEmpty() || chNomePasta.equals("N")) {
            pasta = getIdInterface();
        } else if (tipo.equals("IN")) {
            pasta = getIdSistema() + "_in_" + getIdInterface();
        } else if (tipo.equals("OUT")) {
            pasta = getIdSistema() + "_out_" + getIdInterface();
        }

        return pasta;
    }

    /**
     * @return the idSistema
     */
    public String getIdSistema() {
        return idSistema;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
