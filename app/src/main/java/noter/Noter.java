package noter;

// Imports ----------
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import utils.*;
import utils.argsParser.OptionsParser;
// -----------------

public class Noter {

    public String resourcesPath;
    public Map<Integer, Task> taskPool = new HashMap<>();
    public int currId;

    private int archivedCount = 0;

    Helpers helper;
    OptionsParser opParser;
    ExHandler stdHandle;
    DataBaseSupport db;

    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";

    public Noter(String[] args) {
        this.resourcesPath = "app/src/main/resources/";
        this.helper = new Helpers();
        this.stdHandle = new ExHandler();
        this.opParser = new OptionsParser(args, this);

        this.db = new DataBaseSupport();
        this.init();
        this.opParser.init();

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

            if (task.getStatus() == 1) {
                this.archivedCount++;
            }
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
        System.out.println("Successfully added a task to database\n");
        this.listTasks(0, "-Pending Tasks");

        currId++;
    }

    public void updateTask(int id, String nName, String nDesc, String due) {

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks yet, create a new one with -new '<name:desc>'");
            return;

        }
        // Update only if the id exists.
        if (!this.taskPool.containsKey(id)) {
            System.out.println("No task found with given ID");
            return;

        }

        // Update the name, desc, and due date
        due = due.equals(".") ? this.taskPool.get(id).getDue() : due;
        nName = nName.length() <= 1 ? this.taskPool.get(id).getTaskName() : nName;
        nDesc = nDesc.length() <= 1 ? this.taskPool.get(id).getTaskDesc() : nDesc;

        Task exTask = this.taskPool.get(id);

        exTask.updateproperties(nName, nDesc, due);
        db.update(exTask);

        System.out.println(String.format("Done!, updated the given task: %d\n", id));
        this.listTasks(0, "-Pending Tasks");

    }

    public void archiveEveryTask(int soStatus, int desStatus) {
        if ((soStatus >= 0 && soStatus <= 1) && (desStatus >= 0 && desStatus <= 1)) {
            for (Task task : this.taskPool.values()) {
                if (task.getStatus() == 1) {
                    this.archivedCount--;
                }
            }

            db.updateAllStatus(soStatus, desStatus);

            return;

        }

        stdHandle.flowError(
                String.format(
                        "Source or Destination task(s) status is not valid, Source: %d, Destination: %d, only 0 or 1 acceptable",
                        soStatus, desStatus));

    }

    public void listTasks(int status, String legend) {
        // int archived = 0;

        if (this.isTaskPoolEmpty()) {
            System.out.println("No tasks found\n");
            return;
        }

        System.out.printf("\n%s:\n\n", legend);
        for (Task task : this.taskPool.values()) {
            if (task.getStatus() == status || status == -1) {
                System.out.printf(
                        "%sID:%s %d \t%sNAME:%s %s \t%sDESCRIPTION:%s %s \t%sDUE:%s %s \t%sSTATUS:%s %s\n\n",
                        RED, RESET, task.getTaskId(),
                        GREEN, RESET, task.getTaskName(),
                        GREEN, RESET, task.getTaskDesc(),
                        GREEN, RESET, task.getDue(),
                        GREEN, RESET, task.getStatus() == 0 ? "Pending" : "Archived");
            }

            // archived += task.getStatus() == 1 ? 1 : 0;
        }

        System.out.printf("\n%d Archived task%s\n", this.archivedCount, this.archivedCount > 1 ? "s" : "");
    }

    public void linkTasks(int sourceId, int destId) {
        if (!(this.taskPool.containsKey(sourceId) && this.taskPool.containsKey(destId))) {
            this.stdHandle.panic("Cannot find the given source/destination IDs, please check and try again");
            return;
        }

        System.out.println("Done linked two tasks");

    }

    public void removeTask(int id) {
        if (this.taskPool.containsKey(id) && !this.isTaskPoolEmpty()) {
            int currTaskStatus = this.taskPool.get(id).getStatus();
            this.taskPool.remove(id);
            db.remove(id);

            if (currTaskStatus == 1) {
                this.archivedCount--;
            }

            System.out.println("Done!, removed the task with ID: " + id);
            return;
        }

        System.out.printf("No task found with id: %d or TaskPool is empty\n", id);

    }

    public void archiveTask(int id) {
        if (!this.taskPool.containsKey(id) && this.isTaskPoolEmpty()) {
            System.out.printf("No task found with id: %d or TaskPool is empty\n", id);
            return;
        }

        if (this.taskPool.get(id).getStatus() == 1) {
            System.out.println("Task already marked as \"Archived\", skipping");
            return;
        }

        this.taskPool.get(id).updateStatus(1);
        db.update(id, 1);

        System.out.println("Done! Updated the status to Archived(1)\n");

        this.archivedCount++;
    }

    public void cleanTaskPool() {
        if (this.isTaskPoolEmpty()) {
            System.out.println("TaskPool is Empty");
            return;
        }

        int[] removable = new int[this.taskPool.size()];
        int index = 0;

        for (int taskId : this.taskPool.keySet()) {
            if (this.taskPool.get(taskId).getStatus() == 1) {
                removable[index] = taskId;
                index++;

                this.archivedCount--;
            }
        }

        if (index <= 0) {
            System.out.println("NO Archived tasks found");
            return;
        }

        for (int taskId : removable) {
            if (this.taskPool.get(taskId) != null) {
                this.taskPool.remove(taskId);
                this.db.remove(taskId);
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

        this.archivedCount = 0;

        System.out.println("Done!, Cleared all the tasks\n");
    }

}
