package infrastructure.entities;

import infrastructure.metrics.QualityMetrics;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class JavaClass {

    private String qualifiedName;
    private QualityMetrics qualityMetrics;

    public JavaClass(String name) {
        this.qualifiedName = name;
        this.qualityMetrics = new QualityMetrics();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaClass aClass = (JavaClass) o;
        return Objects.equals(qualifiedName, aClass.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
