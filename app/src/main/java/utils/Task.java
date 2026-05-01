
package utils;

// imports ----------
import java.time.LocalDateTime;
// ------------------

public class Task {

    private int tId;
    private String tName;
    private String tDesc;
    private String created;
    private String deadLine;

    public Task(int id, String name, String desc, String deadLine) {

        this.tId = id;
        this.tName = name;
        this.tDesc = desc;
        this.deadLine = deadLine;
        this.created = LocalDateTime.now().toString().substring(0, 10);

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

    public String getDeadLine() {
        return this.deadLine;
    }

    public String getTaskDateTime() {
        return this.created;
    }

}
