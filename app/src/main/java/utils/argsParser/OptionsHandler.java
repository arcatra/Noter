package utils.argsParser;

// Imports ------------
import noter.Noter;
import utils.exp.*;
import utils.ExHandler;

import java.util.ArrayList;
// -------------------

public class OptionsHandler {

    String[] args;
    Noter noter;
    ExHandler stdHandle;
    int len;
    OptionsParser opParser;

    public OptionsHandler(Noter noter, OptionsParser opParser) {
        this.noter = noter;
        this.stdHandle = new ExHandler();
        this.len = this.args.length;
        this.opParser = opParser;
    }

    public void handleNewTask(ArrayList<String> values, String option) {
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

        if (this.opParser.isOptionExixts("--due")) {
            dueDate = this.opParser.getOptionValues("--due").get(0);
        }

        this.noter.addTask(tName, tDesc, dueDate);

    }

    public void handleUpdate(ArrayList<String> values, String option) {
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

    public void handleArchive(ArrayList<String> values, String option) {
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

    public void handleRemove(ArrayList<String> values, String option) {
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

}
