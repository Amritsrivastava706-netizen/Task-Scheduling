import java.util.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- ProManage Solutions System ---");
            System.out.println("1. Add New Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Generate Optimal Weekly Schedule");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addProject(sc);
                    break;
                case 2:
                    viewProjects();
                    break;
                case 3:
                    generateSchedule();
                    break;
                case 4:
                    System.out.println("Exiting ProManage System...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void addProject(Scanner sc) {
        try (Connection co = connection.getConnection()) {
            System.out.print("Enter Project ID (Integer): ");
            int id = sc.nextInt();
            sc.nextLine(); // Clear buffer

            System.out.print("Enter Project Title: ");
            String title = sc.nextLine();

            System.out.print("Enter Deadline (1-5 days): ");
            int deadline = sc.nextInt();

            System.out.print("Enter Expected Revenue: ");
            double revenue = sc.nextDouble();

            // Explicitly mapping values to avoid data type mismatch errors
            String query = "INSERT INTO projects VALUES (?, ?, ?, ?)";
            PreparedStatement ps = co.prepareStatement(query);

            ps.setInt(1, id);          // Matches project_id (INT)
            ps.setString(2, title);    // Matches title (VARCHAR)
            ps.setInt(3, deadline);    // Matches deadline (INT)
            ps.setDouble(4, revenue);  // Matches revenue (DOUBLE)

            ps.executeUpdate();
            System.out.println("Project successfully saved to Database.");
        } catch (Exception e) {
            System.out.println("Error: Could not add project. Check your table structure.");
            e.printStackTrace();
        }
    }

    private static void viewProjects() {
        try (Connection co = connection.getConnection()) {
            String query = "SELECT * FROM projects";
            PreparedStatement ps = co.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- All Logged Projects ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt(1) +
                        " | Title: " + rs.getString(2) +
                        " | Deadline: " + rs.getInt(3) +
                        " | Revenue: " + rs.getDouble(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateSchedule() {
        List<Project> projectList = new ArrayList<>();

        try (Connection co = connection.getConnection()) {
            ResultSet rs = co.prepareStatement("SELECT * FROM projects").executeQuery();
            while (rs.next()) {
                projectList.add(new Project(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4)));
            }

            projectList.sort((p1, p2) -> Double.compare(p2.getRevenue(), p1.getRevenue()));


            Project[] schedule = new Project[5];
            boolean[] slotsOccupied = new boolean[5];
            double totalWeeklyRevenue = 0;

            // 3. Optimal Allocation (Finding the latest possible day for each project)
            for (Project p : projectList) {
                // Determine starting search day (Deadline 3 means search Day 3, then 2, then 1)
                int searchStart = Math.min(4, p.getDeadline() - 1);

                for (int i = searchStart; i >= 0; i--) {
                    if (!slotsOccupied[i]) {
                        slotsOccupied[i] = true;
                        schedule[i] = p;
                        totalWeeklyRevenue += p.getRevenue();
                        break;
                    }
                }
            }

            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            System.out.println("\n--- FINAL OPTIMIZED WEEKLY SCHEDULE ---");
            for (int i = 0; i < 5; i++) {
                System.out.print(days[i] + ": ");
                if (schedule[i] != null) {
                    System.out.println(schedule[i].getProjectTitle() + " (Revenue: " + schedule[i].getRevenue() + ")");
                } else {
                    System.out.println("[Unassigned - No Project Fits]");
                }
            }
            System.out.println("---------------------------------------");
            System.out.println("MAXIMIZED REVENUE FOR THE WEEK: " + totalWeeklyRevenue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}