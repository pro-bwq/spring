package com.bwq.framework.core.base;


import cn.hutool.core.util.ObjectUtil;

import java.util.EnumSet;

/**
 * @author BWQ
 * @description: 枚举通用接口
 * @date 2024/2/22 08:40
 */
public interface IBaseEnum<T> {

    /**
     * 获取值
     *
     * @return
     */
    T getValue();

    /**
     * 获取标签
     *
     * @return 字符串
     */
    String getLabel();


    /**
     * 根据值获取枚举
     *
     * @param value
     * @param clazz
     * @param <E>   枚举
     * @return
     */
    static <E extends Enum<E> & IBaseEnum> E getEnumByValue(Object value, Class<E> clazz) {

        if (value == null) {
            return null;
        }

        // 获取类型下的所有枚举
        EnumSet<E> allEnums = EnumSet.allOf(clazz);
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getValue(), value))
                .findFirst()
                .orElse(null);
        return matchEnum;
    }

    /**
     * 根据文本标签获取值
     *
     * @param value
     * @param clazz
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IBaseEnum> String getLabelByValue(Integer value, Class<E> clazz) {
        if (value == null) {
            return null;
        }

        // 获取类型下的所有枚举
        EnumSet<E> allEnums = EnumSet.allOf(clazz);
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getValue(), value))
                .findFirst()
                .orElse(null);

        String label = null;
        if (matchEnum != null) {
            label = matchEnum.getLabel();
        }
        return label;
    }


    /**
     * 根据文本标签获取值
     *
     * @param label
     * @param clazz
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IBaseEnum> Object getValueByLabel(String label, Class<E> clazz) {
        if (label == null) {
            return null;
        }
        // 获取类型下的所有枚举
        EnumSet<E> allEnums = EnumSet.allOf(clazz);
        String finalLabel = label;
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getLabel(), finalLabel))
                .findFirst()
                .orElse(null);

        Object value = null;
        if (matchEnum != null) {
            value = matchEnum.getValue();
        }
        return value;
    }
}
