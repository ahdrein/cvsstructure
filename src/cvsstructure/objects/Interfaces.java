/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import cvsstructure.CVSStructure;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.model.Interface;
import cvsstructure.util.Arquivo;
import cvsstructure.util.DataTableLayout;
import cvsstructure.util.Diretorio;
import cvsstructure.util.Estatisticas;

/**
 *
 * @author ahdrein
 */
public class Interfaces {
    private Interface interfaces;

    public Interfaces(Interface interfaces){
        this.interfaces = interfaces;
    }
    /**************************************************************************
     * <b>Gerar scripts das interfaces</b>
     **************************************************************************/
    public Interfaces() throws Exception {
        String fileNameScripts = null;
        String fileName = "interface.sql";
        try {
            if (interfaces.getTipoInterface().equals("S")) {
                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("OUT") + "\\INOUT\\Interface\\" + fileName;
            } else if (interfaces.getTipoInterface().equals("E")) {
                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + getNomePasta("IN") + "\\INOUT\\Interface\\" + fileName;
            } else {
                fileNameScripts = Diretorio.path + "\\" + Cliente.userNameSys + "\\Scripts\\" + interfaces.getIdInterface() + "\\INOUT\\Interface\\" + fileName;
            }

            // Criando arquivo na pasta de scripts
            Arquivo fileScripts = new Arquivo(fileNameScripts);
            if (!fileScripts.exists()) {


                StringBuilder strOut = new StringBuilder();

                //logMessage("Creating or appending to file " + fileNameScripts);

                if (CVSStructure.chConexaoPorArquivos.equals("S")) {
                    strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                }
                strOut.append("--  ///////" + CVSStructure.QUEBRA_LINHA);
                strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + CVSStructure.QUEBRA_LINHA);
                strOut.append("--  ///////     Interface: " + interfaces.getDescricao() + CVSStructure.QUEBRA_LINHA);
                strOut.append("--  ///////" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("delete from PERMISSAO_TABELA where ID_INTERFACE = '" + interfaces.getIdInterface() + "';" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("delete from SISTEMA_INTERFACE where ID_INTERFACE = '" + interfaces.getIdInterface() + "';" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("begin" + CVSStructure.QUEBRA_LINHA);
                strOut.append("   insert into    INTERFACES" + CVSStructure.QUEBRA_LINHA);
                strOut.append("      (ID_INTERFACE," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      DESCRICAO," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      NOME_MAQUINA," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      EXECUTAVEL," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      USERNAME," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      TEMPO_MEDIO," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      TIPO_INTERFACE");

                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().equals("")) {
                    strOut.append("," + CVSStructure.QUEBRA_LINHA + "     INTERFERE_PROC_DIR)" + CVSStructure.QUEBRA_LINHA);
                } else {
                    strOut.append(")" + CVSStructure.QUEBRA_LINHA);
                }

                strOut.append("      values" + CVSStructure.QUEBRA_LINHA);
                strOut.append("      ('" + interfaces.getIdInterface() + "'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("      '" + interfaces.getDescricao() + "'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("   	  '&&USER_MACHINE'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("	  '" + interfaces.getExecutavelCompl() + "'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("	  'ADM'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("	  '0'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("   '" + interfaces.getTipoInterface() + "'");
                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().equals("")) {
                    strOut.append(CVSStructure.QUEBRA_LINHA);
                    strOut.append("      ,'" + interfaces.getInterfereProcessamentoDireto() + "'" + CVSStructure.QUEBRA_LINHA);
                }
                strOut.append(");" + CVSStructure.QUEBRA_LINHA);
                strOut.append("exception" + CVSStructure.QUEBRA_LINHA);
                strOut.append("    when dup_val_on_index then" + CVSStructure.QUEBRA_LINHA);
                strOut.append("        update INTERFACES" + CVSStructure.QUEBRA_LINHA);
                strOut.append("        set" + CVSStructure.QUEBRA_LINHA);
                strOut.append("                DESCRICAO = '" + interfaces.getDescricao() + "'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("                NOME_MAQUINA = '&&USER_MACHINE'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("                EXECUTAVEL = '" + interfaces.getExecutavelCompl() + "'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("                USERNAME = 'ADM'," + CVSStructure.QUEBRA_LINHA);
                strOut.append("                TIPO_INTERFACE = '" + interfaces.getTipoInterface() + "'" + CVSStructure.QUEBRA_LINHA);
                strOut.append("        where id_interface  = '" + interfaces.getIdInterface() + "';" + CVSStructure.QUEBRA_LINHA);
                strOut.append("end;" + CVSStructure.QUEBRA_LINHA);
                strOut.append("/" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);

                strOut.append(new DataTableLayout("INOUT",
                        "permissao_tabela",
                        new cvsstructure.model.ArquivosExternos().getPermissaoTabelaByIdInterface()).create());

                strOut.append("delete from INTERFACE_SAIDA where ID_INTERFACE =  '" + interfaces.getIdInterface() + "';" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('" + interfaces.getIdInterface() + "' , '" + (interfaces.getIdSistema().toUpperCase().equals("SFW") ? "BG" : interfaces.getIdSistema().toUpperCase()) + "');" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("commit;" + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA + CVSStructure.QUEBRA_LINHA);
                strOut.append("-- //////" + CVSStructure.QUEBRA_LINHA);
                strOut.append("-- //////  FIM DO SCRIPT" + CVSStructure.QUEBRA_LINHA);
                strOut.append("-- //////");

                if (strOut != null && !strOut.toString().equals("")) {
                    fileScripts.saveArquivo(strOut);
                }

                Estatisticas.nTotalInterfaces++;
            }
        } catch (Exception ex) {
            //logMessage("Error generating " + fileNameScripts);
            //logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
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
}
