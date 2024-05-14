package com.example.template.util;

import com.example.template.setting.exception.BusinessException;
import com.example.template.setting.exception.CodeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilJson {
//    public static String convertObjectToJsonString(Object object) {
//        return ToStringBuilder.reflectionToString(object, ToStringStyle.JSON_STYLE);
//    }

    public static String convertObjectToJsonString(Object object){
        String cvtJson = null;
        if( object == null ) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            // Convert object to JSON string
            cvtJson = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new BusinessException("Fail to convert object to json string", CodeException.INTERNAL_SERVER_ERROR);
        }

        return cvtJson;
    }

    public static String convertObjectToJsonString(Object object, JsonInclude.Include include) {
        String cvtJson = null;

        if (object == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(include);
        try {
            cvtJson = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new BusinessException("Fail to convert object to json string", CodeException.INTERNAL_SERVER_ERROR);
        }
        return cvtJson;
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertJsonStringToObject(Class<T> boundClass, String jsonString, boolean ignoreUnknownProperties) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Object bean = null;
        try {
            if (ignoreUnknownProperties) {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
            bean = mapper.readValue(jsonString, boundClass);

        } catch (Exception e) {
            throw new BusinessException("Fail to convert json string to object", CodeException.INTERNAL_SERVER_ERROR);
        }

        return (T) bean;
    }

    public static <T> T convertJsonStringToObject(Class<T> boundClass, String jsonString) throws Exception {
        return convertJsonStringToObject(boundClass, jsonString, false);
    }

    public static <T> T jsonToObject(Class<T> boundClass, String jsonString, boolean failOnUnknownProperties, boolean failOnUnresolvedObjectIds) throws Exception {
        return convertJsonStringToObject(boundClass, jsonString, failOnUnknownProperties, failOnUnresolvedObjectIds, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertJsonStringToObject(Class<T> boundClass, String jsonString, boolean failOnUnknownProperties, boolean failOnUnresolvedObjectIds, boolean acceptSingleValueAsArray) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Object bean = null;
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
            mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, failOnUnresolvedObjectIds);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, acceptSingleValueAsArray);
            bean = mapper.readValue(jsonString, boundClass);
        } catch (Exception e) {
            throw new BusinessException("Fail to convert json string to object", CodeException.INTERNAL_SERVER_ERROR);
        }
        return (T) bean;
    }
}
