package fileutil;

import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class FileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
    private final static String DEFAULT_OUTPUT = "health_scores.csv";

    @FunctionalInterface
    private interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    private static <T> Consumer<T> throwingConsumerWrapper(
            ThrowingConsumer<T, Exception> throwingConsumer) {

        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                LOGGER.error("Exception occurs {}", ex.getMessage());
            }
        };
    }

    public static Optional<Integer> writeOutToCSV(Map<Long, Project> projects, String outFile) {
        if (outFile == null) {
            outFile = DEFAULT_OUTPUT;
        }
        if (Files.exists(Paths.get(outFile))) {
            try {
                Files.delete(Paths.get(outFile));
            } catch (IOException e) {
                LOGGER.error("unable to delete health check file");
                return Optional.empty();
            }
        }
        if (projects.size() == 0) {
            return Optional.empty(); // No input
        }
        List<Project> projectList = projects.values().parallelStream().collect(Collectors.toList());
        projectList.sort(Comparator.comparingDouble(Project::getHeathyScore).reversed());
        FileWriter csvWriter = null;
        try {
            //org,repo_name,health_score,num_commits
            csvWriter = new FileWriter(outFile);
            csvWriter.append("org");
            csvWriter.append(",");
            csvWriter.append("repo_name");
            csvWriter.append(",");
            csvWriter.append("health_score");
            csvWriter.append(",");
            csvWriter.append("num_commits");
            csvWriter.append(",");
            csvWriter.append("time_issue_remain_open");
            csvWriter.append(",");
            csvWriter.append("pull_request_get_merged");
            csvWriter.append(",");
            csvWriter.append("ratio_commit_per_dev");

            csvWriter.append("\n");
            int count = 0;

            for (Project project : projectList) {
                if (count >= 1000) {
                    break;
                }
                if (project.getOrg() != null) {
                    csvWriter.append(project.getOrg()).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getName() != null) {
                    csvWriter.append(project.getName()).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getHeathyScore() != null) {
                    csvWriter.append(String.format("%.02f", project.getHeathyScore())).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getNumCommit() != null) {
                    csvWriter.append(project.getNumCommit().toString()).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getAverageIssueOpen() != null) {
                    csvWriter.append(project.getAverageIssueOpen().toString()).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getAveragePullRequestGetMerged() != null) {
                    csvWriter.append(project.getAveragePullRequestGetMerged().toString()).append(",");
                } else {
                    csvWriter.append("N/A").append(",");
                }
                if (project.getRatioCommitPerDev() != null) {
                    csvWriter.append(project.getRatioCommitPerDev().toString()).append("\n");
                } else {
                    csvWriter.append("N/A").append("\n");
                }
                count++;
            }
            csvWriter.close();
        } catch (Exception e) {
            LOGGER.error("Exception when writeOutToCSV {}", e.getMessage());
            return Optional.empty();
        } finally {
            try {
                csvWriter.close();
            } catch (Exception e) {
                LOGGER.error("Unable to close file {}", e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.of(0);
    }

    public static Optional<Stream<String>> getLines(String filename) {
        try {
            return Optional.of(Files.lines(Paths.get(filename)));
        } catch (Throwable t) {
            // Either would permit me to express what went wrong
            return Optional.empty();
        }
    }

    /**
     * GunZip it
     */
    public static Optional<String> gunzipIt(String inputFile) {

        byte[] buffer = new byte[1024];

        String outputFile = inputFile.substring(0, inputFile.indexOf(".gz"));

        try {

            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(inputFile));

            FileOutputStream out =
                    new FileOutputStream(outputFile);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Done to unzip the file {}", inputFile);
            }
            return Optional.of(outputFile);

        } catch (IOException ex) {
            LOGGER.error("Failed to unzip the file {}", inputFile);
            return Optional.empty();
        }
    }
}
