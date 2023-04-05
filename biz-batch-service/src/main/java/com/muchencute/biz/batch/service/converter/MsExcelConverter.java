package com.muchencute.biz.batch.service.converter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvUtil;
import com.muchencute.biz.batch.service.converter.html.ToHtml;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.LinkedList;

public class MsExcelConverter {

  private static String getCellValue(Workbook workbook, Cell cell) {

    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf(cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> {
        final var formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        yield getCellValue(workbook, formulaEvaluator.evaluateInCell(cell));
      }
      default -> "";
    };
  }

  @SneakyThrows
  public static String xlsx2csv(String xlsxPath) {

    @Cleanup final var writer = new StringWriter();
    @Cleanup final var csvWriter = CsvUtil.getWriter(writer);
    @Cleanup final var inputStream = FileUtil.getInputStream(xlsxPath);
    @Cleanup final var workbook = new XSSFWorkbook(inputStream);
    final var sheet = workbook.getSheetAt(0);
    final var rowIterator = sheet.rowIterator();
    while (rowIterator.hasNext()) {
      final var row = rowIterator.next();
      // Cell 遍历此处不能用 CellIterator，因为空格会被跳过，导致 CSV 中出现列的缺失。
      final var cellNum = row.getLastCellNum() - row.getFirstCellNum() + 1;
      final var line = new LinkedList<String>();
      for (var i = 0; i < cellNum; i++) {
        final var cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        line.add(getCellValue(workbook, cell));
      }
      csvWriter.writeLine(line.toArray(new String[0]));
    }
    csvWriter.flush();
    return writer.toString();
  }

  @SneakyThrows
  public static String xls2csv(String xlsPath) {

    @Cleanup final var writer = new StringWriter();
    @Cleanup final var csvWriter = CsvUtil.getWriter(writer);
    @Cleanup final var inputStream = FileUtil.getInputStream(xlsPath);
    @Cleanup final var workbook = new HSSFWorkbook(inputStream);
    final var sheet = workbook.getSheetAt(0);
    final var rowIterator = sheet.rowIterator();
    while (rowIterator.hasNext()) {
      final var row = rowIterator.next();
      final var cellNum = row.getLastCellNum() - row.getFirstCellNum() + 1;
      final var line = new LinkedList<String>();
      for (var i = 0; i < cellNum; i++) {
        final var cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        line.add(getCellValue(workbook, cell));
      }
      csvWriter.writeLine(line.toArray(new String[0]));
    }
    csvWriter.flush();
    return writer.toString();
  }

  @SneakyThrows
  public static String xls2html(String excelPath) {

    final var tempFile = File.createTempFile("xls2html", ".xls");
    @Cleanup final var outputStream = FileUtil.getOutputStream(tempFile);
    @Cleanup final var excel = new HSSFWorkbook(FileUtil.getInputStream(excelPath));
    excel.write(outputStream);
    final var converter = new ExcelToHtmlConverter(
      DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
    converter.processWorkbook(excel);
    final var htmlDoc = converter.getDocument();
    @Cleanup final var out = new ByteArrayOutputStream();
    final var domSource = new DOMSource(htmlDoc);
    final var streamResult = new StreamResult(out);
    final var transformerFactory = TransformerFactory.newInstance();
    final var serializer = transformerFactory.newTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty(OutputKeys.METHOD, "html");
    serializer.transform(domSource, streamResult);
    return out.toString();
  }

  @SneakyThrows
  public static String xlsx2html(String excelPath) {

    final var writer = new StringWriter();
    final var toHtml = ToHtml.create(new XSSFWorkbook(excelPath), writer);
    toHtml.setCompleteHTML(true);
    toHtml.printPage();
    return writer.toString();
  }
}
