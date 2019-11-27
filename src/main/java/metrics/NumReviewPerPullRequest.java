package metrics;

import github.Project;
import github.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


// Question: should we care about close review.

/**
 * The type Num review per pull request.
 */
public class NumReviewPerPullRequest extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOpenPullRequest.class);
    private ConcurrentMap<Long, PullRequest> pullRequests = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Num review per pull request.
     *
     * @param properties the properties
     * @param projectMap the project map
     */
    public NumReviewPerPullRequest(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projectMap) {
        super(properties, projectMap);
    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            Long pulRequestId = (Long) objs.get("pull_request_id");
            if (pullRequests.containsKey(id)) {
                PullRequest pullRequest = pullRequests.get(id);
                pullRequest.addPRReview(pulRequestId);
            } else {
                PullRequest pullRequest = new PullRequest();
                pullRequest.addPRReview(pulRequestId);
                pullRequests.put(id, pullRequest);
            }
            pullRequests.entrySet().stream().forEach(entry -> {
                if (projects.containsKey(entry.getKey())) {
                    projects.get(entry.getKey()).setAverageReviewPerPR(entry.getValue().calculateAverageNumOfReview());
                }
            });
        } catch (Exception e) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("There is a error while calculating number of review per PR {}", e.getMessage());
        }
    }

    @Override
    public void resetContainer() {
        pullRequests = null;

    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("PullRequestReviewCommentEvent");
    }

    @Override
    public void calculateMetric() {
        Integer minAvgNumReviewPerPR = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getAverageReviewPerPR)).get().getAverageReviewPerPR();
        Integer maxAvgNumReviewPerPR = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getAverageReviewPerPR)).get().getAverageReviewPerPR();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Integer numAvgReviewPerPRMetric = 0;
            if (maxAvgNumReviewPerPR != 0) {
                numAvgReviewPerPRMetric = (project.getAverageReviewPerPR() - minAvgNumReviewPerPR) /
                        (maxAvgNumReviewPerPR - minAvgNumReviewPerPR);
            }
            project.setHeathyScore(project.getHeathyScore() + numAvgReviewPerPRMetric);

        }

    }

    @Override
    public String getName() {
        return "The average number of review per PR";
    }
}
