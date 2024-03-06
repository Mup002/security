package tmdtdemo.tmdt.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
public interface BaseRedisService {
    // lưu cặp key value vào redis
    void set(String key, String value);
    // set thời gian dữ liệu tồn tại trên bộ nhớ cache của redis
    void setTimeToLive(String key, long timeout);
    // lưu
    void hashSet(String key, String field, Object value);
    // kiểm tra tồn tại
    boolean hashExists(String key, String field);
    // lấy ra
    Object get(String key);
    public Map<String , Object> getField(String key);
    Object hashGet(String key, String field);
    List<Object> hashGetByFilePrefix(String key, String filePrefix);
    Set<String> getFieldPrefixes(String key);
    void delete(String key);
    void delete(String key, String field);
    void delete(String key, List<String> fields);

}
