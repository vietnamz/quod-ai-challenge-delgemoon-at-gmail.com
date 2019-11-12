import org.junit.Assert;
import org.junit.Test;

public class HealthScoreCalculatorTest {

    @Test
    public void test() {
        String hello = "Hello World";
        Assert.assertEquals(hello, "Hello World");
    }

    @Test
    public void healthScoreCalculator_validateInputTime() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-11-11T00:00:00Z", "2019-11-11T20:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 21);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-11-11-20.json.gz");
    }

    @Test
    public void healthScoreCalculator_validateInputTime_whenDayLess10() {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        healthScoreCalculator.validateDateTimeInput("2019-10-07T00:00:00Z", "2019-10-08T00:00:00Z");
        Assert.assertEquals(healthScoreCalculator.getAnHoursFiles().size(), 25);
        String firstElement = healthScoreCalculator.getAnHoursFiles().get(0);
        Assert.assertEquals(firstElement, "https://data.gharchive.org/2019-10-08-0.json.gz");
    }
}
