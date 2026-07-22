package bd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Carga un archivo .env buscando en: working dir → raíz del proyecto → variables de entorno del sistema. */
public final class CargadorEnv {

    private CargadorEnv() {}

    public static Properties cargar(String... clavesSistemaFallback) {
        String[] candidatos = {
            ".env",
            System.getProperty("user.dir") + File.separator + ".env",
            new File(CargadorEnv.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath())
                .getParentFile().getParent() + File.separator + ".env"
        };
        Properties props = new Properties();
        for (String path : candidatos) {
            File f = new File(path);
            if (f.exists()) {
                try (InputStream in = new FileInputStream(f)) {
                    props.load(in);
                    return props;
                } catch (IOException ignored) {}
            }
        }
        for (String key : clavesSistemaFallback) {
            String val = System.getenv(key);
            if (val != null) props.setProperty(key, val);
        }
        return props;
    }
}
