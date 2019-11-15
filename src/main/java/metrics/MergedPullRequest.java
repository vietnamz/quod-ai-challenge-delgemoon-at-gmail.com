package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.Project;
import github.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Merged pull request.
 */
public class MergedPullRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MergedPullRequest.class);

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
    public static Optional<Integer> calculatePullRequestGetMerged(List<String> files,
                                                                  Map<Long, Project> projects) {
        Map<Long, PullRequest> pullRequests = new HashMap<>();
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
                        return new GithubJsonParser().readMergedPullRequest(s, false);
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
                    .filter(s -> s.get("merged") != null && (boolean) s.get("merged"))
                    .forEach(s -> {
                        Long id = (Long) s.get("id");
                        String createdAt = (String) s.get("created_at");
                        String mergedAt = (String) s.get("merged_at");
                        Long pulRequestId = (Long) s.get("pull_request_id");
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
                    });

            pullRequests.entrySet().stream().forEach(e -> {
                if (projects.containsKey(e.getKey())) {
                    projects.get(e.getKey()).setAveragePullRequestGetMerged(e.getValue().calculateTheAverageTimePullRequestGetMerged());
                }
            });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }

}
