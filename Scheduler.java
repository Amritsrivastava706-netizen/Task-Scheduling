import java.util.*;
import java.sql.*;

public class Scheduler {

    private static final int CURRENT_WEEK = 5;  // Change manually each week

    public void generateWeeklySchedule() {

        List<Project> projects = new ArrayList<>();

        double historicalAvg = calculateRecentAverageRevenue();
        if (historicalAvg == 0) historicalAvg = 600;

        System.out.println("Historical Avg (Last 4 Weeks): $" + historicalAvg);

        double expectedFutureRevenue = simulateFutureRevenue(historicalAvg);
        System.out.println("Predicted Next Week Revenue: $" + expectedFutureRevenue);

        try (Connection co = ConnectionDB.getConnection()) {

            String query = "SELECT * FROM projects WHERE status='pending'";
            ResultSet rs = co.prepareStatement(query).executeQuery();

            while (rs.next()) {

                projects.add(new Project(
                        rs.getInt("project_id"),
                        rs.getString("title"),
                        rs.getInt("deadline"),
                        rs.getDouble("revenue"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Comparative Scoring
        Map<Project, Double> scoreMap = new HashMap<>();

        for (Project p : projects) {

            double urgency = 1.0 / p.getDeadline();
            double risk = (p.getDeadline() <= 5) ? 1 : 0;

            double futurePenalty = 0;

            if (p.getDeadline() > 5 && p.getRevenue() < expectedFutureRevenue) {
                futurePenalty = 1;
            }

            double score = (0.6 * p.getRevenue())
                    + (0.3 * urgency * 1000)
                    + (0.1 * risk * 1000)
                    - (0.4 * futurePenalty * 1000);

            scoreMap.put(p, score);
        }

        projects.sort((a, b) ->
                Double.compare(scoreMap.get(b), scoreMap.get(a)));

        // Greedy Allocation
        Project[] weekSlots = new Project[5];
        boolean[] filled = new boolean[5];
        double totalRevenue = 0;

        for (Project p : projects) {

            if (p.getDeadline() > 5) continue;

            int start = Math.min(4, p.getDeadline() - 1);

            for (int i = start; i >= 0; i--) {
                if (!filled[i]) {

                    filled[i] = true;
                    weekSlots[i] = p;
                    totalRevenue += p.getRevenue();

                    markCompleted(p.getProjectId());
                    break;
                }
            }
        }

        displaySchedule(weekSlots, totalRevenue);
    }

    // ------------------- CALCULATE LAST 4 WEEKS AVG -------------------

    private double calculateRecentAverageRevenue() {

        double avg = 0;

        String query = """
            SELECT AVG(weekly_total)
            FROM (
                SELECT SUM(revenue) AS weekly_total
                FROM projects
                WHERE status='completed'
                GROUP BY week_no
                ORDER BY week_no DESC
                LIMIT 4
            ) AS last_weeks
        """;

        try (Connection co = ConnectionDB.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                avg = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return avg;
    }

    // ------------------- MONTE CARLO SIMULATION -------------------

    private double simulateFutureRevenue(double base) {

        Random rand = new Random();
        int simulations = 200;
        double total = 0;

        for (int i = 0; i < simulations; i++) {

            double variation = (rand.nextDouble() - 0.5) * 0.4; // ±20%
            double simulated = base + (base * variation);
            total += simulated;
        }

        return total / simulations;
    }

    // ------------------- MARK COMPLETED -------------------

    private void markCompleted(int id) {

        try (Connection co = ConnectionDB.getConnection()) {

            String query = """
                UPDATE projects
                SET status='completed', week_no=?
                WHERE project_id=?
            """;

            PreparedStatement ps = co.prepareStatement(query);
            ps.setInt(1, CURRENT_WEEK);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------- DISPLAY -------------------

    private void displaySchedule(Project[] schedule, double total) {

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        System.out.println("\n--- AI OPTIMIZED WEEK PLAN ---");

        for (int i = 0; i < 5; i++) {

            System.out.print(days[i] + ": ");

            if (schedule[i] != null) {
                System.out.println(schedule[i].getProjectTitle()
                        + " ($" + schedule[i].getRevenue() + ")");
            } else {
                System.out.println("[Free]");
            }
        }

        System.out.println("TOTAL REVENUE: $" + total);
    }
}