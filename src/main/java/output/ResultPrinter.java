package output;

import main.ProjectMetricsAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultPrinter {

    private static final Logger logger = LogManager.getLogger(ResultPrinter.class);

    private static final String[] HEADERS = { "Name",
            "WMC", "DIT", "NOCC", "CBO", "RFC", "LCOM",
            "WMC*", "NOM", "MPC", "DAC", "SIZE1", "SIZE2", "DSC", "NOH", "ANA", "DAM", "DCC", "CAMC", "MOA", "MFA", "NOP", "CIS", "NPM",
            "Reusability", "Flexibility", "Understandability", "Functionality", "Extendibility", "Effectiveness",
            "fanIn", "ClassNames" };

    private ResultPrinter() {
    }

    private static String buildOutput(ProjectMetricsAnalyzer mc, String delimiter) {
        StringBuilder output = new StringBuilder();
        output.append(String.join(delimiter, HEADERS)).append(System.lineSeparator());

        mc.getProject().getJavaFiles().forEach(javaFile ->
                output.append(String.join(delimiter,
                                javaFile.getPath().replace("\\", "/"),
                                javaFile.getQualityMetrics().toString(delimiter),
                                javaFile.getClassNames()))
                        .append(System.lineSeparator())
        );

        return output.toString();
    }

    public static boolean printCSV(ProjectMetricsAnalyzer mc, String fullOutFilePath, String delimiter) {
        String output = buildOutput(mc, delimiter);
        return writeFile(fullOutFilePath, new StringBuilder(output));
    }

    public static boolean printCSV(ProjectMetricsAnalyzer mc, String fullOutFilePath) {
        return printCSV(mc, fullOutFilePath, "\t");
    }

    public static boolean printString(ProjectMetricsAnalyzer mc) {
        logger.info(buildOutput(mc, "\t"));
        return true;
    }

    private static boolean writeFile(String path, StringBuilder data) {
        try {
            Files.write(Paths.get(path), data.toString().getBytes());
        } catch (IOException e) {
            logger.error("Exception was thrown", e);
            return false;
        }
        return true;
    }
}
