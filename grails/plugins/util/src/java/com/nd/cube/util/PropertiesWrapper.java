package com.nd.cube.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by wuzj on 2015/1/30.
 */
public class PropertiesWrapper {
    Properties properties = null;

    public PropertiesWrapper(Properties properties) {
        this.properties = properties;
    }

    public Integer getIntProperty(String key) {
        Object value = properties.get(key);
        Integer intValue = 0;
        
        if (value instanceof String) {
            String v = (String)value ;
            try {
                intValue = Integer.valueOf(v);
            } catch (NumberFormatException e) {
                throw e;
            }
        }else if(value instanceof Number) {
            intValue = (Integer)value;
        }else {
            throw new NumberFormatException();
        }
            
        return intValue;
        
    }

    public Integer getIntProperty(String key, Integer defaultValue) {
        Integer intValue = 0;
        try {
            intValue = getIntProperty(key);
        } catch (NumberFormatException e) {
            intValue = defaultValue;
        }
        return intValue;
    }

    public Long getLongProperty(String key) {
        Object value = properties.get(key);
        Long longValue = 0L;

        if (value instanceof String) {
            String v = (String)value ;
            try {
                longValue = Long.valueOf(v);
            } catch (NumberFormatException e) {
                throw e;
            }
        }else if(value instanceof Number) {
            longValue = (Long)value;
        }else {
            throw new NumberFormatException();
        }

        return longValue;
    }

    public Long getLongProperty(String key, Long defaultValue) {
        Long longValue = 0L;
        try {
            longValue = getLongProperty(key);
        } catch (NumberFormatException e) {
            longValue = defaultValue;
        }
        return longValue;
    }

    public Float getFloatProperty(String key) {
        Object value = properties.get(key);
        Float floatValue = 0.0f;

        if (value instanceof String) {
            String v = (String)value ;
            try {
                floatValue = Float.valueOf(v);
            } catch (NumberFormatException e) {
                throw e;
            }
        }else if(value instanceof Number) {
            floatValue = (Float)value;
        }else {
            throw new NumberFormatException();
        }

        return floatValue;
    }

    public Float getFloatProperty(String key, Float defaultValue) {
        Float floatValue = 0.0f;
        try {
            floatValue = getFloatProperty(key);
        } catch (NumberFormatException e) {
            floatValue = defaultValue;
        }
        return floatValue;
    }

    public Double getDoubleProperty(String key) {
        Object value = properties.get(key);
        Double doubleValue = 0.0;

        if (value instanceof String) {
            String v = (String)value ;
            try {
                doubleValue = Double.valueOf(v);
            } catch (NumberFormatException e) {
                throw e;
            }
        }else if(value instanceof Number) {
            doubleValue = (Double)value;
        }else {
            throw new NumberFormatException();
        }

        return doubleValue;
    }

    public Double getDoubleProperty(String key, Double defaultValue) {
        Double doubleValue = 0.0;
        try {
            doubleValue = getDoubleProperty(key);
        } catch (NumberFormatException e) {
            doubleValue = defaultValue;
        }
        return doubleValue;
    }

    public Date getDateTimeProperty(String key) {
        return getDateProperty(key, "yyyy-MM-dd HH:mm:ss");
    }

    public Date getDateProperty(String key) {
        return getDateProperty(key, "yyyy-MM-dd");
    }

    public Date getDateProperty(String key, String format) {
        Object value = properties.get(key);
        Date date = null;
        if (value instanceof Date){
            date = (Date)value;
        }else if(value instanceof String){
            try{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                date = simpleDateFormat.parse((String)value);
            }catch (Exception e){

            }
        }
        return date;
    }

    public Class getClassProperty(String key) {
        String value = properties.getProperty(key);
        Class clazz = null;
        try {
            clazz = Class.forName(value);
        } catch (ClassNotFoundException e) {

        }
        return clazz;
    }
}
