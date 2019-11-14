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

    private int readIssuePayload(JsonParser jsonParser, Map<String, Object> aMap) {
        try {
            while (jsonParser.nextToken() != null) {
                String property = jsonParser.getText();
                switch (property) {
                    case "id":
                        jsonParser.nextValue();
                        Long id = jsonParser.getLongValue();
                        aMap.put("issue_id", id);
                        break;
                    case "action":
                        jsonParser.nextValue();
                        String action = jsonParser.getText();
                        aMap.put("action", action);
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
}
