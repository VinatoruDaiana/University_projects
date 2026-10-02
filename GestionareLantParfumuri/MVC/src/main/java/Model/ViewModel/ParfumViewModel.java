package Model.ViewModel;

import Controller.dto.ParfumDTO;
import Controller.dto.ParfumMapper;
import Model.Parfum;
import Model.Observable;
import Model.Repository.ParfumRepository;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class ParfumViewModel extends Observable {
    private final ParfumRepository repository;
    private List<ParfumDTO> currentParfumuri;

    public final TextField idParfumerieField = new TextField();
    public final Label idParfumerieLabel = new Label("ID Parfumerie:");


    public ParfumViewModel(ParfumRepository repository) {
        this.repository = repository;
    }

    public void loadParfumuri() {
        List<Parfum> parfumuri = repository.getAll();

        this.currentParfumuri = parfumuri.stream()
                .map(ParfumMapper::toDTO)
                .sorted((p1, p2) -> p1.getNume().compareToIgnoreCase(p2.getNume()))
                .collect(Collectors.toList());

        notifyObservers();
    }


    public List<ParfumDTO> getCurrentParfumuri() {
        return currentParfumuri;
    }

    public void addParfum(ParfumDTO dto) {
        repository.insert(ParfumMapper.toEntity(dto));
        loadParfumuri();
    }

    public void updateParfum(ParfumDTO dto) {
        repository.update(ParfumMapper.toEntity(dto));
        loadParfumuri();
    }

    public void deleteParfum(ParfumDTO dto) {
        repository.deleteById(ParfumMapper.toEntity(dto).getId_parfum());
        loadParfumuri();
    }

    public List<String> filtreazaParfumuri(int idParfumerie, String producator) {
        return repository.getParfumuriFiltrate(idParfumerie, producator);
    }


    public List<String> filtreazaParfumuriCuDisponibilitate(int idParfumerie, String producator, boolean doarDisponibile) {
        List<String> toate = repository.getParfumuriFiltrate(idParfumerie, producator);

        if (!doarDisponibile) return toate;

        return toate.stream()
                .filter(r -> {
                    try {
                        String[] tokens = r.split("\\|");
                        int parfumId = Integer.parseInt(tokens[0].replaceAll("[^0-9]", ""));
                        return repository.esteDisponibil(parfumId, idParfumerie);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public boolean verificaDisponibilitate(int idParfum, int idParfumerie) {
        return repository.esteDisponibil(idParfum, idParfumerie);
    }

    public String getImagePathForParfum(ParfumDTO parfum) {

        return switch (parfum.getNume().toLowerCase()) {
            case "armani si" -> "/Imagini/armani_si.jpeg";
            case "chanel no 5" -> "/Imagini/chanel_no_5.jpg";
            case "dior sauvage" -> "/Imagini/dior_sauvage.jpg";
            case "gucci bloom" -> "/Imagini/gucci_bloom.png";
            case "versace eros" -> "/Imagini/versace_eros.jpg";
            default -> null;
        };
    }


    public List<String> cautaParfumCuParfumerii(String nume) {
        List<Parfum> toateParfumurile = repository.getAll();

        return toateParfumurile.stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .flatMap(p -> repository.getParfumeriiCuParfumDisponibil(p.getId_parfum()).stream()
                        .map(parfumerie -> "Parfum: " + p.getNume() + " | Parfumerie: " + parfumerie.getNume()))
                .collect(Collectors.toList());

    }

    public List<ParfumDTO> getParfumuriEpuizateDTO(int idParfumerie) {
        List<String> bruteList = repository.getParfumuriEpuizate(idParfumerie);
        List<ParfumDTO> dtoList = new ArrayList<>();

        for (String line : bruteList) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String nume = parts[1].trim();
                    String producator = parts[2].trim();
                    String descriere = parts[3].trim();
                    dtoList.add(new ParfumDTO(id, nume, producator, descriere));
                } catch (NumberFormatException e) {
                    // opțional: logare
                }
            }
        }

        return dtoList;
    }



}
