package Controller;


import Controller.dto.ParfumDTO;
import Model.Parfum;
import Model.Stoc;
import Model.Repository.StocRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
public class Export {
    public static void exportParfumuriEpuizateCsv(List<ParfumDTO> parfumuri, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("ID,Nume,Producator,Descriere\n");
            for (ParfumDTO p : parfumuri) {
                writer.write(p.getParfum_id() + "," +
                        p.getNume() + "," +
                        p.getProducator() + "," +
                        p.getDescriere() + "\n");
            }
            System.out.println("CSV salvat cu succes la: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportParfumuriEpuizateDoc(List<ParfumDTO> parfumuri, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("Parfumuri Epuizate:\n\n");
            for (ParfumDTO p : parfumuri) {
                writer.write("ID: " + p.getParfum_id() + "\n");
                writer.write("Nume: " + p.getNume() + "\n");
                writer.write("Producător: " + p.getProducator() + "\n");
                writer.write("Descriere: " + p.getDescriere() + "\n");
                writer.write("--------------\n");
            }
            System.out.println("Document salvat cu succes la: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



