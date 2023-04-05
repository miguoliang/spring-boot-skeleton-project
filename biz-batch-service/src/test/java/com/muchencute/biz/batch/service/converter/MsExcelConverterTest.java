package com.muchencute.biz.batch.service.converter;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MsExcelConverterTest {

  @Test
  void xlsx2csv() {
    final var file = ResourceUtil.getResource("TestXLSXConversion.xlsx").getPath();
    final var csv = MsExcelConverter.xlsx2csv(file);
    Assertions.assertFalse(csv.isBlank());
    log.debug(csv);
  }

  @Test
  void xls2csv() {

    final var file = ResourceUtil.getResource("TestXLSConversion.xls").getPath();
    final var csv = MsExcelConverter.xls2csv(file);
    Assertions.assertFalse(csv.isBlank());
    log.debug(csv);
  }

  @Test
  void xls2html() {

    final var file = ResourceUtil.getResource("TestXLSConversion.xls").getPath();
    final var html = MsExcelConverter.xls2html(file);
    Assertions.assertFalse(html.isBlank());
    log.debug(html);
  }

  @Test
  void xlsx2html() {

    final var file = ResourceUtil.getResource("TestXLSXConversion.xlsx").getPath();
    final var html = MsExcelConverter.xlsx2html(file);
    Assertions.assertFalse(html.isBlank());
    log.debug(html);
  }
}