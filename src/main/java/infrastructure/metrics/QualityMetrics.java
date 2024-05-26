package infrastructure.metrics;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class QualityMetrics {

    private Integer dsc;
    private Double complexity;
    private Integer dit;
    private Integer nocc;
    private Double rfc;
    private Double lcom;
    private Double wmc;
    private Double nom;
    private Double mpc;
    private Integer dac;
    private Double cbo;
    private Integer size1;
    private Integer size2;
    private Integer noh;
    private Integer ana;
    private Double dam;
    private Double dcc;
    private Double camc;
    private Integer moa;
    private Double mfa;
    private Integer nop;
    private Integer cis;
    private Integer npm;
    private Integer fanIn;
    protected double reusability;
    protected double flexibility;
    protected double understandability;
    protected double functionality;
    protected double extendibility;
    protected double effectiveness;

    public QualityMetrics() {
        dsc = 0;
        complexity = 0.0;
        dit = 0;
        nocc = 0;
        rfc = 0.0;
        lcom = 0.0;
        wmc = 0.0;
        nom = 0.0;
        mpc = 0.0;
        dac = 0;
        cbo = 0.0;
        size1 = 0;
        size2 = 0;
        noh = 0;
        ana = 0;
        dam = 0.0;
        dcc = 0.0;
        camc = 0.0;
        moa = 0;
        mfa = 0.0;
        nop = 0;
        cis = 0;
        npm = 0;
        fanIn = 0;
        reusability = 0.0;
        flexibility = 0.0;
        understandability = 0.0;
        functionality = 0.0;
        extendibility = 0.0;
        effectiveness = 0.0;
    }

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

    public void zero() {
        dsc = 0;
        complexity = 0.0;
        dit = 0;
        rfc = 0.0;
        lcom = 0.0;
        wmc = 0.0;
        nom = 0.0;
        mpc = 0.0;
        dac = 0;
        cbo = 0.0;
        size1 = 0;
        size2 = 0;
        noh = 0;
        ana = 0;
        dam = 0.0;
        dcc = 0.0;
        camc = 0.0;
        moa = 0;
        mfa = 0.0;
        nop = 0;
        cis = 0;
        npm = 0;
        fanIn = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityMetrics that = (QualityMetrics) o;
        return Objects.equals(dsc, that.dsc) && Objects.equals(dit, that.dit) && Objects.equals(nocc, that.nocc) && Objects.equals(rfc, that.rfc) && Objects.equals(lcom, that.lcom) && Objects.equals(wmc, that.wmc) && Objects.equals(nom, that.nom) && Objects.equals(mpc, that.mpc) && Objects.equals(dac, that.dac) && Objects.equals(cbo, that.cbo) && Objects.equals(size1, that.size1) && Objects.equals(size2, that.size2) && Objects.equals(noh, that.noh) && Objects.equals(ana, that.ana) && Objects.equals(dam, that.dam) && Objects.equals(dcc, that.dcc) && Objects.equals(camc, that.camc) && Objects.equals(moa, that.moa) && Objects.equals(mfa, that.mfa) && Objects.equals(nop, that.nop) && Objects.equals(cis, that.cis) && Objects.equals(npm, that.npm) && Objects.equals(fanIn, that.fanIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dsc, dit, nocc, rfc, lcom, wmc, nom, mpc, dac, cbo, size1, size2, noh, ana, dam, dcc, camc, moa, mfa, nop, cis, npm, fanIn);
    }

    @Override
    public String toString() {
        return getWmc() + "\t" + getDit() + "\t" + getNocc() + "\t" + getCbo() + "\t" + getRfc() + "\t" + getLcom() + "\t" + getComplexity() + "\t" + getNom() + "\t" + getMpc() + "\t" + getDac() + "\t" + getSize1() + "\t" + getSize2() + "\t" + getDsc() + "\t" + getNoh() + "\t" + getAna() + "\t" + getDam() + "\t" + getDcc() + "\t" + getCamc() + "\t" + getMoa() + "\t" + getMfa() + "\t" + getNop() + "\t" + getCis() + "\t" + getNpm() + "\t" + getReusability() + "\t" + getFlexibility() + "\t" + getUnderstandability() + "\t" + getFunctionality() + "\t" + getExtendibility() + "\t" + getEffectiveness() + "\t" + getFanIn();
    }

    public String toString(String delimiter) {
        return getWmc() + delimiter + getDit() + delimiter + getNocc() + delimiter + getCbo() + delimiter + getRfc() + delimiter + getLcom() + delimiter + getComplexity() + delimiter + getNom() + delimiter + getMpc() + delimiter + getDac() + delimiter + getSize1() + delimiter + getSize2() + delimiter + getDsc() + delimiter + getNoh() + delimiter + getAna() + delimiter + getDam() + delimiter + getDcc() + delimiter + getCamc() + delimiter + getMoa() + delimiter + getMfa() + delimiter + getNop() + delimiter + getCis() + delimiter + getNpm() + delimiter + getReusability() + delimiter + getFlexibility() + delimiter + getUnderstandability() + delimiter + getFunctionality() + delimiter + getExtendibility() + delimiter + getEffectiveness() + delimiter + getFanIn();
    }
}

