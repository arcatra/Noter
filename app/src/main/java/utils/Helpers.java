package utils;

// Imports -------------------
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
//---------------------------

public class Helpers {

    ExHandler stdHandle;

    public Helpers() {
        this.stdHandle = new ExHandler();
    }

    private boolean isPathExists(String filePath) {
        Path path = Paths.get(filePath);
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

    public void writeFile(String filePath, Collection<Task> content) {
        if (this.isPathExists(filePath)) {
            stdHandle.panic("File path is not valid: " + filePath);
        }

        try (BufferedWriter bfr = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : content) {
                if (!task.toString().equals("")) {
                    bfr.write(task.toString());
                    bfr.newLine();

                }
            }

            stdHandle.message("Successfully encrypted added the given task");

        } catch (Exception e) {
            stdHandle.panic(e + "");
        }
    }
}
