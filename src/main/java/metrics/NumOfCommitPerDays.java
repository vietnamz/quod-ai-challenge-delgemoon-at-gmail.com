package metrics;

import github.Commit;
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
 * The type Num of commit per days.
 */
public class NumOfCommitPerDays extends BaseMetric {

    /**
     * Calculate num of commit per day float.
     *
     * @param commits the commits
     * @return the float
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOfCommitPerDays.class);
    private ConcurrentMap<Long, Commit> commits = new ConcurrentHashMap<>();
    private Object lock = new Object();


    /**
     * Instantiates a new Num of commit per days.
     *
     * @param properties the properties
     * @param projectMap the project map
     */
    public NumOfCommitPerDays(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projectMap) {
        super(properties, projectMap);
    }


    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id;
            String createdAt;
            if (objs.get("repo_id") == null) {
                return;
            }
            id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            createdAt = (String) objs.get("created_at");
            Long timeEpoch;
            if (TimeUtil.convertStringToEpochDay(createdAt).isPresent()) {
                timeEpoch = TimeUtil.convertStringToEpochDay(createdAt).get();
            } else {
                return;
            }
            if (commits.containsKey(id)) {
                Commit commit = commits.get(id);
                commit.incrCommitForDay(timeEpoch);
            } else {
                Commit commit = new Commit();
                commit.incrCommitForDay(timeEpoch);
                commits.put(id, commit);
            }
            commits.entrySet().parallelStream().forEach(
                    entrySet -> {
                        Commit commit = entrySet.getValue();
                        if (this.projects.containsKey(entrySet.getKey())) {
                            //synchronized (lock) {
                            Project project = this.projects.get(entrySet.getKey());
                            project.setNumCommit(commit.calculateAverageCommit());
                            //}

                        }
                    }
            );
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating number of commit {}", ex.getMessage());
        }

    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return true;
    }

    @Override
    public void calculateMetric() {
        Integer maxNumberOfcommit = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getNumCommit)).get().getNumCommit();
        Integer minNumberOfcommit = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getNumCommit)).get().getNumCommit();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numOfCommitMetric = 0F;
            if (maxNumberOfcommit != 0) {
                numOfCommitMetric = ((float) project.getNumCommit() - (float) minNumberOfcommit) /
                        ((float) maxNumberOfcommit - (float) minNumberOfcommit);
            }
            project.setHeathyScore(project.getHeathyScore() + numOfCommitMetric);
        }
    }

    @Override
    public void resetContainer() {
        Map<Long, Commit> commits = null;
    }

    @Override
    public String getName() {
        return "The average number of commit per day";
    }
}

