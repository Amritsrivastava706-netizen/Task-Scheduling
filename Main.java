import java.util.Scanner;
import java.sql.*;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static Scheduler scheduler = new Scheduler();

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n--- ProManage Solutions Pvt. Ltd. ---");
            System.out.println("1. Add New Client Project");
            System.out.println("2. View All Pending Projects");
            System.out.println("3. Generate AI Optimized 5-Day Plan");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            String input = sc.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter number between 1-4.");
                continue;
            }

            switch (choice) {
                case 1:
                    addProject();
                    break;
                case 2:
                    viewProjects();
                    break;
                case 3:
                    scheduler.generateWeeklySchedule();
                    break;
                case 4:
                    System.out.println("System shutting down...");
                    return;
                default:
                    System.out.println("Invalid selection!");
            }
        }
    }

    // ================= ADD PROJECT =================
    private static void addProject() {

        try (Connection co = ConnectionDB.getConnection()) {

            System.out.print("Enter Project Title: ");
            String title = sc.nextLine();

            System.out.print("Enter Deadline (Working Days): ");
            int deadline = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Expected Revenue: ");
            double revenue = Double.parseDouble(sc.nextLine());

            String query = "INSERT INTO projects (title, deadline, revenue, status) VALUES (?, ?, ?, 'pending')";
            PreparedStatement ps = co.prepareStatement(query);

            ps.setString(1, title);
            ps.setInt(2, deadline);
            ps.setDouble(3, revenue);

            ps.executeUpdate();

            System.out.println("Project added successfully.");

        } catch (Exception e) {
            System.out.println("Error while adding project.");
            e.printStackTrace();
        }
    }

    // ================= VIEW PROJECTS =================
    private static void viewProjects() {

        try (Connection co = ConnectionDB.getConnection()) {

            String query = "SELECT * FROM projects WHERE status='pending'";
            PreparedStatement ps = co.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- PENDING PROJECTS ---");

            while (rs.next()) {

                System.out.println(
                        "ID: " + rs.getInt("project_id") +
                                " | Title: " + rs.getString("title") +
                                " | Deadline: " + rs.getInt("deadline") +
                                " | Revenue: $" + rs.getDouble("revenue")
                );
            }

        } catch (Exception e) {
            System.out.println("Error fetching projects.");
            e.printStackTrace();
        }
    }
}