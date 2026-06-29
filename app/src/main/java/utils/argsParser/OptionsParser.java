package utils.argsParser;

// imports ------------------
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

import noter.Noter;
import utils.exp.*;
import utils.ExHandler;

// --------------------------

public class OptionsParser {

    private Noter noter;
    private OptionsHandler opHandler;
    private String[] args;
    private int len;
    private ExHandler stdHandle;

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

    private Map<String, ArrayList<String>> processedOptions = new LinkedHashMap<>();
    private ArrayList<String> whatWentWrong = new ArrayList<>();

    // Color codes
    private final String RESET = "\u001B[0m";
    private final String REDTEXT = "\u001B[31m";
    private final String GREENTEXT = "\u001B[32m";

    public OptionsParser(String[] args, Noter noterObj) {
        this.noter = noterObj;
        this.opHandler = new OptionsHandler(this.noter, this);
        this.stdHandle = new ExHandler();
        this.args = args;
        this.len = args.length;
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

    public boolean isOptionExixts(String option) {
        return this.processedOptions.containsKey(option);

    }

    public ArrayList<String> getOptionValues(String option) {
        if (this.isOptionExixts(option)) {
            return this.processedOptions.get(option);

        }

        return null;
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

    private void executeOptions(Scanner userIn) {
        String option;
        ArrayList<String> values;

        for (Map.Entry<String, ArrayList<String>> entry : this.processedOptions.entrySet()) {
            option = entry.getKey();
            values = entry.getValue();

            try {
                switch (option) {
                    case "-add", "-a":
                        this.opHandler.handleNewTask(values, option);
                        break;

                    case "-remove", "-rm":
                        this.opHandler.handleRemove(values, option);
                        break;

                    case "-archive", "-arc":
                        this.opHandler.handleArchive(values, option);
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
                        this.opHandler.handleListTaks(values);
                        break;

                    case "-listall", "-la":
                        this.noter.listTasks(-1, "Tasks");
                        break;

                    case "-update", "-u":
                        this.opHandler.handleUpdate(values, option);
                        break;

                    case "-link", "-ln":
                        this.opHandler.handleTaskLink(values, option);
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
                    System.out.println("\nExecution Ended\n");
                    return;
                }

            }
        }

    }
}
