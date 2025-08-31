package com.example.astrafarma.SupabaseUpload.domain;

import com.example.astrafarma.SupabaseUpload.dto.UploadResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_API_KEY}")
    private String supabaseApiKey;

    @Value("${SUPABASE_BUCKET_PRODUCT}")
    private String bucketProduct;

    @Value("${SUPABASE_BUCKET_OFFER}")
    private String bucketOffer;

    // Subir imagen, retorna URL pública
    public UploadResponseDTO uploadImage(MultipartFile file, boolean isProduct) throws Exception {
        String bucket = isProduct ? bucketProduct : bucketOffer;
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;
        URL url = new URL(uploadUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + supabaseApiKey);
        conn.setRequestProperty("Content-Type", file.getContentType());
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(file.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
            return new UploadResponseDTO(publicUrl, fileName);
        } else {
            throw new RuntimeException("Error uploading to Supabase: " + responseCode);
        }
    }

    public void deleteImage(String imageUrl, boolean isProduct) throws Exception {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        String bucket = isProduct ? bucketProduct : bucketOffer;
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        URL url = new URL(deleteUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", "Bearer " + supabaseApiKey);
        int responseCode = conn.getResponseCode();
        if (responseCode != 200 && responseCode != 204) {
            throw new RuntimeException("Error deleting from Supabase: " + responseCode);
        }
    }
}