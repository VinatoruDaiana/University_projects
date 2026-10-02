package Model;

public class Stoc {
    private int id_stoc;
    private int id_parfumerie;
    private int id_parfum;
    private int cantitate;
    private boolean disponibilitate;

    public Stoc(int id_stoc, int id_parfumerie, int id_parfum, int cantitate, boolean disponibilitate) {
        this.id_stoc = id_stoc;
        this.id_parfumerie = id_parfumerie;
        this.id_parfum = id_parfum;
        this.cantitate = cantitate;
        this.disponibilitate = disponibilitate;
    }

    public int getIdStoc() { return id_stoc; }
    public int getIdParfumerie() { return id_parfumerie; }
    public int getIdParfum() { return id_parfum; }
    public int getCantitate() { return cantitate; }
    public boolean isDisponibilitate() { return disponibilitate; }

    public void setCantitate(int cantitate) { this.cantitate = cantitate; }
    public void setDisponibilitate(boolean disponibilitate) { this.disponibilitate = disponibilitate; }
}
