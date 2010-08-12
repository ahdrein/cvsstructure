package cvsstructure.util;

import cvsstructure.util.PrepararConsultas;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author andrein
 */
    public class DataTableLayout {
        private String sTableNome;
        private String system;
        private PreparedStatement psConsulta;

        public DataTableLayout(String system, String sTableNome, PreparedStatement psConsulta){
            this.sTableNome = sTableNome;
            this.system = system;
            this.psConsulta = psConsulta;
        }

        public StringBuffer create() throws SQLException{
            PreparedStatement psUserTabColumns = null;
            if(system.equals("INOUT")){
                psUserTabColumns = ConnectionInout.getConnection().prepareStatement(PrepararConsultas.getUserTabColumns().toString());
            }else{
                psUserTabColumns = ConnectionIntegracao.getConnection().prepareStatement(PrepararConsultas.getUserTabColumns().toString());
            }

            ResultSet resultSet = psConsulta.executeQuery();

            StringBuffer insert = new StringBuffer();

            // loop dos registros
            while(resultSet.next()){
                insert.append("insert into ");
                insert.append(this.sTableNome);
                insert.append("\r\n(");

                StringBuffer bind = new StringBuffer();
                bind.append(" \r\n(");

                //loop dos campos
                boolean flag = true;
                psUserTabColumns.setString(1, this.sTableNome.toUpperCase());
                ResultSet resultSetUserTabColumns = psUserTabColumns.executeQuery();
                while(resultSetUserTabColumns.next()){
                    if(!flag){
                        insert.append(",");
                    }
                    insert.append(resultSetUserTabColumns.getString("column_name"));

                    if(!flag){
                        bind.append(",");
                    }
                    if((resultSetUserTabColumns.getString("TypeName").equals("VARCHAR2")
                            || resultSetUserTabColumns.getString("TypeName").equals("CHAR"))
                            && (resultSet.getString( resultSetUserTabColumns.getString("column_name")) != null)
                            ){
                        bind.append("'").append(resultSet.getString(resultSetUserTabColumns.getString("column_name"))).append( "'");
                    }else{
                        bind.append(resultSet.getString( resultSetUserTabColumns.getString("column_name") ));
                    }
                    flag = false;
                }
                //resultSetUserTabColumns.beforeFirst();
                insert.append(") \r\nvalues ").append(bind).append(");");
                insert.append("\r\n\r\n");

                resultSetUserTabColumns.close();
            }


            psUserTabColumns.close();

            return insert;
        }

        public String getSTableNome() {
            return sTableNome;
        }
    }