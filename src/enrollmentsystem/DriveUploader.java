package enrollmentsystem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class DriveUploader {

    public static String uploadToDrive(File file, String name, String enrolleeId) {
        try {
            String boundary = "----WebKitFormBoundary";
            URL url = new URL("https://script.google.com/macros/s/AKfycbwO2A5mbbACXeF2UX54yaQQDzAeF1023TMVj494A1VWSI4B1LX8HTFpg5jPTdUcYniF6w/exec");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String base64File = Base64.getEncoder().encodeToString(fileBytes);

            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                // Name
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + name + "\r\n");

                // Enrollee ID
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"enrolleeId\"\r\n\r\n" + enrolleeId + "\r\n");

                // File name
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"fileName\"\r\n\r\n" + file.getName() + "\r\n");

                // Base64 content
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"fileBase64\"\r\n\r\n" + base64File + "\r\n");

                // End boundary
                out.writeBytes("--" + boundary + "--\r\n");
            }

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
}
