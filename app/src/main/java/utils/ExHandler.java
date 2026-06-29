package utils;

public class ExHandler {

    public void flowError(String err) {
        System.err.printf("Flow Error: %s\n", err);

    }

    public void panic(String err) {
        System.out.printf("Fatal: %s\n", err);
    }

}
