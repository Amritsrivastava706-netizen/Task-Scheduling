public class Project {

    private int projectId;
    private String projectTitle;
    private int deadline;
    private double revenue;
    private String status;   // pending / scheduled / completed

    // Constructor without status (default = pending)
    public Project(int projectId, String projectTitle, int deadline, double revenue) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.deadline = deadline;
        this.revenue = revenue;
        this.status = "pending";
    }

    // Full Constructor
    public Project(int projectId, String projectTitle, int deadline, double revenue, String status) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.deadline = deadline;
        this.revenue = revenue;
        this.status = status;
    }

    // Getters
    public int getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public int getDeadline() {
        return deadline;
    }

    public double getRevenue() {
        return revenue;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString() for debugging / logging
    @Override
    public String toString() {
        return "Project{" +
                "ID=" + projectId +
                ", Title='" + projectTitle + '\'' +
                ", Deadline=" + deadline +
                ", Revenue=" + revenue +
                ", Status='" + status + '\'' +
                '}';
    }
}