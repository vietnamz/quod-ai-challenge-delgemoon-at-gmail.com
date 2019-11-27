package metrics;

import github.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Base metric.
 */
public abstract class BaseMetric implements IMetricAction {

    /**
     * The constant LOGGER.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseMetric.class);

    /**
     * The Properties.
     */
    protected List<Map<String, Object>> properties = null;
    /**
     * The Projects.
     */
    protected ConcurrentMap<Long, Project> projects = null;

    /**
     * Instantiates a new Base metric.
     *
     * @param properties the properties
     * @param projects   the projects
     */
    public BaseMetric(List<Map<String, Object>> properties, ConcurrentMap<Long, Project> projects) {
        this.projects = projects;
        this.properties = properties;
    }

    /**
     * Calculate metric.
     */
    public abstract void calculateMetric();

    /**
     * Reset container.
     */
    public abstract void resetContainer();


    @Override
    public boolean filter(Map<String, Object> objs) {
        return true;
    }

    @Override
    public void run(Map<String, Object> objs) {
    }

    @Override
    public void execute() {
        try {
            LOGGER.info("------------------BEGIN------------------");
            LOGGER.info("--- EXECUTING {}", this.getName());
            LocalTime start = LocalTime.now();
            LOGGER.info("Starting time = {}", LocalDateTime.now().toString());
            properties.parallelStream().filter(this::filter).forEach(this::run);
            this.calculateMetric();
            this.resetContainer();
            LocalTime end = LocalTime.now();
            LOGGER.info("End time = {}", LocalDateTime.now().toString());
            LOGGER.info("Elapsed Time = {}", Duration.between(start, end));
            LOGGER.info("------------------END------------------");
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Base Metric";
    }
}
