package com.example.astrafarma.SupabaseUpload.controller;


import com.example.astrafarma.SupabaseUpload.domain.ProductExcelExportService;
import com.example.astrafarma.SupabaseUpload.domain.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/excel/products")
public class SupabaseStorageController {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageController.class);

    @Autowired
    private ProductExcelExportService productExcelExportService;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/export")
    public ResponseEntity<?> exportProductsToExcelAndUpload() {
        try {
            logger.info("Generando Excel de productos...");
            byte[] excelBytes = productExcelExportService.exportProductsToExcel();
            logger.info("Excel generado, tamaño: " + excelBytes.length);

            String excelFilename = "productos_categorizados.xlsx";
            logger.info("Subiendo Excel a Supabase con nombre: " + excelFilename);
            String publicUrl = supabaseStorageService.uploadProductExcel(excelBytes, excelFilename);

            logger.info("Excel subido correctamente, URL: " + publicUrl);
            return ResponseEntity.ok().body("{\"message\": \"Exportación exitosa\", \"url\": \"" + publicUrl + "\"}");
        } catch (Exception e) {
            logger.error("Error al exportar productos: ", e);
            return ResponseEntity.status(500).body("{\"error\": \"Error al exportar productos: " + e.getMessage() + "\"}");
        }
    }
}