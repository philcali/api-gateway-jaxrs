package me.philcali.api.gateway.jaxrs;

public class PathUtils {
    private PathUtils() {

    }

    public static String normalize(String path) {
        if (path.equals("/")) {
            return "";
        } else {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash == path.length() - 1) {
                path = path.substring(0, lastSlash);
            }
            if (path.indexOf('/') == 0) {
                return path;
            } else {
                return "/" + path;
            }
        }
    }
}
