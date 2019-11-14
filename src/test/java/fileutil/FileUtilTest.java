package fileutil;

import github.Project;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtilTest {

    @Test
    public void fileUtil_unzipfile_success() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resources/test.json.gz");
    }

    @Test
    public void fileUtil_unzipfile_failed_whenFileNotFound() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resources/test1.json.gz");
        if (!outFile.isPresent()) {
            System.out.println("Problem with unzip file");
        }
    }

    @Test
    public void fileUtil_unzipfile_failed_whenWrongFormat() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resources/test_fail.json.gz");
        if (!outFile.isPresent()) {
            System.out.println("Problem with unzip file");
        }
    }

    @Test
    public void fileUtil_getline_success() {
        Assert.assertEquals(11351, Stream.of(("src/test/resources/test.json"))
                .map(s -> FileUtil.getLines(s))
                .peek(o -> {
                    if (!o.isPresent()) {
                        System.out.println("Problem with opening file");
                    }
                })
                .filter(o -> o.isPresent())
                .flatMap(o -> o.get())
                .count());

    }

    @Test
    public void fileUtil_getline_success_whenArrayFile() {
        Assert.assertEquals(11352, Arrays.asList("src/test/resources/test.json", "src/test/resources/test_individual_1.json").stream()
                .map(s -> FileUtil.getLines(s))
                .peek(o -> {
                    if (!o.isPresent()) {
                        System.out.println("Problem with opening file");
                    }
                })
                .filter(o -> o.isPresent())
                .flatMap(o -> o.get())
                .count());
    }

    @Test
    public void fileUtil_writeOutToCSV_success() {
        Map<Long, Project> projects = new HashMap<>();
        projects.put(1L, new Project(1L, "testOrg1", "Test1", 0.98F, 178784));
        projects.put(2L, new Project(2L, "testOrg1", "Test1", 1.2F, 178784));
        FileUtil.writeOutToCSV(projects, null);
    }
}
