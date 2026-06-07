
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

    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";

    public Noter(String[] args) {
        this.resourcesPath = "app/src/main/resources/";
        this.helper = new Helpers();
        this.stdHandle = new ExHandler();
        this.argsParser = new ArgsParser(args, this);

        this.db = new DataBaseSupport();
        this.init();
        this.argsParser.init();

    }

    public static void main(String[] args) {
        new Noter(args);

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

    public void getPartialhelp() {
        helper.readFile(this.resourcesPath + "partialhelp.txt");

    }

    public void getEveryHelp() {
        if (helper.isPathExists(this.resourcesPath)) {
            this.getPartialhelp();
            this.hGetUsage();
            this.hGetExamples();
        }
    }

    public void hGetUsage() {
        helper.readFile(this.resourcesPath + "usage.txt");
    }

    public void hGetExamples() {
        helper.readFile(this.resourcesPath + "examples.txt");
    }

    public void addTask(String tName, String tDesc, String due) {

        Task newTask = new Task(currId, tName, tDesc, due);
        this.taskPool.put(currId, newTask);

        db.insert(newTask);
        stdHandle.message("Successfully added a task to database\n");
        this.listTasks(0, "Pending Tasks");

        currId++;
    }

    public void updateTask(int id, String nName, String nDesc, String due) {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks yet, create a new one with -new '<name:desc>'");
            return;

        }

        if (this.taskPool.containsKey(id)) {
            due = due.equals(".") ? this.taskPool.get(id).getDue() : due;
            nName = nName.length() <= 1 ? this.taskPool.get(id).getTaskName() : nName;
            nDesc = nDesc.length() <= 1 ? this.taskPool.get(id).getTaskDesc() : nDesc;

            Task newTask = new Task(id, nName, nDesc, due);

            this.taskPool.put(id, newTask);
            db.update(newTask);

            stdHandle.message(String.format("Done!, updated the given task: %d\n", id));
            this.listTasks(0, "Pending Tasks");

        } else {
            stdHandle.message("No task found with given ID");
        }

    }

    public void updateEveryTaskStatus(int pastStatus, int newStatus) {
        if ((pastStatus >= 0 && pastStatus <= 1) && (newStatus >= 0 && newStatus <= 1)) {
            db.updateAllStatus(pastStatus, newStatus);
            return;

        }

        stdHandle.flowError(
                String.format("One of the task status is not valid, past: %d, new: %d, only 0 or 1 acceptable",
                        pastStatus, newStatus));

    }

    public void listTasks(int status, String legend) {
        int archived = 0;
        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks found\n");
            return;
        }

        System.out.printf("\n%s:\n\n", legend);
        for (Task task : this.taskPool.values()) {
            if (task.getStatus() == status || status == -1) {
                System.out.printf(
                        "%sID: %s%d\t%sName: %s%s\t%sDescription: %s%s\t%sDue: %s%s\t%sStatus: %s%s\n\n",
                        RED,
                        RESET,
                        task.getTaskId(),
                        GREEN,
                        RESET,
                        task.getTaskName(),
                        GREEN,
                        RESET,
                        task.getTaskDesc(),
                        GREEN,
                        RESET,
                        task.getDue(),
                        GREEN,
                        RESET,
                        task.getStatus() == 0 ? "Pending" : "Completed");

            }

            archived += task.getStatus() == 1 ? 1 : 0;
        }

        System.out.printf("\n%d Archived task%s\n", archived, archived > 1 ? "s" : "");
    }

    public void removeTask(int id) {
        if (this.taskPool.containsKey(id) && !this.isTaskPoolEmpty()) {
            this.taskPool.remove(id);
            db.remove(id);

            this.stdHandle.message("Done!, removed task: " + id);
            return;
        }

        System.out.printf("No task found with id: %d or TaskPool is empty\n", id);

    }

    public void updateStatus(int id) {

        if (this.taskPool.containsKey(id) && !this.isTaskPoolEmpty()) {
            this.taskPool.get(id).updateStatus(1);
            db.update(id, 1);

            this.stdHandle.message("Done! Updated the status to Completed(1)\n");
            return;
        }

        System.out.printf("No task found with id: %d or TaskPool is empty\n", id);
    }

    public void cleanTaskPool() {
        if (this.isTaskPoolEmpty()) {
            System.out.println("TaskPool is Empty");
            return;
        }

        Task[] removable = new Task[this.taskPool.size()];
        int index = 0;

        for (Task task : this.taskPool.values()) {
            if (task.getStatus() == 1) {
                removable[index] = task;
                index++;
            }
        }

        if (index <= 0) {
            System.out.println("NO Archived tasks found");
            return;
        }

        for (Task task : removable) {
            if (task != null) {
                this.taskPool.remove(task.getTaskId());
                this.db.remove(task.getTaskId());
            }
        }

        System.out.println("Successfully cleaned the TaskPool");

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
