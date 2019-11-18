package github;

import java.util.HashMap;
import java.util.Map;

public class PullRequestReview {
    private Map<Long, Integer> pullRequest = new HashMap<>();

    public void addReview(Long pullReviewId) {
        if (pullRequest.containsKey(pullReviewId)) {
            Integer count = pullRequest.get(pullReviewId);
            pullRequest.replace(pullReviewId, ++count);
        } else {
            pullRequest.put(pullReviewId, 1);
        }
    }

    public int calculateAverageNumOfReview() {
        if (pullRequest.size() == 0) {
            return 0;
        }
        int total = pullRequest.values().parallelStream().reduce((v1, v2) -> v1 + v2).get();
        return (int) Math.ceil((float) total / pullRequest.size());
    }
}
