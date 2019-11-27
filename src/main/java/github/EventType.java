package github;

public enum EventType {
    PUSH("PushEvent"),
    PULL_REQUEST("PullRequestEvent"),
    RELEASE("ReleaseEvent"),
    PULL_REQUEST_REVIEW_COMMENT("PullRequestReviewCommentEvent"),
    ISSUE("IssuesEvent"),
    ISSUE_COMMENT("IssueCommentEvent");


    private String type;

    public static EventType valueOfEvent(String event) {
        if (event == null) {
            return null;
        }
        for (EventType e : values()) {
            if (e.type.equals(event)) {
                return e;
            }
        }
        return null;
    }

    EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

