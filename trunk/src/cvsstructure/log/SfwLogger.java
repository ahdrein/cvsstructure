package sfw.structure.log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import sfw.structure.cvsStructure.CVSStructure;

public class SfwLogger {

	private static Logger logInfo;
	private static Logger logDebug;
    private static String currentTime = null;
    private static String nameArq;

	static{
		logInfo = Logger.getLogger("CVSStructureInfo");
		logDebug = Logger.getLogger("CVSStructureDebug");


        
	}

	private SfwLogger(){
	}

	public static void log(Object object){
		if(object instanceof Throwable){
			logInfo.info((Object)((Throwable)object).getMessage(), (Throwable) object);
		}
		else{
			logInfo.info(object);
		}
	}

	public static void debug(Object object){
		if(object instanceof Throwable){
			logDebug.debug((Object)((Throwable)object).getMessage(), (Throwable) object);
		}
		else{
			logDebug.debug(object);
		}
	}


	/**************************************************************************
	 * <b>Save Logs</b>
	 * @param sLog
	 **************************************************************************/
	public static void saveLog(String sLog) throws IOException{
		File arquivo;

		if(currentTime == null){
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String DATE_FORMAT = "yyyy-MM-dd HHmmss";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getDefault());

            currentTime = sdf.format(cal.getTime());

            nameArq = CVSStructure.path+ "\\log_cvs_structure_" + CVSStructure.userNameSys + currentTime + ".log";
        }

		try{
            if(CVSStructure.sDebug.equals("S")){
                arquivo = new File(nameArq);
                if (!arquivo.exists()){
                    arquivo.createNewFile();
                }

                PrintWriter arqLog = new PrintWriter(new FileOutputStream(arquivo, true), true);

                //arqLog.println(currentTime);
                arqLog.println(sLog);

                System.out.println(sLog);
                CVSStructure.logMessage(sLog);

                arqLog.close();
            }
		}catch(FileNotFoundException e) {
			System.out.println("Arquivo de Log não existe");
		}catch (IOException e) {
			System.out.println("Impossível abrir arquivo de log");
		}
     }


	public static void saveLog(String sLog, StackTraceElement[] stackTrace){
		File arquivo;

		if(currentTime == null){
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String DATE_FORMAT = "yyyy-MM-dd HHmmss";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getDefault());

            currentTime = sdf.format(cal.getTime());

            nameArq = CVSStructure.path+ "\\log_cvs_structure_" + CVSStructure.userNameSys + currentTime + ".log";
        }
        
		try{
			arquivo = new File(nameArq);
			if (!arquivo.exists()){
				arquivo.createNewFile();
			}

			PrintWriter arqLog = new PrintWriter(new FileOutputStream(arquivo, true), true);

			//arqLog.println(currentTime);
			arqLog.println(sLog);
			if(stackTrace.length != 0){
				for(int y = 1; y < stackTrace.length; y++){
					arqLog.println(stackTrace[y]);
				}
			}

            arqLog.close();
		}catch(FileNotFoundException e) {
			System.out.println("Arquivo de Log não existe");
		}catch (IOException e) {
			System.out.println("Impossível abrir arquivo de log");
		}
     }

}
