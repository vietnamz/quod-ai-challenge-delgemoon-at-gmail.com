package github;

/**
 * The type Project.
 */
public class Project {

    private Long id = null;
    private String org = null;
    private String name = null;

    private Float heathyScore = 0.0F;
    private Integer numCommit = 0;
    private Long averageIssueOpen = 0L;

    private Integer ratioCommitPerDev = 0;

    private Long averagePullRequestGetMerged = 0L;

    private Integer numOfReleases = 0;

    private Integer numOfOpenPullRequest = 0;

    private Integer numOfPeopleOpenNewIssue = 0;

    private Float rationClosedToOpenIssue = 0F;

    private Integer averageReviewPerPR = 0;

    private Float contributorGrowthRate = 0F;

    /**
     * Sets org.
     *
     * @param org the org
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Instantiates a new Project.
     */
    public Project() {
    }

    /**
     * Instantiates a new Project.
     *
     * @param id          the id
     * @param org         the org
     * @param name        the name
     * @param heathyScore the heathy score
     * @param numCommit   the num commit
     */
    public Project(Long id, String org, String name, Float heathyScore, Integer numCommit) {
        this.id = id;
        this.org = org;
        this.name = name;
        this.heathyScore = heathyScore;
        this.numCommit = numCommit;
    }

    /**
     * Gets ratio commit per dev.
     *
     * @return the ratio commit per dev
     */
    public Integer getRatioCommitPerDev() {
        return ratioCommitPerDev;
    }

    /**
     * Sets ratio commit per dev.
     *
     * @param ratioCommitPerDev the ratio commit per dev
     */
    public void setRatioCommitPerDev(Integer ratioCommitPerDev) {
        this.ratioCommitPerDev = ratioCommitPerDev;
    }

    /**
     * Sets heathy score.
     *
     * @param heathyScore the heathy score
     */
    public void setHeathyScore(Float heathyScore) {
        this.heathyScore = heathyScore;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets num commit.
     *
     * @param numCommit the num commit
     */
    public void setNumCommit(Integer numCommit) {
        this.numCommit = numCommit;
    }

    /**
     * Gets num commit.
     *
     * @return the num commit
     */
    public Integer getNumCommit() {
        return numCommit;
    }

    /**
     * Gets org.
     *
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * Gets heathy score.
     *
     * @return the heathy score
     */
    public Float getHeathyScore() {
        return heathyScore;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets average issue open.
     *
     * @return the average issue open
     */
    public Long getAverageIssueOpen() {
        return averageIssueOpen;
    }

    /**
     * Sets average issue open.
     *
     * @param averageIssueOpen the average issue open
     */
    public void setAverageIssueOpen(Long averageIssueOpen) {
        this.averageIssueOpen = averageIssueOpen;
    }

    /**
     * Gets average pull request get merged.
     *
     * @return the average pull request get merged
     */
    public Long getAveragePullRequestGetMerged() {
        return averagePullRequestGetMerged;
    }

    /**
     * Sets average pull request get merged.
     *
     * @param averagePullRequestGetMerged the average pull request get merged
     */
    public void setAveragePullRequestGetMerged(Long averagePullRequestGetMerged) {
        this.averagePullRequestGetMerged = averagePullRequestGetMerged;
    }

    /**
     * Gets num of releases.
     *
     * @return the num of releases
     */
    public Integer getNumOfReleases() {
        return numOfReleases;
    }

    /**
     * Sets num of releases.
     *
     * @param numOfReleases the num of releases
     */
    public void setNumOfReleases(Integer numOfReleases) {
        this.numOfReleases = numOfReleases;
    }

    /**
     * Gets num of open pull request.
     *
     * @return the num of open pull request
     */
    public Integer getNumOfOpenPullRequest() {
        return numOfOpenPullRequest;
    }

    /**
     * Sets num of open pull request.
     *
     * @param numOfOpenPullRequest the num of open pull request
     */
    public void setNumOfOpenPullRequest(Integer numOfOpenPullRequest) {
        this.numOfOpenPullRequest = numOfOpenPullRequest;
    }

    /**
     * Sets num of people open new issue.
     *
     * @param numOfPeopleOpenNewIssue the num of people open new issue
     */
    public void setNumOfPeopleOpenNewIssue(Integer numOfPeopleOpenNewIssue) {
        this.numOfPeopleOpenNewIssue = numOfPeopleOpenNewIssue;
    }

    /**
     * Gets num of people open new issue.
     *
     * @return the num of people open new issue
     */
    public Integer getNumOfPeopleOpenNewIssue() {
        return numOfPeopleOpenNewIssue;
    }

    /**
     * Sets ration closed to open issue.
     *
     * @param rationClosedToOpenIssue the ration closed to open issue
     */
    public void setRationClosedToOpenIssue(Float rationClosedToOpenIssue) {
        this.rationClosedToOpenIssue = rationClosedToOpenIssue;
    }

    /**
     * Gets ration closed to open issue.
     *
     * @return the ration closed to open issue
     */
    public Float getRationClosedToOpenIssue() {
        return rationClosedToOpenIssue;
    }

    /**
     * Gets average review per pr.
     *
     * @return the average review per pr
     */
    public Integer getAverageReviewPerPR() {
        return averageReviewPerPR;
    }

    /**
     * Sets average review per pr.
     *
     * @param averageReviewPerPR the average review per pr
     */
    public void setAverageReviewPerPR(Integer averageReviewPerPR) {
        this.averageReviewPerPR = averageReviewPerPR;
    }

    /**
     * Gets contributor growth rate.
     *
     * @return the contributor growth rate
     */
    public Float getContributorGrowthRate() {
        return contributorGrowthRate;
    }

    /**
     * Sets contributor growth rate.
     *
     * @param contributorGrowthRate the contributor growth rate
     */
    public void setContributorGrowthRate(Float contributorGrowthRate) {
        this.contributorGrowthRate = contributorGrowthRate;
    }

}
