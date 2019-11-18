package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.Project;
import github.PullRequestReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


// Question: should we care about close review.

public class NumReviewPerPullRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOpenPullRequest.class);

    public static Optional<Integer> calculateNumReviewPerPR(List<String> files,
                                                            Map<Long, Project> projects) {
        Map<Long, PullRequestReview> pullRequests = new HashMap<>();
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
                        return new GithubJsonParser().readPullRequestReviewEvent(s, false);
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
                        Long id = (Long) s.get("id");
                        Long pulRequestId = (Long) s.get("pull_request_id");
                        if (pullRequests.containsKey(id)) {
                            PullRequestReview pullRequestReview = pullRequests.get(id);
                            pullRequestReview.addReview(pulRequestId);
                        } else {
                            PullRequestReview pullRequestReview = new PullRequestReview();
                            pullRequestReview.addReview(pulRequestId);
                            pullRequests.put(id, pullRequestReview);
                        }
                    });
            pullRequests.entrySet().stream().forEach(entry -> {
                if (projects.containsKey(entry.getKey())) {
                    projects.get(entry.getKey()).setAverageReviewPerPR(entry.getValue().calculateAverageNumOfReview());
                }
            });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }
}
