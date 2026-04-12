
package utils;

// imports ----------

// ------------------

public class Task {

    private int tId;
    private String tName;
    private String tDesc;

    public Task(int id, String name, String desc) {

        this.tId = id;
        this.tName = name;
        this.tDesc = desc;

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

}
