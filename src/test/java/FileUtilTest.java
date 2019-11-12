import fileutil.FileUtil;
import org.junit.Test;

import java.util.Optional;

public class FileUtilTest {

    @Test
    public void fileUtil_unzipfile_success() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resource/test.json.gz");
    }

    @Test
    public void fileUtil_unzipfile_failed_whenFileNotFound() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resource/test1.json.gz");
        if (!outFile.isPresent()) {
            System.out.println("Problem with unzip file");
        }
    }

    @Test
    public void fileUtil_unzipfile_failed_whenWrongFormat() {
        Optional<String> outFile = FileUtil.gunzipIt("src/test/resource/test_fail.json.gz");
        if (!outFile.isPresent()) {
            System.out.println("Problem with unzip file");
        }
    }
}
