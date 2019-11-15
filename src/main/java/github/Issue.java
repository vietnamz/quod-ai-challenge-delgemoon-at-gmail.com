package github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class IssueLive {
    Long open = null;
    Long close = null;
}

public class Issue {

    private Map<Long, IssueLive> issueLiveMap = new HashMap<>();

    public void addNewIssue(Long issueId, Long open, Long close) {
        if (issueLiveMap.containsKey(issueId)) {
            IssueLive issueLive = issueLiveMap.get(issueId);
            /*
             * if We get an open first, there is no close date. We fill the maximum end date first.
             * So We should check if the newer close date is less than the current, then we will update.
             * if we get an close first, then then current will less than the current. no update.
             *
             */
            if (close != null) {
                if (issueLive.close > close) {
                    issueLive.close = close;
                }
            }
            issueLiveMap.replace(issueId, issueLive);
        } else {
            IssueLive issueLive = new IssueLive();
            issueLive.open = open;
            issueLive.close = close;
            issueLiveMap.put(issueId, issueLive);
        }
    }

    public long calculateTheAverageIssueOpen() {
        if (issueLiveMap.size() == 0) {
            return 0;
        }
        int size = issueLiveMap.size();

        List<Long> total = issueLiveMap.entrySet()
                .parallelStream().map(s -> s.getValue().close - s.getValue().open).collect(Collectors.toList());
        Long result = total.parallelStream().reduce((s1, s2) -> s1 + s2).get();
        return (long) Math.ceil((double) result.doubleValue() / (double) size);


    }


}
