package com.example.astrafarma.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCategory {

    CUIDADO_PERSONAL_HIGIENE,
    OTROS,
    VITAMINAS_SUPLEMENTOS_NUTRICIONALES,
    RESPIRATORIOS_EXPECTORANTES,
    ANTIBIOTICOS_ANTIVIRALES,
    DERMATOLOGICOS_TRATAMIENTOS_CUTANEOS,
    ANALGESICOS_ANTINFLAMATORIOS,
    MATERIAL_MEDICO_EQUIPOS,
    MEDICINA_NATURAL_HIDRATACION,
    PEDIATRICOS_LACTANCIA,
    GASTROINTESTINALES_DIGESTIVOS,
    GINECOLOGICOS_UROLOGICOS,
    CARDIOVASCULARES_ANTIDIABETICOS,
    OFTALMOLOGICOS,
    ANTIHISTAMINICOS_ANTIALERGICOS,
    NEUROLOGICOS_PSIQUIATRICOS;

    @JsonCreator
    public static ProductCategory fromString(String value) {
        return ProductCategory.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
