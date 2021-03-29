package io.eurora.euroratest.archive;

import io.eurora.euroratest.EuroraTestConfiguration;
import io.eurora.euroratest.EuroraTestException;
import io.eurora.euroratest.report.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveService {

  private final ZipService zipService;
  private final UnzipService unzipService;
  private final FilePool filePool;
  private final ExecutorService taskExecutor;
  private final ReportService reportService;
  private final EuroraTestConfiguration config;

  public void zip(int fileCount) {
    IntStream.range(0, fileCount).forEach(i -> {
      ZipOutputStream zipOutputStream = null;
      try {
        String filename = "file_" + i;
        zipOutputStream = makeZipOutputStream(filename);
        zipService.createArchive(zipOutputStream, filename);
      }
      catch (Exception e) {
        log.error("Error {} composing file {}", e.getMessage(), i);
      }
    });
  }

  public void unzipAll() throws EuroraTestException {
    try {
      Path unzipFolder = Paths.get(config.getUnzipDirectoryPath());
      filePool.loadPool(unzipFolder);
      reportService.updatePoolFiles(filePool.getSize());

      for (int i = 0; i < config.getDefaultThreadCount(); i++) {
        taskExecutor.execute(new UnzipTask(filePool, unzipService, reportService, config));
      }
      taskExecutor.awaitTermination(1, TimeUnit.SECONDS);
      taskExecutor.shutdown();

    }
    catch (Exception e) {
      log.error("Exception caught when unzipping files: ", e);
      throw new EuroraTestException(e.getMessage());
    }
  }

  private ZipOutputStream makeZipOutputStream(String filename) throws IOException {
    Path zipFolder = Paths.get(config.getZipDirectoryPath());
    File file = new File(zipFolder.toFile(), filename + ".zip");
    System.out.println("File is " + file.getAbsolutePath());

    return new ZipOutputStream(new FileOutputStream(file, false));
  }

}
