package github;

import java.util.HashSet;
import java.util.Set;

public class Release {

    Set<Long> numOfReleases = new HashSet<>();


    public void addRelease(Long releaseId) {
        numOfReleases.add(releaseId);
    }

    public int calculateNumOfRelease() {
        return numOfReleases.size();
    }
}
