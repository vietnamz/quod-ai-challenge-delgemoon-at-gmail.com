package github;

import java.util.*;
import java.util.stream.Collectors;


public class Issue {

    private Map<Long, IssueTime> issueLifetimes = new HashMap<>();
    // really crazy with the name!! ahihi
    private Map<Long, IssueState> issueStates = new HashMap<>();
    private Set<Long> issueCreator = new HashSet<>();

    public void addIssueLifeTime(Long issueId, Long open, Long close) {
        if (issueLifetimes.containsKey(issueId)) {
            IssueTime issueLive = issueLifetimes.get(issueId);
            /*
             * if We get an open first, there is no close date. We fill the maximum end date first.
             * So We should check if the newer close date is less than the current, then we will update.
             * if we get an close first, then then current will less than the current. no update.
             *
             */
            if (close != null) {
                if (issueLive.close > close) {
                    issueLive.close = close;
                }
            }
            issueLifetimes.replace(issueId, issueLive);
        } else {
            IssueTime issueLive = new IssueTime();
            issueLive.open = open;
            issueLive.close = close;
            issueLifetimes.put(issueId, issueLive);
        }
    }

    public long calculateTheAverageIssueOpen() {
        if (issueLifetimes.size() == 0) {
            return 0;
        }
        int size = issueLifetimes.size();

        List<Long> total = issueLifetimes.entrySet()
                .parallelStream().map(s -> s.getValue().close - s.getValue().open).collect(Collectors.toList());
        Long result = total.parallelStream().reduce((s1, s2) -> s1 + s2).get();
        return (long) Math.ceil(result.doubleValue() / (double) size);
    }

    // I don't like void but it simple.
    public void addIssueState(Long issueId, String state, Long updatedAt) {
        if (issueStates.containsKey(issueId)) {
            IssueState issueState = issueStates.get(issueId);
            if (issueState.updatedAt < updatedAt) {
                issueState.updatedAt = updatedAt;
                issueState.state = state;
            } else {
                //nothing to do.
            }

        } else {
            IssueState issueState = new IssueState();
            issueState.state = state;
            issueState.updatedAt = updatedAt;
            issueStates.put(issueId, issueState);
        }
    }

    public float calculateRatioClosedToOpenIssue() {
        long sizeOfClosedIssue = issueStates.values().parallelStream().filter(value -> value.state.equals("closed"))
                .count();
        long sizeOfOpenIssue = issueStates.size() - sizeOfClosedIssue;
        if (sizeOfOpenIssue == 0) {
            return sizeOfClosedIssue;
        }
        return (float) Math.ceil((float) sizeOfClosedIssue / (float) sizeOfOpenIssue);
    }


    public void addNewCreator(Long userId) {
        // only check the issue with action is open.
        issueCreator.add(userId);
    }

    public int calculateUserOpenNewIssue() {
        return issueCreator.size();
    }

}
