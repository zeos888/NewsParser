package web.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by alxev on 03.12.2015.
 */
public class FilesHelper {
    public static void copyFile(String source, String destination) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        java.io.FileInputStream fileInputStream = new java.io.FileInputStream(source);
        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(destination);

        while (fileInputStream.available()>0)
        {
            int data = fileInputStream.read();
            fileOutputStream.write(data);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
