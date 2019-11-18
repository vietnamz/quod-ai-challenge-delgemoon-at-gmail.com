package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.Project;
import github.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NumberOfRelease {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergedPullRequest.class);


    public static Optional<Integer> calCulateNumberOfRelease(List<String> files, Map<Long, Project> projects) {
        Map<Long, Release> releases = new HashMap<>();

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
                        return new GithubJsonParser().readNumberOfRelease(s, false);
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
                    .filter(s -> s.get("action") != null && s.get("action").equals("published"))
                    .forEach(s -> {
                        Long id = (Long) s.get("id");
                        Long releaseId = (Long) s.get("release_id");
                        if (releases.containsKey(id)) {
                            // This should not happen.
                            Release release = releases.get(id);
                            release.addRelease(releaseId);
                        } else {
                            Release release = new Release();
                            release.addRelease(releaseId);
                            releases.put(id, release);
                        }
                    });

            releases.entrySet().stream().forEach(e -> {
                if (projects.containsKey(e.getKey())) {
                    projects.get(e.getKey()).setNumOfReleases(e.getValue().calculateNumOfRelease());
                }
            });

        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);

    }
}
