package com.muchencute.biz.batch.service.converter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

public class TxtConverter {

  @SneakyThrows
  public static String txt2html(String txtPath) {

    return str2html(FileUtil.readString(txtPath, StandardCharsets.UTF_8));
  }

  @SneakyThrows
  public static String str2html(String str) {

    return StrUtil.split(str, "\n")
        .stream()
        .map(it -> "<p>" + it + "</p>")
        .reduce((a, b) -> a + b)
        .orElse("");
  }
}
