package output;

import calculator.MetricsCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultPrinter {

    private static final String[] HEADERS = { "Name",
            "WMC", "DIT", "NOCC", "CBO", "RFC", "LCOM",
            "WMC*", "NOM", "MPC", "DAC", "SIZE1", "SIZE2", "DSC", "NOH", "ANA", "DAM", "DCC", "CAMC", "MOA", "MFA", "NOP", "CIS", "NPM",
            "Reusability", "Flexibility", "Understandability", "Functionality", "Extendibility", "Effectiveness",
            "FanIn", "ClassNames" };


    public static boolean printCSV(MetricsCalculator mc, String fullOutFilePath) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, "\t");
        output.replace(output.lastIndexOf("\t"), output.lastIndexOf("\t")+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath()).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n"));

        return writeFile(fullOutFilePath, output);
    }

    public static boolean printCSV(MetricsCalculator mc, String fullOutFilePath, String delimiter) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, delimiter);
        output.replace(output.lastIndexOf(delimiter), output.lastIndexOf(delimiter)+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath()).append(delimiter).append(javaFile.getQualityMetrics().toString(delimiter)).append(delimiter).append(javaFile.getClassNames()).append("\n"));

        return writeFile(fullOutFilePath, output);
    }

    public static boolean printString(MetricsCalculator mc) {
        StringBuilder output = new StringBuilder();
        appendHeaders(output, "\t");
        output.replace(output.lastIndexOf("\t"), output.lastIndexOf("\t")+1, "\n");
        mc.getProject().getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath()).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n"));

        System.out.println(output);
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
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
