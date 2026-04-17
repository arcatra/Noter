package utils;

// Imports -------------
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
// ---------------------

public class DataBaseSupport {
    private static final String DBPATH = "../data/taskpool.db";
    private static final String URL = "jdbc:sqlite:" + DBPATH;

    ExHandler stdHandle;

    public DataBaseSupport() {
        this.stdHandle = new ExHandler();
        this.init();

    }

    private void init() {
        try {
            // Force load the class
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("The JVM cannot see the SQLite JAR!");
            System.err.println("Check this path: " + System.getProperty("java.class.path"));
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS taskpool ( " +
                "id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT)";

        try (Connection dbConn = DriverManager.getConnection(URL);
                PreparedStatement excQuery = dbConn.prepareStatement(query)) {

            excQuery.execute();

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in init -> %s\n", e.getMessage()));
        }

    }

    public void insert(Task task) {
        if (task == null) {
            stdHandle.panic("Serious -> Task Object expected but got 'null'");
            return;
        }

        String insert = "INSERT INTO taskpool (id, name, description) VALUES (?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET name = excluded.name, description = excluded.description";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(insert)) {

            excQuery.setInt(1, task.getTaskId());
            excQuery.setString(2, task.getTaskName());
            excQuery.setString(3, task.getTaskDesc());

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in insert -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic(e.getMessage());

        }

    }

    public void update(Task Utask) {
        String insert = "UPDATE taskpool SET name = ?, description = ? WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(insert)) {

            excQuery.setString(1, Utask.getTaskName());
            excQuery.setString(2, Utask.getTaskDesc());
            excQuery.setInt(3, Utask.getTaskId());

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in update -> %s\n", e.getMessage()));

        }
    }

    public void clear() {
        String query = "DELETE FROM taskpool";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in clear -> %s\n", e.getMessage()));

        }

    }

    public void remove(int id) {
        String query = "DELETE FROM taskpool WHERE id = ?";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            excQuery.setInt(1, id);
            excQuery.executeUpdate();

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in remove -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

    }

    public List<Task> get() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM taskpool";

        try (Connection db = DriverManager.getConnection(URL);
                PreparedStatement excQuery = db.prepareStatement(query)) {

            ResultSet res = excQuery.executeQuery();

            while (res.next()) {
                Task newTask = new Task(res.getInt("id"), res.getString("name"), res.getString("description"));
                tasks.add(newTask);
            }

            return tasks;

        } catch (SQLException e) {
            stdHandle.panic(String.format("Serious in get -> %s\n", e.getMessage()));

        } catch (Exception e) {
            stdHandle.panic("Unknown exception: " + e);

        }

        return null;
    }

}
