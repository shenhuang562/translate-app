package com.example.trans;

import java.util.HashMap;
import java.util.Map;

public class language {

    // 创建一个映射表
    public static final Map<String, String> languageMap;

    // 初始化映射表
    static {
        languageMap = new HashMap<>();
        languageMap.put("自动检测", "auto");
        languageMap.put("中文", "zh");
        languageMap.put("英语", "en");
        languageMap.put("粤语", "yue");
        languageMap.put("文言文", "wyw");
        languageMap.put("日语", "jp");
        languageMap.put("韩语", "kor");
        languageMap.put("法语", "fra");
        languageMap.put("西班牙语", "spa");
        languageMap.put("泰语", "th");
        languageMap.put("阿拉伯语", "ara");
        languageMap.put("俄语", "ru");
        languageMap.put("葡萄牙语", "pt");
        languageMap.put("德语", "de");
        languageMap.put("意大利语", "it");
        languageMap.put("希腊语", "el");
        languageMap.put("荷兰语", "nl");
        languageMap.put("波兰语", "pl");
        languageMap.put("保加利亚语", "bul");
        languageMap.put("爱沙尼亚语", "est");
        languageMap.put("丹麦语", "dan");
        languageMap.put("芬兰语", "fin");
        languageMap.put("捷克语", "cs");
        languageMap.put("罗马尼亚语", "rom");
        languageMap.put("斯洛文尼亚语", "slo");
        languageMap.put("瑞典语", "swe");
        languageMap.put("匈牙利语", "hu");
        languageMap.put("繁体中文", "cht");
        languageMap.put("越南语", "vie");
    }

    // 根据语言名称获取语言代码
    public static String getLanguageCode(String languageName) {
        String languageCode = languageMap.get(languageName);
        return languageCode != null ? languageCode : "Unknown"; // 如果找不到匹配项，则返回 "Unknown"
    }

    public static Map<String, String> getLanguageMap() {
        return languageMap;
    }
}
