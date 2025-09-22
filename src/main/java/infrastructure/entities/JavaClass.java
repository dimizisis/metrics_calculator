package infrastructure.entities;

import infrastructure.metrics.QualityMetrics;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class JavaClass {
    @ToString.Include
    private String qualifiedName;
    private QualityMetrics qualityMetrics;

    public JavaClass(String name) {
        this.qualifiedName = name;
        this.qualityMetrics = new QualityMetrics();
    }
}
