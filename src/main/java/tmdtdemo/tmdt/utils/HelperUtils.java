package tmdtdemo.tmdt.utils;
public class HelperUtils {
    public static String StringBuilderRedisKey(String str){
        StringBuilder builder = new StringBuilder();
        builder.append("accessKey_");
        builder.append(str);
        return builder.toString();
    }
}
