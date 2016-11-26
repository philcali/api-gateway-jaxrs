package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static Optional<Object> findContextType(Map<String, Object> context, Parameter param) {
        return findContextType(context, param.getType());
    }

    public static Optional<Object> findContextType(Map<String, Object> context, Class<?> paramType) {
        return context.values().stream().filter(value -> isAssignable(paramType, value.getClass())).findFirst();
    }

    private static boolean isAssignable(Class<?> paramC, Class<?> valueC) {
        if (paramC.isPrimitive() && valueC.isPrimitive()) {
            return paramC.equals(valueC);
        } else if (paramC.isPrimitive() && !valueC.isPrimitive()) {
            switch (valueC.getName()) {
            case "java.lang.Integer":
                return paramC.equals(Integer.TYPE);
            case "java.lang.Double":
                return paramC.equals(Double.TYPE);
            case "java.lang.Long":
                return paramC.equals(Long.TYPE);
            case "java.lang.Boolean":
                return paramC.equals(Boolean.TYPE);
            case "java.lang.Short":
                return paramC.equals(Short.TYPE);
            case "java.lang.Float":
                return paramC.equals(Float.TYPE);
            case "java.lang.Byte":
                return paramC.equals(Byte.TYPE);
            default:
                return false;
            }
        } else {
            return paramC.isAssignableFrom(valueC);
        }
    }
}
