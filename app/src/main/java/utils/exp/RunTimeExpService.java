package utils.exp;

public class RunTimeExpService extends RuntimeException {
    public ErrorType errorType;

    public RunTimeExpService(ErrorType errorType, Object... args) {

        String errorMsg = errorType.getMessage().length() > 0
                ? String.format(errorType.getMessage(), args)
                : errorType.getMessage();

        errorMsg = String.format("%s: %s", errorType.name(), errorMsg);
        super(
                errorMsg, // Error message for RuntimeException
                null, // Cause for exception
                false, // Enable Supperession
                false // Writable Stack Trace
        );
        this.errorType = errorType;

    }

    // public String getErrorMessage() {
    // return this.errorType.getMessage();
    // }
    //
    public String getErrorName() {
        return this.errorType.name();
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

}
