package ca.yorku.cmg.cnsim;

import java.io.InputStream;

public class ResourceLoader {
    private ResourceLoader() {}

    public static InputStream getResourceAsStream(String fileName) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(fileName);
    }
}
