package infrastructure.metrics;

import lombok.*;

@Data
public class QualityMetrics {
    private int dsc;
    private double complexity;
    private int dit;
    private int nocc;
    private double rfc;
    private double lcom;
    private double wmc;
    private double nom;
    private double mpc;
    private int dac;
    private double cbo;
    private int size1;
    private int size2;
    private int noh;
    private int ana;
    private double dam;
    private double dcc;
    private double camc;
    private int moa;
    private double mfa;
    private int nop;
    private int cis;
    private int npm;
    private int fanIn;
    private double reusability;
    private double flexibility;
    private double understandability;
    private double functionality;
    private double extendibility;
    private double effectiveness;

    public void add(QualityMetrics o) {
        complexity = performAdditionWithSanityCheck(complexity, o.getComplexity());
        dit = performAdditionWithSanityCheck(dit, o.getDit());
        lcom = performAdditionWithSanityCheck(lcom, o.getLcom());
        nocc += o.getNocc();
        rfc += o.getRfc();
        wmc += o.getWmc();
        nom += o.getNom();
        mpc += o.getMpc();
        dac += o.getDac();
        cbo += o.getCbo();
        size1 += o.getSize1();
        size2 += o.getSize2();
        noh += o.getNoh();
        ana += o.getAna();
        dam += o.getDam();
        dcc += o.getDcc();
        camc += o.getCamc();
        moa += o.getMoa();
        mfa += o.getMfa();
        nop += o.getNop();
        cis += o.getCis();
        npm += o.getNpm();
        fanIn += o.getFanIn();
        ++dsc;
    }

    /**
     * Performs addition of two metric values with sanity checks.
     * Makes sense to use only for the metrics that may hold negative
     * value.
     *
     * @param metricValue The current value of the metric.
     * @param metricValueToBeAdded The value to be added to the metric.
     * @return The updated metric value after performing sanity checks.
     */
    private double performAdditionWithSanityCheck(double metricValue, double metricValueToBeAdded) {
        /* Ignore negative values to be added */
        if (metricValueToBeAdded < 0.0) {
            return metricValue;
        }

        /* Reset negative metric value to 0.0 */
        if (metricValue < 0.0) {
            metricValue = 0.0;
        }

        /* Perform the addition */
        metricValue += metricValueToBeAdded;

        return metricValue;
    }

    /**
     * Performs addition of two metric values with sanity checks.
     *
     * @param metricValue The current value of the metric.
     * @param metricValueToBeAdded The value to be added to the metric.
     * @return The updated metric value after performing sanity checks.
     */
    private int performAdditionWithSanityCheck(int metricValue, int metricValueToBeAdded) {
        /* Ignore negative values to be added */
        if (metricValueToBeAdded < 0) {
            return metricValue;
        }

        /* Reset negative metric value to 0 */
        if (metricValue < 0) {
            metricValue = 0;
        }

        /* Perform the addition */
        metricValue += metricValueToBeAdded;

        return metricValue;
    }

    /** *
     * Increments the number of occurrences (nocc) by 1.
     *
     * @return The updated value of nocc after incrementing.
     */
    public int incrementNocc() {
        return ++nocc;
    }

    @Override
    public String toString() {
        return toString("\t");
    }

    /**
     * Returns a delimited string of all metric values.
     * Uses reflection to automatically include all fields in declaration order.
     *
     * @param delimiter the delimiter to use between values
     * @return delimited string of all metric values
     */
    public String toString(String delimiter) {
        return String.join(delimiter, getMetricValuesAsStrings());
    }

    /**
     * Returns the names of all metric fields in declaration order.
     * Static method so it can be called without an instance.
     *
     * @return array of metric field names
     */
    public static String[] getMetricNames() {
        return java.util.Arrays.stream(QualityMetrics.class.getDeclaredFields())
                .filter(f -> !java.lang.reflect.Modifier.isStatic(f.getModifiers()))
                .map(java.lang.reflect.Field::getName)
                .map(name -> name.toUpperCase().replace("_", " "))
                .toArray(String[]::new);
    }

    /**
     * Returns the values of all metric fields as strings in declaration order.
     *
     * @return list of metric values as strings
     */
    private java.util.List<String> getMetricValuesAsStrings() {
        return java.util.Arrays.stream(QualityMetrics.class.getDeclaredFields())
                .filter(f -> !java.lang.reflect.Modifier.isStatic(f.getModifiers()))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        return value != null ? value.toString() : "0";
                    } catch (IllegalAccessException e) {
                        return "0";
                    }
                })
                .toList();
    }
}

