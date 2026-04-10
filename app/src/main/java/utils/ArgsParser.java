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
            List.of("-new", "-display", "-remove", "-about", "-help"));

    public ArgsParser(String[] args) {
        this.args = args;
        this.noter = new Noter();
        this.stdHandle = new ExHandler();

        this.checkArgs(args);
    }

    private int createNewTask(String[] args, int cIndex, int len) {
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

            switch (arg1) {
                case "-new":
                    updIndex = this.createNewTask(args, index, len);
                    index = updIndex;

                    break;

                case "-remove":
                    updIndex = this.noter.removeTask(args, index);
                    index = updIndex;

                    break;

                case "-display":
                    this.noter.displayTasks();
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
