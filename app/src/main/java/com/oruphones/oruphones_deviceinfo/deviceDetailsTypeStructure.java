package com.oruphones.oruphones_deviceinfo;

import java.util.HashMap;
import java.util.Map;

public class deviceDetailsTypeStructure {
    private String category;
    private HashMap<String, String> details;

    public deviceDetailsTypeStructure() {
        details = new HashMap<>();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void addDetail(String detailName, String value) {
        details.put(detailName, value);
    }

    public String getDetailValue(String detailName) {
        return details.get(detailName);
    }



    public Map<String, String> getAllDetails() {
        return new HashMap<>(details);
    }

    public void removeDetail(String detailName) {
        details.remove(detailName);
    }
}
