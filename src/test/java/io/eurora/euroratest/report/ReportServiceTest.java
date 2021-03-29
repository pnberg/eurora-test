package io.eurora.euroratest.report;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import io.eurora.euroratest.EuroraTestException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

  private static final LocalDateTime REPORT_START_TIME = LocalDateTime.of(2020, 10, 15, 12, 31, 16);
  private static final LocalDateTime REPORT_END_TIME = LocalDateTime.of(2020, 10, 15, 12, 31, 20);
  private static final int FOUR_SECONDS_IN_MILLIS = 4 * 1000;

  @Mock private Clock clock;
  private ReportService reportService;

  @BeforeEach
  void init() {
    reportService = new ReportService(clock);
  }

  @Nested
  class startReport {

    @Test
    @DisplayName("Given current time and no ongoing unzipping " +
                 "when startReport is called " +
                 "then create new Report and set correct value for start time")
    void withNoOngoingReport() throws EuroraTestException {
      // given
      givenCurrentTime(clock, REPORT_START_TIME);

      // when
      reportService.startReport();

      // then
      assertThat(reportService.reportStart).isEqualTo(REPORT_START_TIME);
    }

    @Test
    @DisplayName("Given ongoing unzipping process " +
                 "when startReport is called " +
                 "then throw CgitestException")
    void withOngoingReport() throws EuroraTestException {
      // given
      givenCurrentTime(clock, REPORT_START_TIME);
      reportService.startReport();

      // when
      Throwable exceptionThrown = catchThrowable(() -> reportService.startReport());

      // then
      assertThat(exceptionThrown).isInstanceOf(EuroraTestException.class);
    }

  }

  @Test
  @DisplayName("Given ongoing unzipping process started " +
               "when finishReport is called " +
               "then return UnzipReport with correct value for duration")
  void finishReport() throws EuroraTestException {
    // given
    givenCurrentTime(clock, REPORT_START_TIME);
    reportService.startReport();
    givenCurrentTime(clock, REPORT_END_TIME);

    // when
    UnzipReport actualReport = reportService.finishReport();

    // then
    assertThat(actualReport.getUnzipTimeInMillis()).isEqualTo(FOUR_SECONDS_IN_MILLIS);
  }

  private void givenCurrentTime(Clock clock, LocalDateTime now) {
    given(clock.instant()).willReturn(now.toInstant(ZoneOffset.UTC));
    given(clock.getZone()).willReturn(ZoneOffset.UTC);
  }

}