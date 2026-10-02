package Model;

public class Stoc {
    private int id_stoc;
    private int id_parfum;
    private int id_parfumerie;
    private int cantitate;
    private boolean disponibilitate;

    public Stoc() {}

    public Stoc(int id_parfum, int id_parfumerie, int cantitate, boolean disponibilitate) {
        this.id_parfum = id_parfum;
        this.id_parfumerie = id_parfumerie;
        this.cantitate = cantitate;
        this.disponibilitate = disponibilitate;
    }

    public Stoc(int id_stoc, int id_parfum, int id_parfumerie, int cantitate, boolean disponibilitate) {
        this.id_stoc = id_stoc;
        this.id_parfum = id_parfum;
        this.id_parfumerie = id_parfumerie;
        this.cantitate = cantitate;
        this.disponibilitate = disponibilitate;
    }

    public int getId_stoc() {
        return id_stoc;
    }

    public void setId_stoc(int id_stoc) {
        this.id_stoc = id_stoc;
    }

    public int getId_parfum() {
        return id_parfum;
    }

    public void setId_parfum(int id_parfum) {
        this.id_parfum = id_parfum;
    }

    public int getId_parfumerie() {
        return id_parfumerie;
    }

    public void setId_parfumerie(int id_parfumerie) {
        this.id_parfumerie = id_parfumerie;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }

    public boolean isDisponibilitate() {
        return disponibilitate;
    }

    public void setDisponibilitate(boolean disponibilitate) {
        this.disponibilitate = disponibilitate;
    }

    @Override
    public String toString() {
        return "Stoc{" +
                "id_stoc=" + id_stoc +
                ", id_parfum=" + id_parfum +
                ", id_parfumerie=" + id_parfumerie +
                ", cantitate=" + cantitate +
                ", disponibilitate=" + disponibilitate +
                '}';
    }
}