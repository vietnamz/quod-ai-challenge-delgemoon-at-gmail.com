import downloader.Downloader;
import fileutil.FileUtil;
import github.JsonParser;
import github.Project;
import metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * The type HealthScoreCalculator.
 */
public class HealthScoreCalculator {

    private final Logger LOGGER = LoggerFactory.getLogger(HealthScoreCalculator.class);

    private final static String githubArchiveUrl = "https://data.gharchive.org/";
    public final String defaultLocation = "src/main/resources/githubdata";

    /**
     * The End date.
     */
    String endDate = null;
    private List<String> anHoursFiles = new ArrayList<>();
    private List<String> listOfJsonFiles = new ArrayList<>();
    private ConcurrentMap<Long, Project> projects = new ConcurrentHashMap<>();
    private LocalDateTime localDateTimeStart = null;
    private LocalDateTime localDateTimeEnd = null;
    /**
     * The Jsons.
     */
    List<Map<String, Object>> jsons = null;
    /**
     * The Metric actions.
     */
    List<IMetricAction> metricActions = new ArrayList<>();


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
        this.localDateTimeEnd = endDateTime;
        this.localDateTimeStart = startDateTime;

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

    /**
     * Gets end date.
     *
     * @return the end date
     */
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

    /**
     * Gets list of json files.
     *
     * @return the list of json files
     */
    public List<String> getListOfJsonFiles() {
        return listOfJsonFiles;
    }

    /**
     * Gets projects.
     *
     * @return the projects
     */
    public Map<Long, Project> getProjects() {
        return projects;
    }

    /**
     * Gets local date time end.
     *
     * @return the local date time end
     */
    public LocalDateTime getLocalDateTimeEnd() {
        return localDateTimeEnd;
    }

    /**
     * Gets local date time start.
     *
     * @return the local date time start
     */
    public LocalDateTime getLocalDateTimeStart() {
        return localDateTimeStart;
    }

    /**
     * Delete directory recursion.
     *
     * @param file the file
     * @throws IOException the io exception
     */
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

    /**
     * Download and store file.
     *
     * @param location the location
     * @throws IllegalStateException the illegal state exception
     */
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

    /**
     * Read project information.
     *
     * @throws IllegalStateException the illegal state exception
     */
    public void readProjectInformation() throws IllegalStateException {
        this.jsons.forEach(s -> {
            if (s.containsKey("repo_id")) {
                if (projects.containsKey(s.get("repo_id"))) {
                    return;
                } else {
                    Project project = new Project();
                    Long id = (Long) s.get("repo_id");
                    project.setId(id);
                    if (s.containsKey("repo_name")) {
                        String org;
                        String name = (String) s.get("repo_name");
                        if (!name.contains("/")) {
                            project.setName(name);
                            project.setOrg(name);
                            projects.put((Long) s.get("repo_id"), project);
                            return;
                        }
                        org = name.substring(0, name.indexOf("/"));
                        String projectName = name.substring(name.indexOf("/") + 1);
                        project.setName(projectName);
                        project.setOrg(org);
                        projects.put((Long) s.get("repo_id"), project);
                    }
                }
            }
        });

    }

    /**
     * Load all json.
     *
     * @throws IllegalStateException the illegal state exception
     */
    public void loadAllJson() throws IllegalStateException {
        if (this.listOfJsonFiles.size() == 0) {
            LOGGER.warn("No data is download yet!!!!");
            throw new IllegalStateException("No data is download yet!!!!");
        }
        LocalTime start = LocalTime.now();
        LOGGER.info("-----STARTING LOADING JSON DATA INTO MEMORY, PLS WAIT--------");
        this.jsons = this.listOfJsonFiles.parallelStream().map(f -> FileUtil.getLines(f))
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.error("Problem with opening file");
                    }
                })
                .filter(Optional::isPresent)
                .flatMap(Optional::get)
                .map(s -> new JsonParser().readJsonContent(s))
                .peek(o -> {
                    if (!o.isPresent()) {
                        LOGGER.warn("The data is invalid");
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());
        LOGGER.info("------------------FINISHED LOADING------------------");
        LOGGER.info("------------------ELAPSE TIME = {} --------------------", Duration.between(start, LocalTime.now()));
    }

    /**
     * Init metrics.
     */
    public void initMetrics() {
        metricActions.add(new NumOfCommitPerDays(jsons, this.projects));
        metricActions.add(new ContributorGrowthOverTime(jsons, this.projects,
                this.localDateTimeStart, this.localDateTimeEnd));
        metricActions.add(new TimeIssueRemainOpen(jsons, this.projects, this.endDate));
        metricActions.add(new RatioCommitPerDev(jsons, this.projects));
        metricActions.add(new RatioClosedToOpenIssue(jsons, this.projects));
        metricActions.add(new NumReviewPerPullRequest(jsons, this.projects));
        metricActions.add(new NumPeopleOpenNewIssue(jsons, this.projects));
        metricActions.add(new NumOpenPullRequest(jsons, this.projects));
        metricActions.add(new NumberOfRelease(jsons, this.projects));
        metricActions.add(new MergedPullRequest(jsons, this.projects));
    }

    /**
     * Calculate healthy score.
     */
    public void calculateHealthyScore() {
        if (this.projects.size() == 0) {
            throw new IllegalStateException("Please start downloading file and store before do calculation");
        }
        LocalTime start = LocalTime.now();
        metricActions.forEach(
                IMetricAction::execute
        );
        LOGGER.info("------------------FINISHED ALL METRICS------------------");
        LOGGER.info("------------------in total {} --------------------", Duration.between(start, LocalTime.now()));
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
        healthScoreCalculator.downloadAndStoreFile(healthScoreCalculator.defaultLocation);
        healthScoreCalculator.loadAllJson();
        healthScoreCalculator.readProjectInformation();
        healthScoreCalculator.initMetrics();
        healthScoreCalculator.calculateHealthyScore();
        try {
            healthScoreCalculator.deleteDirectoryRecursion(new File(healthScoreCalculator.defaultLocation));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        FileUtil.writeOutToCSV(healthScoreCalculator.getProjects(), "health_scores.csv");

    }

    /**
     * Sets list of json files.
     *
     * @param listOfJsonFiles the list of json files
     */
    public void setListOfJsonFiles(List<String> listOfJsonFiles) {
        this.listOfJsonFiles = listOfJsonFiles;
    }
}
