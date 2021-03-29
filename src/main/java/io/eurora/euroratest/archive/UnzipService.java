package io.eurora.euroratest.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnzipService {

  public boolean extract(ZipInputStream zipInputStream, File unzipFolder) {
    byte[] buffer = new byte[1024];
    try {
      ZipEntry ze = zipInputStream.getNextEntry();
      while(ze != null){
        String fileName = ze.getName();
        File newFile = new File(unzipFolder, fileName);
        //create directories for sub directories in zip
        new File(newFile.getParent()).mkdirs();
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
        //close this ZipEntry
        zipInputStream.closeEntry();
        ze = zipInputStream.getNextEntry();
      }
    }
    catch (Exception e) {
      return false;
    }
    finally {
      try {
        //close last ZipEntry
        zipInputStream.closeEntry();
        zipInputStream.close();
      }
      catch (IOException e) {
        log.error("Exception caught when closing zip input stream", e);
      }
    }
    return true;
  }

}
