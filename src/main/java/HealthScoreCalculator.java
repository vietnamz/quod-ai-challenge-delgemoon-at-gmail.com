import downloader.Downloader;
import fileutil.FileUtil;
import github.GithubJsonParser;
import github.Project;
import metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type HealthScoreCalculator.
 */
public class HealthScoreCalculator {

    private final Logger LOGGER = LoggerFactory.getLogger(HealthScoreCalculator.class);

    private final static String githubArchiveUrl = "https://data.gharchive.org/";

    String endDate = null;
    private List<String> anHoursFiles = new ArrayList<>();
    private List<String> listOfJsonFiles = new ArrayList<>();
    private Map<Long, Project> projects = new HashMap<>();


    /**
     * Just start to see java docwork.
     *
     * @param hello the hello
     */
    public void justStartToSeeJavaDocwork(String hello) {
        System.out.println(hello);
    }

    private String appendToZero(int number) {
        StringBuilder sb = new StringBuilder();
        if (number < 10) {
            sb.append(0);
        }
        sb.append(number);
        return sb.toString();
    }

    /**
     * Validate date time input.
     *
     * @param startDate the start date
     * @param endDate   the end date
     */
    public void validateDateTimeInput(String startDate, String endDate) {
        Optional<LocalDateTime> convertStartDateTime = TimeUtil.convertStringToDateTime(startDate);
        if (!convertStartDateTime.isPresent()) {
            throw new IllegalArgumentException(
                    "Failed to convert the start date input, please make sure it have this format: YYYY-MM-DDTHH:MM:SSZ");
        }
        Optional<LocalDateTime> convertEndDateTime = TimeUtil.convertStringToDateTime(endDate);
        if (!convertEndDateTime.isPresent()) {
            throw new IllegalArgumentException(
                    "Failed to convert the end date input, please make sure it have this format: YYYY-MM-DDTHH:MM:SSZ");
        }

        LocalDateTime startDateTime = convertStartDateTime.get();
        LocalDateTime endDateTime = convertEndDateTime.get();

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException(
                    "The start date must be after the end date"
            );
        }
        LocalDateTime localDateTime = endDateTime.minusHours(1); // [Start, end). exclude the end time.
        while (true) {
            if (localDateTime.isBefore(startDateTime)) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(githubArchiveUrl);
            sb.append(appendToZero(localDateTime.getYear())).append("-");
            sb.append(appendToZero(localDateTime.getMonthValue())).append("-");
            sb.append(appendToZero(localDateTime.getDayOfMonth())).append("-");
            sb.append(localDateTime.getHour());
            sb.append(".json.gz");
            anHoursFiles.add(sb.toString());
            localDateTime = localDateTime.minusHours(1);
        }
        this.endDate = endDate;

    }

    public String getEndDate() {
        return endDate;
    }

    /**
     * Gets an hours files.
     *
     * @return the an hours files
     */
    public List<String> getAnHoursFiles() {
        return anHoursFiles;
    }

    public List<String> getListOfJsonFiles() {
        return listOfJsonFiles;
    }

    public Map<Long, Project> getProjects() {
        return projects;
    }

    public void deleteDirectoryRecursion(File file) throws IOException {
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

    public void downloadAndStoreFile(String location) throws IllegalStateException {

        if (this.getAnHoursFiles().size() == 0) {
            throw new IllegalStateException("Please make sure you input the start date and end date first");
        }

        this.listOfJsonFiles = this.getAnHoursFiles().parallelStream().map(s -> Downloader.downloadResource(s, location))
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.error("Problem with download file");
                    }
                })
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .map(s -> FileUtil.gunzipIt(s))
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.error("Problem with unzip the file");
                    }
                })
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .collect(Collectors.toList());
    }

    public void readProjectInformation() throws IllegalStateException {
        if (this.listOfJsonFiles.size() == 0) {
            LOGGER.warn("No data is download yet!!!!");
            throw new IllegalStateException("No data is download yet!!!!");
        }
        this.listOfJsonFiles.stream().map(f -> FileUtil.getLines(f))
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.error("Problem with opening file");
                    }
                })
                .filter(o -> o.isPresent())
                .flatMap(s -> s.get())
                .map(s -> {
                    return new GithubJsonParser().readRepoInfo(s, false);
                })
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.warn("The data is invalid");
                    }
                })
                .filter(o -> o.isPresent())
                .map(s -> s.get())
                .forEach(s -> {
                    if (s.containsKey("id")) {
                        if (projects.containsKey(s.get("id"))) {
                            return;
                        } else {
                            Project project = new Project();
                            Long id = (Long) s.get("id");
                            project.setId(id);
                            if (s.containsKey("name")) {
                                String name = (String) s.get("name");
                                String org = name.substring(0, name.indexOf("/"));
                                String projectName = name.substring(name.indexOf("/") + 1);
                                project.setName(projectName);
                                project.setOrg(org);
                                projects.put((Long) s.get("id"), project);
                            }
                        }
                    }
                });

    }

    public void calculateHealthyScore() {
        if (this.getProjects().size() == 0) {
            throw new IllegalStateException("Please start downloading file and store before do calculation");
        }
        Integer maxNumberOfcommit = this.getProjects().values().parallelStream()
                .max(Comparator.comparing(Project::getNumCommit)).get().getNumCommit();
        Integer minNumberOfcommit = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getNumCommit)).get().getNumCommit();
        Long minTimeIssueRemainingOpen = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getAverageIssueOpen)).get().getAverageIssueOpen();
        Integer maxRatioCommitPerDev = this.getProjects().values().parallelStream()
                .max(Comparator.comparing(Project::getRatioCommitPerDev)).get().getRatioCommitPerDev();
        Integer minRatioCommitPerDev = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getRatioCommitPerDev)).get().getRatioCommitPerDev();
        Long minTimePullRequestGetMerged = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getAveragePullRequestGetMerged)).get().getAveragePullRequestGetMerged();
        Long maxTimePullRequestGetMerged = this.getProjects().values().parallelStream()
                .max(Comparator.comparing(Project::getAveragePullRequestGetMerged)).get().getAveragePullRequestGetMerged();
        Integer minNumOfRelease = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getNumOfReleases)).get().getNumOfReleases();
        Integer maxNumOfRelease = this.getProjects().values().parallelStream()
                .max(Comparator.comparing(Project::getNumOfReleases)).get().getNumOfReleases();
        Integer minNumOfOpenPullRequest = this.getProjects().values().parallelStream()
                .min(Comparator.comparing(Project::getNumOfOpenPullRequest)).get().getNumOfOpenPullRequest();
        Integer maxNumOfOpenPullRequest = this.getProjects().values().parallelStream()
                .max(Comparator.comparing(Project::getNumOfOpenPullRequest)).get().getNumOfOpenPullRequest();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numOfCommitMetric = 0F;
            if (maxNumberOfcommit != 0) {
                numOfCommitMetric = ((float) project.getNumCommit() - (float) minNumberOfcommit) / ((float) maxNumberOfcommit - (float) minNumberOfcommit);
            }
            Float numTimeIssueRemainMetric = 0F;
            if (project.getAverageIssueOpen() != 0) {
                numTimeIssueRemainMetric = (float) minTimeIssueRemainingOpen / (float) project.getAverageIssueOpen();
            }

            Float ratioCommitPerDevMetric = 0F;
            if (maxRatioCommitPerDev != 0) {
                ratioCommitPerDevMetric = ((float) project.getRatioCommitPerDev() - (float) minRatioCommitPerDev) / ((float) maxRatioCommitPerDev - (float) minRatioCommitPerDev);
            }
            Float numTimePullRequestGetMergedMetric = 0F;
            if (maxTimePullRequestGetMerged != 0) {
                numTimePullRequestGetMergedMetric = ((float) project.getAveragePullRequestGetMerged() - (float) minTimePullRequestGetMerged) / ((float) maxTimePullRequestGetMerged - (float) minTimePullRequestGetMerged);
            }
            Float numOfReleaseMetric = 0F;
            if (maxNumOfRelease != 0) {
                numOfReleaseMetric = ((float) project.getNumOfReleases() - (float) minNumOfRelease) / ((float) maxNumOfRelease - (float) minNumOfRelease);
            }
            Float numOfOpenPullRequestMetric = 0F;
            if (maxNumOfOpenPullRequest != 0) {
                numOfOpenPullRequestMetric = ((float) project.getNumOfOpenPullRequest() - (float) minNumOfOpenPullRequest) / ((float) maxNumOfOpenPullRequest - (float) minNumOfOpenPullRequest);
            }
            Float healthyScore = numOfCommitMetric + numTimeIssueRemainMetric + ratioCommitPerDevMetric + numTimePullRequestGetMergedMetric + numOfReleaseMetric + numOfOpenPullRequestMetric;
            project.setHeathyScore(healthyScore);
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide the start date and end date to continue!!!");
        }
        /*
         * validate the user input date to make it's as we expected
         */
        healthScoreCalculator.validateDateTimeInput(args[0], args[1]);
        healthScoreCalculator.downloadAndStoreFile("src/main/resources/githubdata");
        healthScoreCalculator.readProjectInformation();
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
        healthScoreCalculator.calculateHealthyScore();
        try {
            healthScoreCalculator.deleteDirectoryRecursion(new File("src/main/resources/githubdata"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        FileUtil.writeOutToCSV(healthScoreCalculator.getProjects(), "health_scores.csv");

    }
}
