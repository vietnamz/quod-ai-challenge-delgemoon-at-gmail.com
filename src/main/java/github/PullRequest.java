package github;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class PullRequest {

    private Map<Long, PullRequestTime> pullRequestTimes = new HashMap<>();
    private Map<Long, Contributor> contributors = new HashMap<>();
    private LocalDateTime startHour = null;
    private LocalDateTime endHour = null;
    private List<LocalDateTime> timeline = new ArrayList<>();
    private Map<Long, OpenPullRequest> openPullRequests = new HashMap<>();
    private Map<Long, Integer> pullRequestReviews = new HashMap<>();


    private void totalHours() {
        LocalDateTime tmp = this.startHour;
        while (true) {
            if (tmp.isAfter(endHour)) {
                break;
            }
            timeline.add(tmp);
            tmp = tmp.plusHours(1);
        }
    }

    public PullRequest() {
    }

    public PullRequest(LocalDateTime startHour, LocalDateTime endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.totalHours();
    }

    public void addNewIssue(Long pullRequestId, Long open, Long merged) {
        if (pullRequestTimes.containsKey(pullRequestId)) {
            PullRequestTime pullRequestLive = pullRequestTimes.get(pullRequestId);
            /*
             * self-request: How can I ensure the last event is the right one?
             * Should I do updating this to the new one ?
             * I only get the event where the merged key is true.
             * I suppose there is only one event like that. hmm!!
             */
            pullRequestLive.open = open;
            pullRequestLive.merged = merged;
            pullRequestTimes.replace(pullRequestId, pullRequestLive);
        } else {
            PullRequestTime pullRequestLive = new PullRequestTime();
            pullRequestLive.open = open;
            pullRequestLive.merged = merged;
            pullRequestTimes.put(pullRequestId, pullRequestLive);
        }
    }

    public long calculateTheAverageTimePullRequestGetMerged() {
        if (pullRequestTimes.size() == 0) {
            return 0;
        }
        int size = pullRequestTimes.size();

        List<Long> total = pullRequestTimes.entrySet()
                .parallelStream().map(s -> s.getValue().merged - s.getValue().open).collect(Collectors.toList());
        Long result = total.parallelStream().reduce((s1, s2) -> s1 + s2).get();
        return (long) Math.ceil((double) result.doubleValue() / (double) size);

    }

    public void addContributor(Long pullRequestId, Long userId, LocalDateTime mergedTime) {
        if (contributors.containsKey(pullRequestId)) {
            Contributor contributorContainer = contributors.get(pullRequestId);
            contributorContainer.mergedTime = mergedTime;
            contributorContainer.userId = userId;
        } else {
            Contributor contributorContainer = new Contributor();
            contributorContainer.userId = userId;
            contributorContainer.mergedTime = mergedTime;
            contributors.put(pullRequestId, contributorContainer);
        }
    }

    public float calculateContrinutorGrownRate() {
        if (contributors.size() == 0) {
            return 0;
        }
        // O(N*T)
        List<Set<Long>> userChains = new ArrayList<>();
        for (int iter = 1; iter < timeline.size(); iter++) {
            Iterator<Map.Entry<Long, Contributor>> iterator = contributors.entrySet().iterator();
            Set<Long> users = new HashSet<>();
            while (iterator.hasNext()) {
                Map.Entry<Long, Contributor> entry = iterator.next();
                if (entry.getValue().mergedTime.isAfter(timeline.get(iter - 1)) &&
                        entry.getValue().mergedTime.isBefore(timeline.get(iter))) {
                    users.add(entry.getValue().userId);
                }
            }
            userChains.add(users);
        }
        if (userChains.size() == 0) {
            return 0;
        }
        List<Float> rateCache = new ArrayList<>();
        for (int iter = 1; iter < userChains.size(); ++iter) {
            int oldVal = userChains.get(iter - 1).size();
            int newVal = userChains.get(iter).size();
            if (oldVal == 0) {
                rateCache.add((float) newVal);
            } else {
                float rate = ((float) newVal - (float) oldVal) / (float) oldVal;
                rateCache.add(rate);
            }
        }
        if (rateCache.size() == 0) {
            return 0;
        }
        float averageRate = rateCache.parallelStream().reduce((v1, v2) -> v1 + v2).get() / (float) rateCache.size();
        if (averageRate < 0) {
            return 0;
        }
        return averageRate;
    }


    public void addPullRequest(Long pullRequestId, String state, Long updateAt) {
        if (openPullRequests.containsKey(pullRequestId)) {
            OpenPullRequest pullRequestContainer = this.openPullRequests.get(pullRequestId);
            if (state.equals("closed")) {
                if (updateAt > pullRequestContainer.updatedAt) {
                    // remove close open pull request. Since it's already close
                    openPullRequests.remove(pullRequestId);
                }
            } else {
                if (pullRequestContainer.state.equals("closed")) {
                    if (updateAt > pullRequestContainer.updatedAt) {
                        pullRequestContainer.state = state;
                        pullRequestContainer.updatedAt = updateAt;
                    } else {
                        openPullRequests.remove(pullRequestId);
                    }
                } else {
                    pullRequestContainer.state = state;
                    pullRequestContainer.updatedAt = updateAt;
                }

            }
        } else {
            OpenPullRequest pullRequestContainer = new OpenPullRequest();
            pullRequestContainer.state = state;
            pullRequestContainer.updatedAt = updateAt;
            openPullRequests.put(pullRequestId, pullRequestContainer);

        }
    }

    public int calculateNumOfOpenPullRequest() {
        return openPullRequests.size();
    }


    public void addPRReview(Long pullReviewId) {
        if (pullRequestReviews.containsKey(pullReviewId)) {
            Integer count = pullRequestReviews.get(pullReviewId);
            pullRequestReviews.replace(pullReviewId, ++count);
        } else {
            pullRequestReviews.put(pullReviewId, 1);
        }
    }

    public int calculateAverageNumOfReview() {
        if (pullRequestReviews.size() == 0) {
            return 0;
        }
        int total = pullRequestReviews.values().parallelStream().reduce((v1, v2) -> v1 + v2).get();
        return (int) Math.ceil((float) total / pullRequestReviews.size());
    }
}
