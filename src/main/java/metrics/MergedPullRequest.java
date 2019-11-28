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
 * The type Merged pull request.
 */
public class MergedPullRequest extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(MergedPullRequest.class);

    private ConcurrentMap<Long, PullRequest> pullRequests = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Merged pull request.
     *
     * @param properties the properties
     * @param projects   the projects
     */
    public MergedPullRequest(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projects) {
        super(properties, projects);
    }


    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("pr_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            String createdAt = (String) objs.get("pr_created_at");
            String mergedAt = (String) objs.get("pr_merged_at");
            Long pulRequestId = (Long) objs.get("pr_id");
            Long createDateEpoch = TimeUtil.convertStringToEpochSecond(createdAt).get();
            Long mergedDateEpoch = TimeUtil.convertStringToEpochSecond(mergedAt).get();
            if (pullRequests.containsKey(id)) {
                // This should not happen.
                PullRequest pullRequest = pullRequests.get(id);
                pullRequest.addNewIssue(pulRequestId, createDateEpoch, mergedDateEpoch);
            } else {
                PullRequest pullRequest = new PullRequest();
                pullRequest.addNewIssue(pulRequestId, createDateEpoch, mergedDateEpoch);
                pullRequests.put(id, pullRequest);
            }
        } catch (Exception e) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
        }
    }

    @Override
    public void resetContainer() {
        pullRequests = null;
    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("PullRequestEvent") && objs.get("merged") != null && (boolean) objs.get("merged");
    }

    @Override
    public void calculateMetric() {
        pullRequests.entrySet().parallelStream().forEach(entry -> {
            if (projects.containsKey(entry.getKey())) {
                projects.get(entry.getKey()).setAveragePullRequestGetMerged(entry.getValue().calculateTheAverageTimePullRequestGetMerged());
            }
        });
        Long minTimePullRequestGetMerged = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getAveragePullRequestGetMerged)).get().getAveragePullRequestGetMerged();
        Long maxTimePullRequestGetMerged = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getAveragePullRequestGetMerged)).get().getAveragePullRequestGetMerged();

        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numTimePullRequestGetMerged = 0F;
            if (maxTimePullRequestGetMerged != 0) {
                numTimePullRequestGetMerged = ((float) project.getAveragePullRequestGetMerged() - minTimePullRequestGetMerged) /
                        ((float) maxTimePullRequestGetMerged - minTimePullRequestGetMerged);
            }
            project.setHeathyScore(project.getHeathyScore() + numTimePullRequestGetMerged);
        }
    }

    @Override
    public String getName() {
        return "The average pull request get merged";
    }
}
