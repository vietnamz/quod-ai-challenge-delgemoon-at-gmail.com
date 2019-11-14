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
        Assert.assertEquals(commitPerDay.get("created_at"), "2015-01-01T15:00:01Z");
    }

    @Test
    public void githubJsonParser_readIssueRemainOpen_success() {
        GithubJsonParser githubJsonParser = new GithubJsonParser();
        Map<String, Object> issues = githubJsonParser
                .readIssueRemainOpen("src/test/resources/test_individual_3.json", true).get();
        Assert.assertEquals(issues.get("id"), 18510170L);
        System.out.println(issues.get("created_at"));
        Assert.assertEquals(issues.get("created_at"), "2015-01-01T07:28:55Z");
    }
}
