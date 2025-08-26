package com.example.astrafarma.config;

import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.domain.ProductCategory;
import com.example.astrafarma.Product.repository.ProductRepository;
import com.example.astrafarma.User.domain.User;
import com.example.astrafarma.User.domain.UserGender;
import com.example.astrafarma.User.domain.UserRole;
import com.example.astrafarma.User.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ProductRepository productRepository;

    @Value("${supabase.excel.url}")
    private String excelUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.fullname:Administrador}")
    private String adminFullName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    private final List<String> descriptionPlaceholders = Arrays.asList(
            "Producto farmacéutico de alta calidad para el cuidado de la salud",
            "Medicamento eficaz recomendado por especialistas médicos",
            "Tratamiento confiable con ingredientes activos de primera línea",
            "Producto terapéutico con excelente perfil de seguridad",
            "Medicación especializada para tratamiento específico",
            "Fórmula avanzada desarrollada con tecnología farmacéutica moderna",
            "Producto sanitario esencial para el bienestar general",
            "Medicamento de uso común en la práctica médica diaria"
    );

    private final List<String> imagePlaceholders = Arrays.asList(
            "https://via.placeholder.com/300x300/0066cc/ffffff?text=Medicamento",
            "https://via.placeholder.com/300x300/00aa44/ffffff?text=Farmaco",
            "https://via.placeholder.com/300x300/cc6600/ffffff?text=Producto",
            "https://via.placeholder.com/300x300/9933cc/ffffff?text=Medicina",
            "https://via.placeholder.com/300x300/cc0066/ffffff?text=Tratamiento"
    );

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando carga de datos desde Excel...");

        if (productRepository.count() > 0) {
            logger.info("Ya existen productos en la base de datos. Saltando inicialización.");
            return;
        }

        try {
            loadProductsFromExcel();
            logger.info("Carga de datos completada exitosamente");
        } catch (Exception e) {
            logger.error("Error al cargar datos desde Excel: {}", e.getMessage(), e);
        }

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFullName(adminFullName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhoneNumber("0000000000");
            admin.setGender(UserGender.OTHER);
            admin.setBirthday(java.time.LocalDate.of(1980, 1, 1));
            admin.setUserRole(UserRole.ADMIN);
            admin.setVerified(true);
            admin.setVerificationToken(null);

            userRepository.save(admin);
            logger.info("Usuario admin creado con email {}", adminEmail);
        } else {
            logger.info("Usuario admin ya existe (email {})", adminEmail);
        }
    }

    private void loadProductsFromExcel() throws IOException {
        logger.info("Descargando archivo Excel desde: {}", excelUrl);

        byte[] excelData = restTemplate.getForObject(excelUrl, byte[].class);
        if (excelData == null) {
            throw new RuntimeException("No se pudo descargar el archivo Excel");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet sheet = workbook.getSheetAt(0);

            List<Product> products = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("El archivo Excel no tiene fila de cabeceras");
            }

            int nameColumnIndex = findColumnIndex(headerRow, "NOMBRE PRODUCTO*");
            int priceColumnIndex = findColumnIndex(headerRow, "PRECIO DE VENTA*");
            int categoryColumnIndex = findColumnIndex(headerRow, "CATEGORIA COMERCIAL");

            if (nameColumnIndex == -1 || priceColumnIndex == -1 || categoryColumnIndex == -1) {
                throw new RuntimeException("No se encontraron todas las columnas requeridas en el Excel");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Product product = createProductFromRow(row, nameColumnIndex, priceColumnIndex, categoryColumnIndex);
                    if (product != null) {
                        products.add(product);
                    }
                } catch (Exception e) {
                    logger.warn("Error procesando fila {}: {}", i + 1, e.getMessage());
                }
            }

            if (!products.isEmpty()) {
                productRepository.saveAll(products);
                logger.info("Se guardaron {} productos en la base de datos", products.size());
            } else {
                logger.warn("No se encontraron productos válidos para guardar");
            }
        }
    }

    private int findColumnIndex(Row headerRow, String columnName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (cellValue.equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Product createProductFromRow(Row row, int nameIndex, int priceIndex, int categoryIndex) {
        String name = getCellValueAsString(row.getCell(nameIndex));
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        BigDecimal price = getCellValueAsBigDecimal(row.getCell(priceIndex));
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Precio inválido para producto: {}", name);
            return null;
        }

        String categoryStr = getCellValueAsString(row.getCell(categoryIndex));
        ProductCategory category = mapToProductCategory(categoryStr);

        Product product = new Product();
        product.setName(name.trim());
        product.setPrice(price);
        product.setCategory(category);

        product.setDescription(getRandomDescription());
        product.setImageUrl(getRandomImageUrl());

        logger.debug("Creado producto: {} - ${} - {}", name, price, category);

        return product;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    strValue = strValue.replaceAll("[^\\d.,]", "");
                    strValue = strValue.replace(",", ".");
                    return new BigDecimal(strValue);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            logger.warn("No se pudo convertir a número: {}", cell.toString());
            return null;
        }
    }

    private ProductCategory mapToProductCategory(String categoryStr) {
        if (categoryStr == null || categoryStr.trim().isEmpty()) {
            return ProductCategory.OTROS;
        }

        String category = categoryStr.trim().toLowerCase();

        if (category.contains("cuidado personal") || category.contains("higiene")) {
            return ProductCategory.CUIDADO_PERSONAL_HIGIENE;
        } else if (category.contains("vitamina") || category.contains("suplemento") || category.contains("nutricional")) {
            return ProductCategory.VITAMINAS_SUPLEMENTOS_NUTRICIONALES;
        } else if (category.contains("respiratorio") || category.contains("expectorante") || category.contains("tos")) {
            return ProductCategory.RESPIRATORIOS_EXPECTORANTES;
        } else if (category.contains("antibiotico") || category.contains("antiviral") || category.contains("infeccion")) {
            return ProductCategory.ANTIBIOTICOS_ANTIVIRALES;
        } else if (category.contains("dermatologico") || category.contains("piel") || category.contains("cutaneo")) {
            return ProductCategory.DERMATOLOGICOS_TRATAMIENTOS_CUTANEOS;
        } else if (category.contains("analgesico") || category.contains("antinflamatorio") || category.contains("dolor")) {
            return ProductCategory.ANALGESICOS_ANTINFLAMATORIOS;
        } else if (category.contains("material medico") || category.contains("equipo") || category.contains("dispositivo")) {
            return ProductCategory.MATERIAL_MEDICO_EQUIPOS;
        } else if (category.contains("medicina natural") || category.contains("hidratacion") || category.contains("natural")) {
            return ProductCategory.MEDICINA_NATURAL_HIDRATACION;
        } else if (category.contains("pediatrico") || category.contains("lactancia") || category.contains("bebe") || category.contains("niño")) {
            return ProductCategory.PEDIATRICOS_LACTANCIA;
        } else if (category.contains("gastrointestinal") || category.contains("digestivo") || category.contains("estomago")) {
            return ProductCategory.GASTROINTESTINALES_DIGESTIVOS;
        } else if (category.contains("ginecologico") || category.contains("urologico") || category.contains("genital")) {
            return ProductCategory.GINECOLOGICOS_UROLOGICOS;
        } else if (category.contains("cardiovascular") || category.contains("antidiabetico") || category.contains("corazon") || category.contains("diabetes")) {
            return ProductCategory.CARDIOVASCULARES_ANTIDIABETICOS;
        } else if (category.contains("oftalmologico") || category.contains("ojo") || category.contains("vision")) {
            return ProductCategory.OFTALMOLOGICOS;
        } else if (category.contains("antihistaminico") || category.contains("antialergico") || category.contains("alergia")) {
            return ProductCategory.ANTIHISTAMINICOS_ANTIALERGICOS;
        } else if (category.contains("neurologico") || category.contains("psiquiatrico") || category.contains("nervioso")) {
            return ProductCategory.NEUROLOGICOS_PSIQUIATRICOS;
        } else {
            return ProductCategory.OTROS;
        }
    }

    private String getRandomDescription() {
        return descriptionPlaceholders.get(random.nextInt(descriptionPlaceholders.size()));
    }

    private String getRandomImageUrl() {
        return imagePlaceholders.get(random.nextInt(imagePlaceholders.size()));
    }
}