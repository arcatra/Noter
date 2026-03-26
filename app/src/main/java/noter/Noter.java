package noter;

// Imports ----------
import java.io.BufferedReader;
import java.io.FileReader;

// import java.io.BufferedWriter;

// -----------------

public class Noter {
    public void displayAbout() {
        try (BufferedReader bfr = new BufferedReader(new FileReader("src/main/resources/about.txt"))) {
            String line;

            while ((line = bfr.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.out.println("Error occured!: " + e);
        }
    }

    public static void main(String[] args) {
        Noter obj = new Noter();
        obj.displayAbout();
    }
}
