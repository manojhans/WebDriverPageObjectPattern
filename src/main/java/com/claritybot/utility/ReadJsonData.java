package com.claritybot.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Manoj Hans
 **/
public class ReadJsonData {

    private final static Logger LOGGER = LogManager.getLogger(ReadJsonData.class);
    private Gson gson = new Gson();
    private List<Map<String, Object>[]> mapData;

    public Object[][] getJsonValues(String filePath, String keyValue) throws IOException {
        Type DATASET_TYPE = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        JsonArray jsArr = new JsonArray();
        LOGGER.debug("Test data is loaded from file " + filePath
            + " and the key value is " + keyValue);
        if (filePath.startsWith("[")) {
            mapData = gson.fromJson(filePath, DATASET_TYPE);
        } else {
            var jsonStr = FileUtils.readFileToString(new File(filePath), "UTF-8");
            var jsObj = gson.fromJson(jsonStr, JsonObject.class);
            jsArr = jsObj.get(keyValue).getAsJsonArray();
            mapData = gson.fromJson(jsArr, DATASET_TYPE);
        }
        Object[][] jsonValue = new Object[mapData.size()][1];
        for (int i = 0; i < mapData.size(); i++) {
            jsonValue[i][0] = mapData.get(i);
        }
        return jsonValue;
    }
}
