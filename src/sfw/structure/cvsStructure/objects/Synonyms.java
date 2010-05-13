/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.cvsStructure.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sfw.structure.cvsStructure.CVSStructure;
import sfw.structure.database.ConnectionInout;
import sfw.structure.database.ConnectionIntegracao;
import sfw.structure.log.SfwLogger;

/**
 *
 * @author andrein
 */
public class Synonyms {
        private String idInterface = "";
        private String idSistema = "";

        private PreparedStatement psArquivosSynonyms = null;
    	private ResultSet rsArquivosSynonyms = null;

        private PreparedStatement psCountArquivosSynonyms = null;
        private ResultSet rsCountArquivosSynonyms = null;

        private PreparedStatement psSynonyms = null;
        private ResultSet rsSynonyms = null;

        private ResultSet rsSqlInterface = null;
        private ResultSet rsBatInterface = null;
        private ResultSet rsDadosInterface = null;

	public Synonyms(String system){

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
		StringBuffer strOutScripts;
		BufferedReader brScripts;

        // Exibe todos os sysnonymos o arquivos que o utilizam
        StringBuffer sbArquivosSynonymos = new StringBuffer();
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


        StringBuffer sbSynonymsAll = new StringBuffer();
        sbSynonymsAll.append("select * from user_synonyms");

        // Obetem os dados do synonymo
        StringBuffer sbSynonyms = new StringBuffer();
        sbSynonyms.append("select * from user_synonyms where synonym_name = ?");

        try{
            if(psArquivosSynonyms == null){
                if(system.equals("INOUT")){
                    psArquivosSynonyms = ConnectionInout.getConnection().prepareStatement(sbArquivosSynonymos.toString());
                }else{
                    psArquivosSynonyms = ConnectionIntegracao.getConnection().prepareStatement(sbSynonymsAll.toString());
                }
            }
            //psGerarArquivosExternos.setString(1, sExecutavel);
            rsArquivosSynonyms = psArquivosSynonyms.executeQuery();

            while(rsArquivosSynonyms.next()){
                int totalArquivos = 0;
                if(system.equals("INOUT")){
                    if(psCountArquivosSynonyms == null){
                        psCountArquivosSynonyms = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                    }

                    // Contagem de interfaces que utilizando o synonymo
                    psCountArquivosSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                    rsCountArquivosSynonyms = psCountArquivosSynonyms.executeQuery();
                    rsCountArquivosSynonyms.next();
                    totalArquivos = rsCountArquivosSynonyms.getInt("TOTAL_ARQUIVOS");
                }else{
                    psCountArquivosSynonyms = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getCountArquivosExternos().toString());
                }

                if(psSynonyms == null){
                    if(system.equals("INOUT")){
                        psSynonyms = ConnectionInout.getConnection().prepareStatement(sbSynonyms.toString());
                    }else{
                        psSynonyms = ConnectionIntegracao.getConnection().prepareStatement(sbSynonyms.toString());
                    }
                }
                psSynonyms.setString(1, rsArquivosSynonyms.getString("SYNONYM_NAME"));
                rsSynonyms = psSynonyms.executeQuery();
                while(rsSynonyms.next()){
                    int totalInterfacesBySql=0;
                    fileName = rsArquivosSynonyms.getString("SYNONYM_NAME").toLowerCase() + ".sql";
                    if(totalArquivos > 1 || totalArquivos == 0){
                        fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\" + system.toUpperCase() + "\\Synonyms\\" + fileName;
                    }else{
                        // Qual interface utiliza o synonymo
                        if(rsArquivosSynonyms.getString("NOME_ARQUIVO").substring(rsArquivosSynonyms.getString("NOME_ARQUIVO").indexOf(".")+1, rsArquivosSynonyms.getString("NOME_ARQUIVO").length()).equals("SQL") ){
                            rsSqlInterface = PrepararConsultas.getSqlInterface(rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            while(rsSqlInterface.next()){
                                idInterface = rsSqlInterface.getString("ID_INTERFACE").toLowerCase();
                                totalInterfacesBySql += 1;
                            }

                        }else{
                            rsBatInterface = PrepararConsultas.getBatInterface(rsArquivosSynonyms.getString("NOME_ARQUIVO"));
                            while(rsBatInterface.next()){
                                idInterface = rsBatInterface.getString("ID_INTERFACE").toLowerCase();
                                totalInterfacesBySql += 1;
                            }
                        }

                        int totalSistemasByInterface = 0;
                        if(totalInterfacesBySql == 1){
                            rsDadosInterface = PrepararConsultas.getDadosInterface(idInterface.toUpperCase());
                            while(rsDadosInterface.next()){
                                executavel = rsDadosInterface.getString("EXECUTAVEL");
                                tipoInterface = rsDadosInterface.getString("TIPO_INTERFACE");
                                idSistema = rsDadosInterface.getString("ID_SISTEMA").toLowerCase();
                                descricao = rsDadosInterface.getString("DESCRICAO");
                                userName = rsDadosInterface.getString("USERNAME");
                                tempoMedio = rsDadosInterface.getString("TEMPO_MEDIO");
                                executavelCompl = rsDadosInterface.getString("EXECUTAVELCOMPL");

                                totalSistemasByInterface += 1;
                            }
                            
                            //if(totalSistemasByInterface){
                            //encontrar o pq essa pasta não foi criada ???????
                            if(tipoInterface.trim().equals("S")){
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("OUT") + "\\" + system.toUpperCase() + "\\Synonyms\\" + fileName;
                            }else if(tipoInterface.trim().equals("E")){
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + getNomePasta("IN") + "\\" + system.toUpperCase() + "\\Synonyms\\" + fileName;
                            }else{
                                fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\" + idInterface + "\\" + system.toUpperCase() + "\\Synonyms\\"  + fileName;
                            }
                        }else{
                            fileNameScripts = CVSStructure.path + "\\"+CVSStructure.userNameSys+"\\Scripts\\comum\\" + system.toUpperCase() + "\\Synonyms\\" + fileName;
                        }
                    }

                    try{
                        fileScripts = new File(fileNameScripts);
                        if(!fileScripts.exists())
                            fileScripts.createNewFile();

                        CVSStructure.logMessage("Creating or appending to file " + fileNameScripts);

                        strOutScripts = new StringBuffer();

                        /******************************************
                        * Gerando arquivos na pasta de Scripts
                        ******************************************/

                        if(CVSStructure.chConexaoPorArquivos.equals("S")){
                            if(system.equals("INOUT")){
                                strOutScripts.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                            }else{
                                strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + CVSStructure.quebraLinha + CVSStructure.quebraLinha);
                            }
                        }

                        strOutScripts.append("-- Create the synonym" + CVSStructure.quebraLinha);
                        strOutScripts.append("create or replace synonym " + rsSynonyms.getString("SYNONYM_NAME") + CVSStructure.quebraLinha);

                        String tableOwner = "";
                        if(rsSynonyms.getString("TABLE_OWNER") == null && rsSynonyms.getString("DB_LINK") == null){
                            tableOwner = Define.SOFT_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER") == null && rsSynonyms.getString("DB_LINK") != null){
                            tableOwner = "";
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IO")){
                            tableOwner = Define.INOUT_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CE")){
                            tableOwner = Define.CAMBIO_EXP_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CI")){
                            tableOwner = Define.CAMBIO_IMP_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IS")){
                            tableOwner = Define.IMPORT_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("EX")){
                            tableOwner = Define.EXPORT_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("DB")){
                            tableOwner = Define.DRAWBACK_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("BS")){
                            tableOwner = Define.BROKER_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("IT")){
                            tableOwner = Define.INTEGRACAO_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("APPS")){
                            tableOwner = Define.APPS_USER;
                        }else if(rsSynonyms.getString("TABLE_OWNER").toUpperCase().contains("CAI")){
                            tableOwner = Define.CAI_USER;
                        }else{
                            tableOwner = Define.SOFT_USER;
                        }

                        //strOutScripts.append("  for &&" + rsSynonyms.getString("TABLE_OWNER") );
                        strOutScripts.append("  for "  );
                        if(rsSynonyms.getString("DB_LINK") == null || !tableOwner.equals("")){
                            strOutScripts.append("&&" + tableOwner + "..");
                        }
                        
                        strOutScripts.append( rsSynonyms.getString("TABLE_NAME") );
                        
                        if(rsSynonyms.getString("DB_LINK") != null){
                            if(rsSynonyms.getString("DB_LINK").toUpperCase().contains("CAI")){
                                strOutScripts.append("@&&" + Define.DBLINK_CAI );
                            }else if(rsSynonyms.getString("DB_LINK").toUpperCase().contains("APPS")){
                                strOutScripts.append("@&&" + Define.DBLINK_APPS );
                            }
                            
                        }
                        //strOutScripts.append(rsSynonyms.getString("DB_LINK") != null ? "@" + rsSynonyms.getString("DB_LINK") + CVSStructure.quebraLinha : "");
                        strOutScripts.append(";" + CVSStructure.quebraLinha);

                        fwScripts = new FileWriter(fileScripts, false);
                        fwScripts.write(strOutScripts.toString(),0,strOutScripts.length());
                        fwScripts.close();
                        CVSStructure.logMessage("File " + fileNameScripts + " was succesfull generated.");
                    }catch(IOException ioex){
                        CVSStructure.logMessage("File " + fileNameScripts + " was error generated.");
                        SfwLogger.saveLog(ioex.getClass().toString(), ioex.getStackTrace());
                        ioex.printStackTrace();
                    }
                }
            }
        }catch(Exception ex){
            CVSStructure.logMessage("Error generating " + fileName);
            CVSStructure.logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
            ex.printStackTrace();
        }catch(Error e){
            e.printStackTrace();
        }finally{
            try{
                SfwLogger.debug("Fechando conexões Synonyms");
                if(psArquivosSynonyms != null) psArquivosSynonyms.close();
                if(rsArquivosSynonyms != null) rsArquivosSynonyms.close();
                if(psCountArquivosSynonyms != null) psCountArquivosSynonyms.close();
                if(rsCountArquivosSynonyms != null) rsCountArquivosSynonyms.close();
                if(psSynonyms != null) psSynonyms.close();
                if(rsSynonyms != null) rsSynonyms.close();
                if(rsSqlInterface != null) rsSqlInterface.close();
                if(rsBatInterface != null) rsBatInterface.close();
                if(rsDadosInterface != null) rsDadosInterface.close();
            }catch(SQLException sqlex){
                SfwLogger.log(sqlex);
                sqlex.printStackTrace();
            }
        }
	}

    public String getIdInterface(){
            return idInterface.trim().replace("(", "").replace(")", ")").replace(".", "").replace(" ", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(">", "").replace("<", "").replace("-", "_").trim().toLowerCase();
    }

    public String getNomePasta(String tipo){
        String pasta = "";

        if(tipo.equals("") || CVSStructure.chNomePasta.equals("N")){
            pasta = getIdInterface();
        }else if (tipo.equals("IN")){
            pasta = getIdSistema() + "_in_" + getIdInterface();
        }else if (tipo.equals("OUT")){
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

}
