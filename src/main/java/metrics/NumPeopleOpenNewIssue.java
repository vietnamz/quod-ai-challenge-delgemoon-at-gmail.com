package metrics;

import github.Issue;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Num people open new issue.
 */
public class NumPeopleOpenNewIssue extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumPeopleOpenNewIssue.class);
    private ConcurrentMap<Long, Issue> issues = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Num people open new issue.
     *
     * @param properties the properties
     * @param projectMap the project map
     */
    public NumPeopleOpenNewIssue(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projectMap) {
        super(properties, projectMap);
    }


    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("IssuesEvent") &&
                objs.get("action") != null && objs.get("action").equals("opened");
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
            Long userId = (Long) objs.get("actor_id");
            if (issues.containsKey(id)) {
                Issue issue = issues.get(id);
                issue.addNewCreator(userId);
            } else {
                Issue issue = new Issue();
                issue.addNewCreator(userId);
                issues.put(id, issue);
            }
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating number of people open new issue {}", ex.getMessage());
        }
    }

    @Override
    public void calculateMetric() {
        issues.entrySet().stream().forEach(e -> {
            if (projects.containsKey(e.getKey())) {
                projects.get(e.getKey()).setNumOfPeopleOpenNewIssue(e.getValue().calculateUserOpenNewIssue());
            }
        });
        Integer minNumOfPeopleOpenIssue = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getNumOfPeopleOpenNewIssue)).get().getNumOfPeopleOpenNewIssue();
        Integer maxNumOfPeopleOpenIssue = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getNumOfPeopleOpenNewIssue)).get().getNumOfPeopleOpenNewIssue();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numOfPeopleOpenIssueMetric = 0F;
            if (maxNumOfPeopleOpenIssue != 0) {
                numOfPeopleOpenIssueMetric = ((float) project.getNumOfPeopleOpenNewIssue() - (float) minNumOfPeopleOpenIssue) /
                        ((float) maxNumOfPeopleOpenIssue - (float) minNumOfPeopleOpenIssue);
            }
            project.setHeathyScore(project.getHeathyScore() + numOfPeopleOpenIssueMetric);
        }
    }

    @Override
    public String getName() {
        return "Number of people open new issue";
    }
}
