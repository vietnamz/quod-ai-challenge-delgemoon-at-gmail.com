package github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class PullRequestContainer {
    Long updatedAt = 0L;
    String state = null;
}

public class OpenPullRequest {

    /*
     * why don't we re-use PullRequest class. just want to make sure load into memory what we use only.
     * Will re-design all the thing later
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenPullRequest.class);


    private Map<Long, PullRequestContainer> pullRequests = new HashMap<>();


    public void addPullRequest(Long pullRequestId, String state, Long updateAt) {
        if (pullRequests.containsKey(pullRequestId)) {
            PullRequestContainer pullRequestContainer = pullRequests.get(pullRequestId);
            if (state.equals("closed")) {
                if (updateAt > pullRequestContainer.updatedAt) {
                    // remove close open pull request. Since it's already close
                    pullRequests.remove(pullRequestId);
                }
            } else {
                if (pullRequestContainer.state.equals("closed")) {
                    if (updateAt > pullRequestContainer.updatedAt) {
                        pullRequestContainer.state = state;
                        pullRequestContainer.updatedAt = updateAt;
                    } else {
                        pullRequests.remove(pullRequestId);
                    }
                } else {
                    pullRequestContainer.state = state;
                    pullRequestContainer.updatedAt = updateAt;
                }

            }
        } else {
            PullRequestContainer pullRequestContainer = new PullRequestContainer();
            pullRequestContainer.state = state;
            pullRequestContainer.updatedAt = updateAt;
            pullRequests.put(pullRequestId, pullRequestContainer);

        }
    }

    public int calculateNumOfOpenPullRequest() {
        return pullRequests.size();
    }
}
