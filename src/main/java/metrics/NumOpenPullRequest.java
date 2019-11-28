package metrics;

import github.Project;
import github.PullRequest;
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
 * The type Num open pull request.
 */
public class NumOpenPullRequest extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOpenPullRequest.class);
    private ConcurrentMap<Long, PullRequest> openPullRequests = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Num open pull request.
     *
     * @param properties the properties
     * @param projectMap the project map
     */
    public NumOpenPullRequest(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projectMap) {
        super(properties, projectMap);
    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            String updatedAt = (String) objs.get("pr_updated_at");
            if (updatedAt == null) {
                updatedAt = (String) objs.get("created_at");
                if (updatedAt == null) {
                    return;
                }
            }
            Long pulRequestId = (Long) objs.get("pr_id");
            String state = (String) objs.get("pr_state");
            Long updatedDateEpoch = TimeUtil.convertStringToEpochSecond(updatedAt).get();
            if (openPullRequests.containsKey(id)) {
                PullRequest pullRequest = openPullRequests.get(id);
                pullRequest.addPullRequest(pulRequestId, state, updatedDateEpoch);
            } else {
                PullRequest pullRequest = new PullRequest();
                pullRequest.addPullRequest(pulRequestId, state, updatedDateEpoch);
                openPullRequests.put(id, pullRequest);
            }
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating number of open PR {}", ex.getMessage());
        }
    }

    @Override
    public void resetContainer() {
        this.openPullRequests = null;
    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("PullRequestEvent");
    }

    @Override
    public void calculateMetric() {
        openPullRequests.entrySet().stream().forEach(e -> {
            if (this.projects.containsKey(e.getKey())) {
                this.projects.get(e.getKey()).setNumOfOpenPullRequest(e.getValue().calculateNumOfOpenPullRequest());
            }
        });
        Integer maxNumOfOpenPullRequest = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getNumOfOpenPullRequest)).get().getNumOfOpenPullRequest();
        Integer minNumOfOpenPullRequest = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getNumOfOpenPullRequest)).get().getNumOfOpenPullRequest();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Float numOfOpenPullRequestMetric = 0F;
            if (maxNumOfOpenPullRequest != 0) {
                Map.Entry<Long, Project> entry = entrySet.next();
                Project project = entry.getValue();
                numOfOpenPullRequestMetric = ((float) project.getNumOfOpenPullRequest() - (float) minNumOfOpenPullRequest) /
                        ((float) maxNumOfOpenPullRequest - (float) minNumOfOpenPullRequest);
                project.setHeathyScore(project.getHeathyScore() + numOfOpenPullRequestMetric);
            }
        }

    }

    @Override
    public String getName() {
        return "The number of open pull request";
    }
}
