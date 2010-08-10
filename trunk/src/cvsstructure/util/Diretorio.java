/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.util;

import cvsstructure.model.Interface;
import cvsstructure.model.Cliente;
import cvsstructure.log.SfwLogger;
import cvsstructure.CVSStructure;
import java.io.File;
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

    public String getNomePasta(String tipo) {
        if (tipo.equals("") || CVSStructure.chNomePasta.equals("N")) {
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
