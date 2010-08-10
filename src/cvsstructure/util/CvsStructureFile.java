/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ahdrein
 */
public class CvsStructureFile extends File{

    public CvsStructureFile(String fileName){
        super(fileName);
    }

    public void saveArquivo(StringBuilder strOutScripts) throws IOException {
        this.createNewFile();

        FileWriter fwScripts = new FileWriter(this, false);
        fwScripts.write(strOutScripts.toString(), 0, strOutScripts.length());
        fwScripts.close();
    }

}
