package github;

public class Project {

    private Long id = null;
    private String org = null;
    private String name = null;

    private Float heathyScore = 0.0F;
    private Integer numCommit = 0;
    private Long averageIssueOpen = 0L;

    private Integer ratioCommitPerDev = 0;

    private Long averagePullRequestGetMerged = 0L;

    public void setOrg(String org) {
        this.org = org;
    }

    public Project() {
    }

    public Project(Long id, String org, String name, Float heathyScore, Integer numCommit) {
        this.id = id;
        this.org = org;
        this.name = name;
        this.heathyScore = heathyScore;
        this.numCommit = numCommit;
    }

    public Integer getRatioCommitPerDev() {
        return ratioCommitPerDev;
    }

    public void setRatioCommitPerDev(Integer ratioCommitPerDev) {
        this.ratioCommitPerDev = ratioCommitPerDev;
    }

    public void setHeathyScore(Float heathyScore) {
        this.heathyScore = heathyScore;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumCommit(Integer numCommit) {
        this.numCommit = numCommit;
    }

    public Integer getNumCommit() {
        return numCommit;
    }

    public String getOrg() {
        return org;
    }

    public Float getHeathyScore() {
        return heathyScore;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getAverageIssueOpen() {
        return averageIssueOpen;
    }

    public void setAverageIssueOpen(Long averageIssueOpen) {
        this.averageIssueOpen = averageIssueOpen;
    }

    public Long getAveragePullRequestGetMerged() {
        return averagePullRequestGetMerged;
    }

    public void setAveragePullRequestGetMerged(Long averagePullRequestGetMerged) {
        this.averagePullRequestGetMerged = averagePullRequestGetMerged;
    }
}
