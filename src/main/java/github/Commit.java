package github;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Commit.
 * this wrapper class to represent the number of commit per for a certain project.
 * We will get the average commit. and use it to calculate the healthy of the project.
 */
public class Commit {

    private Long id = null;
    private Map<Long, Integer> commitPerDays = new HashMap<>();

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
     *
     * @param id the id
     */
    public Commit(Long id) {
        this.id = id;
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
}
