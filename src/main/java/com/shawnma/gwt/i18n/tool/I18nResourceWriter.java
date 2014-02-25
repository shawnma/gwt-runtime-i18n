package com.shawnma.gwt.i18n.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class I18nResourceWriter {
    private static final String NONE = "NONE";
    // locale => json string mapping.
    private static Map localizationCache = new HashMap();

    public String generateJSON(String resource, String localeString) {
        if (localeString == null) {
            return generateJson(resource, null, null);
        }
        String[] parts = localeString.split("_");
        if (parts.length == 2) {
            return generateJson(resource, parts[0], parts[1]);
        } else {
            return generateJson(resource, parts[0], null);
        }
    }

    private String generateJson(String resource,String lang, String country) {
        String result = null;
        if (country != null && country.length() > 1) {
            result = search(resource, lang + "_" + country);
        }
        if (result == null && lang != null)
            result = search(resource, lang);
        if (result == null)
            result = loadFromFile(resource);
        if (result == null)
            throw new RuntimeException("Unable to get any resource file for: " + resource);
        String[] paths = resource.split("/");
        return paths[paths.length - 1] + "=" + result + ";";
    }

    public String generateJSON(String resource, Locale locale) {
        String country = locale.getCountry();
        if (country != null)
            country = country.toUpperCase();
        // country="CN";
        String lang = locale.getLanguage();
        if (lang != null)
            lang = lang.toLowerCase();
        return generateJson(resource, country, lang);
    }

    private String search(String resource, String suffix) {
        if (suffix != null)
            resource = resource + "_" + suffix;
        System.out.println("searching " + resource);
        String cached = (String) localizationCache.get(resource);
        if (cached == NONE)
            return null;
        if (cached != null)
            return cached;
        cached = loadFromFile(resource);
        if (cached == null)
            localizationCache.put(resource, NONE);
        else
            localizationCache.put(resource, cached);
        return cached;
    }

    private String loadFromFile(String resource) {
        InputStream is = I18nResourceWriter.class.getResourceAsStream(resource + ".properties");
        if (is == null)
            return null;
        System.out.println("Found " + resource);
        Properties props = new Properties();
        try {
            props.load(new InputStreamReader(is, "UTF-8"));
            StringWriter out = new StringWriter();
            JsonWriter writer = new JsonWriter(out);
            writer.setHtmlSafe(false);
            Gson gson = new Gson();
            gson.toJson(props, Properties.class, writer);
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
