/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.objects;

import static cvsstructure.CVSStructure.QUEBRA_LINHA;
import static cvsstructure.CVSStructure.chConexaoPorArquivos;
import static cvsstructure.CVSStructure.chNomePasta;
import cvsstructure.log.SfwLogger;
import cvsstructure.model.Cliente;
import cvsstructure.model.Interface;
import cvsstructure.util.CvsStructureFile;
import cvsstructure.util.DataTableLayout;
import cvsstructure.util.Diretorio;
import cvsstructure.util.Estatisticas;
import java.sql.PreparedStatement;

/**
 *
 * @author ahdrein
 */
public class Interfaces extends Thread {

    private Interface interfaces;

    public Interfaces() {
    }

    public Interfaces(Interface interfaces) {
        this.interfaces = interfaces;
    }

    /**************************************************************************
     * <b>Gerar scripts das interfaces</b>
     **************************************************************************/
    @Override
    public void run() {
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
            CvsStructureFile fileScripts = new CvsStructureFile(fileNameScripts);
            if (!fileScripts.exists()) {
                StringBuilder strOut = new StringBuilder();

                //logMessage("Creating or appending to file " + fileNameScripts);

                if (chConexaoPorArquivos.equals("S")) {
                    strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                }
                strOut.append("--  ///////" + QUEBRA_LINHA);
                strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT" + QUEBRA_LINHA);
                strOut.append("--  ///////     Interface: " + interfaces.getDescricao() + QUEBRA_LINHA);
                strOut.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from PERMISSAO_TABELA where ID_INTERFACE = '" + interfaces.getIdInterface() + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from SISTEMA_INTERFACE where ID_INTERFACE = '" + interfaces.getIdInterface() + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("begin" + QUEBRA_LINHA);
                strOut.append("   insert into    INTERFACES" + QUEBRA_LINHA);
                strOut.append("      (ID_INTERFACE," + QUEBRA_LINHA);
                strOut.append("      DESCRICAO," + QUEBRA_LINHA);
                strOut.append("      NOME_MAQUINA," + QUEBRA_LINHA);
                strOut.append("      EXECUTAVEL," + QUEBRA_LINHA);
                strOut.append("      USERNAME," + QUEBRA_LINHA);
                strOut.append("      TEMPO_MEDIO," + QUEBRA_LINHA);
                strOut.append("      TIPO_INTERFACE");

                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().equals("")) {
                    strOut.append("," + QUEBRA_LINHA + "     INTERFERE_PROC_DIR)" + QUEBRA_LINHA);
                } else {
                    strOut.append(")" + QUEBRA_LINHA);
                }

                strOut.append("      values" + QUEBRA_LINHA);
                strOut.append("      ('" + interfaces.getIdInterface() + "'," + QUEBRA_LINHA);
                strOut.append("      '" + interfaces.getDescricao() + "'," + QUEBRA_LINHA);
                strOut.append("   	  '&&USER_MACHINE'," + QUEBRA_LINHA);
                strOut.append("	  '" + interfaces.getExecutavelCompl() + "'," + QUEBRA_LINHA);
                strOut.append("	  'ADM'," + QUEBRA_LINHA);
                strOut.append("	  '0'," + QUEBRA_LINHA);
                strOut.append("   '" + interfaces.getTipoInterface() + "'");
                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().equals("")) {
                    strOut.append(QUEBRA_LINHA);
                    strOut.append("      ,'" + interfaces.getInterfereProcessamentoDireto() + "'" + QUEBRA_LINHA);
                }
                strOut.append(");" + QUEBRA_LINHA);
                strOut.append("exception" + QUEBRA_LINHA);
                strOut.append("    when dup_val_on_index then" + QUEBRA_LINHA);
                strOut.append("        update INTERFACES" + QUEBRA_LINHA);
                strOut.append("        set" + QUEBRA_LINHA);
                strOut.append("                DESCRICAO = '" + interfaces.getDescricao() + "'," + QUEBRA_LINHA);
                strOut.append("                NOME_MAQUINA = '&&USER_MACHINE'," + QUEBRA_LINHA);
                strOut.append("                EXECUTAVEL = '" + interfaces.getExecutavelCompl() + "'," + QUEBRA_LINHA);
                strOut.append("                USERNAME = 'ADM'," + QUEBRA_LINHA);
                strOut.append("                TIPO_INTERFACE = '" + interfaces.getTipoInterface() + "'" + QUEBRA_LINHA);
                strOut.append("        where id_interface  = '" + interfaces.getIdInterface() + "';" + QUEBRA_LINHA);
                strOut.append("end;" + QUEBRA_LINHA);
                strOut.append("/" + QUEBRA_LINHA + QUEBRA_LINHA);

                PreparedStatement psPermissaoTabela = cvsstructure.model.ArquivosExternos.getInstance().getPermissaoTabelaByIdInterface();
                psPermissaoTabela.setString(1, interfaces.getIdInterface());
                strOut.append(new DataTableLayout("INOUT",
                        "permissao_tabela",
                        psPermissaoTabela).create());

                strOut.append("delete from INTERFACE_SAIDA where ID_INTERFACE =  '" + interfaces.getIdInterface() + "';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('" + interfaces.getIdInterface() + "' , '" + (interfaces.getIdSistema().toUpperCase().equals("SFW") ? "BG" : interfaces.getIdSistema().toUpperCase()) + "');" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("commit;" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("-- //////" + QUEBRA_LINHA);
                strOut.append("-- //////  FIM DO SCRIPT" + QUEBRA_LINHA);
                strOut.append("-- //////");

                if (strOut != null && !strOut.toString().equals("")) {
                    fileScripts.saveArquivo(strOut);
                }

                Estatisticas.nTotalInterfaces++;
            }
        } catch (Exception ex) {
            SfwLogger.log("Error generating " + fileNameScripts);
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
