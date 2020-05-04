package com.orange.admin.common.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 聚合计算的常量类型对象。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public final class AggregationType {

    /**
     * sum 计数
     */
    public final static int SUM = 0;
    /**
     * count 汇总
     */
    public final static int COUNT = 1;
    /**
     * average 平均值
     */
    public final static int AVG = 2;
    /**
     * min 最小值
     */
    public final static int MIN = 3;
    /**
     * max 最大值
     */
    public final static int MAX = 4;

    public static final Map<Object, String> DICT_MAP = new HashMap<>(5);
    static {
        DICT_MAP.put(0, "累计总和");
        DICT_MAP.put(1, "数量总和");
        DICT_MAP.put(2, "平均值");
        DICT_MAP.put(3, "最小值");
        DICT_MAP.put(4, "最大值");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(Integer value) {
        return DICT_MAP.containsKey(value);
    }

    /**
     * 获取与SQL对应的聚合函数字符串名称。
     *
     * @return 聚合函数名称。
     */
    public static String getAggregationFunction(Integer aggregationType) {
        switch (aggregationType) {
            case COUNT:
                return "COUNT";
            case AVG:
                return "AVG";
            case SUM:
                return "SUM";
            case MAX:
                return "MAX";
            case MIN:
                return "MIN";
            default:
                throw new IllegalArgumentException("无效的聚合类型！");
        }
    }
}