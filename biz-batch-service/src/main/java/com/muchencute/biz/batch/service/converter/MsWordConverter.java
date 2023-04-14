package com.muchencute.biz.batch.service.converter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.batch.service.converter.picture.PictureManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.OpcPackage;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MsWordConverter {

  private final PictureManager pictureManager;

  public MsWordConverter(PictureManager pictureManager) {

    this.pictureManager = pictureManager;
  }

  @SneakyThrows
  public String doc2html(String docPath) {

    @Cleanup final var document = new HWPFDocument(FileUtil.getInputStream(docPath));
    final var newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final var wordToHtmlConverter = new WordToHtmlConverter(newDocument);
    wordToHtmlConverter.setPicturesManager(
      (content, pictureType, suggestedName, widthInches, heightInches) ->
        pictureManager.picture(content, pictureType.getMime(), pictureType.getExtension()));
    wordToHtmlConverter.processDocument(document);
    final var stringWriter = new StringWriter();
    final var transformer = TransformerFactory.newInstance()
      .newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
    transformer.setOutputProperty(OutputKeys.METHOD, "html");
    transformer.transform(
      new DOMSource(wordToHtmlConverter.getDocument()),
      new StreamResult(stringWriter));
    return stringWriter.toString();
  }

  @SneakyThrows
  public String docx2html(String docxPath) {

    @Cleanup final var inputStream = FileUtil.getInputStream(docxPath);
    final var opcPackage = OpcPackage.load(inputStream);
    final var tempFilename = File.createTempFile("docx2html", ".html");
    @Cleanup final var outputStream = FileUtil.getOutputStream(tempFilename);
    final var htmlSettings = Docx4J.createHTMLSettings();
    htmlSettings.setOpcPackage(opcPackage);
    htmlSettings.setImageHandler((abstractWordXmlPicture, relationship, binaryPart) -> {
      final var pictureType = PictureType.findMatchingType(binaryPart.getBytes());
      return pictureManager.picture(binaryPart.getBytes(), pictureType.getMime(),
        pictureType.getExtension());
    });
    Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
    return StrUtil.join("",
      FileUtil.readLines(tempFilename.getAbsolutePath(), StandardCharsets.UTF_8));
  }

  @SneakyThrows
  public List<String> doc2lines(String docPath) {

    @Cleanup final var inputStream = FileUtil.getInputStream(docPath);
    final var wordExtractor = new WordExtractor(inputStream);
    return Arrays.asList(wordExtractor.getParagraphText());
  }

  @SneakyThrows
  public List<String> docx2lines(String docxPath) {

    @Cleanup final var inputStream = FileUtil.getInputStream(docxPath);
    final var document = new XWPFDocument(inputStream);
    return document.getParagraphs().stream().map(XWPFParagraph::getText).toList();
  }

  @SneakyThrows
  public String doc2txt(String docPath) {

    return StrUtil.join("\n", doc2lines(docPath));
  }

  @SneakyThrows
  public String docx2txt(String docxPath) {

    return StrUtil.join("\n", docx2lines(docxPath));
  }
}
