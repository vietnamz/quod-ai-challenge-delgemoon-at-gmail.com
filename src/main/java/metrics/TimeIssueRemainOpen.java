package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.Issue;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Time issue remain open.
 */
public class TimeIssueRemainOpen {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOfCommitPerDays.class);

    /**
     * Calculate time issue remain open optional.
     * <p>
     * I suppose the issue will be open then close only.
     * <p>
     * That means, We opened an issue then it close. We don't consider case it might be re-open.
     * <p>
     * Open --> close. For some specical case like reopen the calculation might be incorrect.
     *
     * @param files    the files
     * @param projects the projects
     * @return the optional
     */
    public static Optional<Integer> calculateTimeIssueRemainOpen(List<String> files,
                                                                 Map<Long, Project> projects,
                                                                 String defaultCloseAt) {
        Map<Long, Issue> issues = new HashMap<>();
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
                    .filter(s -> s.get("action") != null && (s.get("action").equals("closed") || s.get("action").equals("opened")))
                    .forEach(s -> {
                        Long id = (Long) s.get("id");
                        String createdAt = (String) s.get("created_at");
                        String closedAt = (String) s.get("closed_at");
                        Long issueId = (Long) s.get("issue_id");
                        String action = (String) s.get("action");
                        // In case the issue is open state, We haven't had the close yet. Just fill the end time.
                        if (closedAt.equals("null")) {
                            closedAt = defaultCloseAt;
                        }
                        Long openTimeEpoch = TimeUtil.convertStringToEpochSecond(createdAt).get();
                        Long closeTimeEpoch = TimeUtil.convertStringToEpochSecond(closedAt).get();
                        if (issues.containsKey(id)) {
                            Issue issue = issues.get(id);
                            issue.addNewIssue(issueId, openTimeEpoch, closeTimeEpoch);
                            issues.replace(id, issue);
                        } else {
                            Issue issue = new Issue();
                            issue.addNewIssue(issueId, openTimeEpoch, closeTimeEpoch);
                            issues.put(id, issue);
                        }


                    });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }

}
