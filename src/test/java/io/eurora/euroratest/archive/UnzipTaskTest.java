package io.eurora.euroratest.archive;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import io.eurora.euroratest.EuroraTestConfiguration;
import io.eurora.euroratest.report.ReportService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnzipTaskTest {

  private static final String ZIP_FOLDER = "/tmp/zip";
  private static final String UNZIP_FOLDER = "/tmp/unzip";
  private static final String FILE_1 = "file_1.zip";
  private static final String FILE_2 = "file_2.zip";

  @TempDir Path unzipFolder;
  @TempDir Path zipFolder;

  @Mock private FilePool pool;
  @Mock private UnzipService unzipService;
  @Mock private ReportService reportService;
  @Mock private EuroraTestConfiguration config;

  @Captor private ArgumentCaptor<File> fileCaptor;

  private UnzipTask unzipTask;

  @BeforeEach
  void init() {
    zipFolder.resolve(ZIP_FOLDER);
    unzipFolder.resolve(UNZIP_FOLDER);
    unzipTask = new UnzipTask(pool, unzipService, reportService, config);
  }

  @Test
  @DisplayName("Given zip files in temp dir, added to pool " +
               "when unzipTask is run " +
               "then call UnzipService#unzip for both files")
  void run() {
    // given
    givenUnzipFolder();
    File file1 = givenZipFileInTempDir(FILE_1);
    File file2 = givenZipFileInTempDir(FILE_2);
    givenPoolWithTwoFiles(file1, file2);

    // when
    unzipTask.run();

    // then
    then(unzipService).should(times(2)).extract(any(ZipInputStream.class), fileCaptor.capture());
  }

  private void givenPoolWithTwoFiles(File file1, File file2) {
    when(pool.getNextFile()).thenReturn(file1, file2, null);
  }

  private File givenZipFileInTempDir(String filename) {
    File zipfile = new File(zipFolder.toFile(), filename + ".zip");
    try {
      ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipfile, false));
      ZipEntry e = new ZipEntry(filename);
      zipOutputStream.putNextEntry(e);

      byte[] data = "Hello, world".getBytes();
      zipOutputStream.write(data, 0, data.length);
      zipOutputStream.closeEntry();
      zipOutputStream.close();
    }
    catch (IOException ioException) {
      ioException.printStackTrace();
    }
    return zipfile;
  }

  private void givenUnzipFolder() {
    given(config.getUnzipDirectoryPath()).willReturn(UNZIP_FOLDER);
  }

}