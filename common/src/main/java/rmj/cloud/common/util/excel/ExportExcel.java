package rmj.cloud.common.util.excel;

import rmj.cloud.common.util.excel.annotation.ExcelField;
import rmj.cloud.common.util.excel.annotation.ExcelWorkbook;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * ---导出Excel文件（导出“XLSX”格式，支持大数据量导出 @see org.apache.poi.ss.SpreadsheetVersion）
 * 导出Excel文件（导出“XLS”格式，03版本或以下Excel,做向下兼容）
 *
 * @author jaye
 * @version 2017-08-01
 */
public class ExportExcel {

    private static Logger log = LoggerFactory.getLogger(ExportExcel.class);

    /**
     * 工作薄对象
     */
    private HSSFWorkbook wb;
    /**
     * 工作表对象
     */
    private HSSFSheet sheet;

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     */
    private int rownum;

    /**
     * 注解列表（Object[]{ ExcelField, Field/Method }）
     */
    private List<Object[]> annotationList = Lists.newArrayList();

    /**
     * 校验器
     * key 为标题
     * value 校验器
     */
    private Map<String, String[]> headerExplicitListMap = Maps.newConcurrentMap();

    /**
     * 标题行数
     */
    private int headerRowNum = 0;

    /**
     * 标题
     */
    private List<String> headerList = Lists.newArrayList();
    /**
     * 影藏标题
     */
    private List<String> headerHiddenList = Lists.newArrayList();

    /**
     * 构造函数
     *
     * @param cls
     *            实体对象，通过annotation.ExportField获取标题
     */
    public ExportExcel(ExcelTemplate.ExcelTemplateAction action, Class<?> cls) {
        this(action, null, cls, 1);
    }

    /**
     * 构造函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param cls
     *            实体对象，通过annotation.ExportField获取标题
     */
    public ExportExcel(ExcelTemplate.ExcelTemplateAction action, String title, Class<?> cls) {
        this(action, title, cls, 1);
    }

    /**
     * 构造函数
     * @param action
     *            表格功能操作
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param cls
     *            实体对象，通过annotation.ExportField获取标题
     * @param type
     *            导出类型（1:导出数据；2：导出模板）
     * @param groups
     *            导入分组
     */
    public ExportExcel(ExcelTemplate.ExcelTemplateAction action, String title, Class<?> cls, int type, int... groups) {
        // Get annotation field
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type() == 0 || ef.type() == type)) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;
                    for (int g : groups) {
                        if (inGroup) {
                            break;
                        }
                        for (int efg : ef.groups()) {
                            if (g == efg) {
                                inGroup = true;
                                annotationList.add(new Object[] { ef, f });
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[] { ef, f });
                }
            }
        }
        //get father
        Field[] ffs = cls.getSuperclass().getDeclaredFields();
        for (Field f : ffs) {
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type() == 0 || ef.type() == type)) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;
                    for (int g : groups) {
                        if (inGroup) {
                            break;
                        }
                        for (int efg : ef.groups()) {
                            if (g == efg) {
                                inGroup = true;
                                annotationList.add(new Object[] { ef, f });
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[] { ef, f });
                }
            }
        }
        // Get annotation method
        Method[] ms = cls.getDeclaredMethods();
        for (Method m : ms) {
            ExcelField ef = m.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type() == 0 || ef.type() == type)) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;
                    for (int g : groups) {
                        if (inGroup) {
                            break;
                        }
                        for (int efg : ef.groups()) {
                            if (g == efg) {
                                inGroup = true;
                                annotationList.add(new Object[] { ef, m });
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[] { ef, m });
                }
            }
        }
        // Field sorting
        annotationList.sort(Comparator.comparingInt(o -> ((ExcelField) o[0]).sort()));
        // Initialize
        List<String> headerList = Lists.newArrayList();
        List<String> headerHiddenList = Lists.newArrayList();
        if (ExcelTemplate.ExcelTemplateAction.EXPORT_DATA_LIST.equals(action)) {
            for (Object[] os : annotationList) {
                String t = ((ExcelField) os[0]).title();
                // 如果是导出，则去掉注释
                if (type == 1) {
                    String[] ss = StringUtils.split(t, "**", 2);
                    if (ss.length == 2) {
                        t = ss[0];
                    }
                }
                if (!((ExcelField) os[0]).errorMessageField()) {
                    headerList.add(t);
                    //生成对应的数据校验
                    putExplicitList2Map(t, ((ExcelField) os[0]).explicitList());

                    if (((ExcelField) os[0]).hidden()) {
                        headerHiddenList.add(t);
                    }
                }

            }

            ExcelWorkbook excelWorkbook = cls.getAnnotation(ExcelWorkbook.class);
            if (excelWorkbook == null) {
                throw new NullPointerException("请在你的子类excelTemplate模板上加上ExcelWorkBook注解并且设置为保护状态");
            }
            boolean protect = excelWorkbook.protect();
            String protectPassword = excelWorkbook.protectPassword();
            initialize(title, "Export", headerList, headerHiddenList, protect, protectPassword);
        } else if (ExcelTemplate.ExcelTemplateAction.EXPORT_ERROR_LIST.equals(action)) {
            for (Object[] os : annotationList) {
                String t = ((ExcelField) os[0]).title();
                // 如果是导出，则去掉注释
                if (type == 1) {
                    String[] ss = StringUtils.split(t, "**", 2);
                    if (ss.length == 2) {
                        t = ss[0];
                    }
                }
                headerList.add(t);

                if (((ExcelField) os[0]).hidden()) {
                    headerHiddenList.add(t);
                }
            }
            initialize(title, "Export", headerList, headerHiddenList, false, StringUtils.EMPTY);
        }
    }

    private void putExplicitList2Map(String header, String[] excelField) {
        if (CollectionUtils.isNotEmpty(Arrays.asList(excelField))) {
            headerExplicitListMap.put(header, excelField);
        }

    }

    /**
     * 构造函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headers
     *            表头数组
     */
    public ExportExcel(String title, String[] headers) {
        initialize(title, "Export", Lists.newArrayList(headers));
    }

    /**
     * 构造函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerList
     *            表头列表
     */
    public ExportExcel(String title, List<String> headerList) {
        initialize(title, "Export", headerList);
    }

    public ExportExcel(String title, String sheetName, List<String> headerList) {
        initialize(title, sheetName, headerList);
    }

    /**
     * 初始化函数
     * @param sheetName
     *            工作表名称
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerList
     *            表头列表
     */
    public void initialize(String title, String sheetName, List<String> headerList) {
        initialize(title, sheetName, headerList, Lists.newArrayList(), false, StringUtils.EMPTY);
    }

    /**
     * 初始化函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerList
     *            表头列表
     */
    public void initialize(String title, String sheetName, List<String> headerList, List<String> headerHiddenList,
            boolean protect, String protectPassword) {
        this.headerList = headerList;
        this.headerHiddenList = headerHiddenList;
        if (this.wb == null) {
            this.wb = new HSSFWorkbook();
        }
        this.sheet = wb.createSheet(sheetName);
        if (protect) {
            sheet.protectSheet(protectPassword);
        }
        this.styles = createStyles(wb);
        // Create title
        if (StringUtils.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rownum++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            CellRangeAddress region = new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(),
                    titleRow.getRowNum(), headerList.size() - 1);
            if (region.getNumberOfCells() > 1)
                sheet.addMergedRegion(region);
        }
        // Create header
        if (headerList == null) {
            throw new RuntimeException("headerList not null!");
        }
        Row headerRow = sheet.createRow(rownum++);
        headerRowNum = headerRow.getRowNum();
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);

            cell.setCellStyle(styles.get("header"));

            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch()
                        .createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }

            //            sheet.trackAllColumnsForAutoSizing();
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }
        log.debug("Initialize success.");
    }

    /**
     * 创建表格样式
     *
     * @param wb
     *            工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
        style.setLocked(false);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setLocked(false);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        style.setLocked(false);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setLocked(true);
        styles.put("lock", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setLocked(false);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("yyyy-MM-dd"));
        styles.put("data-dateformate", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("lock"));
        style.setLocked(true);
        DataFormat lockFormat = wb.createDataFormat();
        style.setDataFormat(lockFormat.getFormat("yyyy-MM-dd"));
        styles.put("lock-dateformate", style);

        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheet.createRow(rownum++);
    }

    /**
     * 添加一个单元格
     *
     * @param row
     *            添加的行
     * @param column
     *            添加列号
     * @param val
     *            添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, Class.class, false);
    }

    /**
     * 添加一个单元格
     *
     * @param row
     *            添加的行
     * @param column
     *            添加列号
     * @param val
     *            添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, Class<?> fieldType, boolean lock) {
        Cell cell = row.createCell(column);
        cell.setCellType(CellType.STRING);
        CellStyle style = null;
        if (lock) {
            style = styles.get("lock");
            if (val instanceof Date) {
                style = styles.get("lock-dateformate");
            }
        } else {
            style = styles.get("data");
            if (val instanceof Date) {
                style = styles.get("data-dateformate");
            }
        }

        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((Integer.toString((Integer) val)));
            } else if (val instanceof Long) {
                cell.setCellValue(Long.toString((Long) val));
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue(Float.toString((Float) val));
            } else if (val instanceof Date) {

                cell.setCellValue((Date) val);
            } else if (val instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) val).toPlainString());
            } else {
                if (fieldType != Class.class) {
                    cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
                } else {
                    cell.setCellValue((String) Class
                            .forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                                    "fieldtype." + val.getClass().getSimpleName() + "Type"))
                            .getMethod("setValue", Object.class).invoke(null, val));
                }
            }
        } catch (Exception ex) {
            log.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            assert val != null;
            cell.setCellValue(val.toString());
        }
        cell.setCellStyle(style);
        return cell;
    }

    /**
     * 添加数据（通过annotation.ExportField添加数据）
     *
     * @return list 数据列表
     */
    public <E> ExportExcel setDataList(List<E> list) {

        //写数据
        for (E e : list) {
            int colunm = 0;
            Row row = addRow();

            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                ExcelField ef = (ExcelField) os[0];
                Object val = null;
                // Get entity value
                try {
                    if (StringUtils.isNotBlank(ef.value())) {
                        val = Reflections.invokeGetter(e, ef.value());
                    } else {
                        if (os[1] instanceof Field) {
                            val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
                        } else if (os[1] instanceof Method) {
                            val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[] {},
                                    new Object[] {});
                        }
                    }
                    // If is dict, get dict label
                    if (StringUtils.isNotBlank(ef.dictType())) {
                        val = "";
                    }
                } catch (Exception ex) {
                    // Failure to ignore
                    log.info(ex.toString());
                    val = "";
                }
                this.addCell(row, colunm++, val, ef.fieldType(), ef.lock());
                sb.append(val).append(", ");
            }

            log.debug("Write success: [" + row.getRowNum() + "] " + sb.toString());
        }

        //创建下拉选择框
        for (Map.Entry<String, String[]> entry : headerExplicitListMap.entrySet()) {
            if (null != entry.getValue()) {
                String headerName = filterInvailChar(entry.getKey());

                genearteOtherSheet(headerName, entry.getValue());
                CellRangeAddressList dataCellRangeAddress = new CellRangeAddressList(headerRowNum,
                        sheet.getLastRowNum() + 1, headerList.indexOf(entry.getKey()),
                        headerList.indexOf(entry.getKey()));
                DataValidationHelper dataValidationHelper = wb.getSheet(headerName).getDataValidationHelper();
                DataValidationConstraint formulaListConstraint = dataValidationHelper
                        .createFormulaListConstraint(headerName + "!$A$1:$A$" + entry.getValue().length);

                sheet.addValidationData(
                        sheet.getDataValidationHelper().createValidation(formulaListConstraint, dataCellRangeAddress));
            }
        }
        //隐藏column
        for (String headerHidden : headerHiddenList) {
            sheet.setColumnHidden(headerList.indexOf(headerHidden), true);
        }

        return this;
    }

    private void genearteOtherSheet(String key, String[] typeArrays) {
        Sheet sheet = wb.createSheet(key);
        // 循环往该sheet中设置添加下拉列表的值
        for (int i = 0; i < typeArrays.length; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(typeArrays[i]);
        }
    }

    private String filterInvailChar(String sheetName) {
        StringBuilder filterSheetName = new StringBuilder();
        for (int i = 0; i < sheetName.length(); i++) {
            char ch = sheetName.charAt(i);
            switch (ch) {
            case '/':
            case '\\':
            case '?':
            case '*':
            case ']':
            case '[':
            case ':':
                break;
            default:
                // all other chars OK
                filterSheetName.append(ch);
            }
        }
        return filterSheetName.toString();
    }

    /**
     * 输出数据流
     *
     * @param os
     *            输出数据流
     */
    public ExportExcel write(OutputStream os) throws IOException {
        wb.write(os);
        return this;
    }

    /**
     * 输出到客户端
     *
     * @param fileName
     *            输出文件名
     */
    public ExportExcel write(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + StringUtils.remove(fileName, " "));
        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件
     *
     * @param name
     *            输出文件名
     */
    public ExportExcel writeFile(String name) throws IOException {
        String folderName = name.substring(0, name.lastIndexOf("/") + 1);
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileOutputStream os = new FileOutputStream(name);
        this.write(os);
        return this;
    }

    /**
     * 清理临时文件
     */
    public ExportExcel dispose() {
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 导出测试
     */
    public static void main(String[] args) throws Throwable {

        List<String> headerList = Lists.newArrayList();
        for (int i = 1; i <= 10; i++) {
            headerList.add("表头" + i);
        }

        List<String> dataRowList = Lists.newArrayList();
        for (int i = 1; i <= headerList.size(); i++) {
            dataRowList.add("数据" + i);
        }

        List<List<String>> dataList = Lists.newArrayList();
        for (int i = 1; i <= 1000000; i++) {
            dataList.add(dataRowList);
        }

        ExportExcel ee = new ExportExcel("表格标题", headerList);

        for (List<String> aDataList : dataList) {
            Row row = ee.addRow();
            for (int j = 0; j < aDataList.size(); j++) {
                ee.addCell(row, j, aDataList.get(j));
            }
        }

        ee.writeFile("export.xls");

        ee.dispose();

        log.debug("Export success.");

    }

    public HSSFWorkbook getWorkbook() {
        return this.wb;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public HSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(HSSFSheet sheet) {
        this.sheet = sheet;
    }
}
