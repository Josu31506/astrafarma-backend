package com.example.astrafarma.SupabaseUpload.domain;

import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductExcelExportService {

    @Autowired
    private ProductRepository productRepository;

    public byte[] exportProductsToExcel() throws IOException {
        List<Product> products = productRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Productos");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("NOMBRE PRODUCTO");
            headerRow.createCell(1).setCellValue("DESCRIPCION");
            headerRow.createCell(2).setCellValue("PRECIO DE VENTA");
            headerRow.createCell(3).setCellValue("CATEGORIA COMERCIAL");
            headerRow.createCell(4).setCellValue("URL DE IMAGEN");

            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getName());
                row.createCell(1).setCellValue(product.getDescription() == null ? "." : product.getDescription());
                row.createCell(2).setCellValue(product.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                row.createCell(3).setCellValue(product.getCategory() == null ? "" : product.getCategory().name());
                row.createCell(4).setCellValue(product.getImageUrl() == null ? "" : product.getImageUrl());
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }
}