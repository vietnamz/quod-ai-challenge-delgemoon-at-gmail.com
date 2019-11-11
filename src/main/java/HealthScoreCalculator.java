import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type HealthScoreCalculator.
 */
public class HealthScoreCalculator {

    private final Logger LOGGER = LoggerFactory.getLogger(HealthScoreCalculator.class);

    /**
     * Just start to see java docwork.
     *
     * @param hello the hello
     */
    public void justStartToSeeJavaDocwork(String hello) {
        System.out.println(hello);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator();
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide the start date and end date to continue!!!");
        }
        ((Runnable) () -> System.out.println("Hello World")).run();
    }
}
