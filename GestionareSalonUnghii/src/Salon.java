class Salon {
    private int id;
    private String numeSalon;
    private int idLocatie;

    public Salon(int id, String numeSalon, int idLocatie) {
        this.id = id;
        this.numeSalon = numeSalon;
        this.idLocatie = idLocatie;
    }

    public int getId() {
        return id;
    }

    public String getNumeSalon() {
        return numeSalon;
    }

    public int getIdLocatie() {
        return idLocatie;
    }

    @Override
    public String toString() {
        return numeSalon;  // Afișează doar numele salonului în JComboBox
    }
}