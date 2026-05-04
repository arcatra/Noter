package utils;

import java.util.List;
import java.util.ArrayList;

// Imports ------------
import noter.Noter;
// -------------------

public class ArgsParser {

    String[] args;
    Noter noter;
    ExHandler stdHandle;
    int len;

    ArrayList<String> validArgs = new ArrayList<>(
            List.of(
                    "-new", "-n",
                    "-display", "-d",
                    "-displayall", "-dpa",
                    "-due", "-db",
                    "-remap", "-rap",
                    "-remove", "-r",
                    "-done", "-do",
                    "-doneall", "-doneAll", "-da",
                    "-update", "-u",
                    "-about", "--about",
                    "-help", "--help"));

    public ArgsParser(String[] args, Noter noter) {
        this.args = args;
        this.noter = noter;
        this.stdHandle = new ExHandler();
        this.len = this.args.length;

        this.checkArgs();
    }

    private int handleNewTask(int cIndex) {
        cIndex++;
        if (cIndex >= this.len) {
            this.stdHandle.panic("Missing values for -new, or invalid values");
            this.stdHandle.message("follow -> -new <name:description>");
            return cIndex;
        }

        // System.out.printf("args[%d]: %s\n", cIndex, this.args[cIndex]);
        if (!this.args[cIndex].contains(":")) {
            this.stdHandle.panic("Not a valid value for -new");
            this.stdHandle.message("follow -> -new <name:description>");
            return cIndex;
        }

        String[] vessal = this.args[cIndex].split(":");
        String tName = vessal[0];
        String tDesc = vessal[1];

        cIndex++;
        String deadLine = "None";
        if (this.len >= 4 && this.args[cIndex].equals("-due")) {
            cIndex++;

            deadLine = this.args[cIndex];

        } else {
            stdHandle.panic("No due date provided\n");
        }

        this.noter.addTask(tName, tDesc, deadLine);

        return cIndex;
    }

    private int handleUpdate(int cIndex) {
        cIndex++;
        if (cIndex + 2 >= this.len) {
            this.stdHandle.panic("Missing values for -update, or invalid values");
            this.stdHandle.message(
                    "Follow -> -update <id> <new task name(. for existing)> <new description(. for existing)> <DueDate(Optional)>");
            return cIndex;

        }

        int id;
        try {
            id = Integer.parseInt(this.args[cIndex]);

        } catch (NumberFormatException e) {
            stdHandle.panic(String.format("Not a valid id: %s\n", this.args[cIndex]));
            return cIndex;

        } catch (Exception e) {
            stdHandle.panic("Unknown exception occured");
            return cIndex;
        }

        cIndex++;
        String tName = this.args[cIndex];
        cIndex++;
        String tDesc = this.args[cIndex];
        cIndex++;
        String due = ".";

        if (!(cIndex >= this.len)) {
            due = this.args[cIndex];
        }

        this.noter.updateTask(id, tName, tDesc, due);

        return cIndex;
    }

    private int handleRemove(int cIndex, String command) {
        cIndex++;
        if (cIndex >= this.len) {
            stdHandle.panic("invalid values");
            this.stdHandle.message(String.format("follow -> %s <Task ID>\n", command));
            return cIndex;

        }

        try {
            int id = Integer.parseInt(this.args[cIndex]);
            this.noter.removeTask(id);

        } catch (NumberFormatException e) {
            stdHandle.panic(String.format("%s: is not a valid task ID\n", this.args[cIndex]));

        } catch (Exception e) {

        }

        return cIndex++;
    }

    private void handleOptions() {
        int index = 0;
        while (index < len) {

            String arg1 = this.args[index];
            int updIndex;

            if (arg1.startsWith("-") && !this.validArgs.contains(arg1)) {
                this.stdHandle.panic(String.format("Cannot find the command %s, use help for more info\n",
                        arg1));
                index++;
                continue;
            }

            // System.out.printf("TEST: arg: %s, index: %d\n", arg1, index);

            switch (arg1) {
                case "-new":
                    updIndex = this.handleNewTask(index);
                    index = updIndex;
                    break;
                case "-remap":
                    this.noter.remap();
                    break;

                case "-remove":
                    updIndex = this.handleRemove(index, "-remove");
                    index = updIndex;
                    break;

                case "-done":
                    updIndex = this.handleRemove(index, "-done");
                    index = updIndex;
                    break;

                case "-clear":
                    this.noter.clearTaskPool();
                    break;

                case "-doneall":
                    this.noter.updateEveryTaskStatus(0, 1);
                    break;

                case "-display":
                    this.noter.displayEveryTask(false);
                    break;

                case "-displayall":
                    this.noter.displayAll();
                    break;

                case "-update":
                    updIndex = this.handleUpdate(index);
                    index = updIndex;
                    break;

                case "-help":
                    this.noter.getHelp();
                    break;

                case "-about":
                    this.noter.getAbout();
                    break;

            }

            index++;

        }
    }

    private void checkArgs() {
        if (!(this.len > 0)) {
            System.out.println("MESSAGE: Could'nt find any args");
            this.noter.getHelp();
            return;

        }

        this.handleOptions();

    }
}
