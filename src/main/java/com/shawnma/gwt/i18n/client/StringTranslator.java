package com.shawnma.gwt.i18n.client;


public class StringTranslator {

    public static String translate(String resource, String key, Object... args) {
        String raw = lookup(resource, key);
        if (raw == null)
            return "@@@@ " + resource + "/" + key + "@@@@";
        return format(raw, args);
    }

    public static String format(String format, Object... arguments) {
        // A very simple implementation of format
        int i = 0;
        while (i < arguments.length) {
            String delimiter = "{" + i + "}";
            while (format.contains(delimiter)) {
                format = format.replace(delimiter, String.valueOf(arguments[i]));
            }
            i++;
        }
        return format;
    }

    private static native String lookup(String resource, String key) /*-{
	    var strs = $wnd[resource];
        if (strs === undefined) {
            console.log("ERROR: undefined resource: " + resource);
            return undefined;
        }
        return strs[key];
    }-*/;

}
