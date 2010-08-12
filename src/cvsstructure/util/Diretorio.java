/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.util;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import cvsstructure.CVSStructure;
import cvsstructure.model.Interface;
import cvsstructure.model.Cliente;
import cvsstructure.log.SfwLogger;
import cvsstructure.gui.SfwValidaScriptsFrame;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author ahdrein
 */
public class Diretorio {

    public static ArrayList<String> dirScriptsValida = new ArrayList<String>(2000);

    private String tipoInterface;
    private ArrayList<String> dirScripts;
    public static String path;
    private Interface interfaces;

    /**************************************************************************
     * <b>Criar diretorio</b>
     **************************************************************************/
    public void criarDiretoriosComuns() throws Exception {
        dirScripts = new ArrayList<String>(50);

        String scripts = Diretorio.path + "\\" + Cliente.userNameSys;

        getDirScriptsValida().add(scripts + "\\Scripts\\comum");

        // Arquivos Inout
        dirScripts.add(scripts);
        
        dirScripts.add(scripts + "\\Arquivos\\");
        dirScripts.add(scripts + "\\Arquivos\\comum");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\Interfaces");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\ArquivosExternos");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\Tabelas");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\Sistemas");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\Synonyms");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\View");
        dirScripts.add(scripts + "\\Arquivos\\comum\\INOUT\\Sequence");

        // Scripts Inout
        dirScripts.add(scripts + "\\Scripts\\");
        dirScripts.add(scripts + "\\Scripts\\comum");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Interfaces");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\ArquivosExternos");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Tabelas");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Sistemas");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Synonyms");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\View");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Sequence");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Package");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\PackageBody");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Procedure");
        dirScripts.add(scripts + "\\Scripts\\comum\\INOUT\\Function");

        // Scripts Integracao
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Function");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Procedure");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Package");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\PackageBody");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Mapeamento_SAP");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Table");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Synonyms");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\View");
        dirScripts.add(scripts + "\\Scripts\\comum\\INTEGRACAO\\Sequence");

        for (int i = 0; i < dirScripts.size(); i++) {
            File file = new File(dirScripts.get(i).toString());
            if (file.mkdir()) {
                SfwLogger.debug("Diretorio criado com sucesso! " + dirScripts.get(i));
            } else {
                SfwLogger.log("Erro ao criar diretorio! " + dirScripts.get(i));
            }
        }
    }

    /**************************************************************************
     * <b>Criar diretorio</b>
     **************************************************************************/
    public void criarDiretorio() throws Exception {
        dirScripts = new ArrayList(50);
        String scripts;
        String arquivos;

        if (getTipoInterface().trim().equals("S")) {
            scripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT");
            arquivos = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("OUT");
        } else if (getTipoInterface().trim().equals("E")) {
            scripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN");
            arquivos = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("IN");
         } else {
            scripts = path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("");
            arquivos = path + "\\" + Cliente.userNameSys + "\\Arquivos\\" + getNomePasta("");
        }

        getDirScriptsValida().add(scripts);

        dirScripts.add(scripts);
        dirScripts.add(arquivos);

        dirScripts.add(scripts + "\\INOUT\\");
        dirScripts.add(arquivos + "\\INOUT\\");

        dirScripts.add(scripts + "\\INOUT\\Interface\\");
        dirScripts.add(arquivos + "\\INOUT\\Interface\\");

        dirScripts.add(scripts + "\\INOUT\\ArquivosExternos\\");
        dirScripts.add(arquivos + "\\INOUT\\ArquivosExternos\\");

        dirScripts.add(scripts + "\\INOUT\\Tabelas\\");
        dirScripts.add(arquivos + "\\INOUT\\Tabelas\\");

        dirScripts.add(scripts + "\\INOUT\\Synonyms\\");
        dirScripts.add(arquivos + "\\INOUT\\Synonyms\\");

        dirScripts.add(scripts + "\\INOUT\\View\\");
        dirScripts.add(arquivos + "\\INOUT\\View\\");

        dirScripts.add(scripts + "\\INOUT\\Sequence\\");
        dirScripts.add(arquivos + "\\INOUT\\Sequence\\");

        dirScripts.add(scripts + "\\INOUT\\Package\\");
        dirScripts.add(scripts + "\\INOUT\\PackageBody\\");
        dirScripts.add(scripts + "\\INOUT\\Procedure\\");
        dirScripts.add(scripts + "\\INOUT\\Function\\");

        /*Integracao*/
        dirScripts.add(scripts + "\\INTEGRACAO");
        dirScripts.add(scripts + "\\INTEGRACAO\\Function");
        dirScripts.add(scripts + "\\INTEGRACAO\\Procedure");
        dirScripts.add(scripts + "\\INTEGRACAO\\Package");
        dirScripts.add(scripts + "\\INTEGRACAO\\PackageBody");
        dirScripts.add(scripts + "\\INTEGRACAO\\Mapeamento");
        dirScripts.add(scripts + "\\INTEGRACAO\\Mapeamento_SAP");
        dirScripts.add(scripts + "\\INTEGRACAO\\Table");
        dirScripts.add(scripts + "\\INTEGRACAO\\View");
        dirScripts.add(scripts + "\\INTEGRACAO\\Sequence");

        for (int i = 0; i < dirScripts.size(); i++) {
            File file = new File(dirScripts.get(i).toString());
            if (file.mkdir()) {
                SfwLogger.debug("Diretorio criado com sucesso! " + dirScripts.get(i));
            } else {
                SfwLogger.log("Erro ao criar diretorio! " + dirScripts.get(i));
            }
        }
    }

    /**************************************************************************
     * <b>Remover diretórios</b>
     **************************************************************************/
    public void removeDiretorio() throws Exception {
        File diretorio = new File(Diretorio.path + "\\" + Cliente.userNameSys);
        File[] subdiretorios = diretorio.listFiles();
        SfwLogger.debug("Removendo diretórios! ");
        for (File subdir : subdiretorios) {
            if (subdir.isDirectory()) {
                //SfwLogger.debug(subdir.getName());
                int nArqs = listaSubDir(subdir);
                if (nArqs == 0) {
                    if (subdir.delete()) {
                        //SfwLogger.debug("Diretório deletado: " + subdir.getName());
                    }
                }
            }
        }
    }

    public void validaDiretorio() throws Exception {
        StringBuilder strOutScripts = new StringBuilder();
        SfwValidaScriptsFrame valid = new SfwValidaScriptsFrame();
        valid.setArqsInstala("N");
        //strOutScripts.append("SPOOL SCRIPT_STATUS.LOG" + QUEBRA_LINHA);
        //strOutScripts.append("@\".\\define.sql\"" + QUEBRA_LINHA);

        // comum em primeiro lugar
        //valid.executar(path + "\\"+Cliente.userNameSys+"\\Scripts\\comum");
        //strOutScripts.append("@\"" + (path + "\\"+Cliente.userNameSys+"\\Scripts\\").replace(path + "\\"+Cliente.userNameSys+"\\Scripts", ".") + "\\comum\\ordem_instalacao.sql\"" + QUEBRA_LINHA);

        strOutScripts.append("--spool instalacao_v01r01p00_txt.log  -- esse comando passou a ser executado no script dispara_script_instalacao.sql" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@define.sql" + QUEBRA_LINHA);
        strOutScripts.append("-- Conectar na base do INOUT para obter a data do inicio do processamento" + QUEBRA_LINHA);
        strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("column \"DATA INICIO\" format A23" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS') AS \"DATA INICIO\" FROM DUAL;" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt INICIO do Processamento" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);

        for (int i = 0; i < Diretorio.dirScriptsValida.size(); i++) {

            // iginora o comum pq ele já foi valido em primeiro lugar
            //if( dirScriptsValida.get(i).toString().indexOf("\\comum\\") == 0 ){
            valid.executar(Diretorio.dirScriptsValida.get(i).toString());
            strOutScripts.append("@\"").append(Diretorio.dirScriptsValida.get(i).toString().replace(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts", ".")).append("\\ordem_instalacao.sql\"" + QUEBRA_LINHA);
            //}
        }

        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@\".\\processa_grants.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        strOutScripts.append("@\".\\processa_grants_sistema.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append("connect_io.sql" + QUEBRA_LINHA);
        strOutScripts.append("@\".\\compila_invalidos.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS" + QUEBRA_LINHA);
        strOutScripts.append("connect_it.sql" + QUEBRA_LINHA);
        strOutScripts.append("@\".\\compila_invalidos.sql\"" + QUEBRA_LINHA);

        strOutScripts.append(QUEBRA_LINHA);
        //strOutScripts.append("@define.sql" + QUEBRA_LINHA);
        //strOutScripts.append("-- Conectar na base do INOUT para obter a data de fim do processamento" + QUEBRA_LINHA);
        //strOutScripts.append("-- utilizar a mesma base utilizada no inicio do processamento" + QUEBRA_LINHA);
        //strOutScripts.append("conn &INOUT_USER/&INOUT_PASS@&TNS" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("SELECT 'Finalizado em: '||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS') AS \"DATA FIM\" FROM DUAL;" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt Fim do Processamento" + QUEBRA_LINHA);
        strOutScripts.append("prompt =======================" + QUEBRA_LINHA);
        strOutScripts.append("prompt" + QUEBRA_LINHA);
        strOutScripts.append("@\"limpa_definicoes.sql\"" + QUEBRA_LINHA);
        strOutScripts.append(QUEBRA_LINHA);

        strOutScripts.append("spool off" + QUEBRA_LINHA);
        strOutScripts.append("exit" + QUEBRA_LINHA);

        valid.copy(new File(".\\definicoes\\define.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "define.sql"));
        valid.copy(new File(".\\definicoes\\Instrucoes.txt"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "Instrucoes.txt"));
        valid.copy(new File(".\\definicoes\\limpa_definicoes.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "limpa_definicoes.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "processa_grants.sql"));
        valid.copy(new File(".\\definicoes\\compila_invalidos.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "compila_invalidos.sql"));
        valid.copy(new File(".\\definicoes\\instala_linux.sh"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "instala_linux.sh"));
        valid.copy(new File(".\\definicoes\\Instala_win.bat"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "Instala_win.bat"));
        valid.copy(new File(".\\definicoes\\dispara_script_instalacao.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "dispara_script_instalacao.sql"));
        valid.copy(new File(".\\definicoes\\connect_io.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "connect_io.sql"));
        valid.copy(new File(".\\definicoes\\connect_it.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "connect_it.sql"));
        valid.copy(new File(".\\definicoes\\processa_grants_sistema.sql"), new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + "processa_grants_sistema.sql"));

        File fileScripts = new File(Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\ordem_instalacao.sql");
        if (!fileScripts.exists()) {
            fileScripts.createNewFile();
        }

        FileWriter fwScripts = new FileWriter(fileScripts, false);
        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
        fwScripts.close();
        SfwLogger.log("File .\\" + Cliente.userNameSys + "\\Scripts\\ordem_instalacao.sql was succesfull generated.");

    }

    /**************************************************************************
     * <b>Listar subDiretórios</b>
     * @param subDir
     **************************************************************************/
    private int listaSubDir(File subDir) {
        int nArqs = 0;
        File[] subdiretorios = subDir.listFiles();
        for (File subdir : subdiretorios) {
            if (subdir.isDirectory()) {
                //System.out.println(subdir.getName());
                if (listaSubDir(subdir) == 0) {
                    if (subdir.delete()) {
                        SfwLogger.debug("Diretório deletado: " + subdir.getName());
                    }
                }
            } else {
                nArqs += 1;
            }
        }
        return nArqs;
    }


    public String getNomePasta(String tipo) {
        if (tipo.isEmpty() || CVSStructure.chNomePasta.equals("N")) {
            return interfaces.getIdInterface();
        } else if (tipo.equals("IN")) {
            return interfaces.getIdSistema() + "_in_" + interfaces.getIdInterface();
        } else if (tipo.equals("OUT")) {
            return interfaces.getIdSistema() + "_out_" + interfaces.getIdInterface();
        }
        return "";
    }

    /**
     * @return the dirScriptsValida
     */
    public ArrayList getDirScriptsValida() {
        return dirScriptsValida;
    }

    /**
     * @return the tipoInterface
     */
    public String getTipoInterface() {
        return tipoInterface;
    }

    /**
     * @param tipoInterface the tipoInterface to set
     */
    public void setTipoInterface(String tipoInterface) {
        this.tipoInterface = tipoInterface;
    }

    /**
     * @return the interfaces
     */
    public Interface getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(Interface interfaces) {
        this.interfaces = interfaces;
    }
}
