
package noter;

// Imports ----------
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import utils.*;
// -----------------

public class Noter {

    public String resourcesPath;
    public Map<Integer, Task> taskPool = new HashMap<>();
    public int currId;

    Helpers helper;
    ArgsParser argsParser;
    ExHandler stdHandle;
    DataBaseSupport db;

    public Noter() {
        this.resourcesPath = "src/main/resources/";
        this.helper = new Helpers();
        this.stdHandle = new ExHandler();
        this.db = new DataBaseSupport();

        this.init();

    }

    public static void main(String[] args) {
        Noter obj = new Noter();
        obj.argsParser = new ArgsParser(args);

    }

    private void init() {
        List<Task> dbTasks = db.get();
        if (dbTasks == null) {
            System.out.println("No tasks yet");
            return;
        }

        for (Task task : dbTasks) {
            this.taskPool.put(task.getTaskId(), task);
            this.currId = task.getTaskId();
        }

        this.currId++;
    }

    private boolean isTaskPoolEmpty() {
        return (this.taskPool.size() <= 0);

    }

    public void getAbout() {
        helper.readFile(this.resourcesPath + "about.txt");

    }

    public void getHelp() {
        helper.readFile(this.resourcesPath + "help.txt");

    }

    public void addTask(String tName, String tDesc, String deadLine) {

        Task newTask = new Task(currId, tName, tDesc, deadLine);
        this.taskPool.put(currId, newTask);

        db.insert(newTask);
        stdHandle.message("Successfully added a task to database\n");
        this.displayTasks();

        currId++;
    }

    public void updateTask(int id, String nName, String nDesc) {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks yet, create a new one with -new '<name:desc>'");
            return;

        }

        if (this.taskPool.containsKey(id)) {
            String deadLine = this.taskPool.get(id).getDeadLine();
            Task newTask = new Task(id, nName, nDesc, deadLine);
            this.taskPool.put(id, newTask);
            db.update(newTask);

            stdHandle.message(String.format("Done!, updated the given task: %d\n", id));
            this.displayTasks();

        } else {
            stdHandle.message("No task found with given ID");
        }

    }

    public void displayTasks() {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks found\n");
            return;
        }

        System.out.println("\nTasks:\n");
        for (Task task : this.taskPool.values()) {
            System.out.printf(
                    "ID: %d\tCreatedOn: %s\tName: %s\tDescription: %s\tDue: %s\n",
                    task.getTaskId(),
                    task.getTaskDateTime(),
                    task.getTaskName(),
                    task.getTaskDesc(),
                    task.getDeadLine());
        }
        System.out.println("");
    }

    public void removeTask(int id) {
        if (this.taskPool.containsKey(id) && !this.isTaskPoolEmpty()) {
            Task removed = this.taskPool.remove(id);
            db.remove(id);

            this.stdHandle.message(String.format("Removed task '%s', with id: %s\n", removed.getTaskName(), id));
            this.displayTasks();

            return;
        }

        System.out.printf("No task found with id: %d\n", id);
    }

    public void clearTaskPool() {
        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks found\n");
            return;
        }

        db.clear();
        this.taskPool.clear();

        stdHandle.message("Done!, Cleared all the tasks\n");
    }

}
