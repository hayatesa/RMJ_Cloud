package rmj.cloud.common.util.excel;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel数据导入导出格式化
 * 举例:
 * 数据导出， {lock,{0:正常，1:锁定}}
 * 数据导入,{lock,{正常:0，锁定:1}}
 */
public class ExcelDataFormatter {

    private Map<String, Map<String, String>> formatter = new HashMap<String, Map<String, String>>();

    public void set(String key, Map<String, String> map) {
        formatter.put(key, map);
    }

    public Map<String, String> get(String key) {
        return formatter.get(key);
    }

}
