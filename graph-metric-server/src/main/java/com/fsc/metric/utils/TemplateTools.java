package com.fsc.metric.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateTools {


    private static final Pattern patternVariable = Pattern.compile("\\{\\{.*?\\}\\}");

    public static String replacePlaceholder(String template, Map<String, Object> params) {
        if (StringUtils.isEmpty(template)) {
            return "";
        }
        if (CollectionUtils.isEmpty(params)) {
            return template;
        }
        Matcher matcher = patternVariable.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group();
            Object res = params.get(variable.substring(2, variable.length() - 2));
            if (res != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(res)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
