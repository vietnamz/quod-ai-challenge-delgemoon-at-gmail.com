import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timeutil.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type HealthScoreCalculator.
 */
public class HealthScoreCalculator {

    private final Logger LOGGER = LoggerFactory.getLogger(HealthScoreCalculator.class);

    private final static String githubArchiveUrl = "https://data.gharchive.org/";


    private List<String> anHoursFiles = new ArrayList<>();


    /**
     * Just start to see java docwork.
     *
     * @param hello the hello
     */
    public void justStartToSeeJavaDocwork(String hello) {
        System.out.println(hello);
    }

    private String appendToZero(int number) {
        StringBuilder sb = new StringBuilder();
        if (number < 10) {
            sb.append(0);
        }
        sb.append(number);
        return sb.toString();
    }

    /**
     * Validate date time input.
     *
     * @param startDate the start date
     * @param endDate   the end date
     */
    public void validateDateTimeInput(String startDate, String endDate) {
        Optional<LocalDateTime> convertStartDateTime = TimeUtil.convertStringToDateTime(startDate);
        if (!convertStartDateTime.isPresent()) {
            throw new IllegalArgumentException(
                    "Failed to convert the start date input, please make sure it have this format: YYYY-MM-DDTHH:MM:SSZ");
        }
        Optional<LocalDateTime> convertEndDateTime = TimeUtil.convertStringToDateTime(endDate);
        if (!convertEndDateTime.isPresent()) {
            throw new IllegalArgumentException(
                    "Failed to convert the end date input, please make sure it have this format: YYYY-MM-DDTHH:MM:SSZ");
        }

        LocalDateTime startDateTime = convertStartDateTime.get();
        LocalDateTime endDateTime = convertEndDateTime.get();

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException(
                    "The start date must be after the end date"
            );
        }
        LocalDateTime localDateTime = endDateTime;
        while (true) {
            if (localDateTime.isBefore(startDateTime)) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(githubArchiveUrl);
            sb.append(appendToZero(localDateTime.getYear())).append("-");
            sb.append(appendToZero(localDateTime.getMonthValue())).append("-");
            sb.append(appendToZero(localDateTime.getDayOfMonth())).append("-");
            sb.append(localDateTime.getHour());
            sb.append(".json.gz");
            anHoursFiles.add(sb.toString());
            localDateTime = localDateTime.minusHours(1);
        }

    }


    /**
     * Gets an hours files.
     *
     * @return the an hours files
     */
    public List<String> getAnHoursFiles() {
        return anHoursFiles;
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
        /*
         * validate the user input date to make it's as we expected
         */
        healthScoreCalculator.validateDateTimeInput(args[0], args[1]);


    }
}
