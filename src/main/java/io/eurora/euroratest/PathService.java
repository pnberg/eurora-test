package io.eurora.euroratest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class PathService {

    private final EuroraTestConfiguration config;

    public Path getZipFolder() {
        return Paths.get(config.getZipDirectoryPath());
    }

    public Path getUnzipFolder() {
        return Paths.get(config.getUnzipDirectoryPath());
    }
}
