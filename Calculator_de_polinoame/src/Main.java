// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Polinom p1 = new Polinom();

        p1.polinom.put(5, 1.0);
        p1.polinom.put(3, 3.0);
        p1.polinom.put(1, 2.0);
        p1.polinom.put(0, -6.0);

        Polinom p2 = new Polinom();
        p2.polinom.put(4, 5.0);
        p2.polinom.put(1, 7.0);
        p2.polinom.put(0, 2.0);

        Polinom rezultat = Operatii.adunare(p1,p2);

        System.out.println("Rezultat adunare: " + rezultat);
    }
}