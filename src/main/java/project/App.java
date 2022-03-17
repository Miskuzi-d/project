package project;

public class App {
    public static void main(String[] args) {
        TestProject project = new TestProject();
        try {
            project.createJsonFile(args[0], args[1]);
        } catch (Throwable e) {
            System.out.println(e.getMessage().isBlank() ? "not enough arguments" : e.getMessage());
        }
    }
}
