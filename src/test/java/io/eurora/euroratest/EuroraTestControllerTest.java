package io.eurora.euroratest;

import io.eurora.euroratest.archive.ArchiveService;
import io.eurora.euroratest.report.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EuroraTestController.class)
@ContextConfiguration(classes = { EuroraTestController.class, EuroraTestConfiguration.class })
class EuroraTestControllerTest {

  private static final String API_URI = "/zipfiles";
  private static final int DEFAULT_FILE_COUNT = 100_000;

  @Autowired private MockMvc mockMvc;

  @MockBean private ArchiveService archiveService;
  @MockBean private EuroraTestConfiguration config;
  @MockBean private ReportService reportService;

  @DisplayName("Given default file count value set in configuration " +
               "when build endpoint is called " +
               "then call ArchiveService#zip with correct file count")
  @ParameterizedTest
  @MethodSource("provideCounts")
  void buildFiles(String givenFiles, int expectedFileCount) throws Exception {
    // given
    given(config.getDefaultFileCount()).willReturn(DEFAULT_FILE_COUNT);
    MockHttpServletRequestBuilder requestBuilder = givenBuildRequest(givenFiles);

    // when
    ResultActions actualResult = this.mockMvc.perform(requestBuilder);

    // then
   actualResult
        .andExpect(status().isOk());

    then(archiveService).should().zip(expectedFileCount);
  }

  @Test
  @DisplayName("When unzip endpoint is called " +
               "then start with new report, call ArchiveService#unzipAll " +
               "and finish unzipping report")
  void unzip() throws Exception {
    // when
    ResultActions actualResult = this.mockMvc.perform(get(API_URI + "/unzip"));

    // then
    actualResult
        .andExpect(status().isOk());

    InOrder unzipOrder = inOrder(reportService, archiveService);
    then(reportService).should(unzipOrder).startReport();
    then(archiveService).should(unzipOrder).unzipAll();
    then(reportService).should(unzipOrder).finishReport();
  }

  private static Stream<Arguments> provideCounts() {
    return Stream.of(
        Arguments.of("10", 10),
        Arguments.of(null, DEFAULT_FILE_COUNT)
    );
  }

  private MockHttpServletRequestBuilder givenBuildRequest(String givenFiles) {
    MockHttpServletRequestBuilder builder = get(API_URI + "/build");
    if (givenFiles != null) {
      builder.queryParam("count", givenFiles);
    }
    return builder;
  }

}