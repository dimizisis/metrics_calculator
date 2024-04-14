package infrastructure.metrics;

import java.util.Objects;

public class QualityMetrics {

    private Integer DSC;
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
    private Integer NOH;
    private Integer ANA;
    private Double DAM;
    private Double DCC;
    private Double CAMC;
    private Integer MOA;
    private Double MFA;
    private Integer NOP;
    private Integer CIS;
    private Integer NPM;
    private Integer FanIn;
    protected double reusability;
    protected double flexibility;
    protected double understandability;
    protected double functionality;
    protected double extendibility;
    protected double effectiveness;

    public QualityMetrics() {
        this.DSC = 0;
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
        this.NOH = 0;
        this.ANA = 0;
        this.DAM = 0.0;
        this.DCC = 0.0;
        this.CAMC = 0.0;
        this.MOA = 0;
        this.MFA = 0.0;
        this.NOP = 0;
        this.CIS = 0;
        this.NPM = 0;
        this.FanIn = 0;
        this.reusability = 0.0;
        this.flexibility = 0.0;
        this.understandability = 0.0;
        this.functionality = 0.0;
        this.extendibility = 0.0;
        this.effectiveness = 0.0;
    }

    public void add(QualityMetrics o) {
        if (this.complexity <= 0 && o.getComplexity() < 0)
            this.complexity = -1.0;
        else if (o.getComplexity() > 0)
            this.complexity += o.getComplexity();
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
        this.NOH += o.getNOH();
        this.ANA += o.getANA();
        this.DAM += o.getDAM();
        this.DCC += o.getDCC();
        this.CAMC += o.getCAMC();
        this.MOA += o.getMOA();
        this.MFA += o.getMFA();
        this.NOP += o.getNOP();
        this.CIS += o.getCIS();
        this.NPM += o.getNPM();
        this.FanIn += o.FanIn;
        ++this.DSC;
    }

    public void zero() {
        this.DSC = 0;
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
        this.NOH = 0;
        this.ANA = 0;
        this.DAM = 0.0;
        this.DCC = 0.0;
        this.CAMC = 0.0;
        this.MOA = 0;
        this.MFA = 0.0;
        this.NOP = 0;
        this.CIS = 0;
        this.NPM = 0;
        this.FanIn = 0;
    }

    public Integer getDSC() {
        return DSC;
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

    public void setDSC(Integer DSC) {
        this.DSC = DSC;
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

    public Integer getNPM() {
        return NPM;
    }

    public void setNPM(Integer NPM) {
        this.NPM = NPM;
    }

    public Integer getNOH() {
        return NOH;
    }

    public void setNOH(Integer NOH) {
        this.NOH = NOH;
    }

    public Integer getANA() {
        return ANA;
    }

    public void setANA(Integer ANA) {
        this.ANA = ANA;
    }

    public Double getDAM() {
        return DAM;
    }

    public void setDAM(Double DAM) {
        this.DAM = DAM;
    }

    public Double getDCC() {
        return DCC;
    }

    public void setDCC(Double DCC) {
        this.DCC = DCC;
    }

    public Double getCAMC() {
        return CAMC;
    }

    public void setCAMC(Double CAMC) {
        this.CAMC = CAMC;
    }

    public Integer getMOA() {
        return MOA;
    }

    public void setMOA(Integer MOA) {
        this.MOA = MOA;
    }

    public Double getMFA() {
        return MFA;
    }

    public void setMFA(Double MFA) {
        this.MFA = MFA;
    }

    public Integer getNOP() {
        return NOP;
    }

    public void setNOP(Integer NOP) {
        this.NOP = NOP;
    }

    public Integer getCIS() {
        return CIS;
    }

    public void setCIS(Integer CIS) {
        this.CIS = CIS;
    }

    public Integer getFanIn() {
        return FanIn;
    }

    public void setFanIn(Integer fanIn) {
        FanIn = fanIn;
    }

    public double getReusability() {
        return reusability;
    }

    public void setReusability(double reusability) {
        this.reusability = reusability;
    }

    public double getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(double flexibility) {
        this.flexibility = flexibility;
    }

    public double getUnderstandability() {
        return understandability;
    }

    public void setUnderstandability(double understandability) {
        this.understandability = understandability;
    }

    public double getFunctionality() {
        return functionality;
    }

    public void setFunctionality(double functionality) {
        this.functionality = functionality;
    }

    public double getExtendibility() {
        return extendibility;
    }

    public void setExtendibility(double extendibility) {
        this.extendibility = extendibility;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(double effectiveness) {
        this.effectiveness = effectiveness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityMetrics that = (QualityMetrics) o;
        return Objects.equals(DSC, that.DSC) && Objects.equals(DIT, that.DIT) && Objects.equals(NOCC, that.NOCC) && Objects.equals(RFC, that.RFC) && Objects.equals(LCOM, that.LCOM) && Objects.equals(WMC, that.WMC) && Objects.equals(NOM, that.NOM) && Objects.equals(MPC, that.MPC) && Objects.equals(DAC, that.DAC) && Objects.equals(CBO, that.CBO) && Objects.equals(SIZE1, that.SIZE1) && Objects.equals(SIZE2, that.SIZE2) && Objects.equals(NOH, that.NOH) && Objects.equals(ANA, that.ANA) && Objects.equals(DAM, that.DAM) && Objects.equals(DCC, that.DCC) && Objects.equals(CAMC, that.CAMC) && Objects.equals(MOA, that.MOA) && Objects.equals(MFA, that.MFA) && Objects.equals(NOP, that.NOP) && Objects.equals(CIS, that.CIS) && Objects.equals(NPM, that.NPM) && Objects.equals(FanIn, that.FanIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DSC, DIT, NOCC, RFC, LCOM, WMC, NOM, MPC, DAC, CBO, SIZE1, SIZE2, NOH, ANA, DAM, DCC, CAMC, MOA, MFA, NOP, CIS, NPM, FanIn);
    }

    @Override
    public String toString() {
        return this.getWMC() + "\t" + this.getDIT() + "\t" + this.getNOCC() + "\t" + this.getCBO() + "\t" + this.getRFC() + "\t" + this.getLCOM() + "\t" + this.getComplexity() + "\t" + this.getNOM() + "\t" + this.getMPC() + "\t" + this.getDAC() + "\t" + this.getSIZE1() + "\t" + this.getSIZE2() + "\t" + this.getDSC() + "\t" + this.getNOH() + "\t" + this.getANA() + "\t" + this.getDAM() + "\t" + this.getDCC() + "\t" + this.getCAMC() + "\t" + this.getMOA() + "\t" + this.getMFA() + "\t" + this.getNOP() + "\t" + this.getCIS() + "\t" + this.getNPM() + "\t" + this.getReusability() + "\t" + this.getFlexibility() + "\t" + this.getUnderstandability() + "\t" + this.getFunctionality() + "\t" + this.getExtendibility() + "\t" + this.getEffectiveness() + "\t" + this.getFanIn();
    }

    public String toString(String delimiter) {
        return this.getWMC() + delimiter + this.getDIT() + delimiter + this.getNOCC() + delimiter + this.getCBO() + delimiter + this.getRFC() + delimiter + this.getLCOM() + delimiter + this.getComplexity() + delimiter + this.getNOM() + delimiter + this.getMPC() + delimiter + this.getDAC() + delimiter + this.getSIZE1() + delimiter + this.getSIZE2() + delimiter + this.getDSC() + delimiter + this.getNOH() + delimiter + this.getANA() + delimiter + this.getDAM() + delimiter + this.getDCC() + delimiter + this.getCAMC() + delimiter + this.getMOA() + delimiter + this.getMFA() + delimiter + this.getNOP() + delimiter + this.getCIS() + delimiter + this.getNPM() + delimiter + this.getReusability() + delimiter + this.getFlexibility() + delimiter + this.getUnderstandability() + delimiter + this.getFunctionality() + delimiter + this.getExtendibility() + delimiter + this.getEffectiveness() + delimiter + this.getFanIn();
    }
}
