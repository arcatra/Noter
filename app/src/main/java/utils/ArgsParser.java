package utils;

// Imports ------------
import noter.Noter;
import utils.exp.ErrorType;
import utils.exp.RunTimeExpService;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
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
                    "--due",
                    "-remap", "-rp",
                    "-remove", "-rm",
                    "-done", "-m",
                    "-clear", "-cr",
                    "-doneall", "-ma",
                    "-update", "-u",
                    "--about",
                    "--help",
                    "--usage",
                    "--examples"));

    private ArrayList<String> whatWentWrong;
    private Map<String, ArrayList<String>> processedFlags;

    // Color codes
    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";

    public ArgsParser(String[] args, Noter noter) {
        this.args = args;
        this.noter = noter;
        this.stdHandle = new ExHandler();
        this.len = this.args.length;

        this.whatWentWrong = new ArrayList<>();
        this.processedFlags = new LinkedHashMap<>();

    }

    public void init() {
        if (this.len <= 0) {
            this.noter.getPartialhelp();
            this.stdHandle.panic("No flags found; skipping database connection, use --help for more help");
            return;
        }

        Scanner userIn = new Scanner(System.in);

        this.generateFlags(userIn);
        this.executeFlags(userIn);

        userIn.close();

        if (this.whatWentWrong.size() > 0) {

            System.out.printf("\n%sGiven Command: %s %s\n", GREEN, String.join(" ", this.args), RESET);
            System.out.printf("%sWhat went wrong%s\n\n", RED, RESET);

            for (String errorMsg : this.whatWentWrong) {
                System.out.printf(errorMsg + "\n\n");
            }
        }
    }

    private ArrayList<String> getCommandValues(int index) {
        ArrayList<String> values = new ArrayList<>();

        while (index < this.len) {
            String flag = this.args[index];

            if (flag.startsWith("-")) {
                break;
            }

            if (flag.startsWith("--") && flag.equals("--due")) {
                values.add(flag);
                index++;
                continue;
            }

            values.add(flag);

            index++;
        }

        return values;
    }

    private void generateFlags(Scanner userIn) {
        int index = 0;
        while (index < this.len) {
            try {

                String flag = this.args[index]; // Gett the current flag

                // Don't skip if we're on a command, skip if we're not
                if (!(flag.startsWith("-") || flag.startsWith("--"))) {
                    index++;
                    continue;

                }

                // Only valid flag or command is allowed to parr through.
                if (!(this.validFlags.contains(flag))) {
                    index++;
                    throw new RunTimeExpService(ErrorType.INVALID_COMMAND, flag);
                }

                ArrayList<String> values = this.getCommandValues(index + 1);
                this.processedFlags.put(flag, values);
                // this.executeFlags(flag, values);

                index += values.size() + 1;

            } catch (RunTimeExpService e) {
                System.out.println(e.getMessage());
                this.whatWentWrong.add(e.getMessage());

                // if (!this.getUserConfirmation(userIn, e.getMessage())) {
                // return;
                // }

                // index++;
            }
        }

        System.out.println(processedFlags);
    }

    private boolean getUserConfirmation(Scanner userIn, String errMsg) {
        String choice = "";
        System.out.printf("\n%sGot -> %s%s\n", RED, errMsg, RESET);
        try {
            System.out.printf("Continue?[y/n]: ");
            choice = userIn.nextLine();

            if (choice.toLowerCase().equals("n")) {
                return false;
            }

        } catch (InputMismatchException ie) {
            System.out.println("Not valid, continue.");
        }

        return true;
    }

    private void handleNewTask(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExpService(ErrorType.NULL_VALUES_FOR_COMMAND, option);

        }

        if (values.size() < 2 || values.get(0).isBlank()) {
            throw new RunTimeExpService(ErrorType.MISSING_VALUES, option);
        }

        String tName = values.get(0);
        String tDesc = values.get(1);
        String dueDate = "None";

        if (this.processedFlags.containsKey("--due")) {
            dueDate = this.processedFlags.get("--due").get(0);
        }

        this.noter.addTask(tName, tDesc, dueDate);

    }

    private boolean isValidSize(int val, int size) {
        return val <= size;

    }

    private void handleUpdate(ArrayList<String> values, String command) {
        if (values.size() <= 0 || !(this.isValidSize(3, values.size()))) {
            throw new RunTimeExpService(ErrorType.MISSING_VALUES, command);
        }

        int id = 0;
        try {
            id = Integer.parseInt(values.get(0));

        } catch (NumberFormatException e) {
            throw new RunTimeExpService(ErrorType.INVALID_ID, values.get(0), command);

        } catch (Exception e) {
            stdHandle.flowError(e.getMessage());
        }

        String due = ".";
        String tName = values.get(1);
        String tDesc = values.get(2);

        if (this.isValidSize(4, values.size())) {
            due = values.get(3);
        }

        this.noter.updateTask(id, tName, tDesc, due);

    }

    private void markAsDone(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExpService(ErrorType.NULL_VALUES_FOR_COMMAND, option);
        }

        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.updateStatus(id);

            } catch (NumberFormatException e) {
                throw new RunTimeExpService(ErrorType.INVALID_ID, values.get(0), option);

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }

        }

        this.noter.listAllTasks(true);

    }

    private void handleRemove(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExpService(ErrorType.NULL_VALUES_FOR_COMMAND, option);

        }
        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.removeTask(id);

            } catch (NumberFormatException e) {
                throw new RunTimeExpService(ErrorType.INVALID_ID, values.get(0), option);

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }
        }

        this.noter.listAll();

    }

    private void executeFlags(Scanner userIn) {
        String flag;
        ArrayList<String> values;

        for (Map.Entry<String, ArrayList<String>> entry : this.processedFlags.entrySet()) {
            flag = entry.getKey();
            values = entry.getValue();

            try {
                switch (flag) {
                    case "-add", "-a":
                        this.handleNewTask(values, flag);
                        break;

                    case "-remove", "-rm":
                        this.handleRemove(values, flag);
                        break;

                    case "-done", "-m":
                        this.markAsDone(values, flag);
                        break;

                    case "-clear", "-cr":
                        this.noter.clearTaskPool();
                        break;

                    case "-doneall", "-ma":
                        this.noter.updateEveryTaskStatus(0, 1);
                        break;

                    case "-list", "-l":
                        this.noter.listAllTasks(false);
                        break;

                    case "-listall", "-la":
                        this.noter.listAll();
                        break;

                    case "-update", "-u":
                        this.handleUpdate(values, flag);
                        break;

                    case "--help":
                        this.noter.getEveryHelp();
                        break;

                    case "--usage":
                        this.noter.hGetUsage();
                        break;

                    case "--examples":
                        this.noter.hGetExamples();
                        break;

                    case "--about":
                        this.noter.getAbout();
                        break;

                }

            } catch (RunTimeExpService e) {
                // System.out.println("\n" + e.getMessage());
                this.whatWentWrong.add(e.getMessage());

                if (!this.getUserConfirmation(userIn, e.getMessage())) {
                    stdHandle.message("\nExecution Ended\n");
                    return;
                }

            }
        }

    }

}
