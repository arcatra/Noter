package utils.argsParser;

// Imports ------------
import noter.Noter;
import utils.exp.ErrorType;
import utils.exp.RunTimeExceptionService;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import utils.ExHandler;
// -------------------

public class ArgsParser {

    String[] args;
    Noter noter;
    ExHandler stdHandle;
    int len;

    private ArrayList<String> whatWentWrong;
    private Map<String, ArrayList<String>> processedOptions;

    private ArrayList<String> validOptions = new ArrayList<>(
            List.of(
                    "-add", "-a",
                    "-clear", "-cl",
                    "-clean", "-cn",
                    "--due",
                    "-list", "-ls",
                    "-listall", "-la",
                    "-archive", "-arc",
                    "-archiveall", "-arca",
                    "-remove", "-rm",
                    "-update", "-u",
                    "-link", "-ln",
                    "--about",
                    "--help",
                    "--usage",
                    "--examples"));

    // Color codes
    private final String RESET = "\u001B[0m";
    private final String REDTEXT = "\u001B[31m";
    private final String GREENTEXT = "\u001B[32m";

    public ArgsParser(String[] args, Noter noter) {
        this.args = args;
        this.noter = noter;
        this.stdHandle = new ExHandler();
        this.len = this.args.length;

        this.whatWentWrong = new ArrayList<>();
        this.processedOptions = new LinkedHashMap<>();

    }

    public void init() {
        if (this.len <= 0) {
            this.noter.getPartialhelp();
            this.stdHandle.panic("No Options found; skipping database connection, use --help for more help");
            return;
        }

        Scanner userIn = new Scanner(System.in);
        this.generateOptions();
        this.executeOptions(userIn);

        userIn.close();

        if (this.whatWentWrong.size() > 0) {

            System.out.printf("\n%sGiven Command: %s %s\n", GREENTEXT, String.join(" ", this.args), RESET);
            System.out.printf("%sWhat went wrong:%s\n\n", REDTEXT, RESET);

            for (String errorMsg : this.whatWentWrong) {
                System.out.printf(errorMsg + "\n\n");
            }
        }
    }

    private ArrayList<String> getOptionValues(int index) {
        ArrayList<String> values = new ArrayList<>();

        while (index < this.len) {
            String option = this.args[index];

            if (option.startsWith("-")) {
                break;
            }

            values.add(option);

            index++;
        }

        return values;
    }

    private void generateOptions() {
        int index = 0;
        while (index < this.len) {
            try {

                String option = this.args[index]; // Gett the current option

                // Don't skip if we're on a OPTION, skip if we're not
                if (!(option.startsWith("-") || option.startsWith("--"))) {
                    index++;
                    continue;

                }

                // Only valid option or OPTION is allowed to pass through.
                if (!(this.validOptions.contains(option))) {
                    index++;
                    throw new RunTimeExceptionService(ErrorType.INVALID_OPTION, option);
                }

                ArrayList<String> values = this.getOptionValues(index + 1);
                this.processedOptions.put(option, values);
                // this.executeOptions(option, values);

                index += values.size() + 1;

            } catch (RunTimeExceptionService e) {
                System.out.printf("\n%s%s%s\n", REDTEXT, e.getMessage(), RESET);
                this.whatWentWrong.add(e.getMessage());
            }

        }

        // System.out.println(this.processedOptions);
    }

    private boolean getUserConfirmation(Scanner userIn, String errMsg) {
        String choice = "";
        System.out.printf("\n%sGot -> %s%s\n", REDTEXT, errMsg, RESET);
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
            throw new RunTimeExceptionService(ErrorType.NULL_VALUE_FOR_OPTION, option);

        }

        if (values.size() < 2) {
            throw new RunTimeExceptionService(ErrorType.MISSING_VALUES, option);
        }

        if (values.get(0).isBlank()) {
            throw new RunTimeExceptionService(ErrorType.MISSING_REQUIRED_VALUE, "\"Task Name\"", option);
        }

        String tName = values.get(0);
        String tDesc = values.get(1);
        String dueDate = "None";

        if (this.processedOptions.containsKey("--due")) {
            dueDate = this.processedOptions.get("--due").get(0);
        }

        this.noter.addTask(tName, tDesc, dueDate);

    }

    private void handleUpdate(ArrayList<String> values, String option) {
        if (values.size() <= 0 || values.size() < 3) {
            throw new RunTimeExceptionService(ErrorType.MISSING_VALUES, option);
        }

        int id = 0;
        try {
            id = Integer.parseInt(values.get(0));

        } catch (NumberFormatException e) {
            throw new RunTimeExceptionService(ErrorType.INVALID_ID, values.get(0), option);

        } catch (Exception e) {
            stdHandle.flowError(e.getMessage());
        }

        String due = ".";
        String tName = values.get(1);
        String tDesc = values.get(2);

        // -u id name desc dueDate[3]
        if (values.size() > 3) {
            due = values.get(3);
        }

        this.noter.updateTask(id, tName, tDesc, due);

    }

    private void handleArchive(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExceptionService(ErrorType.NULL_VALUE_FOR_OPTION, option);
        }

        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.archiveTask(id);

            } catch (NumberFormatException e) {
                throw new RunTimeExceptionService(ErrorType.INVALID_ID, values.get(0), option);

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }

        }

    }

    private void handleRemove(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExceptionService(ErrorType.NULL_VALUE_FOR_OPTION, option);

        }
        for (String index : values) {
            try {
                int id = Integer.parseInt(index);
                this.noter.removeTask(id);

            } catch (NumberFormatException e) {
                throw new RunTimeExceptionService(ErrorType.INVALID_ID, values.get(0), option);

            } catch (Exception e) {
                this.stdHandle.flowError(e.getMessage());

            }
        }

        this.noter.listTasks(0, "-Pending Tasks");

    }

    public void handleTaskLink(ArrayList<String> values, String option) {
        if (values.size() <= 0) {
            throw new RunTimeExceptionService(ErrorType.NULL_VALUE_FOR_OPTION, option);

        }

        if (values.size() < 2) {
            throw new RunTimeExceptionService(ErrorType.INVALID_VALUES, option, "-link[-ln] <Source> <Destination>");

        }

        int source;
        int destination;

        try {
            source = Integer.parseInt(values.get(0));
            destination = Integer.parseInt(values.get(1));

        } catch (Exception e) {
            throw new RunTimeExceptionService(ErrorType.INVALID_VALUES, option, "-link[-ln] <Source> <Destination>");
        }

        this.noter.linkTasks(source, destination);

    }

    public void handleListTaks(ArrayList<String> values) {
        if (values.size() <= 0) {
            this.noter.listTasks(0, "-Pending Tasks");
            return;
        }

        int status = 0;
        try {
            status = Integer.parseInt(values.get(0));
        } catch (NumberFormatException e) {
            throw new RunTimeExceptionService(ErrorType.STATUS_NOT_VALID, values.get(0));

        }

        if (status >= 0 && status <= 1) {
            this.noter.listTasks(status, status == 0 ? "-Pending Tasks" : "-Archived Tasks");
            return;
        }

        throw new RunTimeExceptionService(ErrorType.STATUS_NOT_VALID, status);

    }

    private void executeOptions(Scanner userIn) {
        String option;
        ArrayList<String> values;

        for (Map.Entry<String, ArrayList<String>> entry : this.processedOptions.entrySet()) {
            option = entry.getKey();
            values = entry.getValue();

            try {
                switch (option) {
                    case "-add", "-a":
                        this.handleNewTask(values, option);
                        break;

                    case "-remove", "-rm":
                        this.handleRemove(values, option);
                        break;

                    case "-archive", "-arc":
                        this.handleArchive(values, option);
                        break;

                    case "-clear", "-cl":
                        this.noter.clearTaskPool();
                        break;

                    case "-clean", "-cn":
                        this.noter.cleanTaskPool();
                        break;

                    case "-archiveall", "-arca":
                        this.noter.archiveEveryTask(0, 1);
                        break;

                    case "-list", "-ls":
                        this.handleListTaks(values);
                        break;

                    case "-listall", "-la":
                        this.noter.listTasks(-1, "Tasks");
                        break;

                    case "-update", "-u":
                        this.handleUpdate(values, option);
                        break;

                    case "-link", "-ln":
                        this.handleTaskLink(values, option);
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

            } catch (RunTimeExceptionService e) {
                this.whatWentWrong.add(e.getMessage());

                if (!this.getUserConfirmation(userIn, e.getMessage())) {
                    stdHandle.message("\nExecution Ended\n");
                    return;
                }

            }
        }

    }

}
