package cvsstructure.excel;

import cvsstructure.util.SFWStringUtils;
import cvsstructure.database.ConnectionInout;
import cvsstructure.database.ConnectionIntegracao;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcel {

    private String inputFile;
    private String tag;

    public void setTagExcel(String tag) {
        this.tag = tag;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void read() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            for (int j = 0; j < sheet.getColumns(); j++) {
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell cell = sheet.getCell(j, i);
                    CellType type = cell.getType();
                    if (cell.getType() == CellType.LABEL) {
                        System.out.println("I got a label "
                                + cell.getContents());
                    }

                    if (cell.getType() == CellType.NUMBER) {
                        System.out.println("I got a number "
                                + cell.getContents());
                    }

                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public String[] readTagsOfExcel() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        String[] sheets = null;
        try {
            w = Workbook.getWorkbook(inputWorkbook);

            sheets = w.getSheetNames();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return sheets;
    }

    public void gerarArquivoTXT() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        StringBuffer strOutScripts = new StringBuffer();
        File fileScripts;
        FileWriter fwScripts;
        String nomeArquivo = "";
        String nomeArquivoNovo = "";
        boolean flag = false;
        boolean flagCampos = false;
        int i = 33;
        int e = 1;
        char[] tabela = new char[128];
        char[] tabelaNumero = new char[128];
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            System.out.println("Numero de linha: " + sheet.getColumns());
            //for (int j = 0; j < sheet.getColumns(); j++) {
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        Cell cellTabela = sheet.getCell(1, j);
                        System.out.println("Tabela " + cellTabela.getContents());
                    }

                    // Arquivo a ser gerado
                    Cell cellNomeArquivo = null;
                    if (cellCampo.getContents().toString().equals("Arquivo Texto")) {
                        cellNomeArquivo = sheet.getCell(1, j);
                        System.out.println("Arquivo " + cellNomeArquivo.getContents());
                        nomeArquivoNovo = cellNomeArquivo.getContents().toString().trim();
                        if (nomeArquivo.equals("")) {
                            nomeArquivo = cellNomeArquivo.getContents().toString().trim();
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {
                        flag = true;
                        flagCampos = false;
                    }

                    // Buscando campo do excel
                    if (flagCampos) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        Cell cellTipo = sheet.getCell(1, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            if (cellTamanho.getType() == CellType.NUMBER) {

                                //
                                // Caso for ENTER ou TAB
                                if (i == 10 || i == 13) {
                                    i = i + 1;
                                }

                                // Exemplo
                                //sNfeSefazNewChNfe = SFWStringUtils.rpad(NfeSefazNewChNfe, " ", 44);
                                System.out.println("Campo " + cellCampo.getContents() + " Tam." + cellTamanho.getContents() + " Tipo: " + cellTipo.getContents());

                                //strOutScripts.append( SFWStringUtils.rpad(cellCampo.getContents().toString().trim(), " ", Integer.parseInt( cellTamanho.getContents().toString().trim() )) );
                                // Tipo AlpahNumérico
                                if (cellTipo.getContents().toString().trim().equals("A")) {
                                    tabela[i] = (char) i;

                                    strOutScripts.append(SFWStringUtils.rpad(String.valueOf(tabela[i]), String.valueOf(tabela[i]), Integer.parseInt(cellTamanho.getContents().toString().trim())));

                                    if (i >= 128) {
                                        i = 33;
                                    } else {
                                        i = i + 1;
                                    }
                                    // Tipo Numérico
                                } else {
                                    tabelaNumero[e] = Character.forDigit(e, 10);
                                    if (e >= 9) {
                                        e = 1;
                                    } else {
                                        e = e + 1;
                                    }

                                    strOutScripts.append(SFWStringUtils.rpad(String.valueOf(tabelaNumero[e]), String.valueOf(tabelaNumero[e]), Integer.parseInt(cellTamanho.getContents().toString().trim())));
                                }
                            } else {
                                System.out.println("O Tamanho do Campo:" + cellCampo.getContents() + " não está como númérico");
                                //strOutScripts.append( "Campo:" + cellCampo.getContents() + " não é númérico" );
                            }
                        } else {
                            System.out.println("O Tamanho do campo:" + cellCampo.getContents() + " está vazio");
                            //strOutScripts.append( "O Tamanho do campo:" + cellCampo.getContents() + " está vazio" );
                        }
                    }

                    // Gerando arquivo
                    if (flag) {
                        fileScripts = new File("c:\\" + nomeArquivoNovo);
                        if (!fileScripts.exists()) {
                            fileScripts.createNewFile();

                            fwScripts = new FileWriter(fileScripts, false);
                            nomeArquivo = nomeArquivoNovo;

                            if (strOutScripts != null) {
                                fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                            }
                            fwScripts.close();
                        }
                        strOutScripts = new StringBuffer();
                        if (i >= 128) {
                            i = 33;
                        }
                        flag = false;
                    }
                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    fileScripts = new File("c:\\" + nomeArquivoNovo);
                    if (!fileScripts.exists()) {
                        fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        nomeArquivo = nomeArquivoNovo;

                        if (strOutScripts != null) {
                            fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        }
                        fwScripts.close();
                    }
                    //strOutScripts = new StringBuffer();
                    if (i >= 128) {
                        i = 0;
                    }
                    j = 500;
                    System.out.println("Final");
                    break;
                }
            }



        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void gerarArquivoXML() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        StringBuffer strOutScripts = new StringBuffer();
        File fileScripts;
        FileWriter fwScripts;
        String nomeArquivo = "";
        String nomeArquivoNovo = "";
        boolean flag = false;
        boolean flagCampos = false;
        int i = 33;
        int e = 1;
        char[] tabela = new char[128];
        char[] tabelaNumero = new char[128];
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            System.out.println("Numero de linha: " + sheet.getColumns());
            //for (int j = 0; j < sheet.getColumns(); j++) {
            int contador = 0;
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        Cell cellTabela = sheet.getCell(1, j);
                        System.out.println("Tabela " + cellTabela.getContents());
                    }

                    // Arquivo a ser gerado
                    Cell cellNomeArquivo = null;
                    if (cellCampo.getContents().toString().equals("Arquivo Texto")) {
                        cellNomeArquivo = sheet.getCell(1, j);
                        System.out.println("Arquivo " + cellNomeArquivo.getContents());
                        nomeArquivoNovo = cellNomeArquivo.getContents().toString().trim();
                        if (nomeArquivo.equals("")) {
                            nomeArquivo = cellNomeArquivo.getContents().toString().trim();
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {
                        flag = true;
                        flagCampos = false;
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        Cell cellTipo = sheet.getCell(1, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            if (cellTamanho.getType() == CellType.NUMBER) {
                                contador += 1;
                                //
                                // Caso for ENTER ou TAB
                                if (i == 10 || i == 13) {
                                    i = i + 1;
                                }

                                // Exemplo
                                //sNfeSefazNewChNfe = SFWStringUtils.rpad(NfeSefazNewChNfe, " ", 44);
                                System.out.println("Campo " + cellCampo.getContents() + " Tam." + cellTamanho.getContents() + " Tipo: " + cellTipo.getContents());

                                //strOutScripts.append( SFWStringUtils.rpad(cellCampo.getContents().toString().trim(), " ", Integer.parseInt( cellTamanho.getContents().toString().trim() )) );
                                // Tipo AlpahNumérico
                                if (cellTipo.getContents().toString().trim().equals("A")) {
                                    tabela[i] = (char) i;

                                    strOutScripts.append("<" + cellCampo.getContents() + ">");
                                    strOutScripts.append(SFWStringUtils.rpad(String.valueOf(tabela[i]), String.valueOf(tabela[i]), Integer.parseInt(cellTamanho.getContents().toString().trim())));
                                    strOutScripts.append("<" + cellCampo.getContents() + "/>");
                                    strOutScripts.append("\r\n");

                                    if (i >= 128) {
                                        i = 33;
                                    }
                                    i = i + 1;
                                    // Tipo Numérico
                                } else {
                                    tabelaNumero[e] = Character.forDigit(e, 10);
                                    if (e >= 9) {
                                        e = 1;
                                    }
                                    e = e + 1;

                                    strOutScripts.append("<" + cellCampo.getContents() + ">");
                                    strOutScripts.append(SFWStringUtils.rpad(String.valueOf(tabelaNumero[e]), String.valueOf(tabelaNumero[e]), Integer.parseInt(cellTamanho.getContents().toString().trim())));
                                    strOutScripts.append("<" + cellCampo.getContents() + "/>");
                                    strOutScripts.append("\r\n");
                                }
                            } else {
                                System.out.println("O Tamanho do Campo:" + cellCampo.getContents() + " não está como númérico");
                                //strOutScripts.append( "Campo:" + cellCampo.getContents() + " não é númérico" );
                            }
                        } else {
                            System.out.println("O Tamanho do campo:" + cellCampo.getContents() + " está vazio");
                            //strOutScripts.append( "O Tamanho do campo:" + cellCampo.getContents() + " está vazio" );
                        }
                    }

                    // Gerando arquivo
                    if (flag) {
                        fileScripts = new File("c:\\" + nomeArquivoNovo);
                        if (!fileScripts.exists()) {
                            fileScripts.createNewFile();

                            fwScripts = new FileWriter(fileScripts, false);
                            nomeArquivo = nomeArquivoNovo;

                            if (strOutScripts != null) {
                                fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                            }
                            fwScripts.close();
                        }
                        strOutScripts = new StringBuffer();
                        if (i >= 128) {
                            i = 33;
                        }
                        flag = false;
                    }
                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    fileScripts = new File("c:\\" + nomeArquivoNovo);
                    if (!fileScripts.exists()) {
                        fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        nomeArquivo = nomeArquivoNovo;

                        if (strOutScripts != null) {
                            fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        }
                        fwScripts.close();
                    }
                    //strOutScripts = new StringBuffer();
                    if (i >= 128) {
                        i = 0;
                    }
                    j = 500;
                    System.out.println("Final");
                    break;
                }
            }

        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void gerarScriptsTabelasTMP() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        StringBuffer strOutScripts = new StringBuffer();
        File fileScripts;
        FileWriter fwScripts;
        String nomeArquivo = "";
        String nomeArquivoNovo = "";
        boolean flag = false;
        boolean flagCampos = false;
        int contador = 0;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            System.out.println("Numero de linha: " + sheet.getColumns());
            //for (int j = 0; j < sheet.getColumns(); j++) {
            String sTabela = "";
            String sTabelaNova = "";
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        //flag = true;
                        Cell cellTabela = sheet.getCell(1, j);
                        sTabelaNova = cellTabela.getContents().toString().trim();
                        if (sTabela.equals("")) {
                            sTabela = cellTabela.getContents().toString().trim();
                        }
                        System.out.println("Tabela " + cellTabela.getContents());

                        strOutScripts.append("insert into tab_interface (table_name, ctl_name, prefix_file, gerar_ctl, prioridade, tipo_interface, eliminar_reg_execucao, id_sistema, procedure_name ) ");
                        strOutScripts.append("values ('" + sTabela + "', '" + sTabela + "', 'IM', 'S', '0', 'LOADER', 'N', 'BG', '' );");
                        strOutScripts.append("\r\n");
                    }

                    // Arquivo a ser gerado
                    Cell cellNomeArquivo = null;
                    if (cellCampo.getContents().toString().equals("Arquivo Texto")) {
                        cellNomeArquivo = sheet.getCell(1, j);
                        System.out.println("Arquivo " + cellNomeArquivo.getContents());
                        nomeArquivoNovo = cellNomeArquivo.getContents().toString().trim();
                        if (nomeArquivo.equals("")) {
                            nomeArquivo = cellNomeArquivo.getContents().toString().trim();
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {
                        flag = true;
                        flagCampos = false;
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        Cell cellTipo = sheet.getCell(1, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            if (cellTamanho.getType() == CellType.NUMBER) {
                                contador += 1;
                                // Exemplo
                                //sNfeSefazNewChNfe = SFWStringUtils.rpad(NfeSefazNewChNfe, " ", 44);
                                System.out.println("Campo " + cellCampo.getContents() + " Tam." + cellTamanho.getContents() + " Tipo: " + cellTipo.getContents());

                                strOutScripts.append("insert into colunas_tab_interface (table_name, column_name, tipo_loader, tamanho, ordem, arg_name, descricao) ");
                                strOutScripts.append("values ('" + sTabela + "', '" + cellCampo.getContents().toUpperCase().toString() + "', 'CHAR', '" + cellTamanho.getContents().toString() + "', '" + contador + "', '', '' );");
                                strOutScripts.append("\r\n");

                            } else {
                                System.out.println("O Tamanho do Campo:" + cellCampo.getContents() + " não está como númérico");
                                //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                            }
                        } else {
                            System.out.println("O Tamanho do campo:" + cellCampo.getContents() + " está vazio");
                            //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                        }
                    }

                    // Gerando arquivo
                    if (flag) {
                        fileScripts = new File("c:\\" + sTabelaNova + ".sql");
                        if (!fileScripts.exists()) {
                            fileScripts.createNewFile();

                            fwScripts = new FileWriter(fileScripts, false);
                            sTabela = sTabelaNova;

                            if (strOutScripts != null) {
                                fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                            }
                            fwScripts.close();
                        }
                        strOutScripts = new StringBuffer();
                        flag = false;
                    }

                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    fileScripts = new File("c:\\" + sTabela + ".sql");
                    if (!fileScripts.exists()) {
                        fileScripts.createNewFile();

                        fwScripts = new FileWriter(fileScripts, false);
                        sTabela = sTabelaNova;

                        if (strOutScripts != null) {
                            fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                        }
                        fwScripts.close();
                    }
                    strOutScripts = new StringBuffer();
                    j = 600;
                    System.out.println("Final");

                }
            }



        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void corrigirCampos() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        StringBuffer strOutScripts = new StringBuffer();
        File fileScripts;
        FileWriter fwScripts;
        String nomeArquivo = "";
        String nomeArquivoNovo = "";
        boolean flag = false;
        boolean flagCampos = false;
        int contador = 0;

        PreparedStatement psSqlInterface;
        ResultSet rsSqlInterface;

        PreparedStatement psSqlCountInterface;
        ResultSet rsSqlCountInterface;

        PreparedStatement psSqlFieldsPackage;
        ResultSet rsSqlFieldPackage;

        PreparedStatement psSqlCountFieldPackage;
        ResultSet rsSqlCountFieldPackage;


        PreparedStatement update_pstmt;

        try {

            //String conn = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.61.9)(PORT=1510)))(CONNECT_DATA=(SERVICE_NAME=DESENV10)))";
            String conn = "192.168.61.9";
            String user = "sfwio2010";
            String pass = "sfwio2010";
            String service = "desenv10.sfw.com.br";
            String port = "1510";

            ConnectionInout.initialize(conn, user, pass, port, service);

            StringBuffer sbUpdateColumns = new StringBuffer();
            sbUpdateColumns.append("update colunas_tab_interface set tamanho = ? and ordem = ? where table_name = ? and column_name = ?");
            //update_notif_pstmt = conn_sw.prepareStatement("UPDATE OIF_EXPORT SET STATUS=? WHERE ID_TRANSACAO = ?");
            update_pstmt = ConnectionInout.getConnection().prepareStatement(sbUpdateColumns.toString());

            StringBuffer sbSqlInterface = new StringBuffer();
            sbSqlInterface.append("select * from colunas_tab_interface where table_name = ? and column_name = ?");
            psSqlInterface = ConnectionInout.getConnection().prepareStatement(sbSqlInterface.toString());

            StringBuffer sbSqlCountInterface = new StringBuffer();
            sbSqlCountInterface.append("select count(*) TOTAL from colunas_tab_interface where table_name = ?");
            psSqlCountInterface = ConnectionInout.getConnection().prepareStatement(sbSqlCountInterface.toString());

            StringBuffer sbSqlFieldsPackage = new StringBuffer();
            //sbSqlFieldsPackage.append("select argument_name from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=? and argument_name like '%' || ? ");
            sbSqlFieldsPackage.append("select argument_name from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=? and argument_name= 'PI_' || ? ");
            psSqlFieldsPackage = ConnectionInout.getConnection().prepareStatement(sbSqlFieldsPackage.toString());

            StringBuffer sbSqlCountFieldsPackage = new StringBuffer();
            sbSqlCountFieldsPackage.append("select count(argument_name) TOTAL from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=?");
            psSqlCountFieldPackage = ConnectionInout.getConnection().prepareStatement(sbSqlCountFieldsPackage.toString());

            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            strOutScripts.append("Numero de linha: " + sheet.getColumns() + "\r\n");
            //for (int j = 0; j < sheet.getColumns(); j++) {
            String sTabela = "";
            String sTabelaNova = "";
            String sPackage = "";
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        //flag = true;
                        Cell cellTabela = sheet.getCell(1, j);
                        sTabelaNova = cellTabela.getContents().toString().trim();
                        if (sTabela.equals("")) {
                            sTabela = cellTabela.getContents().toString().trim();
                        }
                        strOutScripts.append("Tabela " + cellTabela.getContents() + "\r\n");

                    }

                    if (cellCampo.getContents().toString().equals("Package")) {
                        //flag = true;
                        Cell cellPackage = sheet.getCell(1, j);
                        sPackage = cellPackage.getContents().toString().trim();
                        //sPackage = sPackage.substring(sPackage.indexOf("."), sPackage.length());
                        strOutScripts.append("Package " + cellPackage.getContents() + "\r\n");

                    }

                    // Arquivo a ser gerado
                    Cell cellNomeArquivo = null;
                    if (cellCampo.getContents().toString().equals("Arquivo Texto")) {
                        cellNomeArquivo = sheet.getCell(1, j);
                        strOutScripts.append("Arquivo " + cellNomeArquivo.getContents() + "\r\n");
                        nomeArquivoNovo = cellNomeArquivo.getContents().toString().trim();
                        if (nomeArquivo.equals("")) {
                            nomeArquivo = cellNomeArquivo.getContents().toString().trim();
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {

                        psSqlCountInterface.setString(1, sTabelaNova);
                        rsSqlCountInterface = psSqlCountInterface.executeQuery();
                        rsSqlCountInterface.next();


                        psSqlCountFieldPackage.setString(1, sPackage.substring(sPackage.indexOf(".") + 1, sPackage.length()));
                        rsSqlCountFieldPackage = psSqlCountFieldPackage.executeQuery();
                        rsSqlCountFieldPackage.next();

                        flag = true;
                        flagCampos = false;
                        strOutScripts.append("*** Total Campos Excel: " + sTabelaNova + " = " + contador + "\r\n");
                        strOutScripts.append("*** Total Campos InOut: " + sTabelaNova + " = " + rsSqlCountInterface.getString("TOTAL") + "\r\n");
                        strOutScripts.append("*** Total Campos Package: " + sPackage + " = " + rsSqlCountFieldPackage.getString("TOTAL") + "\r\n");
                        strOutScripts.append("\r\n");
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        Cell cellTipo = sheet.getCell(1, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            if (cellTamanho.getType() == CellType.NUMBER) {
                                contador += 1;
                                // Exemplo
                                //sNfeSefazNewChNfe = SFWStringUtils.rpad(NfeSefazNewChNfe, " ", 44);

                                //System.out.println("Campo " + cellCampo.getContents() + " Tam." + cellTamanho.getContents() + " Tipo: " + cellTipo.getContents());

                                psSqlInterface.setString(1, sTabelaNova);
                                psSqlInterface.setString(2, cellCampo.getContents().toUpperCase().trim());
                                rsSqlInterface = psSqlInterface.executeQuery();
                                rsSqlInterface.next();

                                try {
                                    if (rsSqlInterface.getString("TABLE_NAME") != null && !rsSqlInterface.getString("TABLE_NAME").equals("")) {



                                        //while(rsSqlInterface.next()){
                                        if (!rsSqlInterface.getString("TAMANHO").equals(cellTamanho.getContents().trim())) {
                                            strOutScripts.append("Tam. Campo " + cellCampo.getContents() + " não confere. Tam. EXCEL: " + cellTamanho.getContents() + " Tam. INOUT: " + rsSqlInterface.getString("TAMANHO") + "\r\n");

                                            update_pstmt.setLong(1, Integer.valueOf(cellTamanho.getContents().trim()));
                                            update_pstmt.setLong(2, Integer.valueOf(rsSqlInterface.getString("ORDEM")));
                                            update_pstmt.setString(3, rsSqlInterface.getString("TABLE_NAME"));
                                            update_pstmt.setString(4, rsSqlInterface.getString("COLUMN_NAME"));
                                            update_pstmt.executeUpdate();
                                        }

                                        if (!rsSqlInterface.getString("ORDEM").equals(String.valueOf(contador))) {
                                            strOutScripts.append("Ordem Campo " + cellCampo.getContents() + " não confere. Ordem EXCEL: " + contador + " Ordem. INOUT: " + rsSqlInterface.getString("ORDEM") + "\r\n");

                                            update_pstmt.setLong(1, Integer.valueOf(rsSqlInterface.getString("TAMANHO")));
                                            update_pstmt.setLong(2, contador);
                                            update_pstmt.setString(3, rsSqlInterface.getString("TABLE_NAME"));
                                            update_pstmt.setString(4, rsSqlInterface.getString("COLUMN_NAME"));
                                            update_pstmt.executeUpdate();
                                        }

                                        //try{
                                        //    psSqlFieldsPackage.setString(1, sPackage.substring(sPackage.indexOf(".")+1, sPackage.length()));
                                        //    psSqlFieldsPackage.setString(2, cellCampo.getContents().toUpperCase().trim());
                                        //    rsSqlFieldPackage = psSqlFieldsPackage.executeQuery();
                                        //    rsSqlFieldPackage.next();

                                        //    if(rsSqlFieldPackage.getString("ARGUMENT_NAME") == null || rsSqlFieldPackage.getString("ARGUMENT_NAME").equals("")){
                                        //        strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. Ordem EXCEL: Ordem. INOUT: \r\n");
                                        //    }else if(!rsSqlFieldPackage.getString("ARGUMENT_NAME").equals(rsSqlInterface.getString("COLUMN_NAME"))){
                                        //        strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. Ordem EXCEL: Ordem. INOUT: \r\n");
                                        //    }
                                        //}catch(Exception ex){
                                        //    strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. \r\n");
                                        //ex.printStackTrace();
                                        //}
                                    } else {
                                        strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no InOut" + "\r\n");
                                    }
                                } catch (Exception ex) {
                                    strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no InOut" + "\r\n");
                                }

                            } else {
                                strOutScripts.append("O Tamanho do Campo:" + cellCampo.getContents() + " não está como númérico" + "\r\n");
                                //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                            }
                        } else {
                            strOutScripts.append("O Tamanho do campo:" + cellCampo.getContents() + " está vazio" + "\r\n");
                            //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                        }
                    }

                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }

            // Gerando arquivo
            fileScripts = new File("c:\\" + this.tag + ".sql");
            if (!fileScripts.exists()) {
                fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);
                sTabela = sTabelaNova;

                if (strOutScripts != null) {
                    fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                }
                fwScripts.close();
            }
            strOutScripts = new StringBuffer();
            flag = false;

        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void comparaCamposTabColumn() throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        StringBuffer strOutScripts = new StringBuffer();
        File fileScripts;
        FileWriter fwScripts;
        String nomeArquivo = "";
        String nomeArquivoNovo = "";
        boolean flag = false;
        boolean flagCampos = false;
        int contador = 0;

        PreparedStatement psSqlInterface;
        ResultSet rsSqlInterface;

        PreparedStatement psSqlInterfaceInt;
        ResultSet rsSqlInterfaceInt;

        PreparedStatement psSqlCountInterface;
        ResultSet rsSqlCountInterface;

        PreparedStatement psSqlCountInterfaceInt;
        ResultSet rsSqlCountInterfaceInt;

        PreparedStatement psSqlFieldsPackage;
        ResultSet rsSqlFieldPackage;

        PreparedStatement psSqlCountFieldPackage;
        ResultSet rsSqlCountFieldPackage;
        try {

            //String conn = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.61.9)(PORT=1510)))(CONNECT_DATA=(SERVICE_NAME=DESENV10)))";
            String conn = "192.168.61.9";
            String user = "sfwio2010";
            String pass = "sfwio2010";
            String service = "desenv10.sfw.com.br";
            String port = "1510";

            ConnectionInout.initialize(conn, user, pass, port, service);

            ConnectionIntegracao.initialize("192.168.61.9", "sfwit2010", "sfwit2010", "1510", "desenv10.sfw.com.br");

            StringBuffer sbSqlInterfaceInt = new StringBuffer();
            sbSqlInterfaceInt.append("select * from int_mapeamento_coluna where id = ( select id from int_mapeamento_layout where layout = ? ) and layout_coluna = ?");
            psSqlInterfaceInt = ConnectionIntegracao.getConnection().prepareStatement(sbSqlInterfaceInt.toString());

            StringBuffer sbSqlCountInterfaceInt = new StringBuffer();
            sbSqlCountInterfaceInt.append("select count(*) TOTAL from int_mapeamento_coluna where id = ( select id from int_mapeamento_layout where layout = ? )");
            psSqlCountInterfaceInt = ConnectionIntegracao.getConnection().prepareStatement(sbSqlCountInterfaceInt.toString());


            StringBuffer sbSqlInterface = new StringBuffer();
            sbSqlInterface.append("select tb.procedure_name, ctb.* from colunas_tab_interface ctb, tab_interface tb where ctb.table_name = ? and ctb.column_name = ? and tb.table_name = ctb.table_name");
            psSqlInterface = ConnectionInout.getConnection().prepareStatement(sbSqlInterface.toString());

            StringBuffer sbSqlCountInterface = new StringBuffer();
            sbSqlCountInterface.append("select count(*) TOTAL from colunas_tab_interface where table_name = ?");
            psSqlCountInterface = ConnectionInout.getConnection().prepareStatement(sbSqlCountInterface.toString());


            StringBuffer sbSqlFieldsPackage = new StringBuffer();
            //sbSqlFieldsPackage.append("select argument_name from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=? and argument_name like '%' || ? ");
            sbSqlFieldsPackage.append("select argument_name from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=? and argument_name= ? ");
            psSqlFieldsPackage = ConnectionInout.getConnection().prepareStatement(sbSqlFieldsPackage.toString());

            StringBuffer sbSqlCountFieldsPackage = new StringBuffer();
            sbSqlCountFieldsPackage.append("select count(argument_name) TOTAL from ALL_ARGUMENTS where owner='SFWES2010' and OBJECT_NAME=?");
            psSqlCountFieldPackage = ConnectionInout.getConnection().prepareStatement(sbSqlCountFieldsPackage.toString());

            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            strOutScripts.append("Numero de linha: " + sheet.getColumns() + "\r\n");
            //for (int j = 0; j < sheet.getColumns(); j++) {
            String sTabela = "";
            String sTabelaNova = "";
            String sPackage = "";
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        //flag = true;
                        Cell cellTabela = sheet.getCell(1, j);
                        sTabelaNova = cellTabela.getContents().toString().trim();
                        if (sTabela.equals("")) {
                            sTabela = cellTabela.getContents().toString().trim();
                        }
                        strOutScripts.append("Tabela " + cellTabela.getContents() + "\r\n");

                    }

                    if (cellCampo.getContents().toString().equals("Package")) {
                        //flag = true;
                        Cell cellPackage = sheet.getCell(1, j);
                        sPackage = cellPackage.getContents().toString().trim();
                        //sPackage = sPackage.substring(sPackage.indexOf("."), sPackage.length());
                        strOutScripts.append("Package " + cellPackage.getContents() + "\r\n");

                    }

                    // Arquivo a ser gerado
                    Cell cellNomeArquivo = null;
                    if (cellCampo.getContents().toString().equals("Arquivo Texto")) {
                        cellNomeArquivo = sheet.getCell(1, j);
                        strOutScripts.append("Arquivo " + cellNomeArquivo.getContents() + "\r\n");
                        nomeArquivoNovo = cellNomeArquivo.getContents().toString().trim();
                        if (nomeArquivo.equals("")) {
                            nomeArquivo = cellNomeArquivo.getContents().toString().trim();
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revis")) {

                        psSqlCountInterface.setString(1, sTabelaNova);
                        rsSqlCountInterface = psSqlCountInterface.executeQuery();
                        rsSqlCountInterface.next();

                        psSqlCountInterfaceInt.setString(1, sTabelaNova);
                        rsSqlCountInterfaceInt = psSqlCountInterfaceInt.executeQuery();
                        rsSqlCountInterfaceInt.next();

                        psSqlCountFieldPackage.setString(1, sPackage.substring(sPackage.indexOf(".") + 1, sPackage.length()));
                        rsSqlCountFieldPackage = psSqlCountFieldPackage.executeQuery();
                        rsSqlCountFieldPackage.next();

                        flag = true;
                        flagCampos = false;
                        strOutScripts.append("*** Total Campos Excel: " + sTabelaNova + " = " + contador + "\r\n");
                        strOutScripts.append("*** Total Campos InOut: " + sTabelaNova + " = " + rsSqlCountInterface.getString("TOTAL") + "\r\n");
                        strOutScripts.append("*** Total Campos Package: " + sPackage + " = " + rsSqlCountFieldPackage.getString("TOTAL") + "\r\n");
                        strOutScripts.append("*** Total Campos IntMapeamento: " + sTabelaNova + " = " + rsSqlCountInterfaceInt.getString("TOTAL") + "\r\n");
                        strOutScripts.append("\r\n");
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        Cell cellTipo = sheet.getCell(1, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            if (cellTamanho.getType() == CellType.NUMBER) {
                                contador += 1;
                                // Exemplo
                                //sNfeSefazNewChNfe = SFWStringUtils.rpad(NfeSefazNewChNfe, " ", 44);

                                //System.out.println("Campo " + cellCampo.getContents() + " Tam." + cellTamanho.getContents() + " Tipo: " + cellTipo.getContents());

                                psSqlInterface.setString(1, sTabelaNova);
                                psSqlInterface.setString(2, cellCampo.getContents().toUpperCase().trim());
                                rsSqlInterface = psSqlInterface.executeQuery();
                                rsSqlInterface.next();

                                psSqlInterfaceInt.setString(1, sTabelaNova);
                                psSqlInterfaceInt.setString(2, cellCampo.getContents().toUpperCase().trim());
                                rsSqlInterfaceInt = psSqlInterfaceInt.executeQuery();
                                rsSqlInterfaceInt.next();

                                try {
                                    if (rsSqlInterface.getString("TABLE_NAME") != null && !rsSqlInterface.getString("TABLE_NAME").equals("")) {

                                        //while(rsSqlInterface.next()){
                                        if (!rsSqlInterface.getString("TAMANHO").equals(cellTamanho.getContents().trim())) {
                                            strOutScripts.append("Tam. Campo " + cellCampo.getContents() + " não confere. Tam. EXCEL: " + cellTamanho.getContents() + " Tam. INOUT: " + rsSqlInterface.getString("TAMANHO") + "\r\n");
                                        } else if (!rsSqlInterface.getString("ORDEM").equals(String.valueOf(contador))) {
                                            strOutScripts.append("Ordem Campo " + cellCampo.getContents() + " não confere. Ordem EXCEL: " + contador + " Ordem. INOUT: " + rsSqlInterface.getString("ORDEM") + "\r\n");
                                        }

                                        try {
                                            if (!sPackage.trim().equals(rsSqlInterface.getString("PROCEDURE_NAME").trim())) {
                                                strOutScripts.append("Package: " + rsSqlInterface.getString("PROCEDURE_NAME") + " não confere com Excel. \r\n");
                                            }

                                            psSqlFieldsPackage.setString(1, sPackage.substring(sPackage.indexOf(".") + 1, sPackage.length()));
                                            psSqlFieldsPackage.setString(2, rsSqlInterface.getString("ARG_NAME"));
                                            rsSqlFieldPackage = psSqlFieldsPackage.executeQuery();
                                            rsSqlFieldPackage.next();

                                            if (rsSqlInterface.getString("ARG_NAME").equals("")) {
                                                strOutScripts.append("Argumento VAZIO  " + cellCampo.getContents() + " não confere. \r\n");
                                            } else if (rsSqlFieldPackage.getString("ARGUMENT_NAME") == null || rsSqlFieldPackage.getString("ARGUMENT_NAME").equals("")) {
                                                strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. NULO \r\n");
                                            } else if (!rsSqlFieldPackage.getString("ARGUMENT_NAME").equals(rsSqlInterface.getString("ARG_NAME"))) {
                                                strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. " + rsSqlInterface.getString("ARG_NAME") + "\r\n");
                                            }
                                        } catch (Exception ex) {
                                            strOutScripts.append("Argumento não encontrado " + cellCampo.getContents() + " não confere. Exception \r\n");
                                            //ex.printStackTrace();
                                        }

                                        try {
                                            if (rsSqlInterfaceInt.getString("API_COLUNA") == null || rsSqlInterfaceInt.getString("API_COLUNA").equals("")) {
                                                strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no IntMapeamento" + "\r\n");
                                            }
                                        } catch (Exception ex) {
                                            strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no IntMapeamento" + "\r\n");
                                        }
                                    } else {
                                        strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no InOut" + "\r\n");
                                    }
                                } catch (Exception ex) {
                                    strOutScripts.append("Campo " + cellCampo.getContents() + " não encontrado no InOut" + "\r\n");
                                }

                            } else {
                                strOutScripts.append("O Tamanho do Campo:" + cellCampo.getContents() + " não está como númérico" + "\r\n");
                                //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                            }
                        } else {
                            //strOutScripts.append("O Tamanho do campo:" + cellCampo.getContents() + " está vazio"+ "\r\n");
                            //strOutScripts.append( SFWStringUtils.rpad("CELULA VAZIA", " ", 12 ) );
                        }
                    }

                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }

            // Gerando arquivo
            fileScripts = new File("c:\\" + this.tag + ".sql");
            if (!fileScripts.exists()) {
                fileScripts.createNewFile();

                fwScripts = new FileWriter(fileScripts, false);
                sTabela = sTabelaNova;

                if (strOutScripts != null) {
                    fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
                }
                fwScripts.close();
            }
            strOutScripts = new StringBuffer();
            flag = false;

        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList readTablesOfExcelAba() {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        File fileScripts;
        FileWriter fwScripts;
        boolean flag = false;
        int contador = 0;
        //String[] sTabela = new String[]{};
        ArrayList sTabela = new ArrayList();

        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        Cell cellTabela = sheet.getCell(1, j);
                        //sTabela[contador] = cellTabela.getContents().toString().trim();
                        sTabela.add(cellTabela.getContents().toString().trim());
                        contador += 1;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sTabela;
    }

    public ArrayList<String> readWeigthFieldsOfTable(String sTabela, String sLinha) {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        File fileScripts;
        FileWriter fwScripts;
        boolean flag = false;
        boolean flagCampos = false;
        boolean flagTabela = false;
        int contador = 0;
        ArrayList sFieldsSeparated = new ArrayList();

        try {
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(tag);
            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        Cell cellTabela = sheet.getCell(1, j);

                        if (cellTabela.getContents().toString().trim().equals(sTabela)) {
                            flagTabela = true;
                        } else {
                            flagTabela = false;
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {
                        flag = true;
                        flagCampos = false;
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos && flagTabela) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            sFieldsSeparated.add(sLinha.substring(contador, Integer.parseInt(cellTamanho.getContents().toString().trim()) + contador));
                            contador += Integer.parseInt(cellTamanho.getContents().toString().trim());
                        } else {
                            System.out.println("O Tamanho do campo:" + cellCampo.getContents() + " está vazio");
                        }
                    }

                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sFieldsSeparated;
    }

    public ArrayList<String> readFieldsOfTable(String sTabela) {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        File fileScripts;
        FileWriter fwScripts;
        boolean flag = false;
        boolean flagCampos = false;
        boolean flagTabela = false;
        int contador = 0;
        ArrayList sFieldsSeparated = new ArrayList();

        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(tag);
            // Loop over first 10 column and lines

            for (int j = 0; j < 600; j++) {
                try {
                    // Tabela
                    Cell cellCampo = sheet.getCell(0, j);
                    if (cellCampo.getContents().toString().equals("Tabela IN-OUT")) {
                        Cell cellTabela = sheet.getCell(1, j);

                        //sTabela[contador] = cellTabela.getContents().toString().trim();
                        //sFieldsSeparated.add(cellTabela.getContents().toString().trim() );
                        contador += 1;
                        if (cellTabela.getContents().toString().trim().equals(sTabela)) {
                            flagTabela = true;
                        } else {
                            flagTabela = false;
                        }
                    }

                    // Identifica final da tabela
                    if (cellCampo.getContents().toString().contains("Controle de Revisão da Interface de")) {
                        flag = true;
                        flagCampos = false;
                        contador = 0;
                    }

                    // Buscando campo do excel
                    if (flagCampos && flagTabela) {
                        Cell cellTamanho = sheet.getCell(2, j);
                        if (!cellTamanho.getContents().trim().equals("")) {
                            contador += 1;
                            System.out.println("Campo " + cellCampo.getContents() + " não encontrado no InOut");

                            //sFieldsSeparated.add(  SFWStringUtils.rpad( String.valueOf( tabela[i] ), String.valueOf( tabela[i] ), Integer.parseInt( cellTamanho.getContents().toString().trim() )) );

                            sFieldsSeparated.add(cellCampo.getContents());

                            //String[] s =  cellCampo.getContents().substring(contador, Integer.parseInt( cellTamanho.getContents().toString().trim()+contador)  )

                            contador += Integer.parseInt(cellTamanho.getContents().toString().trim());
                        } else {
                            System.out.println("O Tamanho do campo:" + cellCampo.getContents() + " está vazio");
                        }
                    }

                    // Identifica inicio dos campos
                    if (cellCampo.getContents().toString().trim().equals("Destino - SFW")) {
                        flagCampos = true;
                        flag = false;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        } catch (BiffException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sFieldsSeparated;
    }

    public static void main(String[] args) throws IOException {
        ReadExcel test = new ReadExcel();
        test.setInputFile("c:/temp/lars.xls");
        test.read();
    }
}
