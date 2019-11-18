package metrics;

import fileutil.FileUtil;
import github.Contributor;
import github.GithubJsonParser;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContributorGrowthOverTime {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorGrowthOverTime.class);

    public static Optional<Integer> calculatePullRequestGetMerged(List<String> files,
                                                                  Map<Long, Project> projects,
                                                                  LocalDateTime startDate,
                                                                  LocalDateTime endDate) {
        Map<Long, Contributor> pullRequests = new HashMap<>();
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
                        String mergedAt = (String) s.get("merged_at");
                        Long userId = (Long) s.get("user_id");
                        Long pulRequestId = (Long) s.get("pull_request_id");
                        Long mergedDateEpoch = TimeUtil.convertStringToEpochSecond(mergedAt).get();
                        LocalDateTime mergedDate = TimeUtil.convertStringToDateTime(mergedAt).get();
                        if (pullRequests.containsKey(id)) {
                            // This should not happen.
                            Contributor pullRequest = pullRequests.get(id);
                            pullRequest.addContributor(pulRequestId, userId, mergedDate);
                        } else {
                            Contributor pullRequest = new Contributor(startDate, endDate);
                            pullRequest.addContributor(pulRequestId, userId, mergedDate);
                            pullRequests.put(id, pullRequest);
                        }
                    });
            pullRequests.entrySet().stream().forEach(entry -> {
                if (projects.containsKey(entry.getKey())) {
                    projects.get(entry.getKey()).setContributorGrowthRate(entry.getValue().calculateContrinutorGrownRate());
                }
            });

        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }
}
