package org.projectcontinuum.feature.example.java;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto-configuration entry point for this feature module.
 *
 * <p>Registered in {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * so a worker that depends on this module picks up every {@code @ContinuumNode} in this package.</p>
 */
@Configuration
@ComponentScan(basePackages = "org.projectcontinuum.feature.example.java")
public class AutoConfigure {

    /** Instantiated by Spring. */
    public AutoConfigure() {
    }
}
