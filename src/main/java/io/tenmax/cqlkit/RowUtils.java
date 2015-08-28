package io.tenmax.cqlkit;

import com.datastax.driver.core.*;
import com.google.gson.*;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class RowUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSSZ");
    public static String toString(
        DataType type,
        Object value)
    {
        if(type.asJavaClass() == String.class) {
            return (String) value;
        } else if(type.asJavaClass() == InetAddress.class) {
            return ((InetAddress)value).getHostAddress();
        } else if(type.getName() == DataType.Name.TIMESTAMP){
            return timestampToString(type, value);
        } else {
            return type.format(value);
        }
    }

    private static String timestampToString(DataType type, Object value) {
        String strValue = type.format(value);
        Long timestamp = Long.parseLong(strValue);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    public static JsonElement toJson(
            DataType type,
            Object value,
            boolean jsonColumn)
    {
        if(value == null) {
            return null;
        }

        switch(type.getName()) {
            case BLOB:
                return new JsonPrimitive(type.format(value));
            case BOOLEAN:
                return new JsonPrimitive((Boolean)value);
            case BIGINT:
            case COUNTER:
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
            case INT:
            case VARINT:
                return new JsonPrimitive((Number)value);
            case ASCII:
            case TEXT:
            case VARCHAR:
                if(jsonColumn) {
                    return new JsonParser().parse((String)value);
                } else {
                    return new JsonPrimitive((String) value);
                }
            case UUID:
            case INET:
            case TIMEUUID:
                return new JsonPrimitive(type.format(value));
            case TIMESTAMP:
                return new JsonPrimitive(timestampToString(type, value));
            case LIST:
            case SET:
                return collectionToJson(type, (Collection)value, jsonColumn);
            case MAP:
                return mapToJson(type, (Map)value, jsonColumn);
            case TUPLE:
            case UDT:
            case CUSTOM:
            default:
                throw new UnsupportedOperationException(
                        "The type is not supported now: " + type.getName());
        }
    }

    private static JsonElement mapToJson(
        DataType type,
        Map map,
        boolean jsonColumn)
    {
        DataType[] dataTypes = type.getTypeArguments().toArray(new DataType[]{});
        JsonObject root = new JsonObject();
        map.forEach((key, value) -> {
            if(value != null) {
                root.add(key.toString(), toJson(dataTypes[1], value, jsonColumn));
            }
        });
        return root;
    }

    private static JsonElement collectionToJson(
            DataType type,
            Collection collection,
            boolean jsonColumn)
    {
        DataType[] dataTypes = type.getTypeArguments().toArray(new DataType[]{});
        JsonArray array = new JsonArray();
        collection.forEach((value) -> {
            if(value != null) {
                array.add(toJson(dataTypes[0], value, jsonColumn));
            }
        });
        return array;
    }
}
