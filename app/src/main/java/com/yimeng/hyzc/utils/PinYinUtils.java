package com.yimeng.hyzc.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 */
public class PinYinUtils {
    public static String getPinYin(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = name.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        for (char ch : chars) {
            // 处理空格
            if (Character.isWhitespace(ch)) {
                continue;
            }
            //  ascII 表
            if (ch > 128 || ch < -127) {    // 中文
                try {
                    String[] strings = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (strings != null && strings.length>0) {
                        stringBuilder.append(strings[0]);
                    }else{
                        stringBuilder.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                    stringBuilder.append(ch);
                }
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }
}
