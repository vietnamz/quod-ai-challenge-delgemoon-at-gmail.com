package timeutil;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

public class TimeUtilTest {

    @Test
    public void timeUtil_convertTime() {
        Optional<LocalDateTime> convertTime = TimeUtil.convertStringToDateTime("2019-09-23T23:56:17Z");
        if (convertTime.isPresent()) {
            LocalDateTime localDateTime = convertTime.get();
            Assert.assertEquals(localDateTime.getHour(), 23);
            Assert.assertEquals(localDateTime.getMonthValue(), 9);
            Assert.assertEquals(localDateTime.getYear(), 2019);
            Assert.assertEquals(localDateTime.getDayOfMonth(), 23);
            Assert.assertEquals(localDateTime.getMinute(), 56);
            Assert.assertEquals(localDateTime.getSecond(), 17);
        }
    }

    @Test
    public void timeUtil_convertTime_WhenFail() {
        Optional<LocalDateTime> convertTime = TimeUtil.convertStringToDateTime("2019-09-33T23:56:17Z");
        if (!convertTime.isPresent()) {
            System.out.println("Failed to convert");
        }
    }

    @Test
    public void timeUtil_convertTime_WhenSuccessToEpochTime() {
        Optional<Long> time = TimeUtil.convertStringToEpochSecond("2019-09-10T23:56:17Z");
        System.out.println(time.get());
    }
}
