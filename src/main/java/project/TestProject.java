package project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

public class TestProject {

    private final String BIN_PATH = "src/main/java/project/binFiles/";
    private final String JSON_PATH = "src/main/java/project/jsonFiles/";

    public void createJsonFile(String binFile, String jsonFile) throws IOException {
        InputStream inputStream = new FileInputStream(BIN_PATH + binFile + (binFile.contains(".bin") ? "" : ".bin"));
        File test = new File(JSON_PATH + jsonFile + (jsonFile.contains(".json") ? "" : ".json"));
        FileWriter writer = new FileWriter(test);
        try {
            test.createNewFile();
            try {
                byte[] file = inputStream.readAllBytes();
                JSONObject root = new JSONObject();
                split(file, root);
                writer.write(root.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            writer.close();
        }
    }

    private void split(byte[] file, JSONObject jsonObject) throws UnsupportedEncodingException {
        int step = -1;
        while (true) {
            try {
                int type = file[step + 1];
                byte[] value = Arrays.copyOfRange(file, step + 5, step + file[step + 3] + 5);
                step += file[step + 3] + 4;
                switch (type) {
                    case 1: {
                        if (value.length <= 4) {
                            jsonObject.put("dateTime", dateFormatter(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    }
                    case 2:
                        if (value.length <= 8) {
                            jsonObject.put("orderNumber", numberDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    case 3:
                        if (value.length <= 1000) {
                            jsonObject.put("customerNumber", stringDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
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
                            jsonObject.put("name", stringDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    case 12:
                        if (value.length <= 6) {
                            jsonObject.put("price", numberDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    case 13:
                        if (value.length <= 8) {
                            jsonObject.put("quantity", floatingPointerNumberDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    case 14:
                        if (value.length <= 6) {
                            jsonObject.put("sum", numberDecoder(value));
                        } else throw new ArrayIndexOutOfBoundsException("incorrect data file");
                        break;
                    default:
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
    }

    private long numberDecoder(byte[] value) {
        if (value.length <= 6) {
            long result = 0;
            for (int i = 0; i < value.length; i++) {
                result += ((long) value[i] & 0xFF) << (8 * i);
            }
            return result;
        } else try {
            throw new Throwable("incorrect data file");
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String dateFormatter(byte[] value) {
        if (value.length <= 4) {
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date.setTimeZone(TimeZone.getTimeZone("UTC"));
            return date.format(numberDecoder(value) * 1000);
        } else try {
            throw new Throwable("incorrect data file");
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    private String stringDecoder(byte[] value) throws UnsupportedEncodingException {
        return new String(value, "Cp866");
    }

    private float floatingPointerNumberDecoder(byte[] value) {
        float result = 0;
        for (int i = 1; i < value.length; i++) {
            result += (value[i] & 0xff) << (8 * (i - 1));
        }
        return result;
    }
}
