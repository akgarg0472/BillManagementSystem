package billingrecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("all")
public class LogFile {

    private final String path = System.getProperty("user.dir");
    private BufferedWriter bufferedWriter = null;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final Date date = new Date();

    public void modifyLogFile(String logStatement) {
        String directoryName = "bin";
        File directory = new File(directoryName);

        if (!directory.exists()) {
            directory.mkdir();
        }

        String fileName = "logs.txt";
        String logFilePath = path + File.separator + directoryName + File.separator + fileName;

        try {
            File logFile = new File(logFilePath);

            if (!logFile.exists()) {
                logFile.createNewFile();
                bufferedWriter = new BufferedWriter(new FileWriter(logFilePath, false));
                bufferedWriter.write("--------------- LOG FILE --------------- \n");
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.out.println("Exception in creating log file");
            e.printStackTrace();
        }

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(logFilePath, true));
            bufferedWriter.write("\n" + formatter.format(date) + " --> " + logStatement);
        } catch (IOException e) {
            System.out.println("Error in writing data in log file");
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing bufferWriter");
                e.printStackTrace();
            }
        }
    }

}
