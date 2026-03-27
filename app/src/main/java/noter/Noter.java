package noter;

// Imports ----------
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

// import java.io.BufferedWriter;

// -----------------

public class Noter {
    public void displayAbout() {
        try (BufferedReader bfr = new BufferedReader(new FileReader("./src/main/resources/about.txt"))) {
            String line;

            while ((line = bfr.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.out.println("Error occured!: " + e);
        }
    }

    public void addTask(String task) {
        System.out.println("Task : " + task);

    }

    public void destructArgs(String args[]) {
        int len = args.length;

        for (int index = 0; index < len; index++) {
            // System.out.println("Current: " + args[index]);
            String val = args[index];

            if (val.startsWith("-")) {
                val = val.toLowerCase();
            }

            switch (val) {
                case "-about":
                    this.displayAbout();

                    break;

                case "-task":
                    if (index + 1 < len) {
                        String taskName = args[index + 1];
                        System.out.println("Adding task: " + taskName);

                    } else {
                        System.out.println("Error: Couldn't find the task");

                    }

                    break;

            }
        }

    }

    public static void main(String[] args) {
        Scanner userIn = new Scanner(System.in);
        userIn.close();

        Noter obj = new Noter();

        if (args.length >= 1) {
            obj.destructArgs(args);

        } else {
            System.out.println("MSG: Could'nt find any args");
        }

    }
}
