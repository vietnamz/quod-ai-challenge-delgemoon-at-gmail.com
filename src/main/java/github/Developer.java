package github;

import java.util.HashMap;
import java.util.Map;

public class Developer {
    private Map<Long, Integer> developers = new HashMap<>();

    public void setDeveloper(Map<Long, Integer> developer) {
        this.developers = developer;
    }

    public Map<Long, Integer> getDeveloper() {
        return developers;
    }

    public void addDeveloper(Long devId) {
        if (developers.containsKey(devId)) {
            Integer count = developers.get(devId);
            developers.replace(devId, ++count);
        } else {
            developers.put(devId, 1);
        }
    }

    public int calculateRatioCommitPerDev() {
        if (developers.size() == 0) {
            return 0;
        }

        int size = developers.size();

        int total = developers.values().parallelStream().reduce((first, second) -> first + second).get();

        return (int) Math.ceil((float) total / (float) size);
    }
}
