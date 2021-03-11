package com.claritybot.utility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author Manoj Hans
 **/
public class ReadJsonData {

    private final static Logger logger = LogManager.getLogger(ReadJsonData.class);
    private final Gson gson = new Gson();

    public Object[][] getJsonValues(String filePath, String keyValue) throws IOException {
        List<Map<String, Object>[]> mapData;
        var DATASET_TYPE = new TypeToken<List<Map<String, Object>>>() {}.getType();
        logger.debug(format("Test data is loaded from file %s and the key value is %s", filePath, keyValue));
        if (filePath.startsWith("[")) {
            mapData = gson.fromJson(filePath, DATASET_TYPE);
        } else {
            var jsonStr = FileUtils.readFileToString(new File(filePath), "UTF-8");
            var jsObj = gson.fromJson(jsonStr, JsonObject.class);
            var jsArr = jsObj.get(keyValue).getAsJsonArray();
            mapData = gson.fromJson(jsArr, DATASET_TYPE);
        }
        var jsonValue = new Object[mapData.size()][1];
        for (var i = 0; i < mapData.size(); i++) {
            jsonValue[i][0] = mapData.get(i);
        }
        return jsonValue;
    }
}
