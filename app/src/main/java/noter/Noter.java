
package noter;

// Imports ----------
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

import utils.*;

// -----------------

public class Noter {

    public String resourcesPath;
    public Map<Integer, Task> taskPool = new HashMap<>();
    public int currId;

    Helpers helper;
    ArgsParser argsParser;
    ExHandler stdHadle;

    public Noter() {
        this.resourcesPath = "app/src/main/resources/";
        this.currId = 0;
        this.helper = new Helpers();
        this.stdHadle = new ExHandler();

    }

    public void getAbout() {
        helper.readFile(this.resourcesPath + "about.txt");

    }

    public void getHelp() {
        helper.readFile(this.resourcesPath + "help.txt");

    }

    public void addTask(String task, String desc) {

        Task newTask = new Task(currId, task, desc);
        this.taskPool.put(currId, newTask);

        this.helper.writeFile(this.resourcesPath + "taskPool.txt", this.taskPool.values());

        this.stdHadle.message("New task added");

        currId++;

    }

    public void displayTasks() {
        if (this.taskPool.size() < 1) {
            this.stdHadle.message("No tasks yet, use -help more info");

            return;
        }

        System.out.println("\nTasks:\n");
        for (Map.Entry<Integer, Task> item : this.taskPool.entrySet()) {
            Task task = item.getValue();
            System.out.printf("ID: %s,\tName: %s,\tDescription: %s\n",
                    item.getKey(),
                    task.getTaskName(),
                    task.getTaskDesc());
        }

        System.out.println("");

    }

    public int removeTask(String[] args, int index) {
        if (index + 1 >= args.length) {
            this.stdHadle.panic("No 'ID' provided to remove task");
            return index + 1;

        }

        int id;

        try {

            id = Integer.parseInt(args[index + 1]);

        } catch (Exception e) {
            this.stdHadle.panic("invalid task ID, Should be an integer");
            return index + 1;
        }

        if (this.taskPool.containsKey(id)) {
            Task removed = this.taskPool.remove(id);
            String msg = String.format("Removed task: %s, with id: %s\n", removed.getTaskName(), id);
            this.stdHadle.message(msg);
            return index + 1;
        }

        System.out.printf("No task with id: %d found in tasks pool\n", id);
        return index + 1;
    }

    public static void main(String[] args) {
        Scanner userIn = new Scanner(System.in);
        userIn.close();

        Noter obj = new Noter();
        obj.argsParser = new ArgsParser(args);

    }
}
