package noter;

// Imports ----------
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.InputMismatchException;

import utils.*;

// -----------------

public class Noter {

    public String root;
    public Map<Integer, Task> taskManager = new HashMap<>();
    public int currId;

    Helpers helper;

    public Noter() {
        this.root = "app/src/main";
        this.helper = new Helpers();
        this.currId = 0;
    }

    public void getAbout() {
        helper.readAFile(this.root + "/resources/about.txt");

    }

    private void getHelp() {
        helper.readAFile(this.root + "/resources/help.txt");

    }

    public void addTask(String task, String desc) {

        Task createNewTask = new Task(currId, task, desc);
        this.taskManager.put(currId, createNewTask);

        System.out.println("Added a task to the task pool");

        currId++;

    }

    public void displayTasks() {
        if (this.taskManager.size() < 1) {
            System.out.println("No tasks yet, use -help more info");
        }

        System.out.println("\nDisplaying Tasks\n");
        for (Map.Entry<Integer, Task> item : this.taskManager.entrySet()) {
            Task tObj = item.getValue();
            System.out.printf("Task ID: %s,\tTask Name: %s,\tTask Description: %s\n",
                    item.getKey(),
                    tObj.getTaskName(),
                    tObj.getTaskDesc());
        }

    }

    public int removeTask(String[] args, int index) {
        if (index + 1 >= args.length) {
            System.out.println("Error: No 'ID' provided to remove task");
            return index + 1;

        }

        int id;

        try {

            id = Integer.parseInt(args[index + 1]);

        } catch (Exception e) {
            System.out.println("Error: invalid task ID, Should be an integer");
            return index + 1;
        }

        if (this.taskManager.containsKey(id)) {
            Task removed = this.taskManager.remove(id);

            System.out.printf("Removed task: %s, with id: %s\n", removed.getTaskName(), id);
            return index + 1;
        }

        System.out.printf("No task with id: %s found in tasks pool\n", id);
        return index + 1;
    }

    private int createNewTask(String[] args, int cIndex, int len) {
        String task = null;
        String desc = null;

        if (cIndex + 1 >= len) {
            System.out.println("Error: No task or description");
            return cIndex;
        }

        for (int i = cIndex + 1; (i < cIndex + 4 && i < len); i++) {
            // System.out.println("Arg: " + args[i]);

            switch (args[i]) {
                case "--task":
                    i += 1;
                    if (i >= len) {
                        return i;
                    }

                    task = args[i];
                    break;

                case "--desc":
                    i += 1;
                    if (i >= len) {
                        System.out.println("Error: No description");
                        return i;
                    }

                    if (task == null) {
                        System.out.println("Error: No task name, for the desc: " + args[i--]);
                        System.out.println("Hint: use -help for more info");
                        return i;

                    }

                    desc = args[i];
                    break;

                default:
                    i += 1;

            }

        }

        if (task != null && desc != null) {
            this.addTask(task, desc);

            return cIndex + 4;

        } else {
            System.out.println("Error: task name or description is null");
            System.out.printf("Task: %s, Description: %s\n", task, desc);

        }

        return cIndex;

    }

    private void handleOptions(String args[], int len) {
        int index = 0;
        while (index < len) {

            String arg1 = args[index];
            int updIndex;
            // System.out.printf("Current val at index %d: \n", index, arg1);

            switch (arg1) {
                case "-help":
                    this.getHelp();
                    break;

                case "-about":
                    this.getAbout();
                    break;

                case "-new":
                    // System.out.println("\ninside createNewTask\n");
                    // System.out.printf("crr index: %d, val: %s\n", index, args[index]);
                    updIndex = this.createNewTask(args, index, len);
                    index = updIndex;
                    // System.out.println("\nend createNewTask\n");

                    break;

                case "-remove":
                    // System.out.println("\ninside removeTask\n");
                    // System.out.printf("crr index: %d, val: %s\n", index, arg1);
                    updIndex = this.removeTask(args, index);
                    index = updIndex;
                    // System.out.println("\nend removeTask\n");

                case "-displayTasks":
                    this.displayTasks();
                    break;
            }
            // System.out.printf("crr index: %d, val: %s\n", index, arg1);

            index++;
        }
    }

    private void handleArgs(String args[]) {
        int len = args.length;
        if (!(args.length > 0)) {
            System.out.println("MESSAGE: Could'nt find any args");
            return;

        }

        // System.out.println("\ninside handleOptions\n");
        this.handleOptions(args, len);
        // System.out.println("\nend handleOptions\n");

    }

    public static void main(String[] args) {
        Scanner userIn = new Scanner(System.in);
        userIn.close();

        Noter obj = new Noter();

        obj.handleArgs(args);
    }
}
