package project;

import org.json.JSONArray;
import org.json.JSONObject;
import project.exceptions.DataException;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

public class TestProject {

    private final String BIN_PATH = "src/main/java/project/binFiles/";
    private final String JSON_PATH = "src/main/java/project/jsonFiles/";
    File jsonFile = null;

    public void createJsonFile(String binFileName, String jsonFileName) throws IOException {
        InputStream inputStream = null;
        jsonFile = new File(JSON_PATH + jsonFileName + (jsonFileName.contains(".json") ? "" : ".json"));
        FileWriter writer = new FileWriter(jsonFile);
        try {
            inputStream = new FileInputStream(BIN_PATH + binFileName + (binFileName.contains(".bin") ? "" : ".bin"));
            byte[] file = inputStream.readAllBytes();
            JSONObject root = new JSONObject();
            split(file, root);
            writer.write(root.toString());
        } catch (IOException | DataException e) {
            writer.close();
            deleteFile();
        } finally {
            if (inputStream != null)
                inputStream.close();
            writer.close();
        }
    }

    private void split(byte[] file, JSONObject jsonObject) throws DataException {
        int step = -1;
        while (true) {
            try {
                int type = file[step + 1];
                byte[] value = Arrays.copyOfRange(file, step + 5, step + file[step + 3] + 5);
                step += file[step + 3] + 4;
                switch (type) {
                    case 1: {
                        if (value.length <= 4) {
                            jsonObject.put("dateTime", formatDate(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    }
                    case 2:
                        if (value.length <= 8) {
                            jsonObject.put("orderNumber", decodeNumber(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    case 3:
                        if (value.length <= 1000) {
                            jsonObject.put("customerNumber", decodeString(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    case 4:
                        JSONObject itemElement = new JSONObject();
                        JSONArray array = jsonObject.optJSONArray("items");
                        if (array == null) {
                            array = new JSONArray();
                            jsonObject.put("items", array);
                        }
                        array.put(itemElement);
                        split(value, itemElement);
                        break;
                    case 11:
                        if (value.length <= 200) {
                            jsonObject.put("name", decodeString(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    case 12:
                        if (value.length <= 6) {
                            jsonObject.put("price", decodeNumber(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    case 13:
                        if (value.length <= 8) {
                            jsonObject.put("quantity", decodeFloatingPointerNumber(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    case 14:
                        if (value.length <= 6) {
                            jsonObject.put("sum", decodeNumber(value));
                        } else {
                            throw new DataException("incorrect data file");
                        }
                        break;
                    default:
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
    }

    private long decodeNumber(byte[] value) {
        long result = 0;
        for (int i = 0; i < value.length; i++) {
            result += ((long) value[i] & 0xFF) << (8 * i);
        }
        return result;
    }

    private String formatDate(byte[] value) throws DataException {
        if (value.length <= 4) {
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date.setTimeZone(TimeZone.getTimeZone("UTC"));
            return date.format(decodeNumber(value) * 1000);
        } else {
            throw new DataException("incorrect data file");
        }
    }

    private String decodeString(byte[] value) {
        try {
            return new String(value, "Cp866");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private BigDecimal decodeFloatingPointerNumber(byte[] value) {
        float result = 0;
        for (int i = 1; i < value.length; i++) {
            result += (value[i] & 0xff) << (8 * (i - 1));
        }
        if (value.length >= 5) {
            byte[] valueRange = Arrays.copyOfRange(value, 1, value.length);
            result = ByteBuffer.wrap(valueRange).getFloat();
        }
        return BigDecimal.valueOf(result).setScale(value[0], RoundingMode.HALF_EVEN);
    }

    private void deleteFile() {
        jsonFile.delete();
    }
}
