package utils.exp;

public enum ErrorType {

    // In-Valid Objects
    INVALID_COMMAND("%s command/flag not found"),
    INVALID_VALUES("Invalid value(s) for %s, Usage: %s"),
    INVALID_ID("Invalid ID %s, for %s"),

    // Null Vlaues
    NULL_VALUES_FOR_COMMAND("No values provided for %s"),
    MISSING_VALUES("No values provided for %s, Use --help for more info"),
    NULL_INPUT("Ecpected %s but got null"),

    // DataBase Connection
    DATABASE_CONNECTION_FAILURE("Failed to connect to the database"),

    // TaskPool and tasks
    TASKPOOL_IS_EMPTY("No tasks - Task pool is empty"),
    TASK_NOT_FOUND("No task found with id %s"),
    EMPTY_OR_NOT_FOUND("No task found with id %s, or TASKPOOL_IS_EMPTY"),
    STATUS_NOT_VALID("Task status %d is not valid(current: %d), only 0 or 1 is allowed");

    private String message;

    private ErrorType(String defaultMessage) {
        this.message = defaultMessage;

    }

    public String getMessage() {
        return this.message;
    }

}
