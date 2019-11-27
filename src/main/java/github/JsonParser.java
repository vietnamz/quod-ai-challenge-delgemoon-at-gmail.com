package github;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * The type Json parser.
 */
public class JsonParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParser.class);

    private Set<EventType> eventTypes = new HashSet<>();


    /*
     * payload.user.id
     *
     */


    /**
     * Create parser com . fasterxml . jackson . core . json parser.
     *
     * @param content the content
     * @return the com . fasterxml . jackson . core . json parser
     * @throws BusinessException the business exception
     */
    protected com.fasterxml.jackson.core.JsonParser createParser(String content) throws BusinessException {
        try {
            return new JsonFactory().createParser(content);
        } catch (IOException exp) {
            throw new BusinessException("Failed to created parser");
        }
    }

    /**
     * Create parser com . fasterxml . jackson . core . json parser.
     *
     * @param fs the fs
     * @return the com . fasterxml . jackson . core . json parser
     * @throws BusinessException the business exception
     */
    protected com.fasterxml.jackson.core.JsonParser createParser(FileInputStream fs) throws BusinessException {
        try {
            return new JsonFactory().createParser(fs);
        } catch (IOException exp) {
            throw new BusinessException("Failed to created parser");
        }
    }


    /**
     * Close parser.
     *
     * @param jsonParser the json parser
     */
    protected void closeParser(com.fasterxml.jackson.core.JsonParser jsonParser) {
        if (jsonParser == null) {
            return;
        }
        try {
            jsonParser.close();
        } catch (Exception ex) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed to close the parser");
            }
        }
    }

    /**
     * Instantiates a new Json parser.
     */
    public JsonParser() {
        eventTypes.add(EventType.ISSUE);
        eventTypes.add(EventType.RELEASE);
        eventTypes.add(EventType.ISSUE_COMMENT);
        eventTypes.add(EventType.PULL_REQUEST);
        eventTypes.add(EventType.PULL_REQUEST_REVIEW_COMMENT);
        eventTypes.add(EventType.PUSH);
    }

    private boolean validateEventType(String type) throws BusinessException {
        if (eventTypes.size() == 0) {
            throw new BusinessException("No expected values is parsed yet");
        }
        for (EventType e : eventTypes) {
            if (type.equals(e.getType())) {
                return true;
            }
        }
        return false;
    }

    private int readListInnerUser(com.fasterxml.jackson.core.JsonParser jsonParser,
                                  Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                readInnerUser(jsonParser, aMap, "issue_assignee_list");
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readInnerUser(com.fasterxml.jackson.core.JsonParser jsonParser,
                              Map<String, Object> aMap,
                              String prefix) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put(prefix.concat("_user_id"), id);
                        break;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readIssueLabels(com.fasterxml.jackson.core.JsonParser jsonParser,
                                Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                String property = jsonParser.getText();
                switch (property) {
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readIssueMilestone(com.fasterxml.jackson.core.JsonParser jsonParser,
                                   Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "creator":
                        readInnerUser(jsonParser, collection, "issue_milestone_creator");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readIssuePayloadInternal(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> collection) {
        try {
            boolean failed = false;
            int ret;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        collection.put("issue_id", id);
                        break;
                    case "updated_at":
                        jsonParser.nextValue();
                        String updatedAt = jsonParser.getText();
                        collection.put("issue_updated_at", updatedAt);
                        break;
                    case "user":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readInnerUser(jsonParser, collection, "issue_user");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "labels":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readIssueLabels(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "milestone":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readIssueMilestone(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "assignee":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readInnerUser(jsonParser, collection, "assignee");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "assignees":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readListInnerUser(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "created_at":
                        jsonParser.nextToken();
                        String CreatedAt = jsonParser.getText();
                        collection.put("issue_created_at", CreatedAt);
                        break;
                    case "closed_at":
                        jsonParser.nextToken();
                        String ClosedAt = jsonParser.getText();
                        collection.put("issue_closed_at", ClosedAt);
                        break;
                    default:
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readIssuePayload(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> collection) {
        try {
            boolean failed = false;
            int ret;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        collection.put("issue_action", action);
                        break;
                    case "issue":
                        ret = readIssuePayloadInternal(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                    case "sender":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readInnerUser(jsonParser, collection, "issue_sender");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "repository":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRRepo(jsonParser, collection, "issue_repository");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readCommitPayload(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "push_id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        collection.put("push_id", id);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readPRRepo(com.fasterxml.jackson.core.JsonParser jsonParser,
                           Map<String, Object> collection,
                           String prefix) {
        int ret;
        boolean failed = false;
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "name":
                        jsonParser.nextValue();
                        break;
                    case "full_name":
                        jsonParser.nextValue();
                        break;
                    case "owner":
                        ret = readInnerUser(jsonParser, collection, prefix.concat("_repo"));
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }

                if (failed) {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;

    }

    private int readPRHead(com.fasterxml.jackson.core.JsonParser jsonParser,
                           Map<String, Object> collection) {
        int ret;
        boolean failed = false;
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "user":
                        ret = readInnerUser(jsonParser, collection, "PR_Head");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "repo":
                        ret = readPRRepo(jsonParser, collection, "PR_Head");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;

    }

    private int readPRBase(com.fasterxml.jackson.core.JsonParser jsonParser,
                           Map<String, Object> collection) {
        int ret;
        boolean failed = false;
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "user":
                        ret = readInnerUser(jsonParser, collection, "PR_Base");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "repo":
                        ret = readPRRepo(jsonParser, collection, "PR_Base");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readHref(com.fasterxml.jackson.core.JsonParser jsonParser,
                         Map<String, Object> collection,
                         String prefix) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "href":
                        jsonParser.nextValue();
                        String value = jsonParser.getText();
                        collection.put(prefix.concat("_href"), value);
                        break;
                }
            }
        } catch (Exception ex) {
            return -1;
        }
        return 0;
    }

    private int readPRLink(com.fasterxml.jackson.core.JsonParser jsonParser,
                           Map<String, Object> collection) {
        int ret;
        boolean failed = false;
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "self":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_self");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "html":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_html");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "issue":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_issue");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "comments":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_comments");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "review_comments":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_review_comments");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "review_comment":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_review_comment");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "commits":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_commits");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "statuses":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readHref(jsonParser, collection, "PR_Link_statuses");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int readPRRequestedTeamList(com.fasterxml.jackson.core.JsonParser jsonParser,
                                        Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int readPRRequestedReviewerList(com.fasterxml.jackson.core.JsonParser jsonParser,
                                            Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int readPRRequestedLabelList(com.fasterxml.jackson.core.JsonParser jsonParser,
                                         Map<String, Object> collection) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int readPRPayloadInternal(com.fasterxml.jackson.core.JsonParser
                                              jsonParser, Map<String, Object> collection) {
        try {
            int ret;
            boolean failed = false;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        collection.put("pr_id", id);
                        break;
                    case "merged":
                        jsonParser.nextValue();
                        boolean merged = jsonParser.getBooleanValue();
                        collection.put("pr_merged", merged);
                        break;
                    case "base":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRBase(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "_links":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRLink(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "user":
                        ret = readInnerUser(jsonParser, collection, "pr_user");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "assignee":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readInnerUser(jsonParser, collection, "pr_assignee");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "state":
                        jsonParser.nextValue();
                        String state = jsonParser.getText();
                        collection.put("pr_state", state);
                        break;
                    case "head":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRHead(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "updated_at":
                        jsonParser.nextValue();
                        String updateAt = jsonParser.getText();
                        collection.put("pr_updated_at", updateAt);
                        break;
                    case "created_at":
                        jsonParser.nextToken();
                        String CreatedAt = jsonParser.getText();
                        collection.put("pr_created_at", CreatedAt);
                        break;
                    case "merged_at":
                        jsonParser.nextToken();
                        String mergedAt = jsonParser.getText();
                        collection.put("pr_merged_at", mergedAt);
                        break;
                    case "requested_teams":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRRequestedTeamList(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "requested_reviewers":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRRequestedReviewerList(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "labels":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRRequestedLabelList(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "assignees":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readListInnerUser(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "milestone":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readIssueMilestone(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;

                }
                if (failed) {
                    break;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1; // will handle later or we might have to refactor this logic
        }
    }

    private int readPRPayload(com.fasterxml.jackson.core.JsonParser jsonParser,
                              Map<String, Object> collection) {
        try {
            int ret;
            boolean failed = false;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "pull_request":
                        ret = readPRPayloadInternal(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "sender":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readInnerUser(jsonParser, collection, "pr_sender");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "repository":
                        jsonParser.nextValue();
                        if (jsonParser.getText().equals("null")) {
                            break;
                        }
                        ret = readPRRepo(jsonParser, collection, "pr_repository");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;

                }
                if (failed) {
                    return -1;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1; // will handle later or we might have to refactor this logic
        }
    }

    private int readIssueComment(com.fasterxml.jackson.core.JsonParser
                                         jsonParser, Map<String, Object> collection) {
        try {
            boolean failed = false;
            int ret;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long commentId = jsonParser.getLongValue();
                        collection.put("comment_id", commentId);
                        break;
                    case "user":
                        ret = readInnerUser(jsonParser, collection, "issue_comment");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "created_at":
                        jsonParser.nextValue();
                        String createdAt = jsonParser.getText();
                        collection.put("comment_created_at", createdAt);
                        break;
                    case "updated_at":
                        jsonParser.nextValue();
                        String updatedAt = jsonParser.getText();
                        collection.put("comment_updated_at", updatedAt);
                        break;
                }
            }
            if (failed) {
                return -1;
            }
        } catch (Exception ex) {
            return -1;
        }
        return 0;
    }

    private int readIssueCommentPayload(com.fasterxml.jackson.core.JsonParser
                                                jsonParser, Map<String, Object> collection) {
        try {
            boolean failed = false;
            int ret;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        collection.put("issue_comment_action", action);
                        break;
                    case "issue":
                        ret = readIssuePayload(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "comment":
                        ret = readIssueComment(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "created_at":
                        jsonParser.nextValue();
                        String createdAt = jsonParser.getText();
                        collection.put("issue_comment_created_at", createdAt);
                        break;
                    case "updated_at":
                        jsonParser.nextValue();
                        String updatedAt = jsonParser.getText();
                        collection.put("issue_comment_updated_at", updatedAt);
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
        } catch (Exception ex) {
            return -1;
        }
        return 0;
    }

    private int readPullRequestCommentLink(com.fasterxml.jackson.core.JsonParser
                                                   jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "self":
                        break;
                    case "html":
                        break;
                    case "pull_request":
                        break;
                }
            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int readPullRequestComment(com.fasterxml.jackson.core.JsonParser
                                               jsonParser, Map<String, Object> aMap) {
        try {
            boolean failed = false;
            int ret;

            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("pr_comment_id", id);
                        break;
                    case "user":
                        ret = readInnerUser(jsonParser, aMap, "pr_comment_comment");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "_links":
                        ret = readPullRequestCommentLink(jsonParser, aMap);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    break;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readPRReviewCommentPayload(com.fasterxml.jackson.core.JsonParser
                                                   jsonParser, Map<String, Object> collection) {
        try {
            int ret;
            boolean failed = false;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "comment":
                        ret = readPullRequestComment(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "pull_request":
                        ret = readPRPayload(jsonParser, collection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readReleasePayload(com.fasterxml.jackson.core.JsonParser
                                           jsonParser, Map<String, Object> collection) {
        try {
            int ret;
            boolean failed = false;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        collection.put("release_id", id);
                        break;
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        collection.put("release_action", action);
                        break;
                    case "author":
                        ret = readInnerUser(jsonParser, collection, "release");
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                }
                if (failed) {
                    return -1;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }


    private int readActor(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("actor_id", id);
                        break;
                    default:
                        break;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readOrg(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        break;
                    default:
                        break;
                }
            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }

    }

    private int readRepo(com.fasterxml.jackson.core.JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("repo_id", id);
                        break;
                    case "name":
                        jsonParser.nextToken();
                        String name = jsonParser.getText();
                        aMap.put("repo_name", name);
                    default:
                        break;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int delegateToPayload(EventType eventType,
                                  com.fasterxml.jackson.core.JsonParser jsonParser,
                                  Map<String, Object> aCollection) {
        if (eventType == null) {
            return -1;
        }
        switch (eventType) {
            case PUSH:
                return readCommitPayload(jsonParser, aCollection);
            case RELEASE:
                return readReleasePayload(jsonParser, aCollection);
            case PULL_REQUEST:
                return readPRPayload(jsonParser, aCollection);
            case ISSUE:
                return readIssuePayload(jsonParser, aCollection);
            case ISSUE_COMMENT:
                return readIssueCommentPayload(jsonParser, aCollection);
            case PULL_REQUEST_REVIEW_COMMENT:
                return readPRReviewCommentPayload(jsonParser, aCollection);
            default:
                return -1;
        }
    }

    /**
     * Read json content optional.
     *
     * @param fs the fs
     * @return the optional
     */
    public Optional<Map<String, Object>> readJsonContent(FileInputStream fs) {
        com.fasterxml.jackson.core.JsonParser jsonParser = null;
        if (fs == null) {
            return Optional.empty();
        }
        try {
            jsonParser = createParser(fs);
            return readJsonContent(jsonParser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Read json content optional.
     *
     * @param content the content
     * @return the optional
     */
    public Optional<Map<String, Object>> readJsonContent(String content) {
        com.fasterxml.jackson.core.JsonParser jsonParser = null;
        if (content.length() == 0) {
            return Optional.empty();
        }
        try {
            jsonParser = createParser(content);
            return readJsonContent(jsonParser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Map<String, Object>> readJsonContent(com.fasterxml.jackson.core.JsonParser jsonParser) {
        Map<String, Object> aCollection = new HashMap<>();
        int ret;
        try {
            String type = null;
            boolean failed = false;
            boolean readRepoAlready = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "type":
                        jsonParser.nextValue();
                        type = jsonParser.getText();
                        aCollection.put("event_type", type);
                        break;
                    case "actor":
                        ret = readActor(jsonParser, aCollection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "repo":
                        if (readRepoAlready) {
                            break;
                        }
                        ret = readRepo(jsonParser, aCollection);
                        if (ret != 0) {
                            failed = true;
                            break;
                        }
                        readRepoAlready = true;
                        break;
                    case "payload":
                        if (!validateEventType(type)) {
                            failed = true;
                            break;
                        }
                        ret = delegateToPayload(EventType.valueOfEvent(type), jsonParser, aCollection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    case "public":
                        //ignore.
                        break;
                    case "created_at":
                        jsonParser.nextValue();
                        String createdAt = jsonParser.getText();
                        aCollection.put("created_at", createdAt);
                        break;
                    case "org":
                        ret = readOrg(jsonParser, aCollection);
                        if (ret != 0) {
                            failed = true;
                        }
                        break;
                    default:
                        break;
                }
                if (failed) {
                    break;
                }
            }
        } catch (Exception ex) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(ex.getMessage());
            }
            return Optional.empty();
        } finally {
            this.closeParser(jsonParser);
        }
        return Optional.of(aCollection);
    }
}
