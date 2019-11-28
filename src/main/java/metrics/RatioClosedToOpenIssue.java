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
 * The type Ratio closed to open issue.
 */
public class RatioClosedToOpenIssue extends BaseMetric {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatioClosedToOpenIssue.class);
    private ConcurrentMap<Long, Issue> issues = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Ratio closed to open issue.
     *
     * @param properties the properties
     * @param projects   the projects
     */
    public RatioClosedToOpenIssue(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projects) {
        super(properties, projects);
    }


    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("IssuesEvent");
    }

    @Override
    public void resetContainer() {
        this.issues = null;

    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            String updatedAt = (String) objs.get("issue_updated_at");
            String action = (String) objs.get("issue_action");
            Long issueId = (Long) objs.get("issue_id");
            Long updateTimeEpoch = TimeUtil.convertStringToEpochSecond(updatedAt).get();
            if (issues.containsKey(id)) {
                Issue issue = issues.get(id);
                issue.addIssueState(issueId, action, updateTimeEpoch);
            } else {
                Issue issue = new Issue();
                issue.addIssueState(issueId, action, updateTimeEpoch);
                issues.put(id, issue);
            }
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating ration closed to open issue {}", ex.getMessage());
        }
    }

    @Override
    public void calculateMetric() {
        issues.entrySet().parallelStream().forEach(entry -> {
            if (projects.containsKey(entry.getKey())) {
                projects.get(entry.getKey()).setRationClosedToOpenIssue(entry.getValue().calculateRatioClosedToOpenIssue());
            }
        });
        Float minRatioClosedToOpenIssue = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getRationClosedToOpenIssue)).get().getRationClosedToOpenIssue();
        Float maxRatioClosedToOpenIssue = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getRationClosedToOpenIssue)).get().getRationClosedToOpenIssue();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float ratioOfClosedToOpenIssueMetric = 0F;
            if (maxRatioClosedToOpenIssue != 0) {
                ratioOfClosedToOpenIssueMetric = (project.getRationClosedToOpenIssue() - minRatioClosedToOpenIssue) /
                        (maxRatioClosedToOpenIssue - minRatioClosedToOpenIssue);
            }
            project.setHeathyScore(project.getHeathyScore() + ratioOfClosedToOpenIssueMetric);
        }
    }

    @Override
    public String getName() {
        return "ratio closed to open issue";
    }
}

