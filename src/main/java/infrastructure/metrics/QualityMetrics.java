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

    public String toString(String delimiter) {
        return getWmc() + delimiter + getDit() + delimiter + getNocc() + delimiter + getCbo() + delimiter + getRfc() + delimiter + getLcom() + delimiter + getComplexity() + delimiter + getNom() + delimiter + getMpc() + delimiter + getDac() + delimiter + getSize1() + delimiter + getSize2() + delimiter + getDsc() + delimiter + getNoh() + delimiter + getAna() + delimiter + getDam() + delimiter + getDcc() + delimiter + getCamc() + delimiter + getMoa() + delimiter + getMfa() + delimiter + getNop() + delimiter + getCis() + delimiter + getNpm() + delimiter + getReusability() + delimiter + getFlexibility() + delimiter + getUnderstandability() + delimiter + getFunctionality() + delimiter + getExtendibility() + delimiter + getEffectiveness() + delimiter + getFanIn();
    }
}

