package pl.grzegorz2047.openguild2047.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PastebinWriter {

    public static void paste(String message, Callback callback) {
        Poster poster = new Poster(message, callback);
        Thread thread = new Thread(poster);
        thread.start();
    }

    public static interface Callback {

        void success(URL url);

        void error(String err);

    }

    private static class Poster implements Runnable {

        private String message;
        private Callback callback;

        public Poster(String message, Callback callback) {
            this.message = message;
            this.callback = callback;
        }

        @Override
        public void run() {
            HttpURLConnection con = null;
            OutputStream out = null;
            InputStream in = null;
            try {
                URL url = new URL("http://pastebin.com/api/api_post.php");
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("POST");
                con.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setInstanceFollowRedirects(false);
                con.setDoOutput(true);
                out = con.getOutputStream();
                out.write(("api_option=paste"
                        + "&api_dev_key=" + URLEncoder.encode("4867eae74c6990dbdef07c543cf8f805", "utf-8")
                        + "&api_paste_code=" + URLEncoder.encode(message, "utf-8")
                        + "&api_paste_private=" + URLEncoder.encode("0", "utf-8")
                        + "&api_paste_name=" + URLEncoder.encode("", "utf-8")
                        + "&api_paste_expire_date=" + URLEncoder.encode("1D", "utf-8")
                        + "&api_paste_format=" + URLEncoder.encode("text", "utf-8")
                        + "&api_user_key=" + URLEncoder.encode("", "utf-8")).getBytes());
                out.flush();
                out.close();

                if(con.getResponseCode() != 200) {
                    callback.error("Nie zdobyto prawidlowej odpowiedzi z serwera!");
                    return;
                }
                in = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\r\n");
                }
                reader.close();
                String result = builder.toString().trim();
                if(result.matches("^https?://.*")) {
                    callback.success(new URL(result.trim()));
                } else {
                    String err = result.trim();
                    if(err.length() > 100) {
                        err = err.substring(0, 100);
                    }
                    callback.error(err);
                }
            } catch(IOException ex) {
                callback.error(ex.getMessage());
            }
            if(con != null) {
                con.disconnect();
            }
            if(in != null) {
                try {
                    in.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
