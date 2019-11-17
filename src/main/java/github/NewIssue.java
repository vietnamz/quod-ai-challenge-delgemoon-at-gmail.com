package github;

import java.util.HashSet;
import java.util.Set;

public class NewIssue {

    // user_id.?
    private Set<Long> users = new HashSet<>();


    public void addNewUser(Long userId) {
        // only check the issue with action is open.
        users.add(userId);
    }

    public int calculateUserOpenNewIssue() {
        return users.size();
    }
}
