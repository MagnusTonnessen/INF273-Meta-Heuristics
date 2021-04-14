package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class JSONCreator {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String filePath;

    public JSONCreator(String filePath) {
        this.filePath = filePath;
    }

    public void save(Map<String, Map<String, Map<String, Object>>> map) throws Exception {
        mapper.writeValue(new File(filePath), map);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, Object>>> read() throws Exception {
        return mapper.readValue(new File(filePath), Map.class);
    }
}
