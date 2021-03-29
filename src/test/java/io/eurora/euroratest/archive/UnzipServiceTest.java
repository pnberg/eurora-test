package io.eurora.euroratest.archive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.eurora.euroratest.EuroraTestException;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnzipServiceTest {

  private static final String FILENAME_1 = "file1";
  private static final String FILENAME_2 = "file2";

  @TempDir
  File targetDirectory;

  private UnzipService unzipService;

  @BeforeEach
  void init() {
    unzipService = new UnzipService();
  }

  @Test
  @DisplayName("Given zip inputStream for zip file containing two files " +
               "when extract is called " +
               "then correct files should be extracted")
  void unzip() throws IOException, EuroraTestException {
    // given
    ZipInputStream zipInputStream = givenZipInputStreamForContainingFiles(FILENAME_1, FILENAME_2);

    // when
    unzipService.extract(zipInputStream, targetDirectory);

    // then
    assertTrue(new File(targetDirectory, FILENAME_1).exists());
    assertTrue(new File(targetDirectory, FILENAME_2).exists());
  }

  private ZipInputStream givenZipInputStreamForContainingFiles(String givenFile1, String givenFile2)
      throws IOException {
    ZipInputStream stream = mock(ZipInputStream.class);
    given(stream.getNextEntry()).willReturn(new ZipEntry(givenFile1), new ZipEntry(givenFile2), null);
    return stream;
  }

}