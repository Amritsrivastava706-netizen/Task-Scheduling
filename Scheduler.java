import java.util.*;
import java.sql.*;

public class Scheduler {

    private double lastWeekAvgRevenue = 600.0;

    // Simulate expected next week revenue (Monte Carlo simplified)
    private double simulateExpectedFutureRevenue() {

        Random rand = new Random();
        int simulations = 200;
        double total = 0;

        for (int i = 0; i < simulations; i++) {

            // Simulate possible revenue ranges
            int scenario = rand.nextInt(4);

            double simulatedRevenue;

            switch (scenario) {
                case 0: simulatedRevenue = 400 + rand.nextInt(200); break; // 400-600
                case 1: simulatedRevenue = 600 + rand.nextInt(300); break; // 600-900
                case 2: simulatedRevenue = 900 + rand.nextInt(500); break; // 900-1400
                default: simulatedRevenue = 1400 + rand.nextInt(600);      // 1400-2000
            }

            total += simulatedRevenue;
        }

        return total / simulations;
    }

    public void generateWeeklySchedule() {

        List<Project> potentialProjects = new ArrayList<>();

        double expectedFutureRevenue = simulateExpectedFutureRevenue();
        System.out.println("Predicted Next Week Avg Revenue (Monte Carlo): $" + expectedFutureRevenue);

        try (Connection co = ConnectionDB.getConnection()) {

            String query = "SELECT * FROM projects WHERE status='pending'";
            ResultSet rs = co.prepareStatement(query).executeQuery();

            while (rs.next()) {

                Project p = new Project(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getDouble(4)
                );

                potentialProjects.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Comparative Scoring Model
        Map<Project, Double> scoreMap = new HashMap<>();

        for (Project p : potentialProjects) {

            double urgency = 1.0 / p.getDeadline();
            double risk = (p.getDeadline() <= 5) ? 1 : 0;

            double futurePenalty = 0;

            // If deadline beyond this week and revenue lower than predicted future
            if (p.getDeadline() > 5 && p.getRevenue() < expectedFutureRevenue) {
                futurePenalty = 1;
            }

            double score = (0.6 * p.getRevenue())
                    + (0.3 * urgency * 1000)
                    + (0.1 * risk * 1000)
                    - (0.4 * futurePenalty * 1000);

            scoreMap.put(p, score);
        }

        // Sort by Score (not just revenue)
        potentialProjects.sort((a, b) ->
                Double.compare(scoreMap.get(b), scoreMap.get(a)));

        // Greedy Allocation
        Project[] weekSlots = new Project[5];
        boolean[] slotFilled = new boolean[5];
        double totalRevenue = 0;

        for (Project p : potentialProjects) {

            if (p.getDeadline() > 5) continue;

            int start = Math.min(4, p.getDeadline() - 1);

            for (int i = start; i >= 0; i--) {
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

        System.out.println("\n--- PROMANAGE AI-OPTIMIZED 5-DAY PLAN ---");

        for (int i = 0; i < 5; i++) {
            System.out.print(days[i] + ": ");
            if (schedule[i] != null) {
                System.out.println(schedule[i].getProjectTitle() +
                        " ($" + schedule[i].getRevenue() + ")");
            } else {
                System.out.println("[Free]");
            }
        }

        System.out.println("TOTAL REVENUE: $" + total);
    }
}