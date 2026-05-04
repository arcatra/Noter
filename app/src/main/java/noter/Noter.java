
package noter;

// Imports ----------
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;

import utils.*;
// -----------------

public class Noter {

    public String resourcesPath;
    public Map<Integer, Task> taskPool = new HashMap<>();
    public int currId;

    public static Noter obj;

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
        Noter.obj = new Noter();
        obj.argsParser = new ArgsParser(args, Noter.obj);

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

    public void addTask(String tName, String tDesc, String due) {

        Task newTask = new Task(currId, tName, tDesc, due);
        this.taskPool.put(currId, newTask);

        db.insert(newTask);
        stdHandle.message("Successfully added a task to database\n");
        this.displayEveryTask(false);

        currId++;
    }

    public void remap() {
        if (this.isTaskPoolEmpty()) {
            stdHandle.message("No tasks yet to sync tasks");
            return;

        }

        int id = 1;
        db.clear();
        for (Entry<Integer, Task> tEntry : this.taskPool.entrySet()) {
            tEntry.getValue().setTaskId(id);
            id++;
            db.insert(tEntry.getValue());
        }

        this.displayEveryTask(false);

    }

    public void updateTask(int id, String nName, String nDesc, String due) {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks yet, create a new one with -new '<name:desc>'");
            return;

        }

        if (this.taskPool.containsKey(id)) {
            due = due.equals(".") ? this.taskPool.get(id).getdue() : due;
            nName = nName.length() <= 1 ? this.taskPool.get(id).getTaskName() : nName;
            nDesc = nDesc.length() <= 1 ? this.taskPool.get(id).getTaskDesc() : nDesc;

            Task newTask = new Task(id, nName, nDesc, due);

            this.taskPool.put(id, newTask);
            db.update(newTask);

            stdHandle.message(String.format("Done!, updated the given task: %d\n", id));
            this.displayEveryTask(false);

        } else {
            stdHandle.message("No task found with given ID");
        }

    }

    public void updateEveryTaskStatus(int pastStatus, int newStatus) {
        if ((pastStatus >= 0 && pastStatus <= 1) && (newStatus >= 0 && newStatus <= 1)) {
            db.updateAllStatus(pastStatus, newStatus);
            return;

        }

        stdHandle.panic(String.format("Not a valid status %d and %d, only 0 or 1", pastStatus, newStatus));

    }

    public void displayAll() {
        this.displayEveryTask(true);
    }

    public void displayEveryTask(boolean all) {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks found\n");
            return;
        }

        System.out.println("\nTasks:\n");
        for (Task task : this.taskPool.values()) {
            if (task.getStatus() == 0 || all) {

                System.out.printf(
                        "ID: %d\tName: %s\tDescription: %s\tDue: %s\tStatus: %s\n",
                        task.getTaskId(),
                        task.getTaskName(),
                        task.getTaskDesc(),
                        task.getdue(),
                        task.getStatus() == 0 ? "Pending" : "Completed");
            }
            System.out.println("");
        }
    }

    public void removeTask(int id) {
        if (this.taskPool.containsKey(id) && !this.isTaskPoolEmpty()) {
            this.taskPool.get(id).setStatus(1);
            db.update(id, 1);

            this.stdHandle.message("Done!\n");
            this.displayEveryTask(false);

            return;
        }

        System.out.printf("No task found with id: %d or TaskPool is empty\n", id);
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
