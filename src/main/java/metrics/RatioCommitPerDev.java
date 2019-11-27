package metrics;

import github.Commit;
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
 * The type Ratio commit per dev.
 */
public class RatioCommitPerDev extends BaseMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatioCommitPerDev.class);

    private ConcurrentMap<Long, Commit> commits = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Ratio commit per dev.
     *
     * @param properties the properties
     * @param projects   the projects
     */
    public RatioCommitPerDev(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projects) {
        super(properties, projects);
    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = null;
            Long actorId = null;
            if (objs.get("repo_id") == null) {
                //LOGGER.error("There is no id in the entry, ignore");
                return;
            }
            id = (Long) objs.get("repo_id");
            actorId = (Long) objs.get("actor_id");
            if (commits.containsKey(id)) {
                Commit developer = commits.get(id);
                developer.addCreator(actorId);
            } else {
                Commit developer = new Commit();
                developer.addCreator(actorId);
                commits.put(id, developer);
            }
            commits.entrySet().parallelStream().forEach(s -> {
                if (projects.containsKey(s.getKey())) {
                    projects.get(s.getKey()).setRatioCommitPerDev(s.getValue().calculateRatioCommitPerDev());
                }
            });
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("Error while calculating ration of commit per dev {}", ex.getMessage());
        }
    }

    @Override
    public void resetContainer() {
        commits = null;
    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("event_type").equals("PushEvent");
    }

    @Override
    public void calculateMetric() {
        Integer maxRatioCommitPerDev = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getRatioCommitPerDev)).get().getRatioCommitPerDev();
        Integer minRatioCommitPerDev = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getRatioCommitPerDev)).get().getRatioCommitPerDev();
        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float ratioCommitPerDevMetric = 0F;
            if (maxRatioCommitPerDev != 0) {
                ratioCommitPerDevMetric = ((float) project.getRatioCommitPerDev() - (float) minRatioCommitPerDev) /
                        ((float) maxRatioCommitPerDev - (float) minRatioCommitPerDev);
            }
            project.setHeathyScore(project.getHeathyScore() + ratioCommitPerDevMetric);
        }

    }
}
