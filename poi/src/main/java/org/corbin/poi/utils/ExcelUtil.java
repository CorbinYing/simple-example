package org.corbin.poi.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.corbin.poi.exception.Excel.FormatNotFoundException;
import org.corbin.poi.exception.FileFormatException;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author xiesu
 */
public final class ExcelUtil {
    private static final String XLS = ".xls";
    private final static String XLSX = ".xlsx";

    /**
     * 日期类型
     */
    public final static String DATE_FORMAT = "Date";
    /**
     * 小数类型
     */
    public final static String DOUBLE_FORMAT = "Double";
    /**
     * 整形类型
     */
    public final static String INTEGER_FORMAT = "Integer";
    /**
     * 字符串类型
     */
    public final static String STRING_FORMAT = "String";
    /**
     * 布尔类型
     */
    public final static String BOOLEAN_FORMAT = "Boolean";
    /**
     * 公式
     */
    public final static String FORMULA_FORMAT = "FORMULA";

    /**
     * 所有excel单元格数据格式
     */
    private static final List<String> ALL_CELL_VALUE_TYPE_LIST = Lists.newArrayList(
            DATE_FORMAT,
            DOUBLE_FORMAT,
            INTEGER_FORMAT,
            STRING_FORMAT,
            BOOLEAN_FORMAT,
            FORMULA_FORMAT);

    /**
     * 读取每个sheet具有相同表头的相同数据类型的excel
     *
     * @param file
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static List<Object[]> readExcel(final File file, final String[] cellValueTypeArr) throws IOException, FileFormatException, FormatNotFoundException {
        Objects.requireNonNull(file, "file不能为空");
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = Objects.requireNonNull(instanceWorkbook(inputStream, file.getName()));
        return readData(workbook, cellValueTypeArr);

    }

    /**
     * 读取每个sheet具有相同表头的相同数据类型的excel
     *
     * @param file
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static List<Object[]> readExcel(final MultipartFile file, final String[] cellValueTypeArr) throws IOException, FileFormatException, FormatNotFoundException {
        Objects.requireNonNull(file, "file不能为空");
        Workbook workbook = Objects.requireNonNull(instanceWorkbook(file.getInputStream(), file.getOriginalFilename()));
        return readData(workbook, cellValueTypeArr);
    }


    /**
     * 读取每个sheet具有相同表头的相同数据类型的excel
     *
     * @param workbook
     * @param cellValueTypeArr 单元格列的类型
     * @return
     */
    private static List<Object[]> readData(Workbook workbook, String[] cellValueTypeArr) throws FormatNotFoundException {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(cellValueTypeArr, "单元格数据类型未定义");


        List<Object[]> dataArrList = Lists.newArrayList();
        //获取工作Sheet个数
        int sheetCount = workbook.getNumberOfSheets();
        for (int sheetAt = 0; sheetAt < sheetCount; sheetAt++) {
            Sheet sheet = workbook.getSheetAt(sheetAt);
            //逻辑开始行,0开始,1不存在
            int firstRowAt = sheet.getFirstRowNum();
            int lastRowAt = sheet.getLastRowNum();

            //获取第一行,属性行,根据其获取开始列,结尾列
            Row attrRow = sheet.getRow(firstRowAt);
            int firstCellAt = attrRow.getFirstCellNum();
            int lastCellAt = attrRow.getLastCellNum();
            int columnCount = attrRow.getPhysicalNumberOfCells();
            Assert.isTrue(columnCount == cellValueTypeArr.length, "指定列的数据格式数量与实际不符");

            //循环读取每一行的所有数据,跳过表头行
            for (int rowAt = firstRowAt; rowAt < lastRowAt; rowAt++) {
                Row row = sheet.getRow(rowAt + 1);
                if (Objects.isNull(row)) {
                    continue;
                }
                //获取所有单元格
                Object[] rowCellValueArr = new Object[columnCount];
                for (int cellAt = firstCellAt, i = 0; cellAt < lastCellAt; cellAt++, i++) {
                    Cell cell = row.getCell(cellAt);
                    //读取单元格值
                    Object cellValue = readCellValue(cell, cellValueTypeArr[i]);
                    rowCellValueArr[i] = cellValue;
                }
                dataArrList.add(rowCellValueArr);
            }

        }

        return dataArrList;
    }


    /**
     * 根据不同的文件格式初始化不同的workbook对象
     *
     * @param inputStream 文件流
     * @param fileName    文件名（以.xsl 或.xlsx 结尾）
     * @return Workbook 对象
     */
    private static Workbook instanceWorkbook(InputStream inputStream, String fileName) throws IOException, FileFormatException {
        Objects.requireNonNull(inputStream, "文件流不能为空");
        if (StringUtils.endsWithIgnoreCase(fileName, XLS)) {
            return new HSSFWorkbook(inputStream);
        } else if (StringUtils.endsWithIgnoreCase(fileName, XLSX)) {
            return new XSSFWorkbook(inputStream);
        } else {
            throw new FileFormatException("文件格式非EXCEL格式");
        }
    }

    /**
     * 指定格式获取excel单元格的值,获取不到值返回null
     *
     * @param cell
     * @param cellValueFormat
     * @return
     * @throws FormatNotFoundException
     */
    private static Object readCellValue(Cell cell, final String cellValueFormat) throws FormatNotFoundException {
        Objects.requireNonNull(cellValueFormat, "类型不能为空");
        if (!ALL_CELL_VALUE_TYPE_LIST.contains(cellValueFormat)) {
            throw new FormatNotFoundException("未定义的EXCEL单元格数据格式");
        }
        Object cellValue;
        switch (cellValueFormat) {
            case STRING_FORMAT:
                cellValue = cell.getStringCellValue();
                break;
            case INTEGER_FORMAT:
            case DOUBLE_FORMAT:
                cellValue = cell.getNumericCellValue();
                break;
            case BOOLEAN_FORMAT:
                cellValue = cell.getBooleanCellValue();
                break;
            case DATE_FORMAT:
                cellValue = cell.getDateCellValue();
                break;
            case FORMULA_FORMAT:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = null;
        }
        if (ObjectUtils.isEmpty(cellValue)) {
            cellValue = null;
        }

        if (cellValueFormat.equals(STRING_FORMAT) && StringUtils.isBlank((String) cellValue)) {
            cellValue = null;
        }

        if (cellValueFormat.equals(INTEGER_FORMAT) && Objects.nonNull(cellValue)) {
            cellValue = ((Double) cellValue).intValue();
        }
        return cellValue;
    }
}
