package utils;

// Imports -------------------
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.FileReader;
//---------------------------

public class Helpers {

    ExHandler stdHandle;

    public Helpers() {
        this.stdHandle = new ExHandler();
    }

    public boolean isPathExists(String filePath) {
        Path path = Paths.get(filePath);
        System.out.println("Current working dir: " + System.getProperty("user.dir"));
        return (Files.exists(path));

    }

    public void readFile(String filePath) {
        if (this.isPathExists(filePath)) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {

                String task;
                while ((task = bfr.readLine()) != null) {
                    System.out.println(task);
                }

            } catch (Exception e) {
                stdHandle.panic("Error occured while processing this path: " + filePath);

            }

        } else {
            System.out.printf("Error: %s Provided path doesn't exists\n", filePath);

        }

    }

}
