package org.corbin.poi.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.corbin.simple.utils.csv.CsvUtil;
import org.corbin.simple.utils.excel.exception.Excel.FormatNotFoundException;
import org.corbin.simple.utils.excel.exception.FileFormatException;
import org.corbin.simple.utils.excel.ExcelUtil;
import org.springframework.http.HttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xiesu
 */
@RestController
public class FileController {

    @RequestMapping("/upload")
    public void uploadExcel(@RequestParam("file") MultipartFile file) throws IOException, FileFormatException, FormatNotFoundException {
        Assert.notNull(file, "文件为空");
        String[] attrTypeArr = new String[]{ExcelUtil.STRING_FORMAT, ExcelUtil.INTEGER_FORMAT, ExcelUtil.DATE_FORMAT};
        List<Object[]> dataArrList = ExcelUtil.readExcel(file, attrTypeArr);
        System.out.println(JSON.toJSONString(dataArrList));
    }

    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws IOException, FormatNotFoundException {
        response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode("测试下载文件.xlsx", StandardCharsets.UTF_8.name()));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/ms-excel");

        Object[] attr = new Object[]{"姓名", "年龄", "学费", "入学日期"};
        Object[] stuent1 = new Object[]{"张三", 22, 1000.3, new Date()};
        Object[] stuent2 = new Object[]{"张四", 23, 1340.3, new Date()};
        List<Object[]> dataList = Lists.newArrayList(attr, stuent1, stuent2);

        String[] attrTypeArr = new String[]{ExcelUtil.STRING_FORMAT, ExcelUtil.INTEGER_FORMAT, ExcelUtil.DOUBLE_FORMAT, ExcelUtil.DATE_FORMAT};
        Workbook workbook = ExcelUtil.writeExcel(dataList, attrTypeArr, ExcelUtil.XLS);
        OutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);


        outputStream.flush();
    }

    @RequestMapping("/down-csv")
    public void down(HttpServletResponse response) throws IOException {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("测试下载文件.csv", StandardCharsets.UTF_8.name()));

        Object[] attr = new Object[]{"姓名", "年龄", "学费", "入学日期"};
        Object[] stuent1 = new Object[]{"张三", 22, 1000.3, new Date()};
        Object[] stuent2 = new Object[]{"张四", 23, 1340.3, new Date()};
        List<Object[]> dataList = Lists.newArrayList(attr, stuent1, stuent2);
        OutputStream outputStream = response.getOutputStream();
        CsvUtil.writeData(dataList, CSVFormat.Predefined.Excel, outputStream);
        outputStream.flush();

    }

}
