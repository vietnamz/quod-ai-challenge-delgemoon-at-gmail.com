package github;

import java.time.LocalDateTime;
import java.util.*;

/*
 * Assumption:
 * growth rate is base on hours basis. divide the number of contributor in 1 hours and calculate accordingly.
 *
 * keep track total hours. start hour and end hour. loop through the hour, check if merged event in there. get
 * the user id, check if user id already exist, if not exist add in.
 */

class ContributorContainer {
    LocalDateTime mergedTime;
    Long userId;
}

public class Contributor {

    private Map<Long, ContributorContainer> contributorMap = new HashMap<>();
    private LocalDateTime startHour = null;
    private LocalDateTime endHour = null;
    private List<LocalDateTime> timeline = new ArrayList<>();


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

    public Contributor() {

    }

    public Contributor(LocalDateTime startHour, LocalDateTime endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.totalHours();
    }

    public void addContributor(Long pullRequestId, Long userId, LocalDateTime mergedTime) {
        if (contributorMap.containsKey(pullRequestId)) {
            ContributorContainer contributorContainer = contributorMap.get(pullRequestId);
            contributorContainer.mergedTime = mergedTime;
            contributorContainer.userId = userId;
        } else {
            ContributorContainer contributorContainer = new ContributorContainer();
            contributorContainer.userId = userId;
            contributorContainer.mergedTime = mergedTime;
            contributorMap.put(pullRequestId, contributorContainer);
        }
    }

    public float calculateContrinutorGrownRate() {
        if (contributorMap.size() == 0) {
            return 0;
        }
        // O(N*T)
        List<Set<Long>> userChains = new ArrayList<>();
        for (int iter = 1; iter < timeline.size(); iter++) {
            Iterator<Map.Entry<Long, ContributorContainer>> iterator = contributorMap.entrySet().iterator();
            Set<Long> users = new HashSet<>();
            while (iterator.hasNext()) {
                Map.Entry<Long, ContributorContainer> entry = iterator.next();
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
}
