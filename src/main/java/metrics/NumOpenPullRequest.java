package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.OpenPullRequest;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NumOpenPullRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumOpenPullRequest.class);

    public static Optional<Integer> calculateOpenPullRequest(List<String> files,
                                                             Map<Long, Project> projects) {
        Map<Long, OpenPullRequest> openPullRequests = new HashMap<>();
        try {
            files.stream().map(f -> FileUtil.getLines(f))
                    .peek(o -> {
                        if (!o.isPresent()) {
                            LOGGER.error("Problem with opening file");
                        }
                    })
                    .filter(o -> o.isPresent())
                    .flatMap(o -> o.get())
                    .map(s -> {
                        return new GithubJsonParser().readMergedPullRequest(s, false);
                    })
                    .peek(o -> {
                        if (!o.isPresent()) {
                            if (LOGGER.isWarnEnabled()) {
                                //LOGGER.warn("Problem with reading json file or the entry is not push event");
                            }
                        }
                    })
                    .filter(o -> o.isPresent())
                    .map(o -> o.get())
                    .forEach(s -> {
                        Long id = (Long) s.get("id");
                        String updatedAt = (String) s.get("updated_at");
                        Long pulRequestId = (Long) s.get("pull_request_id");
                        String state = (String) s.get("state");
                        Long updatedDateEpoch = TimeUtil.convertStringToEpochSecond(updatedAt).get();
                        if (openPullRequests.containsKey(id)) {
                            OpenPullRequest openPullRequest = openPullRequests.get(id);
                            openPullRequest.addPullRequest(pulRequestId, state, updatedDateEpoch);
                        } else {
                            OpenPullRequest openPullRequest = new OpenPullRequest();
                            openPullRequest.addPullRequest(pulRequestId, state, updatedDateEpoch);
                            openPullRequests.put(id, openPullRequest);
                        }
                    });

            openPullRequests.entrySet().stream().forEach(e -> {
                if (projects.containsKey(e.getKey())) {
                    projects.get(e.getKey()).setNumOfOpenPullRequest(e.getValue().calculateNumOfOpenPullRequest());
                }
            });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }
}
