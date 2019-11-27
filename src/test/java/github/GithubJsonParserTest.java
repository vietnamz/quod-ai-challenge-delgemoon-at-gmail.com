package github;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class GithubJsonParserTest {

    @Test
    public void githubJsonParser_readRepoInfo_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> repoInfo = githubJsonParser
                .readRepoInfo("src/test/resources/test_individual_1.json", true).get();
        Assert.assertEquals(repoInfo.get("id"), 28688495L);
        Assert.assertEquals(repoInfo.get("name"), "petroav/6.828");
    }

    @Test
    public void githubJsonParser_readCommitsPerDay_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> commitPerDay = githubJsonParser
                .readCommitsPerDay("src/test/resources/test_individual_2.json", true).get();
        Assert.assertEquals(commitPerDay.get("id"), 28671719L);
        Assert.assertEquals(commitPerDay.get("actor_id"), 3854017L);
        Assert.assertEquals(commitPerDay.get("created_at"), "2015-01-01T15:00:01Z");
    }

    @Test
    public void githubJsonParser_readIssueRemainOpen_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> issues = githubJsonParser
                .readIssueRemainOpen("src/test/resources/test_individual_3.json", true).get();
        Assert.assertEquals(issues.get("id"), 18510170L);
        Assert.assertEquals(issues.get("created_at"), "2015-01-01T07:28:55Z");
        Assert.assertEquals(issues.get("closed_at"), "2015-01-01T15:03:09Z");
        Assert.assertEquals(issues.get("action"), "closed");
        Assert.assertEquals(issues.get("issue_id"), 53214955L);
    }

    @Test
    public void githubJsonParser_readIssueRemainOpen_success1() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> issues = githubJsonParser
                .readIssueRemainOpen("src/test/resources/test_individual_4.json", true).get();
        Assert.assertEquals(issues.get("id"), 18510170L);
        Assert.assertEquals(issues.get("actor_id"), 3883059L);
        Assert.assertEquals(issues.get("created_at"), "2015-01-01T07:28:55Z");
        Assert.assertEquals(issues.get("closed_at"), "null");
        Assert.assertEquals(issues.get("action"), "opened");
        Assert.assertEquals(issues.get("issue_id"), 53214955L);
    }

    @Test
    public void githubJsonParser_readMergedPullRequest_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> pullRequest = githubJsonParser
                .readMergedPullRequest("src/test/resources/test_individual_5.json", true).get();
        Assert.assertEquals(pullRequest.get("id"), 13315164L);
        Assert.assertEquals(pullRequest.get("created_at"), "2013-11-18T12:59:21Z");
        Assert.assertEquals(pullRequest.get("updated_at"), "2015-01-01T15:00:42Z");
        Assert.assertEquals(pullRequest.get("merged_at"), "null");
        Assert.assertEquals(pullRequest.get("merged"), false);
        Assert.assertEquals(pullRequest.get("pull_request_id"), 10047845L);
        Assert.assertEquals(pullRequest.get("state"), "closed");
        Assert.assertEquals(pullRequest.get("user_id"), 1144478L);
    }

    @Test
    public void githubJsonParser_readNumOfRelease_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> releases = githubJsonParser
                .readNumberOfRelease("src/test/resources/test_individual_6.json", true).get();
        Assert.assertEquals(releases.get("id"), 28688179L);
        Assert.assertEquals(releases.get("action"), "published"); // collect action to filter. just care about published.
        Assert.assertEquals(releases.get("release_id"), 818679L);
    }

    @Test
    public void githubJsonParser_readNumCommentPerPR_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> pr = githubJsonParser
                .readPullRequestReviewEvent("src/test/resources/test_individual_7.json", true).get();
        Assert.assertEquals(pr.get("id"), 28668460L);
        Assert.assertEquals(pr.get("comment_id"), 22400085L);
        Assert.assertEquals(pr.get("pull_request_id"), 26743766L);
    }
    
}
