package metrics;

import java.util.Map;

/**
 * The interface Metric action.
 */
public interface IMetricAction {

    /**
     * Run.
     *
     * @param objs the objs
     */
    void run(Map<String, Object> objs);

    /**
     * Filter boolean.
     *
     * @param objs the objs
     * @return the boolean
     */
    boolean filter(Map<String, Object> objs);

    /**
     * Execute.
     */
    void execute();

    String getName();
}
