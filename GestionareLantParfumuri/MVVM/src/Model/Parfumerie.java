package Model;

public class Parfumerie {
    private int id_parfumerie;
    private String nume;
    private String adresa;
    private String telefon;

    public Parfumerie(int id_parfumerie, String nume, String adresa, String telefon) {
        this.id_parfumerie = id_parfumerie;
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
    }

    public int getIdParfumerie() { return id_parfumerie; }
    public String getNume() { return nume; }
    public String getAdresa() { return adresa; }
    public String getTelefon() { return telefon; }

    public void setNume(String nume) { this.nume = nume; }
    public void setAdresa(String adresa) { this.adresa = adresa; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    @Override
    public String toString() {
        return "Nume: " + nume + " | Adresa: " + adresa + " | Telefon: " + telefon;
    }

}
