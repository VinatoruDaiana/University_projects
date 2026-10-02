package Model;

public class Parfum {
    private int id_parfum;
    private String nume;
    private String producator;
    private String descriere;
    private String imaginePath;

    public Parfum(int id_parfum, String nume, String producator, String descriere) {
        this.id_parfum = id_parfum;
        this.nume = nume;
        this.producator = producator;
        this.descriere = descriere;


    }
    public String getImaginePath() {
        return imaginePath;
    }
    public int getIdParfum() { return id_parfum; }
    public String getNume() { return nume; }
    public String getProducator() { return producator; }
    public String getDescriere() { return descriere; }


    public void setNume(String nume) { this.nume = nume; }
    public void setProducator(String producator) { this.producator = producator; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public void setImaginePath(String imaginePath) {
        this.imaginePath = imaginePath;
    }

    @Override
    public String toString() {
        return "Parfum: " + nume + ", Producător: " + producator + ", Descriere: " + descriere;
    }

}
