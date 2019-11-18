import fileutil.FileUtil;
import github.Project;
import metrics.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    @Test
    public void test() {
        String hello = "Hello World";
        Assert.assertEquals(hello, "Hello World");
    }

    @Test
    public void healthScoreCalculator_validateInputTime() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-11-11T00:00:00Z", "2019-11-11T20:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 20);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-11-11-19.json.gz");
    }

    @Test
    public void healthScoreCalculator_validateInputTime_whenDayLess10() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-10-07T00:00:00Z", "2019-10-08T00:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 24);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-10-07-23.json.gz");
    }

    @Test
    public void healthScoreCalculator_downloadAndStore_success() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-10-14T00:00:00Z", "2019-10-14T01:00:00Z");
        healthScoreCalculator.downloadAndStoreFile(rootFolder);
        healthScoreCalculator.readProjectInformation();
        Map<Long, Project> projectMap = healthScoreCalculator.getProjects();
        Project project = projectMap.get(214733191L);
        Assert.assertEquals(project.getName(), "artist-song-modules-online-web-pt-090819");
        Assert.assertEquals(project.getOrg(), "ETyannikov");
        NumOfCommitPerDays
                .calculateNumOfCommitPerDay(healthScoreCalculator.getListOfJsonFiles(),
                        healthScoreCalculator.getProjects());
        TimeIssueRemainOpen.calculateTimeIssueRemainOpen(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects(), healthScoreCalculator.getEndDate());
        RatioCommitPerDev.calculateRatioCommitPerDev(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        MergedPullRequest.calculatePullRequestGetMerged(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        NumberOfRelease.calCulateNumberOfRelease(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        NumOpenPullRequest.calculateOpenPullRequest(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        NumPeopleOpenNewIssue.calculatePeopeOpenNewIssue(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        RatioClosedToOpenIssue.calculateRatioClosedToOpenIssue(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        NumReviewPerPullRequest.calculateNumReviewPerPR(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects());
        ContributorGrowthOverTime.calculatePullRequestGetMerged(healthScoreCalculator.getListOfJsonFiles(),
                healthScoreCalculator.getProjects(), healthScoreCalculator.getLocalDateTimeStart(),
                healthScoreCalculator.getLocalDateTimeEnd());
        healthScoreCalculator.calculateHealthyScore();
        FileUtil.writeOutToCSV(healthScoreCalculator.getProjects(), "src/test/resources/health.csv");
    }
}
