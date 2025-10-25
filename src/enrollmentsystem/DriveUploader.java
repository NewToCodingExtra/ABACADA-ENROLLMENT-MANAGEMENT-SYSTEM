package enrollmentsystem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class DriveUploader {
  
    public static String uploadToDrive(File file, String documentType, String folderName, String oldFileLink) {
        try {
            String boundary = "----WebKitFormBoundary";
            URL url = new URL("https://script.google.com/macros/s/AKfycbymoWiQXIeeBm1UP5mjRR08Kc_k08ysBKcUO42LQ7Jf0gtmlIDjWAEQc6cbHF7G1LVdIQ/exec");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String base64File = Base64.getEncoder().encodeToString(fileBytes);
            
            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + documentType + "\r\n");
                
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"enrolleeId\"\r\n\r\n" + folderName + "\r\n");
                
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"fileName\"\r\n\r\n" + file.getName() + "\r\n");
                
                out.writeBytes("--" + boundary + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"fileBase64\"\r\n\r\n" + base64File + "\r\n");
                
                if (oldFileLink != null && !oldFileLink.isEmpty()) {
                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes("Content-Disposition: form-data; name=\"oldFileLink\"\r\n\r\n" + oldFileLink + "\r\n");
                }
                
                out.writeBytes("--" + boundary + "--\r\n");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return response.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
  
    public static String uploadToDrive(File file, String documentType, String folderName) {
        return uploadToDrive(file, documentType, folderName, null);
    }
}