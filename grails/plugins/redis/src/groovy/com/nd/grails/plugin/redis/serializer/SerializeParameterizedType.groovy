package com.nd.grails.plugin.redis.serializer

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 * @sina 2014/7/28.
 */
class SerializeParameterizedType implements ParameterizedType {

    private Type owerType

    private Type[] actualTypeArguments

    private Type rawType

    public SerializeParameterizedType(Type rawType,Type[] actualTypeArguments){
        this.rawType = rawType
        this.actualTypeArguments = actualTypeArguments
    }

    @Override
    Type[] getActualTypeArguments() {
        return new Type[0]
    }

    @Override
    Type getRawType() {
        return null
    }

    @Override
    Type getOwnerType() {
        return null
    }
}
