package com.muchencute.test.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestUnitTest {

  @RegisterExtension
  final RestDocumentationExtension restDocumentationExtension = new RestDocumentationExtension("src/asciidoc");

  protected MockMvc mockMvc;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
  }
}
