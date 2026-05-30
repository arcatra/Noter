package utils;

public class ExHandler {

    public void flowError(String err) {
        System.err.printf("Flow Error: %s\n", err);

    }

    public void message(String msg) {
        System.out.printf("Message: %s\n", msg);
    }

    public void panic(String err) {
        System.out.printf("Fatal: %s\n", err);
    }

}
