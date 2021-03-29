package io.eurora.euroratest.report;

import io.eurora.euroratest.EuroraTestException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final Clock clock;

  @Getter private UnzipReport report = null;
  LocalDateTime reportStart;  // visible for testing

  public void startReport() throws EuroraTestException {
    if (report != null) {
      throw new EuroraTestException("Report already exists");
    }
    report = new UnzipReport();
    reportStart = LocalDateTime.now(clock);
  }

  public void updatePoolFiles(int poolFiles) {
    report.setPoolFiles(poolFiles);
  }

  public void addUnzippedFile(long filesize) {
    report.addUnzippedFile(filesize);
  }

  public UnzipReport finishReport() {
    report.setUnzipTimeInMillis(ChronoUnit.MILLIS.between(reportStart, LocalDateTime.now(clock)));
    UnzipReport result = report;
    this.report = null;
    this.reportStart = null;
    return result;
  }

}
