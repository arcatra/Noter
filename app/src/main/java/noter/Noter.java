package noter;

// Imports ----------
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

// import java.io.BufferedWriter;

// -----------------

public class Noter {

    public String root;
    public String[] currentTask = new String[2];

    public Noter() {
        this.root = "src/main";
    }

    public void addTask(String task) {
        System.out.println("Task : " + task);

    }

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

    public void getAbout() {
        this.readAFile(this.root + "/resources/about.txt");

    }

    private void getHelp() {
        this.readAFile(this.root + "/resources/help.txt");

    }

    private void handleArgs(String args[]) {
        int len = args.length;

        for (int index = 0; index < len; index++) {
            // System.out.printf("Current val at index %d: %s\n", index, args[index]);
            String arg = args[index];

            switch (arg) {
                case "-help":
                    this.getHelp();
                    break;

                case "-about":
                    this.getAbout();
                    break;

                case "--task":
                    if (++index >= len) {
                        System.out.println("Error: no task name");
                    }

                    arg = args[index];
                    System.out.println("task: " + arg);

                    break;

                case "--desc":
                    if (++index >= len) {
                        System.out.println("Error: no task name");
                    }

                    arg = args[index];
                    System.out.println("desc: " + arg);

                    break;

            }
        }

    }

    public static void main(String[] args) {
        Scanner userIn = new Scanner(System.in);
        userIn.close();

        Noter obj = new Noter();

        // for (int i = 0; i < args.length; i++) {
        // System.out.printf("%d = %s\n", i, args[i]);
        // }
        // System.out.println("Current root: " + root);

        if (args.length >= 1) {
            obj.handleArgs(args);

        } else {
            System.out.println("MESSAGE: Could'nt find any args");
        }

    }
}
