package org.corbin.poi.web;

import com.alibaba.fastjson.JSON;
import org.corbin.poi.exception.Excel.FormatNotFoundException;
import org.corbin.poi.exception.FileFormatException;
import org.corbin.poi.utils.ExcelUtil;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author xiesu
 */
@RestController
public class FileController {

    @RequestMapping("/test")
    public void uploadExcel(@RequestParam("file") MultipartFile file) throws IOException, FileFormatException, FormatNotFoundException {
        Assert.notNull(file, "文件为空");
        String[] attrTypeArr = new String[]{ExcelUtil.STRING_FORMAT, ExcelUtil.INTEGER_FORMAT, ExcelUtil.DATE_FORMAT};
        List<Object[]> dataArrList = ExcelUtil.readExcel(file, attrTypeArr);
        System.out.println(JSON.toJSONString(dataArrList));
    }

}
