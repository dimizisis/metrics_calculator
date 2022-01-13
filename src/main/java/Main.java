import calculator.MetricsCalculator;
import infrastructure.entities.Project;

public class Main {
    public static void main(String[] args) {
        Project project = new Project(args[0].replace("\\", "/"));
        MetricsCalculator mc = new MetricsCalculator(project);
        mc.start();
        System.out.println(mc.printResults());
    }
}
