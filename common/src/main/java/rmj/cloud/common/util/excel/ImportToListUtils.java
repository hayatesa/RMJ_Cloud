package rmj.cloud.common.util.excel;

import rmj.cloud.common.util.ReflectUtils;
import rmj.cloud.common.util.excel.annotation.Excel;
import rmj.cloud.common.util.excel.annotation.ExcelWorkbook;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel导入工具类
 */
public class ImportToListUtils<E> {
    private E e;
    private static Logger LOG = LoggerFactory.getLogger(ImportToListUtils.class);

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String TITLELEVELTWO = "two";

    public ImportToListUtils(E e) {
        this.e = e;
    }

    @SuppressWarnings("unchecked")
    public E get() throws InstantiationException, IllegalAccessException {
        return (E) this.e.getClass().newInstance();
    }

    /**
     * @param edf           数据格式化
     * @param ，支持xls、xlsx后缀
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readFromFile(ExcelDataFormatter edf, String fileName, InputStream is,
            String... titleLevel) {
        Field[] fields;
        Map<String, Object> map = null;
        List<ExcelTemplate> list = Lists.newArrayList();
        Set<ExcelTemplate> errorlist = Sets.newHashSet();
        E e = null;
        ExcelTemplate excelTemplate = null;
        try {

            fields = ReflectUtils.getClassFieldsAndSuperClassFields(this.e.getClass());
            map = new HashMap<>();
            Map<String, String> textToKey = new HashMap<String, String>();

            Excel _excel = null;
            for (Field field : fields) {
                _excel = field.getAnnotation(Excel.class);
                if (_excel == null || _excel.skip()) {
                    continue;
                }
                textToKey.put(_excel.name(), field.getName());
            }
            Workbook wb;
            Sheet sheet;
            if (StringUtils.isNotBlank(fileName) && fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook(is);
                sheet = wb.getSheetAt(0);
            } else {
                wb = new XSSFWorkbook(is);
                sheet = wb.getSheetAt(0);
            }

            Row title = sheet.getRow(0);
            // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
            String[] titlesLevelOne = new String[title.getPhysicalNumberOfCells()];

            for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
                titlesLevelOne[i] = title.getCell(i).getStringCellValue();
                // titlesLevelTwo[i] = titleTwo.getCell(i).getStringCellValue();
            }
            String[] titles = titlesLevelOne; // 一级标题
            int rowBegin = 1;// 设置开始行
            // 存在二级标题
            if (null != titleLevel && titleLevel.length > 0) {
                String level = titleLevel[0];
                if (level.equals(TITLELEVELTWO)) {
                    Row titleTwo = sheet.getRow(1);
                    String[] titlesLevelTwo = new String[titleTwo.getPhysicalNumberOfCells()];
                    for (int i = 0; i < titleTwo.getPhysicalNumberOfCells(); i++) {
                        titlesLevelTwo[i] = titleTwo.getCell(i).getStringCellValue();
                    }
                    titles = minxin(titlesLevelOne, titlesLevelTwo);
                    rowBegin = 2;
                }
            }

            int columnCount = Arrays.stream(titles).filter(StringUtils::isNotEmpty).collect(Collectors.toList()).size();
            Cell cell = null;
            Row row = null;
            for (int rowNum = rowBegin; rowNum <= sheet.getLastRowNum(); rowNum++) {
                LOG.debug("整条数据有效数据校验");
                // 如果数据全部为空，则直接跳过该条数据
                if (!checkDataIsAccess(sheet.getRow(rowNum), columnCount)) {
                    continue;
                }
                // 如果是备注行，结束读取数据
                if (checkDataIsRemark(sheet.getRow(rowNum)))
                    break;
                LOG.debug("共有数据：", sheet.getLastRowNum());
                row = sheet.getRow(rowNum);
                if (row == null) {
                    break;
                }
                e = get();
                excelTemplate = (ExcelTemplate) e;
                excelTemplate = checkExcelTemplate4NewOrUpdate(sheet, excelTemplate);

                list.add(excelTemplate);
                for (int i = 0; i < columnCount; i++) {
                    cell = row.getCell(i);
                    if (cell == null) {
                        continue;
                    }
                    readCellContent(textToKey.get(titles[i]), fields, cell, excelTemplate, edf, list, errorlist);
                }
                try {
                    // 强制转回子类进行判断
                    validate((E) excelTemplate);
                    if (ExcelTemplate.ExcelTemplateAction.IMPORT_FOR_NEW.equals(excelTemplate.getAction())) {
                        excelTemplate.check4New();
                    } else if (ExcelTemplate.ExcelTemplateAction.IMPORT_FOR_UPDATE.equals(excelTemplate.getAction())) {
                        excelTemplate.check4Update();
                    }

                    // 模板数据是否相同
                    excelTemplateEqualFilter(excelTemplate, list);
                } catch (Exception e2) {
                    excelTemplate.addErrorMessages(e2.getMessage());
                    errorlist.add(excelTemplate);
                    list.remove(excelTemplate);
                }
            }
        } catch (Exception exception) {
            LOG.warn("读取Excel数据异常！", exception);
            excelTemplate.addErrorMessages("读取Excel数据异常！ " + exception.getMessage());
        }
        List<ExcelTemplate> rList = new ArrayList<>(errorlist);
        map.put(ERROR, rList);
        map.put(SUCCESS, list);
        return map;
    }

    private String[] minxin(String[] titlesLevelOne, String[] titlesLevelTwo) {
        int len = titlesLevelOne.length;
        String[] res = new String[len];
        String titleOne = "";
        for (int i = 0; i < len; i++) {
            if (StringUtils.isNotEmpty(titlesLevelOne[i]) && StringUtils.isEmpty(titlesLevelTwo[i])) {
                res[i] = titlesLevelOne[i];
            } else if (StringUtils.isNotEmpty(titlesLevelOne[i]) && StringUtils.isNotEmpty(titlesLevelTwo[i])) {
                titleOne = titlesLevelOne[i];
                res[i] = titlesLevelOne[i] + titlesLevelTwo[i];
            } else {
                res[i] = titleOne + titlesLevelTwo[i];
            }

        }
        return res;
    }

    /**
     * 对Excel 数据重复进行过滤
     *
     * @param excelTemplate
     * @param list
     * @throws Exception
     */
    private void excelTemplateEqualFilter(ExcelTemplate excelTemplate, List<ExcelTemplate> list) throws Exception {
        LOG.debug("excelTemplateEqualFilter action");
        if (CollectionUtils.isNotEmpty(list)) {
            list.remove(excelTemplate);
            Optional<ExcelTemplate> hadExcelTemplate = list.stream()
                    .filter(et -> excelTemplate.templateEqual(excelTemplate, et)).findAny();
            list.add(excelTemplate);
            if (hadExcelTemplate.isPresent()) {
                throw new Exception("该数据在模板中有重复");
            }

        }
    }

    /**
     * 数据有效性校验
     *
     * @param columnCount 列的总数
     * @param row         当前行
     * @return true：有效 false：无效
     */
    private boolean checkDataIsAccess(Row row, int columnCount) {
        LOG.debug("checkDataIsAccess action");
        if (row == null) {
            return false;
        }
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDataIsRemark(Row row) {
        LOG.debug("checkDataIsRemark action");
        Cell cell = row.getCell(0);
        return cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING
                && cell.getStringCellValue().contains("【备注】");
    }

    /**
     * 不为空进行通用判断
     *
     * @param e2
     * @throws Exception
     */
    private void validate(E e2) throws Exception {
        LOG.debug("notNullCheck action");
        Field[] fields = null;
        try {
            fields = ReflectUtils.getClassFieldsAndSuperClassFields(e2.getClass());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.debug(e.getMessage());
        }
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Excel excelAnnotation = field.getAnnotation(Excel.class);
                if (excelAnnotation != null) {
                    if (excelAnnotation.checkNull()) {
                        try {
                            field.setAccessible(true);
                            if (field.get(e2) == null) {
                                throw new Exception(
                                        excelAnnotation.name() + "为空或者无法找到该" + excelAnnotation.name() + "的值");
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                            LOG.debug(e.getMessage());
                        }
                    }

                    field.setAccessible(true);
                    if (!Objects.isNull(field.get(e2))) {
                        if (field.get(e2) instanceof String) {
                            int length = excelAnnotation.length();
                            if (length < field.get(e2).toString().length()) {
                                throw new Exception(excelAnnotation.name() + "的值超过长度");
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 判断模板为新增还是编辑操作
     *
     * @param sheet
     * @param excelTemplate
     * @return
     */
    private ExcelTemplate checkExcelTemplate4NewOrUpdate(@NotNull Sheet sheet, @NotNull ExcelTemplate excelTemplate) {
        ExcelWorkbook excelWorkbook = excelTemplate.getClass().getAnnotation(ExcelWorkbook.class);
        if (!Objects.isNull(excelWorkbook)) {
            if (excelWorkbook.protect()) {
                if (sheet instanceof HSSFSheet) {
                    HSSFSheet hssfSheet = (HSSFSheet) sheet;
                    if (hssfSheet.getPassword() == (short) CryptoFunctions
                            .createXorVerifier1(excelWorkbook.protectPassword())) {
                        excelTemplate.setAction(ExcelTemplate.ExcelTemplateAction.IMPORT_FOR_UPDATE);
                        return excelTemplate;
                    }
                } else if (sheet instanceof XSSFSheet) {
                    XSSFSheet xssfSheet = (XSSFSheet) sheet;
                    if (xssfSheet.validateSheetPassword(excelWorkbook.protectPassword())) {
                        excelTemplate.setAction(ExcelTemplate.ExcelTemplateAction.IMPORT_FOR_UPDATE);
                        return excelTemplate;
                    }
                }
            }
        }
        excelTemplate.setAction(ExcelTemplate.ExcelTemplateAction.IMPORT_FOR_NEW);
        return excelTemplate;
    }

    /**
     * @param key           当前单元格对应的Bean字段
     * @param fields        Bean所有的字段数组
     * @param cell          单元格对象
     * @param excelTemplate
     */
    @SuppressWarnings("deprecation")
    private void readCellContent(String key, Field[] fields, Cell cell, ExcelTemplate excelTemplate,
            ExcelDataFormatter edf, List<ExcelTemplate> list, Set<ExcelTemplate> errorlist) {

        Object o = null;

        switch (cell.getCellType()) {
        case XSSFCell.CELL_TYPE_BOOLEAN:
            o = cell.getBooleanCellValue();
            break;
        case XSSFCell.CELL_TYPE_NUMERIC:
            o = cell.getNumericCellValue();
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                o = DateUtil.getJavaDate(cell.getNumericCellValue());
            }
            break;
        case XSSFCell.CELL_TYPE_STRING:
            o = cell.getStringCellValue();
            if (StringUtils.isBlank(o.toString())) {
                o = null;
            }
            break;
        case XSSFCell.CELL_TYPE_ERROR:
            o = cell.getErrorCellValue();
            break;
        case XSSFCell.CELL_TYPE_BLANK:
            break;
        case XSSFCell.CELL_TYPE_FORMULA:
            o = cell.getCellFormula();
            break;
        default:
            break;
        }

        if (o == null)
            return;

        // 类型转换
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getName().equals(key)) {
                    Boolean bool = true;
                    Map<String, String> map = null;
                    if (edf == null) {
                        bool = false;
                    } else {
                        map = edf.get(field.getName());
                        if (map == null) {
                            bool = false;
                        }
                    }
                    // 日期
                    if (field.getType().equals(Date.class)) {
                        if (o.getClass().equals(Date.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, simpleDateFormat.parse(o.toString()));
                        }
                    }
                    // string
                    else if (field.getType().equals(String.class)) {
                        if (o.getClass().equals(String.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, o.toString());
                        }
                    }
                    // long
                    else if (field.getType().equals(Long.class)) {
                        if (o.getClass().equals(Long.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, Long.parseLong(o.toString()));
                        }
                    }
                    // int
                    else if (field.getType().equals(Integer.class)) {
                        if (o.getClass().equals(Integer.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            // 检查是否需要转换
                            if (bool) {
                                field.set(excelTemplate, map.get(o.toString()) != null
                                        ? Integer.parseInt(map.get(o.toString())) : Integer.parseInt(o.toString()));
                            } else {
                                BigDecimal formateBigDecimal = new BigDecimal(o.toString());
                                field.set(excelTemplate, formateBigDecimal.intValue());
                            }

                        }
                    }
                    // BigDecimal
                    else if (field.getType().equals(BigDecimal.class)) {
                        if (o.getClass().equals(BigDecimal.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, BigDecimal.valueOf(Double.parseDouble(o.toString())));
                        }
                    }
                    // Boolean
                    else if (field.getType().equals(Boolean.class)) {
                        if (o.getClass().equals(Boolean.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            // 检查是否需要转换
                            if (bool) {
                                field.set(excelTemplate,
                                        map.get(o.toString()) != null ? Boolean.parseBoolean(map.get(o.toString()))
                                                : Boolean.parseBoolean(o.toString()));
                            } else {
                                field.set(excelTemplate, Boolean.parseBoolean(o.toString()));
                            }
                        }
                    }
                    // Float
                    else if (field.getType().equals(Float.class)) {
                        if (o.getClass().equals(Float.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, Float.parseFloat(o.toString()));
                        }
                    }
                    // Double
                    else if (field.getType().equals(Double.class)) {
                        if (o.getClass().equals(Double.class)) {
                            field.set(excelTemplate, o);
                        } else {
                            field.set(excelTemplate, Double.parseDouble(o.toString()));
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.warn(ex.toString());
                excelTemplate.addErrorMessages("error input");
                errorlist.add(excelTemplate);
                list.remove(excelTemplate);
            }
        }

    }

}
