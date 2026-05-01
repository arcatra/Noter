package utils;

public class ExHandler {

    public void panic(String err) {
        System.err.printf("Error: %s\n", err);

    }

    public void message(String msg) {
        System.out.printf("Msg: %s\n", msg);
    }

}
