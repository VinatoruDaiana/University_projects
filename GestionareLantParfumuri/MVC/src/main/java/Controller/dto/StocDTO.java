package Controller.dto;

import Model.Stoc;

public class StocDTO {
    private final int id_stoc;
    private final int id_parfum;
    private final int id_parfumerie;
    private final int cantitate;
    private final boolean disponibilitate;

    public StocDTO(int id_stoc, int id_parfum, int id_parfumerie, int cantitate, boolean disponibilitate) {
        this.id_stoc = id_stoc;
        this.id_parfum = id_parfum;
        this.id_parfumerie = id_parfumerie;
        this.cantitate = cantitate;
        this.disponibilitate = disponibilitate;
    }

    public int getStoc_id() {
        return id_stoc;
    }

    public int getId_parfum() {
        return id_parfum;
    }

    public int getId_parfumerie() {
        return id_parfumerie;
    }

    public int getCantitate() {
        return cantitate;
    }

    public boolean isDisponibilitate() {
        return disponibilitate;
    }

    public Stoc toEntity() {
        Stoc stoc = new Stoc();
        stoc.setId_stoc(id_stoc);
        stoc.setId_parfum(id_parfum);
        stoc.setId_parfumerie(id_parfumerie);
        stoc.setCantitate(cantitate);
        stoc.setDisponibilitate(disponibilitate);
        return stoc;
    }

    @Override
    public String toString() {
        return "StocDTO{" +
                "id_stoc=" + id_stoc +
                ", id_parfum=" + id_parfum +
                ", id_parfumerie=" + id_parfumerie +
                ", cantitate=" + cantitate +
                ", disponibilitate=" + disponibilitate +
                '}';
    }
}