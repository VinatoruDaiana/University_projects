import java.util.Iterator;
import java.util.Map;

public class Operatii {

    public static Polinom adunare(Polinom p1,Polinom p2) {

        Polinom rezultat = new Polinom();

    for(Map.Entry<Integer,Double> intrare: p1.polinom.entrySet()){
        int putere=intrare.getKey();
        double coeficient=intrare.getValue();
        rezultat.polinom.put(putere,coeficient);
    }
        for(Map.Entry<Integer,Double> intrare: p2.polinom.entrySet()){
            int putere=intrare.getKey();
            double coeficient=intrare.getValue();

            rezultat.polinom.put(putere,rezultat.polinom.getOrDefault(putere,0.0)+coeficient);
        }
        rezultat.polinom.entrySet().removeIf(entry -> entry.getValue() == 0.0);
        return rezultat;
    }


    public static Polinom scadere(Polinom p1,Polinom p2) {

        Polinom rezultat = new Polinom();

        for (Map.Entry<Integer, Double> intrare : p1.polinom.entrySet()) {
            int putere = intrare.getKey();
            double coeficient = intrare.getValue();
            rezultat.polinom.put(putere, coeficient);
        }


        for (Map.Entry<Integer, Double> intrare : p2.polinom.entrySet()) {
            int putere = intrare.getKey();
            //neg coeficientul pt scadere
            double coeficient = -intrare.getValue();
            rezultat.polinom.put(putere, rezultat.polinom.getOrDefault(putere, 0.0) + coeficient);
        }

        // elimin termenii cu coeficienț zero
        rezultat.polinom.entrySet().removeIf(entry -> entry.getValue() == 0.0);

        return rezultat;
    }

     public static Polinom inmultire(Polinom p1,Polinom p2){

        Polinom rezultat=new Polinom();

     for(Map.Entry<Integer,Double> intrare1 : p1.polinom.entrySet()) {
         int putere1 = intrare1.getKey();
         double coeficient1 = intrare1.getValue();

         for (Map.Entry<Integer, Double> intrare2 : p2.polinom.entrySet()) {
             int putere2 = intrare2.getKey();
             double coeficient2 = intrare2.getValue();

             int putereRezultat=putere1+putere2;
             double coeficientRezultat=coeficient1*coeficient2;

             rezultat.polinom.put(putereRezultat, rezultat.polinom.getOrDefault(putereRezultat, 0.0) + coeficientRezultat);
         }

     }
         rezultat.polinom.entrySet().removeIf(entry -> entry.getValue() == 0.0);
    return rezultat;
     }

     public static Polinom derivare(Polinom p1){

        Polinom rezultat=new Polinom();

        for(Map.Entry<Integer,Double> intrare: p1.polinom.entrySet()){
            int putere= intrare.getKey();
            double coeficent= intrare.getValue();

            //pt toti termenii am coef*putere
            //daca putere<0, am putere-1
            //daca puterea=0, termenul dispare
            if(putere>0){
                double coeficientRezultat=coeficent*putere;
                int putereRezultat=putere-1;
                rezultat.polinom.put(putereRezultat,coeficientRezultat);
            }
        }
         rezultat.polinom.entrySet().removeIf(entry -> entry.getValue() == 0.0);
        return rezultat;
     }

     public static Polinom integrare(Polinom p1){

         Polinom rezultat = new Polinom();

         for (Integer putere : p1.polinom.keySet()) {
             double coeficient = p1.polinom.get(putere);

             int putereRezultat = putere + 1;
             double coeficientRezultat = coeficient / putereRezultat;

             // afisez doar 2 zecimale dupa virgula
             String coeficientNou = String.format("%.2f", coeficientRezultat);
             // inlocuiesc virgula cu punct
             coeficientNou = coeficientNou.replace(',', '.');
             // convertesc sirul inapoi la double
             coeficientRezultat = Double.parseDouble(coeficientNou);


             /*String coeficientFormatat = String.format("%.2f", coeficientRezultat);
             coeficientRezultat = Double.parseDouble(coeficientFormatat);
             Asa nu functioneaza,imi da exceptie ???????
              */
             rezultat.polinom.put(putereRezultat, coeficientRezultat);
         }

         return rezultat;
     }
}





