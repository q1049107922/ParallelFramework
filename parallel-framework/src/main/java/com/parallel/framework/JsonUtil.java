package com.parallel.framework;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    static {
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    public static Map<String, String> convertToMap(Object object) {
        Map<String, String> map = new HashMap<>();
        if (object == null) {
            return map;
        }
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // 私有属性必须设置访问权限
                Object resultValue = field.get(object);
                if (resultValue != null) {
                    map.put(field.getName(), resultValue.toString());
                }
            } catch (Exception e) {
                logger.error("convertToMap",e);
            }
        }
        return map;
    }

    public static String beanToJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("beanToJson",e);
            return "";
        }
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            logger.error(jsonStr,e);
            return null;
        }
    }

    public static <T> T jsonToBean(String jsonStr, TypeReference clazz) {
        try {
            return (T) mapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            logger.error(jsonStr,e);
            return null;
        }
    }

    public static <T> T deepCopy(Object source, Class<? extends T> clazz) {
        return jsonToBean(beanToJson(source), clazz);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T[] jsonToArray(String jsonString, Class<T> elementClass) throws IOException {
        if (StringUtils.isNotEmpty(jsonString))
            return (T[]) new Object[]{};
        return (T[]) mapper.readValue(jsonString, mapper.getTypeFactory().constructArrayType(elementClass));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> jsonToCollection(String jsonString, Class<? extends Collection> collectionClass, Class<T> elementClass) throws IOException {
        if (StringUtils.isNotEmpty(jsonString))
            return new ArrayList<>();
        return (Collection<T>) mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<? extends Map> mapClass, Class<K> keyClass, Class<V> valueClass) throws IOException {
        if (StringUtils.isNotEmpty(jsonString))
            return new HashMap<>();
        return (Map<K, V>) mapper.readValue(jsonString, mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
    }
    /**
     *  update exist object, when jsonString has partial fields, only update these fields
     */
    public static void update(String jsonString, Object object) throws IOException {
        mapper.readerForUpdating(object).readValue(jsonString);
    }

    public static JsonNode parseJsonNode(String json) {
        if (json == null || json.length() == 0) {
            return null;
        }
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            logger.error("parse json node error.", e);
            throw new RuntimeException(e);
        }
    }
}
