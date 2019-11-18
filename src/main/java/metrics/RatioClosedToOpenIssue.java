package metrics;

import fileutil.FileUtil;
import github.CloseIssue;
import github.GithubJsonParser;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RatioClosedToOpenIssue {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatioClosedToOpenIssue.class);

    public static Optional<Integer> calculateRatioClosedToOpenIssue(List<String> files,
                                                                    Map<Long, Project> projects) {
        Map<Long, CloseIssue> issues = new HashMap<>();
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
                        return new GithubJsonParser().readIssueRemainOpen(s, false);
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
                        // LOGGER.info("Info some id = {}", s.get("id"));LOGGER.info("Info some id = {}", s.get("id"));
                        Long id = (Long) s.get("id");
                        String updatedAt = (String) s.get("updated_at");
                        String action = (String) s.get("action");
                        Long issueId = (Long) s.get("issue_id");
                        Long updateTimeEpoch = TimeUtil.convertStringToEpochSecond(updatedAt).get();
                        if (issues.containsKey(id)) {
                            CloseIssue closeIssue = issues.get(id);
                            closeIssue.addIssue(issueId, action, updateTimeEpoch);
                        } else {
                            CloseIssue closeIssue = new CloseIssue();
                            closeIssue.addIssue(issueId, action, updateTimeEpoch);
                            issues.put(id, closeIssue);
                        }
                    });
            issues.entrySet().stream().forEach(entry -> {
                if (projects.containsKey(entry.getKey())) {
                    projects.get(entry.getKey()).setRationClosedToOpenIssue(entry.getValue().calculateRatioClosedToOpenIssue());
                }
            });
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(0);
    }
}

