package metrics;

import github.Issue;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Time issue remain open.
 */
public class TimeIssueRemainOpen extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeIssueRemainOpen.class);
    /**
     * The Issues.
     */
    ConcurrentMap<Long, Issue> issues = new ConcurrentHashMap<>();
    private String defaultCloseAt = null;

    /**
     * Instantiates a new Time issue remain open.
     *
     * @param properties the properties
     * @param projects   the projects
     * @param end        the end
     */
    public TimeIssueRemainOpen(List<Map<String, Object>> properties,
                               ConcurrentMap<Long, Project> projects,
                               String end) {
        super(properties, projects);
        defaultCloseAt = end;
    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return (objs.get("event_type").equals("IssuesEvent") &&
                objs.get("issue_action") != null && (objs.get("issue_action").equals("closed") ||
                objs.get("issue_action").equals("opened")));
    }

    @Override
    public void resetContainer() {
        issues = null;
    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            String createdAt = (String) objs.get("issue_created_at");
            String closedAt = (String) objs.get("issue_updated_at");
            Long issueId = (Long) objs.get("issue_id");
            // In case the issue is open state, We haven't had the close yet. Just fill the end time.
            if (closedAt.equals("null")) {
                closedAt = defaultCloseAt;
            }
            // REVIEW: Do we really need to make this to second.
            // The value seem to be big. and not reliable. But I have to go with this approach first.
            Long openTimeEpoch = TimeUtil.convertStringToEpochSecond(createdAt).get();
            Long closeTimeEpoch = TimeUtil.convertStringToEpochSecond(closedAt).get();
            if (issues.containsKey(id)) {
                Issue issue = issues.get(id);
                issue.addIssueLifeTime(issueId, openTimeEpoch, closeTimeEpoch);
                issues.replace(id, issue);
            } else {
                Issue issue = new Issue();
                issue.addIssueLifeTime(issueId, openTimeEpoch, closeTimeEpoch);
                issues.put(id, issue);
            }
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating number of issue remaining opened {}", ex.getMessage());
        }
    }

    @Override
    public void calculateMetric() {
        this.issues.entrySet().parallelStream().forEach(entry -> {
            if (projects.containsKey(entry.getKey())) {
                projects.get(entry.getKey()).setAverageIssueOpen(entry.getValue().calculateTheAverageIssueOpen());
            }
        });
        Long minTimeIssueRemainingOpen = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getAverageIssueOpen)).get().getAverageIssueOpen();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numTimeIssueRemainMetric = 0F;
            if (project.getAverageIssueOpen() != 0) {
                numTimeIssueRemainMetric = (float) minTimeIssueRemainingOpen /
                        (float) project.getAverageIssueOpen();
            }
            project.setHeathyScore(project.getHeathyScore() + numTimeIssueRemainMetric);

        }
    }

    @Override
    public String getName() {
        return "The average time issue remain opened";
    }
}
