package github;

class IssueState {
    String state;
    Long updatedAt;

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public boolean equals(IssueState obj) {

        return state.equals(obj.getState()) || updatedAt.equals(obj.getUpdatedAt());
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
