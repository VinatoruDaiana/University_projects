package Model;

public class Parfumerie {
    private int id_parfumerie;
    private String nume;
    private String adresa;
    private String telefon;

    public Parfumerie() {}

    public Parfumerie(String nume, String adresa, String telefon) {
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
    }

    public Parfumerie(int id_parfumerie, String nume, String adresa, String telefon) {
        this.id_parfumerie = id_parfumerie;
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
    }

    public int getId_parfumerie() {
        return id_parfumerie;
    }

    public void setId_parfumerie(int id_parfumerie) {
        this.id_parfumerie = id_parfumerie;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return "Parfumerie{" +
                "id_parfumerie=" + id_parfumerie +
                ", nume='" + nume + '\'' +
                ", adresa='" + adresa + '\'' +
                ", telefon='" + telefon + '\'' +
                '}';
    }
}
