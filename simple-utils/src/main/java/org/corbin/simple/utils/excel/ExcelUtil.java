package org.corbin.simple.utils.excel;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.corbin.simple.utils.excel.exception.Excel.FormatNotFoundException;
import org.corbin.simple.utils.excel.exception.FileFormatException;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author xiesu
 */
public final class ExcelUtil {
    public static final String XLS = ".xls";
    public final static String XLSX = ".xlsx";

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
     * 读取时受支持的excel单元格数据格式
     */
    private static final List<String> ALL_READ_CELL_VALUE_TYPE_LIST = Lists.newArrayList(
            DATE_FORMAT,
            DOUBLE_FORMAT,
            INTEGER_FORMAT,
            STRING_FORMAT,
            BOOLEAN_FORMAT,
            FORMULA_FORMAT);

    /**
     * 写入时时受支持的excel单元格数据格式
     */
    private static final List<String> ALL_WRITE_CELL_VALUE_TYPE_LIST = Lists.newArrayList(
            DATE_FORMAT,
            DOUBLE_FORMAT,
            INTEGER_FORMAT,
            STRING_FORMAT,
            BOOLEAN_FORMAT);

    /**
     * 读取每个sheet具有相同表头的相同数据类型的excel
     *
     * @param file 文件对象
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static List<Object[]> readExcel(final File file, final String[] cellValueTypeArr) throws IOException, FileFormatException, FormatNotFoundException {
        Objects.requireNonNull(file, "file不能为空");
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = Objects.requireNonNull(instanceReadWorkbook(inputStream, file.getName()));
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
        Workbook workbook = Objects.requireNonNull(instanceReadWorkbook(file.getInputStream(), file.getOriginalFilename()));
        return readData(workbook, cellValueTypeArr);
    }

    /**
     * 根据指定的数据生成excel 流
     *
     * @param dataList           待写入数据(包含表头属性行)
     * @param columnValueTypeArr 属性类型()
     * @param excelFileFormat    待创建的excel格式({@link ExcelUtil#XLS} {@link ExcelUtil#XLSX})
     */
    public static Workbook writeExcel(final List<Object[]> dataList, String[] columnValueTypeArr, String excelFileFormat) throws FormatNotFoundException {
        Objects.requireNonNull(dataList, "待写入数据不能为空");
        Objects.requireNonNull(columnValueTypeArr, "单元格列属性类型不能为空");
        Assert.isTrue(dataList.get(0).length == columnValueTypeArr.length, "属性列数目与数据列长度不一致");

        Workbook workbook = instanceWriteWorkbook(excelFileFormat);
        writeData(workbook, dataList, columnValueTypeArr);
        return workbook;

    }

    /**
     * 写入excel数据
     *
     * @param workbook
     * @param dataList
     * @param columnValueTypeArr
     * @throws FormatNotFoundException
     */
    private static void writeData(Workbook workbook, List<Object[]> dataList, String[] columnValueTypeArr) throws FormatNotFoundException {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(dataList, "待写入数据不能为空");
        Objects.requireNonNull(columnValueTypeArr, "单元格列属性类型不能为空");

        Sheet sheet = workbook.createSheet();
        //写入属性名行
        writeRowAttrName(workbook, sheet.createRow(0), dataList.get(0), columnValueTypeArr);
        //写入数据行
        for (int row = 1; row < dataList.size(); row++) {
            Row dataRow = sheet.createRow(row);
            writeRowData(workbook, dataRow, dataList.get(row), columnValueTypeArr);
        }
    }

    /**
     * 写入属性名行
     *
     * @param workbook
     * @param row
     * @param rowDataArr
     * @param columnValueTypeArr
     * @throws FormatNotFoundException
     */
    private static void writeRowAttrName(Workbook workbook, Row row, Object[] rowDataArr, String[] columnValueTypeArr) throws FormatNotFoundException {
        Objects.requireNonNull(row);
        Assert.isTrue(rowDataArr.length == columnValueTypeArr.length, "属性个数与数据列数不相同");
        for (int col = 0; col < columnValueTypeArr.length; col++) {
            writeCellDataWithString(row.createCell(col), rowDataArr[col]);
        }
    }

    /**
     * 写入每一行的数据
     *
     * @param workbook
     * @param row
     * @param rowDataArr
     * @param columnValueTypeArr
     * @throws FormatNotFoundException
     */
    private static void writeRowData(Workbook workbook, Row row, Object[] rowDataArr, String[] columnValueTypeArr) throws FormatNotFoundException {
        Objects.requireNonNull(row);
        Assert.isTrue(rowDataArr.length == columnValueTypeArr.length, "属性个数与数据列数不相同");
        for (int col = 0; col < columnValueTypeArr.length; col++) {
            writeCellData(workbook, row.createCell(col), rowDataArr[col], columnValueTypeArr[col]);
        }
    }

    /**
     * 写入单元格数据
     *
     * @param workbook
     * @param cell
     * @param data
     * @param cellValueFormat
     * @throws FormatNotFoundException
     */
    private static void writeCellData(Workbook workbook, Cell cell, Object data, String cellValueFormat) throws FormatNotFoundException {
        Objects.requireNonNull(cell);
        if (!ALL_WRITE_CELL_VALUE_TYPE_LIST.contains(cellValueFormat)) {
            throw new FormatNotFoundException("未定义的EXCEL单元格数据格式");
        }
        CellStyle cellStyle = workbook.createCellStyle();
        if (Objects.isNull(data)) {
            return;
        }

        switch (cellValueFormat) {
            case DATE_FORMAT:
                cell.setCellValue((Date) data);
                cellStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
                break;
            case INTEGER_FORMAT:
            case DOUBLE_FORMAT:
                cell.setCellValue(Double.parseDouble(data.toString()));
                break;
            case STRING_FORMAT:
                cell.setCellValue((String) data);
                break;
            case BOOLEAN_FORMAT:
                cell.setCellValue((Boolean) data);
                break;
            default:
                break;
        }
        cell.setCellStyle(cellStyle);
    }


    /**
     * 使用string格式写入单元格数据,如果其他格式的数据将抛出异常
     *
     * @param cell
     * @param data
     */
    private static void writeCellDataWithString(Cell cell, Object data) {
        Objects.requireNonNull(cell);
        cell.setCellValue((String) data);
    }

    private static Workbook instanceWriteWorkbook(String excelFileFormat) {
        if (StringUtils.isBlank(excelFileFormat)) {
            excelFileFormat = XLSX;
        }
        if (XLSX.contains(excelFileFormat)) {
            return new XSSFWorkbook();
        } else {
            return new HSSFWorkbook();
        }
    }


    /**
     * 读取每个sheet具有相同表头的相同数据类型的excel
     *
     * @param workbook
     * @param columnValueTypeArr 单元格列的类型
     * @return
     */
    private static List<Object[]> readData(Workbook workbook, String[] columnValueTypeArr) throws FormatNotFoundException {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(columnValueTypeArr, "单元格数据类型未定义");


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
            Assert.isTrue(columnCount == columnValueTypeArr.length, "指定列的数据格式数量与实际不符");

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
                    Object cellValue = readCellValue(cell, columnValueTypeArr[i]);
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
    private static Workbook instanceReadWorkbook(InputStream inputStream, String fileName) throws IOException, FileFormatException {
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
        if (!ALL_READ_CELL_VALUE_TYPE_LIST.contains(cellValueFormat)) {
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
