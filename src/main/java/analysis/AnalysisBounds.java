package analysis;

@FunctionalInterface
public interface AnalysisBounds {
    /**
     * Check if the given qualified name is within the analysis bounds.
     * @param qualifiedName The qualified name to check.
     * @return True if the qualified name is within bounds, false otherwise.
     */
    boolean contains(String qualifiedName);
}
