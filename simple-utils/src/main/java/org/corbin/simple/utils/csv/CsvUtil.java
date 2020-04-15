package org.corbin.simple.utils.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class CsvUtil {

    /**
     * 写入CSV文件
     *
     * @param contentArrList
     * @param predefined
     * @param outputStream
     */
    public static void writeData(List<Object[]> contentArrList, CSVFormat.Predefined predefined, OutputStream outputStream) {
        Objects.requireNonNull(contentArrList);
        CSVFormat csvFormat = CSVFormat.valueOf(predefined.name());
        CSVPrinter csvPrinter = null;
        BufferedWriter bufferedWriter = null;
        try {
            //添加BOM标识，避免EXCEL打开乱码
            // 先把标示符号用流的方式传过去  这样文件内就会把他识别为标示  而不是标示字符串
            outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            csvPrinter = new CSVPrinter(bufferedWriter, csvFormat);
            for (Object[] rowData : contentArrList) {
                csvPrinter.printRecord(rowData);
                csvPrinter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert csvPrinter != null;
                csvPrinter.close();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
