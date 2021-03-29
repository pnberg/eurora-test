package io.eurora.euroratest.archive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import io.eurora.euroratest.RandomTextService;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZipServiceTest {

  private static final String RANDOM_TEXT = "lorem ipsum";
  private static final String FILE_NAME = "file";

  @Mock private RandomTextService randomTextService;
  @Mock private ZipOutputStream zipOutputStream;

  @Captor private ArgumentCaptor<ZipEntry> zipEntryCaptor;
  @Captor private ArgumentCaptor<byte[]> dataCaptor;

  private ZipService zipService;

  @BeforeEach
  void init() {
    zipService = new ZipService(randomTextService);
  }

  @Nested
  class createArchive {

    @Test
    @DisplayName("Given given output stream for zip file, filename and random text " +
                 "when createArchive is called " +
                 "then create zip file, which contains file with expected name and expected content " +
                 "and return true")
    void success() throws Exception {
      // given
      String givenText = givenRandomText();

      // when
      boolean actualResult = zipService.createArchive(zipOutputStream, FILE_NAME);

      // then
      then(zipOutputStream).should().putNextEntry(zipEntryCaptor.capture());
      ZipEntry actualZipEntry = zipEntryCaptor.getValue();
      assertThat(actualZipEntry.getName()).isEqualTo(FILE_NAME);

      then(zipOutputStream).should().write(dataCaptor.capture(), eq(0), eq(givenText.length()));
      assertThat(dataCaptor.getValue()).isEqualTo(RANDOM_TEXT.getBytes());

      assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("Given exception thrown while constructing zip file " +
                 "when createArchive is called " +
                 "then return false")
    void exceptionThrown() throws IOException {
      // given
      givenException();

      // when
      boolean actualResult = zipService.createArchive(zipOutputStream, FILE_NAME);

      // then
      assertThat(actualResult).isFalse();
    }

    private String givenRandomText() {
      given(randomTextService.generateRandomText()).willReturn(RANDOM_TEXT);
      return RANDOM_TEXT;
    }

    private void givenException() throws IOException {
      willThrow(new IOException()).given(zipOutputStream).putNextEntry(any(ZipEntry.class));
    }

  }

}