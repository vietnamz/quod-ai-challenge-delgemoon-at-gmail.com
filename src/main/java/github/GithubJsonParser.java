package github;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The type Github json parser.
 */
public class GithubJsonParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubJsonParser.class);

    /**
     * The Json factory.
     */
    JsonFactory jsonFactory = new JsonFactory();

    /**
     * Instantiates a new Github json parser.
     */
    public GithubJsonParser() {

    }

    private int readPullRequestUser(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("user_id", id);
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

    private int readPullRequestComment(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("comment_id", id);
                        return 0; // well bad.
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readReleaseSegment(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("release_id", id);
                        return 0; // well bad.
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readRepo(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("id", id);
                        break;
                    case "name":
                        jsonParser.nextToken();
                        String name = jsonParser.getText();
                        aMap.put("name", name);
                        return 0;
                    default:
                        break;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readActor(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("actor_id", id);
                    default:
                        break;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private int readPullRequest(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            boolean readFullRequestId = false;
            boolean readCreatedAt = false;
            boolean readMergedAt = false;
            boolean readUpdatedAt = false;
            boolean readUserAlready = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        if (readFullRequestId) {
                            break;
                        }
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("pull_request_id", id);
                        readFullRequestId = true;
                        break;
                    case "merged":
                        jsonParser.nextValue();
                        boolean merged = jsonParser.getBooleanValue();
                        aMap.put("merged", merged);
                        return 0;
                    case "user":
                        if (readUserAlready) {
                            break;
                        }
                        readPullRequestUser(jsonParser, aMap);
                        readUserAlready = true;
                        break;
                    case "state":
                        jsonParser.nextValue();
                        String state = jsonParser.getText();
                        aMap.put("state", state);
                        break;
                    case "updated_at":
                        if (readUpdatedAt) {
                            break;
                        }
                        jsonParser.nextValue();
                        String updateAt = jsonParser.getText();
                        aMap.put("updated_at", updateAt);
                        readUpdatedAt = true;
                        break;
                    case "created_at":
                        if (readCreatedAt) {
                            break;
                        }
                        jsonParser.nextToken();
                        String CreatedAt = jsonParser.getText();
                        aMap.put("created_at", CreatedAt);
                        readCreatedAt = true;
                        break;
                    case "merged_at":
                        if (readMergedAt) {
                            break;
                        }
                        jsonParser.nextToken();
                        String mergedAt = jsonParser.getText();
                        aMap.put("merged_at", mergedAt);
                        readMergedAt = true;
                        break;
                }
            }
            return 0;
        } catch (Exception e) {
            return -1; // will handle later or we might have to refactor this logic
        }
    }

    private int readIssueUser(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("user_id", id);
                        return 0;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    private int readIssuePayload(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            boolean readIssueId = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        if (readIssueId) {
                            break;
                        }
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("issue_id", id);
                        readIssueId = true;
                        break;
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        aMap.put("action", action);
                        break;
                    case "updated_at":
                        jsonParser.nextValue();
                        String updatedAt = jsonParser.getText();
                        aMap.put("updated_at", updatedAt);
                        break;
                    case "created_at":
                        jsonParser.nextToken();
                        String CreatedAt = jsonParser.getText();
                        aMap.put("created_at", CreatedAt);
                        break;
                    case "closed_at":
                        jsonParser.nextToken();
                        String ClosedAt = jsonParser.getText();
                        aMap.put("closed_at", ClosedAt);
                        return 0;
                    default:
                        break;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Read repo info optional.
     * <p>
     * We only interested in on repo id, name and org information only.
     *
     * @param content the json content. It support both file location or json string.
     * @param isFile  the is file
     * @return the optional <p> TODO: Should refactor. DRY violation.
     */
    public Optional<Map<String, Object>> readRepoInfo(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        break;
                    case "actor":
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        stop = true;
                        break;
                    case "payload":
                        break;
                    case "public":
                        break;
                    case "created_at":
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                LOGGER.error(e.getMessage());
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);
    }

    /**
     * Read commits per day optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> readCommitsPerDay(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("PushEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        break;
                    case "actor":
                        readActor(jsonParser, aMap);
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    case "payload":
                        break;
                    case "public":
                        break;
                    case "created_at":
                        jsonParser.nextValue();
                        String createdAt = jsonParser.getText();
                        aMap.put("created_at", createdAt);
                        stop = true;
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);
    }

    /**
     * Read issue remain open optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> readIssueRemainOpen(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("IssuesEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        break;
                    case "actor":
                        readActor(jsonParser, aMap);
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    case "payload":
                        readIssuePayload(jsonParser, aMap);
                        stop = true;
                        break;
                    case "public":
                        break;
                    case "created_at":
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);

    }

    /**
     * Read merged pull request optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> readMergedPullRequest(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("PullRequestEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        break;
                    case "pull_request":
                        readPullRequest(jsonParser, aMap);
                        stop = true;
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);
    }

    /**
     * Read number of release optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> readNumberOfRelease(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("ReleaseEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        break;
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        // self review: should I filter the action type here or later. just move with this.
                        // we can improve later.
                        aMap.put("action", action);
                        break;
                    case "release":
                        readReleaseSegment(jsonParser, aMap);
                        stop = true;
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);
    }

    /**
     * Read pull request review event optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> readPullRequestReviewEvent(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            boolean readTypeAlready = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "id":
                        break;
                    case "type":
                        if (readTypeAlready) {
                            break;
                        }
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("PullRequestReviewCommentEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        readTypeAlready = true;
                        break;
                    case "comment":
                        readPullRequestComment(jsonParser, aMap);
                        break;
                    case "pull_request":
                        readPullRequest(jsonParser, aMap);
                        stop = true;
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);

    }

    //IssueCommentEvent

    private int readIssueComment(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            boolean readCommentIdAlready = false;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        if (readCommentIdAlready) {
                            break;
                        }
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("comment_id", id);
                        readCommentIdAlready = true;
                        break;
                    case "created_at":
                        jsonParser.nextValue();
                        String createdAt = jsonParser.getText();
                        aMap.put("comment_created_at", createdAt);
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

    /**
     * Issue comment event optional.
     *
     * @param content the content
     * @param isFile  the is file
     * @return the optional
     */
    public Optional<Map<String, Object>> IssueCommentEvent(String content, boolean isFile) {
        Map<String, Object> aMap = new HashMap<>();
        JsonParser jsonParser = null;
        try {
            if (isFile) {
                jsonParser = jsonFactory.createParser(new FileInputStream(content));
            } else {
                jsonParser = jsonFactory.createParser(content);
            }
            boolean stop = false;
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                if (property == null) {
                    continue;
                }
                switch (property) {
                    case "type":
                        jsonParser.nextValue();
                        String type = jsonParser.getText();
                        if (!type.equals("IssueCommentEvent")) {
                            throw new IllegalArgumentException("Just throw something to close stream");
                        }
                        aMap.put("type", type);
                        break;
                    case "repo":
                        readRepo(jsonParser, aMap);
                        break;
                    case "issue":
                        readIssuePayload(jsonParser, aMap);
                        break;
                    case "comment":
                        readIssueComment(jsonParser, aMap);
                        break;
                    default:
                        break;
                }
                if (stop) {
                    break;
                }
            }
            jsonParser.close();
        } catch (Exception e) {
            try {
                if (jsonParser != null) {
                    jsonParser.close();
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(aMap);

    }

}
