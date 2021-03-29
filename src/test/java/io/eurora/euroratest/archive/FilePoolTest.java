package io.eurora.euroratest.archive;

import static org.assertj.core.api.Assertions.assertThat;

import io.eurora.euroratest.EuroraTestException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FilePoolTest {

  private static final String FILENAME_1 = "file1.txt";
  private static final String FILENAME_2 = "file2.txt";

  @TempDir
  Path zipFolder;

  private FilePool pool;

  @BeforeEach
  void init() {
    pool = new FilePool();
    zipFolder.resolve("/tmp");
  }

  @Test
  @DisplayName("Given directory containing two files " +
               "when pool is loaded " +
               "then both files should be added to the pool")
  void loadPool() throws EuroraTestException, IOException {
    // given
    addFileToFolder(zipFolder, FILENAME_1);
    addFileToFolder(zipFolder, FILENAME_2);

    // when
    pool.loadPool(zipFolder);

    // then
    assertThat(pool.getFiles()).hasSize(2);
    assertThat(pool.getFiles()).extracting(File::getName)
        .containsExactlyInAnyOrder(FILENAME_1, FILENAME_2);
  }

  @Test
  @DisplayName("Given pool with two files " +
               "when next file is asked " +
               "then return correct result")
  void getNextZipFile() {
    // given
    File file1 = new File(FILENAME_1);
    File file2 = new File(FILENAME_2);
    givenPoolWithFiles(file1, file2);

    // when then
    File actualNextFile = pool.getNextFile();
    assertThat(actualNextFile).isEqualTo(file1);
    actualNextFile = pool.getNextFile();
    assertThat(actualNextFile).isEqualTo(file2);
    actualNextFile = pool.getNextFile();
    assertThat(actualNextFile).isNull();

  }

  private void addFileToFolder(Path folder, String filename) throws IOException {
    File newFile = new File(folder.toFile(), filename);
    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
    writer.append("Lorem ipsum");
    writer.close();
  }

  private void givenPoolWithFiles(File... files) {
    Queue<File> poolFiles = new LinkedList<>(Arrays.asList(files));
    pool.setFiles(poolFiles);
  }

}