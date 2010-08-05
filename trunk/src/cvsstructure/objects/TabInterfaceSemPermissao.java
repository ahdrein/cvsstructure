/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cvsstructure.objects;

import cvsstructure.database.ConnectionInout;
import cvsstructure.log.SfwLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ahdrein
 */
public class TabInterfaceSemPermissao {
    /**************************************************************************
     * <b>Exportar Tabelas sem vinculo com alguma interface</b>
     **************************************************************************/
    private void TabInterfaceSemPermissao(Object[] selectInterfaces) {
        try {
            String sSelectTabsSemPermissao = "select table_name from tab_interface tab where tab.table_name not in (select table_name from permissao_tabela)";
            PreparedStatement psTabsSemPermissao = ConnectionInout.getConnection().prepareStatement(sSelectTabsSemPermissao);
            ResultSet rsTabsSemPermissao = psTabsSemPermissao.executeQuery();
       
            while (rsTabsSemPermissao.next()) {
                new TabInterfaces(rsTabsSemPermissao.getString("TABLE_NAME"),
                                  selectInterfaces);
            }
        } catch (Exception ex) {
            //logMessage(ex.getLocalizedMessage());
            SfwLogger.saveLog(ex.getClass().toString(), ex.getStackTrace());
        }
    }

}
