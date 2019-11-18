package github;

import java.util.HashSet;
import java.util.Set;

public class Release {

    Set<Long> counts = new HashSet<>();


    public void addRelease(Long releaseId) {
        counts.add(releaseId);
    }

    public int calculateNumOfRelease() {
        return counts.size();
    }
}
