package reactors;

import readers.ReactorTypeReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReactorManager {
    private final Map<String, ReactorType> reactorsMap;
    public ReactorManager() {
        this.reactorsMap = new HashMap<>();
        ReactorTypeReader reactorImporter = new ReactorTypeReader();

        try (InputStream inputStream = getClass().getResourceAsStream("/ReactorType.json")) {
            if (inputStream == null) {
                System.out.println("Файл ReactorType.json не найден внутри JAR");
            } else {
                reactorImporter.importReactorsFromStream(inputStream, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addReactor(String key, ReactorType reactor) {
        reactorsMap.put(key, reactor);
    }
    public Map<String, ReactorType> getReactorMap() {
        return reactorsMap;
    }
}