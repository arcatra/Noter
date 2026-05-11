package utils;

public class ExHandler {

    public void panic(String err) {
        System.err.printf("Error: %s\n", err);

    }

    public void message(String msg) {
        System.out.printf("Msg: %s\n", msg);
    }

    public void fatal(String err) {
        System.out.printf("Fatal: %s", err);
    }

    public void stdout(String msg) {
        System.out.println(msg);
    }

}
