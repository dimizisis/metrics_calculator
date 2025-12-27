package output;

import main.ProjectMetricsAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultPrinter {

    private static final Logger logger = LogManager.getLogger(ResultPrinter.class);

    private ResultPrinter() {
    }

    private static final String[] HEADERS = { "Name",
            "WMC", "DIT", "NOCC", "CBO", "RFC", "LCOM",
            "WMC*", "NOM", "MPC", "DAC", "SIZE1", "SIZE2", "DSC", "NOH", "ANA", "DAM", "DCC", "CAMC", "MOA", "MFA", "NOP", "CIS", "NPM",
            "Reusability", "Flexibility", "Understandability", "Functionality", "Extendibility", "Effectiveness",
            "fanIn", "ClassNames" };


    /** *
     * Print the analysis results in a CSV file.
     *
     * @param mc                The ProjectMetricsAnalyzer containing the analysis results.
     * @param fullOutFilePath   The full path of the output CSV file.
     * @return true if the file was written successfully, false otherwise.
     */
    public static boolean printCSV(ProjectMetricsAnalyzer mc, String fullOutFilePath) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, "\t");
        output.replace(output.lastIndexOf("\t"), output.lastIndexOf("\t")+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath().replace("\\", "/")).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n"));

        return writeFile(fullOutFilePath, output);
    }

    /** *
     * Print the analysis results in a CSV file with a custom delimiter.
     *
     * @param mc                The ProjectMetricsAnalyzer containing the analysis results.
     * @param fullOutFilePath   The full path of the output CSV file.
     * @param delimiter         The delimiter to use in the CSV file.
     * @return true if the file was written successfully, false otherwise.
     */
    public static boolean printCSV(ProjectMetricsAnalyzer mc, String fullOutFilePath, String delimiter) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, delimiter);
        output.replace(output.lastIndexOf(delimiter), output.lastIndexOf(delimiter)+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath().replace("\\", "/")).append(delimiter).append(javaFile.getQualityMetrics().toString(delimiter)).append(delimiter).append(javaFile.getClassNames()).append("\n"));

        return writeFile(fullOutFilePath, output);
    }

    /** *
     * Print the analysis results to the console.
     *
     * @param mc    The ProjectMetricsAnalyzer containing the analysis results.
     * @return true if the results were printed successfully, false otherwise.
     */
    public static boolean printString(ProjectMetricsAnalyzer mc) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, "\t");
        output.replace(output.lastIndexOf("\t"), output.lastIndexOf("\t")+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath().replace("\\", "/")).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n"));

        logger.info(output);
        return true;
    }

    private static void appendHeaders(StringBuilder data, String delimiter) {
        for (String header : HEADERS)
            data.append(header).append(delimiter);
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
