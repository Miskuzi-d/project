package project;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class AppTest {

    TestProject project = new TestProject();
    String JSON_PATH = "src/main/java/project/jsonFiles/";
    String firstFile = "data-1.bin";
    String incorrectFile = "test.bin";
    String secondFile = "test.json";

    @After
    public void deleteFile() {
        File jsonObject = new File(JSON_PATH + secondFile);
        jsonObject.delete();
    }


    @Test
    public void testApp() throws IOException {
        File jsonFile = new File(JSON_PATH + secondFile);

        project.createJsonFile(firstFile, secondFile);
        if (jsonFile.exists()) Assert.assertTrue(true);
        else Assert.fail();

        project.createJsonFile(incorrectFile, secondFile);
        if (!jsonFile.exists()) Assert.assertTrue(true);
        else Assert.fail();
    }

}
