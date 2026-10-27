package org.projectcontinuum.feature.example.java.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.projectcontinuum.core.commons.annotation.ContinuumNode;
import org.projectcontinuum.core.commons.model.ContinuumWorkflowModel;
import org.projectcontinuum.core.commons.node.ProcessNodeModel;
import org.projectcontinuum.core.commons.protocol.progress.NodeProgressCallback;
import org.projectcontinuum.core.commons.utils.NodeInputReader;
import org.projectcontinuum.core.commons.utils.NodeOutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Hello Java Node — a minimal example Continuum node written in Java.
 *
 * <p>Reads every row from the input table and appends a new "greeting" column
 * whose value is "Hello, {name}!" where {name} comes from a configurable
 * source column (default: "name"). If the source column is missing, the
 * greeting falls back to "Hello, World!".</p>
 */
@ContinuumNode
public class HelloJavaNodeModel extends ProcessNodeModel {

    private static final Logger logger = LoggerFactory.getLogger(HelloJavaNodeModel.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** Instantiated by Spring as a prototype-scoped bean (see {@link ContinuumNode}). */
    public HelloJavaNodeModel() {
    }

    // ── Ports ────────────────────────────────────────────────

    @NotNull
    private final Map<String, ContinuumWorkflowModel.NodePort> inputPorts = Map.of(
            "input", new ContinuumWorkflowModel.NodePort(
                    "Input Table",
                    MediaType.APPLICATION_OCTET_STREAM_VALUE
            )
    );

    @NotNull
    private final Map<String, ContinuumWorkflowModel.NodePort> outputPorts = Map.of(
            "output", new ContinuumWorkflowModel.NodePort(
                    "Output Table",
                    MediaType.APPLICATION_OCTET_STREAM_VALUE
            )
    );

    @NotNull
    @Override
    public Map<String, ContinuumWorkflowModel.NodePort> getInputPorts() {
        return inputPorts;
    }

    @NotNull
    @Override
    public Map<String, ContinuumWorkflowModel.NodePort> getOutputPorts() {
        return outputPorts;
    }

    // ── Categories ───────────────────────────────────────────

    @NotNull
    @Override
    public List<String> getCategories() {
        return List.of("Examples");
    }

    // ── Properties JSON Schema ───────────────────────────────

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    // Jackson 3 throws the unchecked JacksonException, so no try/catch is needed
    private static final Map<String, Object> propertiesSchema = objectMapper.readValue("""
            {
              "type": "object",
              "properties": {
                "nameColumn": {
                  "type": "string",
                  "title": "Name Column",
                  "description": "Column to read the name from.",
                  "default": "name"
                },
                "greeting": {
                  "type": "string",
                  "title": "Greeting Prefix",
                  "description": "The greeting word to use before the name.",
                  "default": "Hello"
                },
                "outputColumn": {
                  "type": "string",
                  "title": "Output Column Name",
                  "description": "Name of the new column that will contain the greeting.",
                  "default": "greeting"
                }
              },
              "required": []
            }
            """, MAP_TYPE);

    // ── Properties UI Schema (JSON Forms) ────────────────────

    private static final Map<String, Object> propertiesUiSchema = objectMapper.readValue("""
            {
              "type": "VerticalLayout",
              "elements": [
                { "type": "Control", "scope": "#/properties/nameColumn" },
                { "type": "Control", "scope": "#/properties/greeting" },
                { "type": "Control", "scope": "#/properties/outputColumn" }
              ]
            }
            """, MAP_TYPE);

    // ── Node Icon (SVG) ──────────────────────────────────────

    private static final String ICON = """
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
              <line x1="9" y1="9" x2="9.01" y2="9"/>
              <line x1="15" y1="9" x2="15.01" y2="9"/>
            </svg>
            """;

    // ── Metadata ─────────────────────────────────────────────

    // Java has no named/default arguments, so every NodeData constructor parameter is passed in order
    @NotNull
    private final ContinuumWorkflowModel.NodeData metadata = new ContinuumWorkflowModel.NodeData(
            getClass().getName(),                // id
            "Appends a greeting column. A Java example node.", // description
            "Hello Java",                        // title
            "Append a greeting column",          // subTitle
            ICON,                                // icon
            getClass().getName(),                // nodeModel
            null,                                // busy
            inputPorts,                          // inputs
            outputPorts,                         // outputs
            Map.of(                              // properties (defaults)
                    "nameColumn", "name",
                    "greeting", "Hello",
                    "outputColumn", "greeting"
            ),
            propertiesSchema,                    // propertiesSchema
            propertiesUiSchema,                  // propertiesUISchema
            null,                                // status
            null,                                // nodeProgress
            null                                 // retryOptions (null = workflow default)
    );

    @NotNull
    @Override
    public ContinuumWorkflowModel.NodeData getMetadata() {
        return metadata;
    }

    // ── Execution ────────────────────────────────────────────

    @Override
    public void execute(
            @Nullable Map<String, ? extends Object> properties,
            @NotNull Map<String, NodeInputReader> inputs,
            @NotNull NodeOutputWriter nodeOutputWriter,
            @NotNull NodeProgressCallback nodeProgressCallback
    ) {
        String nameColumn = getProperty(properties, "nameColumn", "name");
        String greetingPrefix = getProperty(properties, "greeting", "Hello");
        String outputColumn = getProperty(properties, "outputColumn", "greeting");

        NodeInputReader inputReader = inputs.get("input");
        if (inputReader == null) {
            throw new IllegalStateException("Input port 'input' is not connected");
        }

        NodeOutputWriter.OutputPortWriter writer = nodeOutputWriter.createOutputPortWriter("output");

        logger.info("HelloJavaNode: nameColumn={}, greeting={}, outputColumn={}",
                nameColumn, greetingPrefix, outputColumn);

        long totalRows = inputReader.getRowCount();
        long rowNumber = 0;

        try {
            Map<String, Object> row;
            while ((row = inputReader.read()) != null) {
                Object nameValue = row.get(nameColumn);
                String name = (nameValue != null) ? nameValue.toString() : "World";
                String greetingValue = greetingPrefix + ", " + name + "!";

                // Original columns plus the new greeting column (row numbers start at 0)
                Map<String, Object> outputRow = new HashMap<>(row);
                outputRow.put(outputColumn, greetingValue);
                writer.write(rowNumber, outputRow);
                rowNumber++;

                if (totalRows > 0) {
                    nodeProgressCallback.report((int) (rowNumber * 100 / totalRows));
                }
            }
        } finally {
            writer.close();
            inputReader.close();
        }

        nodeProgressCallback.report(100);
        logger.info("HelloJavaNode: processed {} rows", rowNumber);
    }

    private static String getProperty(Map<String, ?> properties, String key, String defaultValue) {
        if (properties == null) return defaultValue;
        Object value = properties.get(key);
        return (value != null) ? value.toString() : defaultValue;
    }
}
