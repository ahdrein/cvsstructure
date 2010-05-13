/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.instalador.model;

import java.sql.PreparedStatement;
import sfw.structure.database.ConnectionSQLLite;

/**
 *
 * @author andrein
 */
public class InterfacesFactory {

    public void InterfacesFactory(){
        
    }

    public void inserir(LayoutInterfaces interfaces) throws Exception {
        PreparedStatement prep = ConnectionSQLLite.getConnection().prepareStatement(
        "insert into LayoutInterfaces (ordem, descInterface, objeto ) values (?, ?, ?);");

        prep.setLong(1,interfaces.getOrdem());
        prep.setString(2,interfaces.getDescInterface());
        prep.setString(3, interfaces.getObjeto());
        prep.addBatch();
        prep.executeBatch();
        prep.clearBatch();
        prep.clearParameters();
        ConnectionSQLLite.getConnection().commit();
        prep.close();
    }

    public void inserir(ObjetosAcessados objetosAcessados ) throws Exception {
        PreparedStatement prep = ConnectionSQLLite.getConnection().prepareStatement(
        "insert into LayoutInterfaces (ordem, descInterface, objeto ) values (?, ?, ?);");

        prep.setString(1,objetosAcessados.getObjeto());
        prep.setString(2,objetosAcessados.getObjetoAcessado());
        prep.addBatch();
        prep.executeBatch();
        prep.clearBatch();
        prep.clearParameters();
        ConnectionSQLLite.getConnection().commit();
        prep.close();
    }


}
