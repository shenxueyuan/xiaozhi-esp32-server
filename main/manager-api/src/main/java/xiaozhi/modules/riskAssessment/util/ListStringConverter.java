package xiaozhi.modules.riskAssessment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串与List<String>转换工具类
 * 用于处理数据库中String存储格式与DTO中List<String>格式的转换
 */
public class ListStringConverter {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SEPARATOR = "||";
    
    /**
     * 将List<String>转换为String存储格式
     * 优先使用JSON格式，如果失败则使用分隔符格式
     * 
     * @param list 要转换的List
     * @return 转换后的String
     */
    public static String listToString(List<String> list) {
        if (isEmpty(list)) {
            return null;
        }
        
        try {
            // 优先使用JSON格式
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            // JSON序列化失败，使用分隔符格式
            return String.join(SEPARATOR, list);
        }
    }
    
    /**
     * 将String转换为List<String>
     * 自动检测JSON格式或分隔符格式
     * 
     * @param str 要转换的String
     * @return 转换后的List
     */
    public static List<String> stringToList(String str) {
        if (isEmpty(str)) {
            return new ArrayList<>();
        }
        
        // 尝试JSON反序列化
        if (str.trim().startsWith("[") && str.trim().endsWith("]")) {
            try {
                return objectMapper.readValue(str, new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                // JSON反序列化失败，继续尝试分隔符格式
            }
        }
        
        // 使用分隔符格式
        if (str.contains(SEPARATOR)) {
            return Arrays.asList(str.split("\\Q" + SEPARATOR + "\\E"));
        }
        
        // 如果没有分隔符，将整个字符串作为单个元素
        List<String> result = new ArrayList<>();
        result.add(str);
        return result;
    }
    
    /**
     * 检查List是否为空
     */
    private static boolean isEmpty(List<String> list) {
        return list == null || list.isEmpty();
    }
    
    /**
     * 检查String是否为空
     */
    private static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
}