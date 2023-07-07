package me.koply.kcommando.plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LightYML {
    private boolean ok = true;

    private final Map<String, String> attributes = new HashMap<>();
    public LightYML(final InputStream fileInputStream) {
        try {
            final InputStreamReader isReader = new InputStreamReader(fileInputStream);
            final BufferedReader reader = new BufferedReader(isReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] sided = line.split(":");
                attributes.put(sided[0].trim(), sided[1].trim());
            }

            isReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
    }

    public final Map<String, String> getAttributes() {
        return attributes;
    }

    public final boolean isOk() {
        return ok && attributes.containsKey("main");
    }

    /* Example plugin.yml
        author: Koply
        main: me.koply.kcommandoplugin.Main
        version: 1.0
        name: FirstPlugin
        description: this plugin is so wonderful
     */
}