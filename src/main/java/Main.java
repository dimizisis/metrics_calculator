import calculator.MetricsCalculator;
import gui.GUI;
import infrastructure.entities.Project;
import org.apache.commons.cli.*;
import output.ResultPrinter;

import java.awt.*;

public class Main {
    public static void main(String[] args) {

        Options options = addOptions();
        CommandLine cmd = getCmd(options, args);
        checkArgs(cmd.getArgs());

        if (cmd.getArgs().length != 0) {
            Project project = new Project(args[0].replace("\\", "/"));
            String outputFilename = cmd.getArgs()[1].replace("\\", "/");
            MetricsCalculator mc = new MetricsCalculator(project);
            mc.start();
            if (outputFilename.equals("str"))
                ResultPrinter.printString(mc);
            else
                ResultPrinter.printCSV(mc, outputFilename);
        } else {
            EventQueue.invokeLater(() -> {
                try {
                    new GUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static CommandLine getCmd(Options options, String[] args){
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }

    private static Options addOptions(){
        return new Options();
    }

    private static void checkArgs(String[] argv){
        if(argv.length != 0 && argv.length != 2){
            System.out.println("Error - Missing arguments");
            System.exit(0);
        }
    }
}
