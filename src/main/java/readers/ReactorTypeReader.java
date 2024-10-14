package readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactors.ReactorType;
import reactors.ReactorManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class ReactorTypeReader {
    public void importReactorsFromStream(InputStream file, ReactorManager reactorsOwner) {
        try {
            Path tempFile = Files.createTempFile("ReactorType", ".json");
            tempFile.toFile().deleteOnExit();
            Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);
            importReactorsFromFile(tempFile.toFile(), reactorsOwner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importReactorsFromFile(File file, ReactorManager reactorsOwner) {
        if (file.getName().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (Iterator<String> fieldNameIterator = rootNode.fieldNames(); fieldNameIterator.hasNext(); ) {
                String fieldName = fieldNameIterator.next();
                JsonNode reactorNode = rootNode.get(fieldName);
                if (reactorNode != null && reactorNode.isObject()) {
                    ReactorType reactor = parseReactorFromJsonNode(reactorNode);
                    reactorsOwner.addReactor(fieldName, reactor);
                }
            }
        } else {
            System.out.println("Неподдерживаемый формат");
        }
    }

    private ReactorType parseReactorFromJsonNode(JsonNode reactorNode) {
        return new ReactorType(
                reactorNode.has("type") ? reactorNode.get("type").asText() : null,
                reactorNode.has("class") ? reactorNode.get("class").asText() : null,
                reactorNode.has("burn_up") ? reactorNode.get("burn_up").asDouble() : 0.0,
                reactorNode.has("efficiency_factor") ? reactorNode.get("efficiency_factor").asDouble() : 0.0,
                reactorNode.has("enrichment") ? reactorNode.get("enrichment").asDouble() : 0.0,
                reactorNode.has("thermal_capacity") ? reactorNode.get("thermal_capacity").asDouble() : 0.0,
                reactorNode.has("electrical_capacity") ? reactorNode.get("electrical_capacity").asDouble() : 0.0,
                reactorNode.has("life_time") ? reactorNode.get("life_time").asInt() : 0,
                reactorNode.has("first_load") ? reactorNode.get("first_load").asDouble() : 0.0,
                "JSON"
        );
    }
}