package edu.eci.MicroSpringBoot;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> values;

    public HttpRequest(String queryString) {
        values = new HashMap<>();
        
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    values.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public String getValues(String varName) {
        return values.getOrDefault(varName, "");
    }

    public String getValue(String key) {
        return getValues(key);
    }
}