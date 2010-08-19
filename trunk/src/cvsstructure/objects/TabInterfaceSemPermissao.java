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
 * <b>Exportar Tabelas sem vinculo com alguma interface</b>
 * @author ahdrein
 */
public class TabInterfaceSemPermissao implements Runnable {

    private Object[] selectInterfaces;

    public TabInterfaceSemPermissao() {
    }

    public void TabInterfaceSemPermissao(Object[] selectInterfaces) {
        this.selectInterfaces = selectInterfaces;
    }

    @Override
    public void run() {
        try {
            String sSelectTabsSemPermissao = "select table_name from tab_interface tab where tab.table_name not in (select table_name from permissao_tabela)";
            PreparedStatement psTabsSemPermissao = ConnectionInout.getConnection().prepareStatement(sSelectTabsSemPermissao);
            ResultSet rsTabsSemPermissao = psTabsSemPermissao.executeQuery();

            while (rsTabsSemPermissao.next()) {
                new TabInterfaces(rsTabsSemPermissao.getString("TABLE_NAME"),
                        this.selectInterfaces);
            }
        } catch (Exception ex) {
            //logMessage(ex.getLocalizedMessage());
            SfwLogger.debug(ex.getClass().toString(), ex.getStackTrace());
        }
    }
}
