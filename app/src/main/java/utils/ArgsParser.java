package utils;

// Imports ------------
import noter.Noter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
// -------------------

public class ArgsParser {

    String[] args;
    Noter noter;
    ExHandler stdHandle;
    int len;

    private ArrayList<String> validFlags = new ArrayList<>(
            List.of(
                    "-add", "-a",
                    "-list", "-l",
                    "-listall", "-la",
                    "-due", "-d",
                    "-remap", "-rp",
                    "-remove", "-r",
                    "-done", "-de",
                    "-doneall", "-doneAll", "-dna",
                    "-update", "-u",
                    "-about", "--about",
                    "-help", "--help"));

    private Map<String, ArrayList<String>> processedFlags = new LinkedHashMap<>();

    public ArgsParser(String[] args, Noter noter) {
        this.args = args;
        this.noter = noter;
        this.stdHandle = new ExHandler();
        this.len = this.args.length;

        this.init();
    }

    private void init() {
        if (!(this.len > 0)) {
            this.noter.getHelp();
            return;

        }

        this.createFlags();
        this.executeFlags();
    }

    private ArrayList<String> getCommandValues(int index) {
        ArrayList<String> values = new ArrayList<>();

        while (index < this.args.length) {
            String flag = this.args[index];

            if ((flag.startsWith("-") || flag.startsWith("--"))) {
                break;
            }

            values.add(flag);

            index++;
        }

        return values;
    }

    private void createFlags() {
        int index = 0;
        while (index < this.args.length) {
            String flag = this.args[index];

            if (!(flag.startsWith("-") || flag.startsWith("--"))) {
                index++;
                continue;

            }

            if (!(this.validFlags.contains(flag))) {
                this.stdHandle.flowError("Not a valid flag: " + flag);
                index++;
                continue;
            }

            ArrayList<String> values = this.getCommandValues(index + 1);
            this.processedFlags.put(flag, values);
            if (values.size() > 0) {
                index += values.size();

            }

            index++;
        }

        System.out.println(processedFlags);
    }

    private void handleNewTask(ArrayList<String> values) {
        if (values.size() <= 0) {
            this.stdHandle.flowError("No values provided for -add");
            return;

        }

        if (values.size() < 2) {
            this.stdHandle.flowError("Missing values for -add");
            this.stdHandle.usage("-add <Task name> <description>");

            return;
        }

        String tName = values.get(0);
        String tDesc = values.get(1);
        String dueDate = "None";

        if (this.processedFlags.containsKey("-due") && this.processedFlags.get("-due").size() > 0) {
            dueDate = this.processedFlags.get("-due").get(0);
        }

        this.noter.addTask(tName, tDesc, dueDate);

    }

    private boolean isValid(int val, int size) {
        return val <= size;

    }

    private void handleUpdate(ArrayList<String> values) {
        if (values.size() <= 0 || !(this.isValid(3, values.size()))) {
            this.stdHandle.flowError("Missing values for -update");
            this.stdHandle.usage(
                    "-update <id> <new task name(. for existing)> <new description(. for existing)> <DueDate(Optional)>");

            return;
        }

        int id = 0;
        try {
            id = Integer.parseInt(values.get(0));

        } catch (NumberFormatException e) {
            stdHandle.flowError(String.format("Not a valid id: %s\n", values.get(0)));
            return;

        } catch (Exception e) {
            stdHandle.flowError(e.getMessage());
        }

        String due = ".";
        String tName = values.get(1);
        String tDesc = values.get(2);

        if (this.isValid(4, values.size())) {
            due = values.get(3);
        }

        this.noter.updateTask(id, tName, tDesc, due);

    }

    private void markAsDone(ArrayList<String> values) {
        if (values.size() <= 0) {
            stdHandle.flowError("invalid values");
            this.stdHandle.usage("-done <Task ID>\n");

        }
        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.updateStatus(id);

            } catch (NumberFormatException e) {
                stdHandle.flowError(String.format("%s: is not a valid task ID\n", index));

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }

        }

        this.noter.listAllTasks(true);

    }

    private void handleRemove(ArrayList<String> values) {
        if (values.size() <= 0) {
            stdHandle.flowError("invalid values");
            this.stdHandle.usage("-remove <Task ID>\n");

        }
        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.removeTask(id);

            } catch (NumberFormatException e) {
                stdHandle.flowError(String.format("%s: is not a valid task ID\n", index));

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }
        }

        this.noter.listAll();

    }

    private void executeFlags() {
        String flag;
        ArrayList<String> values;

        for (Map.Entry<String, ArrayList<String>> cmdEntry : this.processedFlags.entrySet()) {
            flag = cmdEntry.getKey();
            values = cmdEntry.getValue();

            switch (flag) {
                case "-add":
                    this.handleNewTask(values);
                    break;

                case "-remove":
                    this.handleRemove(values);
                    break;

                case "-done":
                    this.markAsDone(values);
                    break;

                case "-clear":
                    this.noter.clearTaskPool();
                    break;

                case "-doneall":
                    this.noter.updateEveryTaskStatus(0, 1);
                    break;

                case "-list":
                    this.noter.listAllTasks(false);
                    break;

                case "-listall":
                    this.noter.listAll();
                    break;

                case "-update":
                    this.handleUpdate(values);
                    break;

                case "-help":
                    this.noter.getHelp();
                    break;

                case "--help":
                    this.noter.getHelp();
                    break;

                case "-about":
                    this.noter.getAbout();
                    break;

                case "--about":
                    this.noter.getAbout();
                    break;

            }

        }
    }

}
