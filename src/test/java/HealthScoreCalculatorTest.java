import fileutil.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class HealthScoreCalculatorTest {

    private static final String rootFolder = "src/test/resources/githubdata";

    private void deleteDirectoryRecursion(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }


    @After
    public void tearDown() {

        // Make folder to save file
        File dir = new File(rootFolder);
        try {
            deleteDirectoryRecursion(dir);
        } catch (IOException ex) {
        }

    }

    @Ignore
    @Test
    public void healthScoreCalculator_validateInputTime() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-11-11T00:00:00Z", "2019-11-11T20:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 20);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-11-11-19.json.gz");
    }

    @Ignore
    @Test
    public void healthScoreCalculator_validateInputTime_whenDayLess10() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-10-07T00:00:00Z", "2019-10-08T00:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 24);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-10-07-23.json.gz");
    }

    @Test
    public void healthScoreCalculator_downloadAndStore_success1() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.setListOfJsonFiles(Arrays.asList("src/test/resources/test1"));
        healthScoreCalculator.loadAllJson();
        healthScoreCalculator.readProjectInformation();
        healthScoreCalculator.initMetrics();
        healthScoreCalculator.calculateHealthyScore();
        FileUtil.writeOutToCSV(healthScoreCalculator.getProjects(), "src/test/resources/health.csv");

    }

}
