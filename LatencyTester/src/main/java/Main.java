import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.application.Application.launch;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {


        String testFolder = "tests";
        File folder = new File(testFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String fileName = "tests/PingTimestamps.txt";
        File pingFile = new File(fileName);
        ArrayList<Integer> fileCounter = new ArrayList<>();
        int arrayCounter = 0;
        while (pingFile.exists()) {
         //                String newFileName = "PingTimestamps" + fileCount + ".txt";
        //  pingFile = new File(newFileName);
         //  fileCount++;
         //  pingFile = new File(newFileName);

         arrayCounter = fileCounter.size() + 1;
            String newFileName = "tests/PingTimestamps" + arrayCounter + ".txt";
            fileCounter.add(arrayCounter);
            pingFile = new File(newFileName);

        }





        ProcessBuilder process = new ProcessBuilder("cmd.exe", "/c", "ping -t google.com");
        // process.redirectOutput(pingFile);
        Process p = process.start();
        InputStream processOut = p.getInputStream();
        Reader reader = new InputStreamReader(processOut);
        BufferedReader br = new BufferedReader(reader);


        ArrayList<PingTimestamps> pings = new ArrayList<PingTimestamps>();

        List<Double> newData = new ArrayList<>();


        br.readLine();
        br.readLine();

        String line;

        // System.out.println(line);



      //  new Thread(() -> Application.launch(LineGraphExample.class, args)).start();

        new Thread(() -> {Application.launch(LineGraphExample.class, args);
              }).start();




        while ((line = br.readLine()) != null) {

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM dd yyyy HH:mm:ss");

            String date = simpleDateFormat1.format(new Date());


            System.out.println(line);

            BufferedWriter writer = new BufferedWriter(new FileWriter(pingFile, true));
            //    String line2 = br.readLine();
            //   writer.write(line2.substring(42,46) +" " + date + "\n");


            writer.write(line + " " + date + "\n");
            //  writer.write(line.substring(42, 46) + " " + date + "\n");
            int pingTime = findTime(line);

            PingTimestamps current = new PingTimestamps(pingTime, date);

            pings.add(current);

            //Thread.sleep(1000);
            while ((line = br.readLine()) == null) {
                System.out.println(line);
                p = process.start();
                //   Thread.sleep(1000);
                //  br.readLine();
            }

            writer.close();
        }



        br.close();
        processOut.close();
    }








    public static int findTime(String readerLine) {

        char startChar = '=';
        char endChar = 'm';

        int startIndex = readerLine.indexOf(startChar);
        int endIndex = readerLine.indexOf(endChar, startIndex + 1);
        int newResult;

        if (startIndex != -1 && endIndex != -1) {

            String result = readerLine.substring(startIndex + 1, endIndex);
            newResult = Integer.parseInt(result);

        //    System.out.println("Substring between '" + startChar + "' and '" + endChar + "': " + newResult);
        } else {
            newResult = 0;
        }
        return newResult;
    }
}








class PingTimestamps {
    int pingTime;
    String date;

    public PingTimestamps(int pingTime, String date) {
        this.pingTime = pingTime;
        this.date = date;
    }

    public String toString() {
        return pingTime + " " + date;
    }
}





