package org.example;

import java.io.IOException;

public interface Getter {
    String getSecureText(String text) throws IOException, DataBaseRuntimeException;
}
