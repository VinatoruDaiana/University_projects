public class User {
    private int id;
    private String nume;
    private String email;
    private String telefon;
    private String rol;
    private String parola;

    public User(int id, String nume, String email, String telefon, String rol, String parola) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.telefon = telefon;
        this.rol = rol;
        this.parola = parola;
    }

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getRol() {
        return rol;
    }

    public String getParola() {
        return parola;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + telefon + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
