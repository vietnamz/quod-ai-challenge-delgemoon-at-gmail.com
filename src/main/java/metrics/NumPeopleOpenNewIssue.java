package metrics;

import fileutil.FileUtil;
import github.GithubJsonParser;
import github.NewIssue;
import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NumPeopleOpenNewIssue {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumPeopleOpenNewIssue.class);

    /**
     * Calculate time issue remain open optional.
     * <p>
     * I suppose the issue will be open then close only.
     * <p>
     * That means, We opened an issue then it close. We don't consider case it might be re-open.
     * <p>
     * Open --> close. For some specical case like reopen the calculation might be incorrect.
     *
     * @param files    the files
     * @param projects the projects
     * @return the optional
     */
    public static Optional<Integer> calculatePeopeOpenNewIssue(List<String> files,
                                                               Map<Long, Project> projects) {
        Map<Long, NewIssue> issues = new HashMap<>();
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
                        return new GithubJsonParser().readIssueRemainOpen(s, false);
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
                    .filter(s -> s.get("action") != null && s.get("action").equals("opened"))
                    .forEach(s -> {
                        Long id = (Long) s.get("id");
                        Long userId = (Long) s.get("actor_id");
                        if (issues.containsKey(id)) {
                            NewIssue newIssue = issues.get(id);
                            newIssue.addNewUser(userId);
                        } else {
                            NewIssue newIssue = new NewIssue();
                            newIssue.addNewUser(userId);
                            issues.put(id, newIssue);
                        }
                    });
            issues.entrySet().stream().forEach(e -> {
                if (projects.containsKey(e.getKey())) {
                    projects.get(e.getKey()).setNumOfPeopleOpenNewIssue(e.getValue().calculateUserOpenNewIssue());
                }
            });
        } catch (Exception e) {
            LOGGER.error("There is a error while calculating number of commit {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(0);
    }

}
