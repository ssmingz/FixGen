package utils;

import codegraph.CtVirtualElement;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtTypeImpl;
import spoon.support.reflect.declaration.CtTypeParameterImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtil {
    /**
     * Get all attributes of current class and its super classes, include public/private/protected
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    /**
     * Get all annotation values of the attributes set that have the target annotation
     */
    public static CtRole[] getAllCtRoles(Class<?> clazz) {
        Field[] fields = getAllFields(clazz);
        List<CtRole> roleList = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MetamodelPropertyField.class)) {
                MetamodelPropertyField roleAnnotation = field.getAnnotation(MetamodelPropertyField.class);
                if (roleAnnotation.role().length > 1)
                    System.out.println("[warn]There are more than one CtRole in this attribute:" + field);
                roleList.addAll(new ArrayList<>(Arrays.asList(roleAnnotation.role())));
            }
        }
        CtRole[] roles = new CtRole[roleList.size()];
        return roleList.toArray(roles);
    }

    public static Object createInstance(String className) {
        try {
            Class clazz = Class.forName(className);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object createInstance(Class clazz) throws InstantiationException {
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return obj;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
