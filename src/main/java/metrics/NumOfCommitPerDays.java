package metrics;

import fileutil.FileUtil;
import github.Commit;
import github.GithubJsonParser;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Num of commit per days.
 */
public class NumOfCommitPerDays {

    /**
     * Calculate num of commit per day float.
     *
     * @param commits the commits
     * @return the float
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOfCommitPerDays.class);

    public static Optional<Integer> calculateNumOfCommitPerDay(List<String> files, Map<Long, Project> projects) {
        Map<Long, Commit> commits = new HashMap<>();
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
                        String createdAt = null;
                        if (s.get("id") == null) {
                            //LOGGER.error("There is no id in the entry, ignore");
                            return;
                        }
                        id = (Long) s.get("id");
                        createdAt = (String) s.get("created_at");
                        Long timeEpoch = null;
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

                    });
            commits.entrySet().parallelStream().forEach(
                    entrySet -> {
                        Commit commit = entrySet.getValue();
                        if (projects.containsKey(entrySet.getKey())) {
                            Project project = projects.get(entrySet.getKey());
                            project.setNumCommit(commit.calculateAverageCommit());
                        }
                    }
            );
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }

}
