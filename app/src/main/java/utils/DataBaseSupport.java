package utils;

// Imports -------------
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

// ---------------------

public class DataBaseSupport {
    private static final String DBPATH = "data/taskpool.db";
    private static final String URL = "jdbc:sqlite:" + DBPATH;

    ExHandler stdHandle;
    Helpers helper;

    public DataBaseSupport() {
        this.stdHandle = new ExHandler();
        this.helper = new Helpers();
        this.init();
        // this.displayTotalRows();

    }

    private void init() {
        String query = "CREATE TABLE IF NOT EXISTS taskpool ( " +
                "id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT, duedate TEXT, status INTEGER)";

        if (this.helper.isPathExists(DBPATH)) {
            System.out.println(String.format("DBPath: %s exists\n", DBPATH));
            return;

        }

        try (Connection dbConn = DriverManager.getConnection(URL);
                PreparedStatement excQuery = dbConn.prepareStatement(query)) {

            excQuery.execute();
            // System.out.println("INIT");

        } catch (SQLException e) {
            stdHandle.panic(String.format("in init -> %s\n", e.getMessage()));
        }

    }

    public void insert(Task task) {
        if (task == null) {
            stdHandle.panic("-> Task Object expected but got 'null'");
            return;
        }

        String insert = "INSERT INTO taskpool (id, name, description, duedate, status) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET name = excluded.name, description = excluded.description";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(insert)) {

            excQuery.setInt(1, task.getTaskId());
            excQuery.setString(2, task.getTaskName());
            excQuery.setString(3, task.getTaskDesc());
            excQuery.setString(4, task.getDue());
            excQuery.setInt(5, task.getStatus());

            excQuery.executeUpdate();
            // System.out.println("INSERT");

        } catch (SQLException e) {
            stdHandle.panic(String.format("in insert -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic(e.getMessage());

        }

    }

    public List<Task> get() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM taskpool";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            ResultSet res = excQuery.executeQuery();
            // System.out.println("GET");

            while (res.next()) {
                Task newTask = new Task(res.getInt("id"), res.getString("name"), res.getString("description"),
                        res.getString("duedate"));
                // System.out.println("status: " + res.getInt("status"));
                newTask.updateStatus(res.getInt("status"));
                tasks.add(newTask);
            }

            return tasks;

        } catch (SQLException e) {
            stdHandle.panic(String.format("in get -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

        return null;
    }

    public void update(Task Utask) {
        String insert = "UPDATE taskpool SET name = ?, description = ?, duedate = ?, status = ? WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(insert)) {

            excQuery.setString(1, Utask.getTaskName());
            excQuery.setString(2, Utask.getTaskDesc());
            excQuery.setString(3, Utask.getDue());
            excQuery.setInt(4, Utask.getStatus());
            excQuery.setInt(5, Utask.getTaskId());

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in update -> %s\n", e.getMessage()));

        }
    }

    public void updateID(int newId, int currId) {
        String query = "UPDATE taskpool SET id = ? WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.setInt(1, newId);
            excQuery.setInt(2, currId);

            excQuery.executeUpdate();
            // System.out.println("UPDATE");
            // db.commit();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in remove -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

    }

    public void update(int id, int status) {
        String query = "UPDATE taskpool SET status = ? WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.setInt(1, status);
            excQuery.setInt(2, id);

            excQuery.executeUpdate();
            // System.out.println("UPDATE");
            // db.commit();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in remove -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }
    }

    public void updateAllStatus(int prevStatus, int newStatus) {
        String query = "UPDATE taskpool SET status = ? WHERE status = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.setInt(1, prevStatus);
            excQuery.setInt(2, newStatus);

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in updateAllStatus -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

    }

    public void remove(int id) {
        String query = "DELETE FROM taskpool WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.setInt(1, id);

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in remove -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

    }

    public void clear() {
        String query = "DELETE FROM taskpool";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("in clear -> %s\n", e.getMessage()));

        }

    }

}
