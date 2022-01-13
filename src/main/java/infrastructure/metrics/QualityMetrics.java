package infrastructure.metrics;

import java.util.Objects;

public class QualityMetrics {

    private Integer classesNum;
    private Double complexity;
    private Integer DIT;
    private Integer NOCC;
    private Double RFC;
    private Double LCOM;
    private Double WMC;
    private Double NOM;
    private Double MPC;
    private Integer DAC;
    private Double CBO;
    private Integer SIZE1;
    private Integer SIZE2;

    public QualityMetrics() {
        this.classesNum = 0;
        this.complexity = 0.0;
        this.DIT = 0;
        this.NOCC = 0;
        this.RFC = 0.0;
        this.LCOM = 0.0;
        this.WMC = 0.0;
        this.NOM = 0.0;
        this.MPC = 0.0;
        this.DAC = 0;
        this.CBO = 0.0;
        this.SIZE1 = 0;
        this.SIZE2 = 0;
    }

    public void add(QualityMetrics o) {
        this.complexity += o.getComplexity();
        if (this.DIT >= 0 && o.getDIT() >= 0)
            this.DIT += o.getDIT();
        this.NOCC += o.getNOCC();
        this.RFC += o.getRFC();
        if (this.LCOM >= 0 && o.getLCOM() >= 0.0)
            this.LCOM += o.getLCOM();
        this.WMC += o.getWMC();
        this.NOM += o.getNOM();
        this.MPC += o.getMPC();
        this.DAC += o.getDAC();
        this.CBO += o.getCBO();
        this.SIZE1 += o.getSIZE1();
        this.SIZE2 += o.getSIZE2();
        ++this.classesNum;
    }

    public void zero() {
        this.classesNum = 0;
        this.complexity = 0.0;
        this.DIT = 0;
        this.RFC = 0.0;
        this.LCOM = 0.0;
        this.WMC = 0.0;
        this.NOM = 0.0;
        this.MPC = 0.0;
        this.DAC = 0;
        this.CBO = 0.0;
        this.SIZE1 = 0;
        this.SIZE2 = 0;
    }

    public Integer getClassesNum() {
        return classesNum;
    }

    public Double getComplexity() {
        return complexity;
    }

    public Integer getDIT() {
        return DIT;
    }

    public Integer getNOCC() {
        return NOCC;
    }

    public Double getRFC() {
        return RFC;
    }

    public Double getLCOM() {
        return LCOM;
    }

    public Double getWMC() {
        return WMC;
    }

    public Double getNOM() {
        return NOM;
    }

    public Double getMPC() {
        return MPC;
    }

    public Integer getDAC() {
        return DAC;
    }

    public Integer getSIZE1() {
        return SIZE1;
    }

    public Integer getSIZE2() {
        return SIZE2;
    }

    public Double getCBO() {
        return CBO;
    }

    public void setClassesNum(Integer classesNum) {
        this.classesNum = classesNum;
    }

    public void setComplexity(Double complexity) {
        this.complexity = complexity;
    }

    public void setDIT(Integer DIT) {
        this.DIT = DIT;
    }

    public void setNOCC(Integer NOCC) {
        this.NOCC = NOCC;
    }

    public void setRFC(Double RFC) {
        this.RFC = RFC;
    }

    public void setLCOM(Double LCOM) {
        this.LCOM = LCOM;
    }

    public void setWMC(Double WMC) {
        this.WMC = WMC;
    }

    public void setNOM(Double NOM) {
        this.NOM = NOM;
    }

    public void setMPC(Double MPC) {
        this.MPC = MPC;
    }

    public void setDAC(Integer DAC) {
        this.DAC = DAC;
    }

    public void setSIZE1(Integer SIZE1) {
        this.SIZE1 = SIZE1;
    }

    public void setSIZE2(Integer SIZE2) {
        this.SIZE2 = SIZE2;
    }

    public void setCBO(Double CBO) {
        this.CBO = CBO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityMetrics that = (QualityMetrics) o;
        return Objects.equals(classesNum, that.classesNum) && Objects.equals(complexity, that.complexity) && Objects.equals(DIT, that.DIT) && Objects.equals(NOCC, that.NOCC) && Objects.equals(RFC, that.RFC) && Objects.equals(LCOM, that.LCOM) && Objects.equals(WMC, that.WMC) && Objects.equals(NOM, that.NOM) && Objects.equals(MPC, that.MPC) && Objects.equals(DAC, that.DAC) && Objects.equals(CBO, that.CBO) && Objects.equals(SIZE1, that.SIZE1) && Objects.equals(SIZE2, that.SIZE2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classesNum, complexity, DIT, NOCC, RFC, LCOM, WMC, NOM, MPC, DAC, CBO, SIZE1, SIZE2);
    }

    @Override
    public String toString() {
        return this.getClassesNum() + "\t" + this.getWMC() + "\t" + this.getDIT() + "\t" + this.getComplexity() + "\t" + this.getLCOM() + "\t" + this.getMPC() + "\t" + this.getNOM() + "\t" + this.getRFC() + "\t" + this.getDAC() + "\t" + this.getNOCC() + "\t" + this.getCBO() + "\t" + this.getSIZE1() + "\t" + this.getSIZE2();
    }
}
