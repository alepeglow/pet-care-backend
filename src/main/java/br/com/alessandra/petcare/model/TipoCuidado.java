package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Locale;

public enum TipoCuidado {
    BANHO,
    TOSA,
    VACINA,
    CONSULTA,
    MEDICACAO,
    VERMIFUGO,
    OUTRO;

    @JsonCreator
    public static TipoCuidado from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tipo de cuidado inválido.");
        }

        String v = normalize(value);

        // Aliases (ajuda muito o front)
        switch (v) {
            // Medicação
            case "MEDICAMENTO":
            case "REMEDIO":
            case "REMÉDIO":
                return MEDICACAO;

            // Vermífugo
            case "VERMIFUGACAO":
            case "VERMIFUGAÇÃO":
            case "VERMIFUGA":
                return VERMIFUGO;

            // Consulta
            case "VETERINARIO":
            case "VETERINARIA":
            case "CONSULTA_VET":
            case "CONSULTA_VETERINARIA":
            case "CONSULTA_VETERINÁRIO":
                return CONSULTA;

            // Banho/Tosa (quando o front manda combinado)
            case "BANHO_E_TOSA":
            case "BANHO_TOSA":
            case "BANHO+TOSA":
                return OUTRO; // ou BANHO (se tu preferir “puxar” pra um)

            // Tosa higienica (variações)
            case "TOSA_HIGIENICA":
            case "TOSA_HIGIENICA_COMPLETA":
                return TOSA;

            default:
                try {
                    return TipoCuidado.valueOf(v);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Tipo de cuidado inválido: " + value);
                }
        }
    }

    private static String normalize(String value) {
        String v = value.trim();

        // remove acentos
        v = Normalizer.normalize(v, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        // padroniza separadores e caixa
        v = v.toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_')
                .replaceAll("_+", "_");

        return v;
    }

    @JsonValue
    public String toJson() {
        return name(); // "BANHO", "TOSA" etc
    }
}
