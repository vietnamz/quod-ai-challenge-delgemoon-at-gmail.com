package metrics;

import github.Project;
import github.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Number of release.
 */
public class NumberOfRelease extends BaseMetric {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergedPullRequest.class);
    private ConcurrentMap<Long, Release> releases = new ConcurrentHashMap<>();


    /**
     * Instantiates a new Number of release.
     *
     * @param properties the properties
     * @param projects   the projects
     */
    public NumberOfRelease(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projects) {
        super(properties, projects);
    }

    @Override
    public boolean filter(Map<String, Object> objs) {
        return objs.get("action") != null && objs.get("action").equals("published");
    }

    @Override
    public void resetContainer() {
        releases = null;

    }

    @Override
    public void run(Map<String, Object> objs) {
        try {
            Long id = (Long) objs.get("repo_id");
            //LOGGER.info("running {} with id {}", this.getClass().getName(), id);
            Long releaseId = (Long) objs.get("release_id");
            if (releases.containsKey(id)) {
                // This should not happen.
                Release release = releases.get(id);
                release.addRelease(releaseId);
            } else {
                Release release = new Release();
                release.addRelease(releaseId);
                releases.put(id, release);
            }
        } catch (Exception ex) {
            objs.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Key = {}, value ={}", entry.getKey(), entry.getValue());
                    }
            );
            LOGGER.error("There is a error while calculating number of release {}", ex.getMessage());
        }
    }

    @Override
    public void calculateMetric() {
        releases.entrySet().stream().forEach(e -> {
            if (this.projects.containsKey(e.getKey())) {
                this.projects.get(e.getKey()).setNumOfReleases(e.getValue().calculateNumOfRelease());
            }
        });
        Integer minNumOfRelease = this.projects.values().parallelStream()
                .min(Comparator.comparing(Project::getNumOfReleases)).get().getNumOfReleases();
        Integer maxNumOfRelease = this.projects.values().parallelStream()
                .max(Comparator.comparing(Project::getNumOfReleases)).get().getNumOfReleases();

        Iterator<Map.Entry<Long, Project>> entrySet = projects.entrySet().iterator();
        while (entrySet.hasNext()) {
            Map.Entry<Long, Project> entry = entrySet.next();
            Project project = entry.getValue();
            Float numNumOfRelease = 0F;
            if (maxNumOfRelease != 0) {
                numNumOfRelease = ((float) project.getNumCommit() - minNumOfRelease) /
                        ((float) maxNumOfRelease - minNumOfRelease);
            }
            project.setHeathyScore(project.getHeathyScore() + numNumOfRelease);
        }
    }

    @Override
    public String getName() {
        return "The average number of release";
    }
}
