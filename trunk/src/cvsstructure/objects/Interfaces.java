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
public class Interfaces implements Runnable {

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

                SfwLogger.log("Creating or appending to file " + fileNameScripts);

                if (chConexaoPorArquivos.equals("S")) {
                    strOut.append("conn &&INOUT_USER/&&INOUT_PASS@&&TNS" + QUEBRA_LINHA + QUEBRA_LINHA);
                }
                strOut.append("--  ///////").append(QUEBRA_LINHA);
                strOut.append("--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT").append(QUEBRA_LINHA);
                strOut.append("--  ///////     Interface: ").append(interfaces.getDescricao()).append(QUEBRA_LINHA);
                strOut.append("--  ///////" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from PERMISSAO_TABELA where ID_INTERFACE = '").append(interfaces.getIdInterface()).append("';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("delete from SISTEMA_INTERFACE where ID_INTERFACE = '").append(interfaces.getIdInterface()).append("';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("begin").append(QUEBRA_LINHA);
                strOut.append("   insert into    INTERFACES").append(QUEBRA_LINHA);
                strOut.append("      (ID_INTERFACE,").append(QUEBRA_LINHA);
                strOut.append("      DESCRICAO,").append(QUEBRA_LINHA);
                strOut.append("      NOME_MAQUINA,").append(QUEBRA_LINHA);
                strOut.append("      EXECUTAVEL,").append(QUEBRA_LINHA);
                strOut.append("      USERNAME,").append(QUEBRA_LINHA);
                strOut.append("      TEMPO_MEDIO,").append(QUEBRA_LINHA);
                strOut.append("      TIPO_INTERFACE");

                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().isEmpty()) {
                    strOut.append("," + QUEBRA_LINHA + "     INTERFERE_PROC_DIR)").append(QUEBRA_LINHA);
                } else {
                    strOut.append(")").append(QUEBRA_LINHA);
                }

                strOut.append("      values").append(QUEBRA_LINHA);
                strOut.append("      ('").append(interfaces.getIdInterface()).append("',").append(QUEBRA_LINHA);
                strOut.append("      '").append(interfaces.getDescricao()).append("',").append(QUEBRA_LINHA);
                strOut.append("   	  '&&USER_MACHINE',").append(QUEBRA_LINHA);
                strOut.append("	  '").append(interfaces.getExecutavelCompl()).append("',").append(QUEBRA_LINHA);
                strOut.append("	  'ADM',").append(QUEBRA_LINHA);
                strOut.append("	  '0',").append(QUEBRA_LINHA);
                strOut.append("   '").append(interfaces.getTipoInterface()).append("'");
                if (interfaces.getInterfereProcessamentoDireto() != null && !interfaces.getInterfereProcessamentoDireto().isEmpty()) {
                    strOut.append(QUEBRA_LINHA);
                    strOut.append("      ,'").append(interfaces.getInterfereProcessamentoDireto()).append("'").append(QUEBRA_LINHA);
                }
                strOut.append(");").append(QUEBRA_LINHA);
                strOut.append("exception").append(QUEBRA_LINHA);
                strOut.append("    when dup_val_on_index then").append(QUEBRA_LINHA);
                strOut.append("        update INTERFACES").append(QUEBRA_LINHA);
                strOut.append("        set").append(QUEBRA_LINHA);
                strOut.append("                DESCRICAO = '").append(interfaces.getDescricao()).append("',").append(QUEBRA_LINHA);
                strOut.append("                NOME_MAQUINA = '&&USER_MACHINE',").append(QUEBRA_LINHA);
                strOut.append("                EXECUTAVEL = '").append(interfaces.getExecutavelCompl()).append("',").append(QUEBRA_LINHA);
                strOut.append("                USERNAME = 'ADM',").append(QUEBRA_LINHA);
                strOut.append("                TIPO_INTERFACE = '").append(interfaces.getTipoInterface()).append("'").append(QUEBRA_LINHA);
                strOut.append("        where id_interface  = '").append(interfaces.getIdInterface()).append("';").append(QUEBRA_LINHA);
                strOut.append("end;").append(QUEBRA_LINHA);
                strOut.append("/" + QUEBRA_LINHA + QUEBRA_LINHA);

                PreparedStatement psPermissaoTabela = cvsstructure.model.ArquivosExternos.getInstance().getPermissaoTabelaByIdInterface();
                psPermissaoTabela.setString(1, interfaces.getIdInterface().toUpperCase());
                strOut.append(new DataTableLayout("INOUT",
                        "permissao_tabela",
                        psPermissaoTabela).create());

                strOut.append("delete from INTERFACE_SAIDA where ID_INTERFACE =  '").append(interfaces.getIdInterface()).append("';" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('").append(interfaces.getIdInterface()).append("' , '").append(interfaces.getIdSistema().toUpperCase().equals("SFW") ? "BG" : interfaces.getIdSistema().toUpperCase()).append("');" + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("commit;" + QUEBRA_LINHA + QUEBRA_LINHA + QUEBRA_LINHA);
                strOut.append("-- //////").append(QUEBRA_LINHA);
                strOut.append("-- //////  FIM DO SCRIPT").append(QUEBRA_LINHA);
                strOut.append("-- //////");

                if (strOut != null && !strOut.toString().isEmpty()) {
                    fileScripts.saveArquivo(strOut);
                    SfwLogger.log("File interface " + fileNameScripts + " was succesfull generated.");
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
