package Model;

public class Parfum {
    private int id_parfum;
    private String nume;
    private String producator;
    private String descriere;

    public Parfum() {}

    public Parfum(String nume, String producator, String descriere) {
        this.nume = nume;
        this.producator = producator;
        this.descriere = descriere;
    }

    public Parfum(int id_parfum, String nume, String producator, String descriere) {
        this.id_parfum = id_parfum;
        this.nume = nume;
        this.producator = producator;
        this.descriere = descriere;
    }


    public int getId_parfum() {
        return id_parfum;
    }

    public void setId_parfum(int id_parfum) {
        this.id_parfum = id_parfum;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getProducator() {
        return producator;
    }

    public void setProducator(String producator) {
        this.producator = producator;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    @Override
    public String toString() {
        return "Parfum{" +
                "id_parfum=" + id_parfum +
                ", nume='" + nume + '\'' +
                ", producator='" + producator + '\'' +
                ", descriere='" + descriere + '\'' +
                '}';
    }
}
