public class Project {

    private final int projectId;
    private String projectTitle;
    private int deadline;
    private double revenue;

    public Project(int projectId, String projectTitle, int deadline, double revenue) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.deadline = deadline;
        this.revenue = revenue;
    }

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

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void displayProject() {
        System.out.println("ID: " + projectId);
        System.out.println("Title: " + projectTitle);
        System.out.println("Deadline: " + deadline);
        System.out.println("Revenue: " + revenue);
        System.out.println("--------------------------");
    }
}