package metrics;

import github.Project;
import github.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Contributor growth over time.
 */
public class ContributorGrowthOverTime extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorGrowthOverTime.class);
    private ConcurrentMap<Long, PullRequest> pullRequests = new ConcurrentHashMap<>();
    /**
     * The Start date.
     */
    LocalDateTime startDate;
    /**
     * The End date.
     */
    LocalDateTime endDate;


    /**
     * Instantiates a new Contributor growth over time.
     *
     * @param properties the properties
     * @param projects   the projects
     * @param startDate  the start date
     * @param endDate    the end date
     */
    public ContributorGrowthOverTime(List<Map<String, Object>> properties,
                                     ConcurrentMap<Long, Project> projects,
                                     LocalDateTime startDate,
                                     LocalDateTime endDate) {
        super(properties, projects);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void calculateMetric() {
        pullRequests.entrySet().stream().forEach(entry -> {
            if (this.projects.containsKey(entry.getKey())) {
                this.projects.get(entry.getKey()).setContributorGrowthRate(entry.getValue().calculateContrinutorGrownRate());
            }
        });
        Float minAvgContributorGrowthRate = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getContributorGrowthRate)).get().getContributorGrowthRate();
        Float maxAvgContributorGrowthRate = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getContributorGrowthRate)).get().getContributorGrowthRate();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numAvgContributorGrowthRate = 0F;
            if (maxAvgContributorGrowthRate != 0) {
                numAvgContributorGrowthRate = ((float) project.getNumCommit() - minAvgContributorGrowthRate) /
                        ((float) maxAvgContributorGrowthRate - minAvgContributorGrowthRate);
            }
            project.setHeathyScore(project.getHeathyScore() + numAvgContributorGrowthRate);
        }

    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("PullRequestEvent") && objs.get("merged") != null && (boolean) objs.get("merged");
    }


    @Override
    public void resetContainer() {
        pullRequests = null;
    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            String mergedAt = (String) objs.get("pr_merged_at");
            Long userId = (Long) objs.get("actor_id");
            Long pulRequestId = (Long) objs.get("pr_id");
            LocalDateTime mergedDate = TimeUtil.convertStringToDateTime(mergedAt).get();
            if (pullRequests.containsKey(id)) {
                // This should not happen.
                PullRequest pullRequest = pullRequests.get(id);
                pullRequest.addContributor(pulRequestId, userId, mergedDate);
            } else {
                PullRequest pullRequest = new PullRequest(this.startDate, this.endDate);
                pullRequest.addContributor(pulRequestId, userId, mergedDate);
                pullRequests.put(id, pullRequest);
            }
        } catch (Exception e) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating contributor growth over time msg={}", e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "The contributor growth over time";
    }
}
