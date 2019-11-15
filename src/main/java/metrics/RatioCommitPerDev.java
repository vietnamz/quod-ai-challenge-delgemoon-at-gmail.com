package metrics;

import fileutil.FileUtil;
import github.Developer;
import github.GithubJsonParser;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RatioCommitPerDev {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatioCommitPerDev.class);

    public static Optional<Integer> calculateRatioCommitPerDev(List<String> files, Map<Long, Project> projects) {
        Map<Long, Developer> developers = new HashMap<>();
        try {
            files.stream().map(f -> FileUtil.getLines(f))
                    .peek(o -> {
                        if (!o.isPresent()) {
                            LOGGER.error("Problem with opening file");
                        }
                    })
                    .filter(o -> o.isPresent())
                    .flatMap(o -> o.get())
                    .map(s -> {
                        return new GithubJsonParser().readCommitsPerDay(s, false);
                    })
                    .peek(o -> {
                        if (!o.isPresent()) {
                            if (LOGGER.isWarnEnabled()) {
                                //LOGGER.warn("Problem with reading json file or the entry is not push event");
                            }
                        }
                    })
                    .filter(o -> o.isPresent())
                    .map(o -> o.get())
                    .forEach(s -> {
                        Long id = null;
                        Long actorId = null;
                        if (s.get("id") == null) {
                            //LOGGER.error("There is no id in the entry, ignore");
                            return;
                        }
                        id = (Long) s.get("id");
                        actorId = (Long) s.get("actor_id");
                        if (developers.containsKey(id)) {
                            Developer developer = developers.get(id);
                            developer.addDeveloper(actorId);
                        } else {
                            Developer developer = new Developer();
                            developer.addDeveloper(actorId);
                            developers.put(id, developer);
                        }
                    });
            developers.entrySet().parallelStream().forEach(s -> {
                if (projects.containsKey(s.getKey())) {
                    projects.get(s.getKey()).setRatioCommitPerDev(s.getValue().calculateRatioCommitPerDev());
                }
            });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }
}
