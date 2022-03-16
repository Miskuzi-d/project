package project;

import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws IOException {
        TestProject project = new TestProject();
        try {
            project.createJsonFile(args[0], args[1]);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("filename is empty");
        }
    }
}
