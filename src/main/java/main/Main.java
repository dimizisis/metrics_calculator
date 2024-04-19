package main;

import calculator.MetricsCalculator;
import gui.GUI;
import infrastructure.entities.Project;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.ResultPrinter;

import java.awt.*;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

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
                ResultPrinter.printCSV(mc, outputFilename, ";");
        } else {
            EventQueue.invokeLater(() -> {
                try {
                    new GUI();
                } catch (Exception e) {
                    logger.error("Exception was thrown ", e);
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
            logger.error(e.getMessage());
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
            logger.error("Error - Missing arguments");
            System.exit(0);
        }
    }
}
