package github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PullRequestLive {
    Long open = null;
    Long merged = null;
}

public class PullRequest {

    private Map<Long, PullRequestLive> pullRequests = new HashMap<>();

    public void addNewIssue(Long pullRequestId, Long open, Long merged) {
        if (pullRequests.containsKey(pullRequestId)) {
            PullRequestLive pullRequestLive = pullRequests.get(pullRequestId);
            /*
             * self-request: How can I ensure the last event is the right one?
             * Should I do updating this to the new one ?
             * I only get the event where the merged key is true.
             * I suppose there is only one event like that. hmm!!
             */
            pullRequestLive.open = open;
            pullRequestLive.merged = merged;
            pullRequests.replace(pullRequestId, pullRequestLive);
        } else {
            PullRequestLive pullRequestLive = new PullRequestLive();
            pullRequestLive.open = open;
            pullRequestLive.merged = merged;
            pullRequests.put(pullRequestId, pullRequestLive);
        }
    }

    public long calculateTheAverageTimePullRequestGetMerged() {
        if (pullRequests.size() == 0) {
            return 0;
        }
        int size = pullRequests.size();

        List<Long> total = pullRequests.entrySet()
                .parallelStream().map(s -> s.getValue().merged - s.getValue().open).collect(Collectors.toList());
        Long result = total.parallelStream().reduce((s1, s2) -> s1 + s2).get();
        return (long) Math.ceil((double) result.doubleValue() / (double) size);

    }
}
