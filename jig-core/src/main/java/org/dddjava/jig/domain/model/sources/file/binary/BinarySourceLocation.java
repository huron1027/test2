package org.dddjava.jig.domain.model.sources.file.binary;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 読み込んだバイナリの場所
 */
public class BinarySourceLocation {

    String value;

    BinarySourceLocation(String value) {
        this.value = value;
    }

    public BinarySourceLocation() {
        this("");
    }

    public BinarySourceLocation(Path path) {
        this(path.toAbsolutePath().toString());
    }

    public URI uri() {
        return Paths.get(value).toUri();
    }
}
