/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelper {

    public final static int INVALID_VALUE = -1;

    public static Object getObjectByConstructor(String className,
                                                Class[] intArgsClass, Object[] intArgs) {

        Object returnObj = null;
        try {
            Class classType = Class.forName(className);
            Constructor constructor = classType
                    .getDeclaredConstructor(intArgsClass);
            constructor.setAccessible(true);
            returnObj = constructor.newInstance(intArgs);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnObj;
    }

    public static void modifyFileValue(Object object, String filedName,
                                       String filedValue) {
        Class classType = object.getClass();
        Field fild = null;
        try {
            fild = classType.getDeclaredField(filedName);
            fild.setAccessible(true);
            fild.set(object, filedValue);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static Object getFieldValue(Object object, String fieldName) {
        Class classType = object.getClass();
        Field fild = null;
        Object fildValue = null;
        try {
            fild = classType.getDeclaredField(fieldName);
            fild.setAccessible(true);
            fildValue = fild.get(object);

        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fildValue;
    }

    public static Object getStaticFieldValue(Class cls, String fieldName) {
        Field fild = null;
        Object fildValue = null;
        try {
            fild = cls.getDeclaredField(fieldName);
            fild.setAccessible(true);
            fildValue = fild.get(null);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fildValue;
    }

    public static int getIntFileValue(Object object, String filedName) {
        Class classType = object.getClass();
        Field fild = null;
        int fildValue = INVALID_VALUE;
        try {
            fild = classType.getDeclaredField(filedName);
            fild.setAccessible(true);
            fildValue = fild.getInt(object);

        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fildValue;
    }


    public static int getIntFileValueFromClass(Class c, String filedName) {
        Field fild = null;
        int fildValue = 0;
        try {
            fild = c.getDeclaredField(filedName);
            fild.setAccessible(true);
            fildValue = fild.getInt(c);

        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fildValue;
    }

    public final static int SM_THEAD_POLICY = 0;
    public final static int SM_VM_POLICY = 1;
    public final static String POLICY_NAMES[] = {
            "android.os.StrictMode$ThreadPolicy",
            "android.os.StrictMode$VmPolicy"};
    public final static String SET_POLICY_NAMES[] = {"setThreadPolicy",
            "setVmPolicy"};

    public static void setStrictModePolicy(int type) {
        try {
            Class<?> clsStrictMode = Class.forName("android.os.StrictMode");
            Class<?> clsPolicy = Class.forName(POLICY_NAMES[type]);
            Class<?> clsBuilder = Class
                    .forName(POLICY_NAMES[type] + "$Builder");

            Method mtdSetPolicy = clsStrictMode.getDeclaredMethod(
                    SET_POLICY_NAMES[type], new Class[]{clsPolicy});
            Method mtdDetectAll = clsBuilder.getDeclaredMethod("detectAll");
            Method mtdPenaltyLog = clsBuilder.getDeclaredMethod("penaltyLog");
            Method mtdBuild = clsBuilder.getDeclaredMethod("build");

            Object objBuilder = mtdDetectAll.invoke(clsBuilder.newInstance());
            objBuilder = mtdPenaltyLog.invoke(objBuilder);
            Object objThreadPolicy = mtdBuild.invoke(objBuilder);

            mtdSetPolicy.invoke(null, objThreadPolicy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void setReflectField(Object obj, String field, Object value) {
        try {
            Field f = null;
            try {
                f = obj.getClass().getDeclaredField(field);
            } catch (Exception e) {
                f = obj.getClass().getField(field);
            }

            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSuperClassReflectFieldValue(Object obj, String field,
                                                      Object value) {
        try {
            Field f = null;
            Class<?> curClass = obj.getClass().getSuperclass();
            for (; curClass != null; ) {
                try {
                    f = curClass.getDeclaredField(field);
                    if (f != null)
                        break;
                } catch (Exception e) {
                    curClass = curClass.getSuperclass();
                }
            }

            if (f != null) {
                f.setAccessible(true);
                f.set(obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getSuperClassReflectFieldValue(Object obj, String field) {
        try {
            Field f = null;
            Class<?> curClass = obj.getClass().getSuperclass();
            for (; curClass != null; ) {
                try {
                    f = curClass.getDeclaredField(field);
                    if (f != null)
                        break;
                } catch (Exception e) {
                    curClass = curClass.getSuperclass();
                }
            }
            if (f != null) {
                f.setAccessible(true);
                return f.get(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getReflectFieldValue(Object obj, String field) {
        try {
            Field f = null;

            try {
                f = obj.getClass().getDeclaredField(field);
            } catch (Exception e) {
                f = obj.getClass().getField(field);
            }

            f.setAccessible(true);
            return f.get(obj);
        } catch (Throwable e) {
        }

        return null;
    }

    public static Object invokeReflectFunction(Object obj, String method) {
        try {
            Method m = null;
            try {
                m = obj.getClass().getDeclaredMethod(method);
            } catch (Exception e) {
                m = obj.getClass().getMethod(method);
            }

            m.setAccessible(true);
            return m.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param o
     * @param methodName
     * @param argsClass
     * @param args
     * @return
     */
    public static Object invokeObjectMethod(Object o, String methodName,
                                            Class[] argsClass, Object[] args) {
        Object returnValue = null;
        try {
            Class<?> c = o.getClass();
            Method method;
            method = c.getMethod(methodName, argsClass);
            returnValue = method.invoke(o, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

}
