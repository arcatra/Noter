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
        String task;
        String desc;

        cIndex++;
        for (int index = cIndex; index < len; index++) {
            if (!args[index].startsWith("-")) {

                if (index >= len) {
                    this.stdHandle.panic("No args after -new command\nUse -help for more info");
                    return cIndex;
                }

                String raw[] = args[index].split(":");
                if (raw.length == 0) {
                    this.stdHandle.panic("No Task Name or Description is provided");
                    return cIndex;
                }

                if (raw.length == 1) {
                    System.out.println("No task name provided, did you missed( : )?\nUse -help for more info");
                    task = "No Name";
                    desc = raw[0];

                } else {

                    task = raw[0];
                    desc = raw[1];

                }

                if (task == null || desc == null) {
                    this.stdHandle.panic("Task or Description is null");
                    System.out.printf("Task: %s, Desc: %s", task, desc);

                    return index;
                }

                this.noter.addTask(task, desc);
                cIndex = index;

            } else {
                return cIndex;

            }
        }
        return cIndex;
    }

    private int handleUpdate(String args[], int cIndex) {
        cIndex++;

        if (cIndex + 2 > args.length) {
            System.out.println("Missing values for -update");
            return cIndex;

        }

        int id;
        String tName = null;
        String tDesc = null;

        try {
            id = Integer.parseInt(args[cIndex]);
        } catch (Exception e) {
            stdHandle.panic(String.format("Not a valid id: %s", args[cIndex]));
            return cIndex++;
        }

        cIndex++;
        tName = args[cIndex];
        cIndex++;
        tDesc = args[cIndex];

        this.noter.updateTask(id, tName, tDesc);

        return cIndex;
    }

    private int handleRemove(String args[], int cIndex, String command) {
        cIndex++;
        if (cIndex >= args.length) {
            stdHandle.panic("No valid value(ID) for command " + command);
            return cIndex;

        }

        try {
            int id = Integer.parseInt(args[cIndex]);
            this.noter.removeTask(id);

        } catch (Exception e) {
            stdHandle.panic(String.format("%s: Not a valid task ID\n", args[cIndex]));

        }

        return cIndex++;
    }

    private void handleOptions(String args[], int len) {
        int index = 0;
        while (index < len) {

            String arg1 = args[index];
            int updIndex;

            if (arg1.startsWith("-") && !this.validArgs.contains(arg1)) {
                System.out.printf("Cannot find the %s %s, use help for more info\n",
                        (arg1.startsWith("-") ? "option" : "command"), arg1);
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
