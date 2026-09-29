import java.util.HashMap;
import java.util.Map;

public class Polinom {

    public Map<Integer,Double> polinom=new HashMap<>();

    public String toString(){

        StringBuilder rezultat=new StringBuilder();

        for(Map.Entry<Integer,Double> intrare : this.polinom.entrySet()){
            Integer putere=intrare.getKey();//puterea este cheia de tipul integer
            Double coeficient=intrare.getValue();//coeficientule este val de tip double

            //adaug + intre termeni in afara de primul termen din lista
            if(rezultat.length()>0 && coeficient>0){
                rezultat.append("+");
            }

            //convertesc la sir si elimin .0 de la finalul coef daca este cazul
            String coeficientSir=String.valueOf(coeficient);

            if(coeficientSir.endsWith(".0")){
                coeficientSir=coeficientSir.substring(0,coeficientSir.length()-2);
            }

            //construiesc sirul
            if(putere==0){
                rezultat.append(coeficientSir);
            }
            else
            if(putere==1){
                rezultat.append(coeficientSir).append("x");
            }
            else {
                rezultat.append(coeficientSir).append("x^").append(putere);
            }
        }
        //convertesc sirul rezultat la String si il returnez
        return rezultat.toString();
    }

    //functie pentru afisarea polinomului,afisez puterea si coeficinetul;
    public void afisarePolinom(){
        for(Integer putere: this.polinom.keySet()){
            double coeficientAfisare=this.polinom.get(putere);
            System.out.printf("Puterea %d si coeficientul %.2f%n",putere,coeficientAfisare);
        }
    }

}

