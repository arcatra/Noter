package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.FileReader;

public class Helpers {

    private boolean isPathExists(String filePath) {
        Path path = Paths.get(filePath);
        return (Files.exists(path));

    }

    public void readAFile(String filePath) {
        if (this.isPathExists(filePath)) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {

                String line;
                while ((line = bfr.readLine()) != null) {
                    System.out.println(line);
                }

            } catch (Exception e) {
                System.out.println("Error occured while processing this path: " + filePath);

            }

        } else {
            System.out.printf("Error: %s Provided path doesn't exists\n", filePath);

        }

    }

}
