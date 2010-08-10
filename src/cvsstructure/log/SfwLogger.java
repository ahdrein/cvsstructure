package cvsstructure.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.TimeZone;
import cvsstructure.CVSStructure;
import cvsstructure.gui.CvsStructureFrame;
import cvsstructure.model.Cliente;
import cvsstructure.util.Diretorio;
import java.util.logging.Logger;

public class SfwLogger {

    private static Logger logInfo;
    private static Logger logDebug;
    private static String currentTime = null;
    private static String nameArq;
    public static CvsStructureFrame cvsStructureFrame;

    static {
        logInfo = Logger.getLogger("CVSStructureInfo");
        logDebug = Logger.getLogger("CVSStructureDebug");
    }

    private SfwLogger() {

    }

     public static void log(String sLog){
         if(cvsStructureFrame != null){
            cvsStructureFrame.setTextArea(sLog + CVSStructure.QUEBRA_LINHA);
            if (CVSStructure.sDebug.equals("S")) {
                SfwLogger.debug(sLog, null);
            }
         }
    }

     public static void debug(String sLog){
         if(cvsStructureFrame != null){
            if (CVSStructure.sDebug.equals("S")) {
                cvsStructureFrame.setTextArea(sLog + CVSStructure.QUEBRA_LINHA);
                SfwLogger.debug(sLog, null);
            }
         }
     }

     public static void debug(String sLog, StackTraceElement[] stackTrace) {
        File arquivo;

        if (currentTime == null) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String DATE_FORMAT = "yyyy-MM-dd HHmmss";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getDefault());

            currentTime = sdf.format(cal.getTime());

            nameArq = Diretorio.path + "\\log_cvs_structure_" + Cliente.userNameSys + currentTime + ".log";
        }

        try {
            if(stackTrace != null){
                arquivo = new File(nameArq);
                if (!arquivo.exists()) {
                    arquivo.createNewFile();
                }

                PrintWriter arqLog = new PrintWriter(new FileOutputStream(arquivo, true), true);

                //arqLog.println(currentTime);
                arqLog.println(sLog);
                if (stackTrace.length != 0) {
                    for (int y = 1; y < stackTrace.length; y++) {
                        arqLog.println(stackTrace[y]);
                    }
                }

                arqLog.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de Log não existe");
        } catch (IOException e) {
            System.out.println("Impossível abrir arquivo de log");
        }
    }
}
