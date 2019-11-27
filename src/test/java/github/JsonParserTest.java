package github;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;

public class JsonParserTest {

    JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParserTest.class);

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessCommitEvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_2.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            Assert.assertEquals(objs.get().get("repo_id"), 28671719L);
            Assert.assertEquals(objs.get().get("actor_id"), 3854017L);
            Assert.assertEquals(objs.get().get("repo_name"), "rspt/rspt-theme");
            Assert.assertEquals(objs.get().get("push_id"), 536863970L);
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessIssueEvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_3.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            Assert.assertEquals(objs.get().get("repo_id"), 18510170L);
            Assert.assertEquals(objs.get().get("actor_id"), 3883059L);
            Assert.assertEquals(objs.get().get("repo_name"), "GreatDevelopers/LibreHatti");
            Assert.assertEquals(objs.get().get("issue_created_at"), "2015-01-01T07:28:55Z");
            Assert.assertEquals(objs.get().get("issue_user_user_id"), 3883059L);
            Assert.assertEquals(objs.get().get("event_type"), "IssuesEvent");
            Assert.assertEquals(objs.get().get("issue_id"), 53214955L);
            Assert.assertEquals(objs.get().get("assignee_user_id"), 3883059L);
            Assert.assertEquals(objs.get().get("issue_updated_at"), "2015-01-01T15:03:09Z");
            Assert.assertEquals(objs.get().get("issue_closed_at"), "2015-01-01T15:03:09Z");
            Assert.assertEquals(objs.get().get("issue_action"), "closed");
        } catch (Exception e) {
        }
    }

    @Ignore
    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessPREvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_5.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            Assert.assertEquals(objs.get().get("repo_id"), 13315164L);
            Assert.assertEquals(objs.get().get("actor_id"), 62572L);
            Assert.assertEquals(objs.get().get("repo_name"), "markbirbeck/sublime-text-shell-command");
            Assert.assertEquals(objs.get().get("pr_state"), "closed");
            Assert.assertEquals(objs.get().get("pr_id"), 10047845L);
            Assert.assertEquals(objs.get().get("PR_Base_user_id"), 62572L);
            Assert.assertEquals(objs.get().get("PR_Head_user_id"), 1144478L);
            Assert.assertEquals(objs.get().get("PR_Head_repo_user_id"), 1144478L);
            Assert.assertEquals(objs.get().get("PR_Base_repo_user_id"), 62572L);
            Assert.assertEquals(objs.get().get("user_user_id"), 1144478L);
            Assert.assertEquals(objs.get().get("event_type"), "PullRequestEvent");
            Assert.assertEquals(objs.get().get("pr_created_at"), "2013-11-18T12:59:21Z");
            Assert.assertEquals(objs.get().get("pr_merged_at"), "null");
            Assert.assertEquals(objs.get().get("pr_updated_at"), "2015-01-01T15:00:42Z");
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessPREvent2() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_9.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            /*objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });*/

            Assert.assertEquals(objs.get().get("repo_id"), 3542607L);
            Assert.assertEquals(objs.get().get("actor_id"), 1277095L);
            Assert.assertEquals(objs.get().get("repo_name"), "leethomason/tinyxml2");
            Assert.assertEquals(objs.get().get("pr_state"), "open");
            Assert.assertEquals(objs.get().get("pr_id"), 26743765L);
            Assert.assertEquals(objs.get().get("PR_Base_user_id"), 699925L);
            Assert.assertEquals(objs.get().get("PR_Head_user_id"), 1277095L);
            Assert.assertEquals(objs.get().get("PR_Head_repo_user_id"), 1277095L);
            Assert.assertEquals(objs.get().get("PR_Base_repo_user_id"), 699925L);
            Assert.assertEquals(objs.get().get("pr_user_user_id"), 1277095L);
            Assert.assertEquals(objs.get().get("event_type"), "PullRequestEvent");
            Assert.assertEquals(objs.get().get("pr_created_at"), "2015-01-01T15:00:06Z");
            Assert.assertEquals(objs.get().get("pr_merged_at"), "null");
            Assert.assertEquals(objs.get().get("pr_updated_at"), "2015-01-01T15:00:06Z");
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessReleaseEvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_6.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            /*objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });*/

            Assert.assertEquals(objs.get().get("release_id"), 818679L);
            Assert.assertEquals(objs.get().get("actor_id"), 1395245L);
            Assert.assertEquals(objs.get().get("repo_name"), "vpg/titon.cache");
            Assert.assertEquals(objs.get().get("repo_id"), 28688179L);
            Assert.assertEquals(objs.get().get("event_type"), "ReleaseEvent");
            Assert.assertEquals(objs.get().get("release_action"), "published");
            Assert.assertEquals(objs.get().get("created_at"), "2015-01-01T15:02:17Z");
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessIssueCommentEvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_8.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            /*LOGGER.info("Length is {}", objs.get().size());
            objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });*/
            Assert.assertEquals(objs.get().get("actor_id"), 31021L);
            Assert.assertEquals(objs.get().get("repo_name"), "atheme/charybdis");
            Assert.assertEquals(objs.get().get("repo_id"), 4342947L);
            Assert.assertEquals(objs.get().get("event_type"), "IssueCommentEvent");
            Assert.assertEquals(objs.get().get("created_at"), "2015-01-01T15:00:51Z");
            Assert.assertEquals(objs.get().get("issue_id"), 53218762L);
            Assert.assertEquals(objs.get().get("comment_id"), 68488508L);
            Assert.assertEquals(objs.get().get("issue_updated_at"), "2015-01-01T15:00:50Z");
            Assert.assertEquals(objs.get().get("issue_created_at"), "2015-01-01T12:25:41Z");
            Assert.assertEquals(objs.get().get("comment_created_at"), "2015-01-01T15:00:50Z");
            Assert.assertEquals(objs.get().get("comment_updated_at"), "2015-01-01T15:00:50Z");
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessPullRequestReviewCommentEvent() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_7.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            /*LOGGER.info("Length is {}", objs.get().size());
            objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });*/
            Assert.assertEquals(objs.get().get("actor_id"), 10357835L);
            Assert.assertEquals(objs.get().get("repo_name"), "mevlan/script");
            Assert.assertEquals(objs.get().get("repo_id"), 28668460L);
            Assert.assertEquals(objs.get().get("event_type"), "PullRequestReviewCommentEvent");
            Assert.assertEquals(objs.get().get("pr_comment_id"), 22400085L);
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessIssue() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_11.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            LOGGER.info("Length is {}", objs.get().size());
            objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });
            Assert.assertEquals(objs.get().get("actor_id"), 29977786L);
            Assert.assertEquals(objs.get().get("repo_name"), "komem3/gonm");
            Assert.assertEquals(objs.get().get("repo_id"), 204879903L);
            Assert.assertEquals(objs.get().get("event_type"), "IssuesEvent");
        } catch (Exception e) {
        }
    }

    @Test
    public void JsonParserTest_readExpectedProperties_WhenSuccessPR() {

        try {
            FileInputStream fs = new FileInputStream("src/test/resources/test_individual_10.json");
            Optional<Map<String, Object>> objs = jsonParser.readJsonContent(fs);
            LOGGER.info("Length is {}", objs.get().size());
            objs.get().entrySet().forEach(entry -> {
                LOGGER.info("Key is {}, value is {}", entry.getKey(), entry.getValue());
            });
        } catch (Exception e) {
        }
    }
}
