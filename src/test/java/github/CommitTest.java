package github;

import org.junit.Assert;
import org.junit.Test;

public class CommitTest {

    @Test
    public void commit_incrCommitForDay_success() {
        Commit commit = new Commit();
        commit.incrCommitForDay(1L);
        commit.incrCommitForDay(2L);
        commit.incrCommitForDay(2L);
        commit.incrCommitForDay(2L);
        commit.incrCommitForDay(2L);
        int averageCommit = commit.calculateAverageCommit();
        Assert.assertEquals(averageCommit, 3);
    }
}
