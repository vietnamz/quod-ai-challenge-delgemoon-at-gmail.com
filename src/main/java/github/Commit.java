package github;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Commit.
 * this wrapper class to represent the number of commit per for a certain project.
 * We will get the average commit. and use it to calculate the healthy of the project.
 */
public class Commit {

    private Map<Long, Integer> commitPerDays = new HashMap<>();
    private Map<Long, Integer> commitCreators = new HashMap<>();

    /**
     * Incr commit for day.
     * increment the commit for a specific date.
     *
     * @param theEpochDay the the epoch day
     */
    public void incrCommitForDay(Long theEpochDay) {
        if (theEpochDay == null) {
            return;
        }
        if (commitPerDays.containsKey(theEpochDay)) {
            Integer count = commitPerDays.get(theEpochDay);
            commitPerDays.replace(theEpochDay, ++count);
        } else {
            commitPerDays.put(theEpochDay, 1);
        }
    }


    /**
     * Instantiates a new Commit.
     */
    public Commit() {
    }

    /**
     * Calculate average commit int.
     * <p>
     * The formula is:
     * Day 1: number of commit is 7
     * Day 2: number of commit is 8
     * <p>
     * average = (7 + 8) / 2
     *
     * @return the int
     */
    public int calculateAverageCommit() {
        if (commitPerDays.size() == 0) {
            return 0;
        }
        int size = commitPerDays.size();
        Integer result = commitPerDays.values().parallelStream().reduce((a, b) -> a + b).get();
        return (int) Math.ceil((double) result / (double) size);
    }


    /**
     * Add creator.
     *
     * @param devId the dev id
     */
    public void addCreator(Long devId) {
        if (commitCreators.containsKey(devId)) {
            Integer count = commitCreators.get(devId);
            commitCreators.replace(devId, ++count);
        } else {
            commitCreators.put(devId, 1);
        }
    }

    /**
     * Calculate ratio commit per dev int.
     *
     * @return the int
     */
    public int calculateRatioCommitPerDev() {
        if (commitCreators.size() == 0) {
            return 0;
        }

        int size = commitCreators.size();

        int total = commitCreators.values().parallelStream().reduce((first, second) -> first + second).get();

        return (int) Math.ceil((float) total / (float) size);
    }
}
