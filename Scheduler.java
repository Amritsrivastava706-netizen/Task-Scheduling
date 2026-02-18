import java.util.*;
import java.sql.*;

public class Scheduler {

    public void generateWeeklySchedule() {
        List<Project> allProjects = new ArrayList<>();

        try (Connection co = connection.getConnection()) {
            String query = "SELECT * FROM projects";
            PreparedStatement ps = co.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                allProjects.add(new Project(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getDouble(4)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allProjects.sort((p1, p2) -> Double.compare(p2.getRevenue(), p1.getRevenue()));

        Project[] weekSlots = new Project[5];
        boolean[] slotFilled = new boolean[5];
        double totalRevenue = 0;

        for (Project p : allProjects) {
            int deadlineDay = p.getDeadline();

            for (int i = Math.min(4, deadlineDay - 1); i >= 0; i--) {
                if (!slotFilled[i]) {
                    slotFilled[i] = true;
                    weekSlots[i] = p;
                    totalRevenue += p.getRevenue();
                    break;
                }
            }
        }

        displaySchedule(weekSlots, totalRevenue);
    }

    private void displaySchedule(Project[] schedule, double total) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        System.out.println("\n--- PROMANAGE OPTIMAL WEEKLY SCHEDULE ---");
        for (int i = 0; i < 5; i++) {
            if (schedule[i] != null) {
                System.out.println(days[i] + ": " + schedule[i].getProjectTitle() + " | Revenue: " + schedule[i].getRevenue());
            } else {
                System.out.println(days[i] + ": [Empty - No eligible project]");
            }
        }
        System.out.println("-----------------------------------------");
        System.out.println("MAXIMIZED WEEKLY REVENUE: " + total);
    }
}