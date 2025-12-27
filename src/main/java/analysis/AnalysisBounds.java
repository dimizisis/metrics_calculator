package analysis;

@FunctionalInterface
public interface AnalysisBounds {
    boolean contains(String qualifiedName);
}
