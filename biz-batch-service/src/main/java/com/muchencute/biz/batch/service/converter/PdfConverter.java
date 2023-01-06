package com.muchencute.biz.batch.service.converter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.batch.service.converter.picture.Base64PictureManager;
import com.muchencute.biz.batch.service.converter.picture.PictureManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.fit.pdfdom.PDFDomTree;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class PdfConverter {

  private final PictureManager pictureManager;

  public PdfConverter(PictureManager pictureManager) {

    this.pictureManager = pictureManager;
  }

  @SneakyThrows
  public String pdf2html(String pdfPath) {

    @Cleanup final var document = PDDocument.load(new File(pdfPath));
    @Cleanup final var output = new ByteArrayOutputStream();
    @Cleanup final var writer = IoUtil.getWriter(output, StandardCharsets.UTF_8);
    final var domTree = new PDFDomTree();
    final var dom = domTree.createDOM(document);

    final var images = dom.getElementsByTagName("img");
    final var imageNum = images.getLength();
    for (var i = 0; i < imageNum; i++) {
      final var image = images.item(i);
      final var src = image.getAttributes().getNamedItem("src");
      final var base64 = StrUtil.subAfter(src.getNodeValue(), "base64,", true).trim();
      final var key = resolvePicture(base64);
      src.setNodeValue(key);
    }

    final var registry = DOMImplementationRegistry.newInstance();
    final var impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
    final var lsSerializer = impl.createLSSerializer();
    final var lsOutput = impl.createLSOutput();
    lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
    lsOutput.setCharacterStream(writer);
    lsSerializer.write(dom, lsOutput);
    return IoUtil.toStr(output, StandardCharsets.UTF_8);
  }

  @SneakyThrows
  private String resolvePicture(String base64) {

    if (pictureManager instanceof Base64PictureManager) {
      return base64;
    }

    final var bytes = Base64.decode(base64);
    final var pictureType = PictureType.findMatchingType(bytes);
    return pictureManager.picture(bytes, pictureType.getMime(), pictureType.getExtension());
  }

  @SneakyThrows
  public List<String> pdf2lines(String pdfPath) {

    final var file = new File(pdfPath);
    @Cleanup final var randomAccessFile = new RandomAccessFile(file, "r");
    final var parser = new PDFParser(randomAccessFile);
    parser.parse();
    @Cleanup final var cosDoc = parser.getDocument();
    final var stripper = new PDFTextStripper();
    final var document = new PDDocument(cosDoc);
    return Arrays.asList(stripper.getText(document).split("\n"));
  }

  @SneakyThrows
  public String pdf2txt(String pdfPath) {

    return StrUtil.join("", pdf2lines(pdfPath));
  }
}
