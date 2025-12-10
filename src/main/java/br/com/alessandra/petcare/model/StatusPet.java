package br.com.alessandra.petcare.model;

public enum StatusPet {
    DISPONIVEL,
    ADOTADO;

    public boolean isBlank() {
        return false;
    }
}
