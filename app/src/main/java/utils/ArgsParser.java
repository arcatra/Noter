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

    ArrayList<String> validArgs = new ArrayList<>(
            List.of(
                    "-new", "-n",
                    "-display", "-d",
                    "-remove", "-r",
                    "-done", "-do",
                    "-doneall", "-doneAll", "-da",
                    "-update", "-u",
                    "-about",
                    "-help"));

    public ArgsParser(String[] args) {
        this.args = args;
        this.noter = new Noter();
        this.stdHandle = new ExHandler();

        this.checkArgs(args);
    }

    private int handleNewTask(String[] args, int cIndex, int len) {
        cIndex++;
        if (cIndex + 2 >= len) {
            this.stdHandle.panic("Missing values for -new, or invalid values");
            this.stdHandle.message("follow -> -new <name:description>");
            return cIndex;
        }

        if (!args[cIndex].contains(":")) {
            this.stdHandle.panic("Not a valid value for -new");
            this.stdHandle.message("follow -> -new <name:description>");
            return cIndex;
        }

        String[] vessal = args[cIndex].split(":");
        String tName = vessal[0];
        String tDesc = vessal[1];

        this.noter.addTask(tName, tDesc);

        return cIndex;
    }

    private int handleUpdate(String args[], int cIndex) {
        cIndex++;
        if (cIndex + 2 >= args.length) {
            this.stdHandle.panic("Missing values for -update, or invalid values");
            this.stdHandle.message("Follow -> -update <id> <new task name> <new description>");
            return cIndex;

        }

        int id;
        try {
            id = Integer.parseInt(args[cIndex]);
        } catch (Exception e) {
            stdHandle.panic(String.format("Not a valid id: %s\n", args[cIndex]));
            return cIndex;
        }

        cIndex++;
        String tName = args[cIndex];
        cIndex++;
        String tDesc = args[cIndex];

        this.noter.updateTask(id, tName, tDesc);

        return cIndex;
    }

    private int handleRemove(String args[], int cIndex, String command) {
        cIndex++;
        if (cIndex >= args.length) {
            stdHandle.panic("invalid values");
            this.stdHandle.message(String.format("follow -> %s <Task ID>\n", command));
            return cIndex;

        }

        try {
            int id = Integer.parseInt(args[cIndex]);
            this.noter.removeTask(id);

        } catch (Exception e) {
            stdHandle.panic(String.format("%s: is not a valid task ID\n", args[cIndex]));

        }

        return cIndex++;
    }

    private void handleOptions(String args[], int len) {
        int index = 0;
        while (index < len) {

            String arg1 = args[index];
            int updIndex;

            if (arg1.startsWith("-") && !this.validArgs.contains(arg1)) {
                this.stdHandle.panic(
                        String.format("Cannot find the %s %s, use help for more info\n",
                                (arg1.startsWith("-") ? "option" : "command"),
                                arg1));
                index++;
                continue;
            }

            // System.out.printf("TEST: arg: %s, index: %d\n", arg1, index);

            switch (arg1) {
                case "-new":
                    updIndex = this.handleNewTask(args, index, len);
                    index = updIndex;

                    break;

                case "-remove":
                    updIndex = this.handleRemove(args, index, "-remove");
                    index = updIndex;

                    break;

                case "-done":
                    updIndex = this.handleRemove(args, index, "-done");
                    index = updIndex;
                    break;

                case "-clear":
                    this.noter.clearTaskPool();
                    break;

                case "-doneall":
                    this.noter.clearTaskPool();
                    break;

                case "-display":
                    this.noter.displayTasks();
                    break;

                case "-update":
                    updIndex = this.handleUpdate(args, index);
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

    private void checkArgs(String args[]) {
        int len = args.length;
        if (!(args.length > 0)) {
            System.out.println("MESSAGE: Could'nt find any args");
            return;

        }

        this.handleOptions(args, len);

    }
}
