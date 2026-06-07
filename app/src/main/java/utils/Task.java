
package utils;

// imports ----------
// ------------------

public class Task {

    private int tId;
    private int status;
    private String tName;
    private String tDesc;
    private String due;

    public Task(int id, String name, String desc, String due) {
        this.tId = id;
        this.tName = name;
        this.tDesc = desc;
        this.due = due;
        this.status = 0; // 0 - pending, 1 - complete
    }

    public int getTaskId() {
        return this.tId;

    }

    public String getTaskName() {
        return this.tName;

    }

    public String getTaskDesc() {
        return this.tDesc;

    }

    public String getDue() {
        return this.due;
    }

    public int getStatus() {
        return this.status;
    }

    public void updateStatus(int newStatus) {
        this.status = newStatus;
    }

}
