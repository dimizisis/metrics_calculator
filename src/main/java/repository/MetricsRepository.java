package repository;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;

import java.util.Collection;

/**
 * Repository providing access to all analyzed classes and their metrics.
 * Used by aggregate metric calculators to query and update metrics across multiple classes.
 */
public interface MetricsRepository {

    /**
     * Get all class names in the repository (both project and external classes).
     *
     * @return collection of all qualified class names
     */
    Collection<String> getAllClassNames();

    /**
     * Get only the class names that belong to the analyzed project.
     *
     * @return collection of project class names
     */
    Collection<String> getProjectClassNames();

    /**
     * Get the analyzed data for a specific class.
     *
     * @param qualifiedName the fully qualified class name
     * @return the class data, or null if not found
     */
    ClassData getClassData(String qualifiedName);

    /**
     * Get the quality metrics for a specific class.
     *
     * @param qualifiedName the fully qualified class name
     * @return the quality metrics, or null if not found
     */
    QualityMetrics getMetrics(String qualifiedName);

    /**
     * Check if a class belongs to the analyzed project.
     *
     * @param qualifiedName the fully qualified class name
     * @return true if the class is part of the project, false otherwise
     */
    boolean isProjectClass(String qualifiedName);
}