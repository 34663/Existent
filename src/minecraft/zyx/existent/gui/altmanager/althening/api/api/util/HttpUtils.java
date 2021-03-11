package zyx.existent.gui.altmanager.althening.api.api.util;

import java.net.*;
import java.io.*;

public class HttpUtils
{
    protected String connect(final String v1) throws IOException {
        final InputStream v2 = new URL(v1).openStream();
        final BufferedReader v3 = new BufferedReader(new InputStreamReader(v2));
        final StringBuilder v4 = new StringBuilder();
        String v5;
        while ((v5 = v3.readLine()) != null) {
            v4.append(v5).append("\n");
        }
        return v4.toString();
    }
}
