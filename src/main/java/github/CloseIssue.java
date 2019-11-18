package github;

import java.util.HashMap;
import java.util.Map;


class IssueState {
    String state;
    Long updatedAt;
}

public class CloseIssue {

    // really crazy with the name!! ahihi
    private Map<Long, IssueState> states = new HashMap<>();

    // I don't like void but it simple.
    public void addIssue(Long issueId, String state, Long updatedAt) {
        if (states.containsKey(issueId)) {
            IssueState issueState = states.get(issueId);
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
            states.put(issueId, issueState);
        }
    }

    public float calculateRatioClosedToOpenIssue() {
        long sizeOfClosedIssue = states.values().parallelStream().filter(value -> value.state.equals("closed"))
                .count();
        long sizeOfOpenIssue = states.size() - sizeOfClosedIssue;
        if (sizeOfOpenIssue == 0) {
            return sizeOfClosedIssue;
        }
        return (float) Math.ceil((float) sizeOfClosedIssue / (float) sizeOfOpenIssue);
    }


}
