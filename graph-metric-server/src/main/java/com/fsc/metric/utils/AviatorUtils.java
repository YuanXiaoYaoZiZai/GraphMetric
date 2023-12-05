package com.fsc.metric.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AviatorUtils {

    private static Logger logger = LoggerFactory.getLogger(AviatorUtils.class);

    public static boolean tryMatchExpression(String expression, Map<String, Object> props, Map<String, Object> fields) {
        try {
            if (StringUtils.isEmpty(expression)) {
                return false;
            }

            Map<String, Object> params = new HashMap<>();
            if (!CollectionUtils.isEmpty(props)) {
                params.putAll(props);
            }
            if (!CollectionUtils.isEmpty(fields)) {
                params.putAll(fields);
            }

            //对结果进行匹配过滤。
            Expression ex = AviatorEvaluator.compile(expression, true);
            Object result = ex.execute(params);

            if (!(result instanceof Boolean)) {
                return false;
            }

            return (boolean) result;
        } catch (Exception e) {
            logger.error("[ExpressionMatch] Match expression error. Expression[{}],Props[{}],Fields[{}]",
                    expression, props, fields, e);
        }
        return false;
    }

    public static <T> T tryExecuteExpression(String expression, Map<String, Object> props) {
        try {
            if (StringUtils.isEmpty(expression)) {
                return null;
            }

            Map<String, Object> params = new HashMap<>();
            if (!CollectionUtils.isEmpty(props)) {
                params.putAll(props);
            }

            //对结果进行匹配过滤。
            Expression ex = AviatorEvaluator.compile(expression, true);
            Object result = ex.execute(params);

            if (result == null) {
                return null;
            }

            return (T) result;
        } catch (Exception e) {
            logger.error("[ExpressionMatch] Execute expression error. Expression[{}],Props[{}]", expression, props, e);
        }
        return null;
    }


}
