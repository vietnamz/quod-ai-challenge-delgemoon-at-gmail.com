package github;

import java.time.LocalDateTime;

/*
 * Assumption:
 * growth rate is base on hours basis. divide the number of contributor in 1 hours and calculate accordingly.
 *
 * keep track total hours. start hour and end hour. loop through the hour, check if merged event in there. get
 * the user id, check if user id already exist, if not exist add in.
 */

public class Contributor {

    LocalDateTime mergedTime;
    Long userId;
}
