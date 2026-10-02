package ViewModel;

import Model.Stoc;
import Model.Repository.StocRepository;
import ViewModel.Commands.StocCommands;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StocVM {
    private final StocRepository repo;

    public final StringProperty idParfum = new SimpleStringProperty();
    public final StringProperty idParfumerie = new SimpleStringProperty();
    public final StringProperty cantitate = new SimpleStringProperty();
    public final StringProperty disponibilitate = new SimpleStringProperty();
    public final StringProperty mesaj = new SimpleStringProperty();

    public final StringProperty searchParfumId = new SimpleStringProperty();
    public final StringProperty searchParfumerieId = new SimpleStringProperty();

    public final ObservableList<List<String>> stocuri = FXCollections.observableArrayList();

    public final StocCommands addCommand;
    public final StocCommands updateCommand;
    public final StocCommands deleteCommand;
    public final StocCommands searchCommand;
    public final StocCommands filtrareCommand;

    public StocVM() {
        this.repo = new StocRepository();

        this.addCommand = new StocCommands(this::addStoc, this::isValid);
        this.updateCommand = new StocCommands(this::updateStoc, () -> isValid() && hasSelection());
        this.deleteCommand = new StocCommands(this::deleteStoc, this::hasSelection);
        this.searchCommand = new StocCommands(this::searchStoc, this::hasSearchCriteria);
        this.filtrareCommand = new StocCommands(this::filtrareEpuizate, () -> true);

        loadStocuri();
    }

    public void loadStocuri() {
        stocuri.setAll(repo.getAll().stream()
                .map(s -> List.of(
                        String.valueOf(s.getIdStoc()),
                        String.valueOf(s.getIdParfumerie()),
                        String.valueOf(s.getIdParfum()),
                        String.valueOf(s.getCantitate()),
                        String.valueOf(s.isDisponibilitate())))
                .collect(Collectors.toList()));
    }

    public void addStoc() {
        try {
            Stoc s = new Stoc(0,
                    Integer.parseInt(idParfumerie.get()),
                    Integer.parseInt(idParfum.get()),
                    Integer.parseInt(cantitate.get()),
                    Boolean.parseBoolean(disponibilitate.get()));

            int id = repo.addStoc(s);
            if (id > 0) {
                mesaj.set("Stoc adăugat cu ID: " + id);
                clear();
                loadStocuri();
            } else {
                mesaj.set("Eroare la adăugare!");
            }
        } catch (Exception e) {
            mesaj.set("Date invalide!");
        }
    }

    public void updateStoc() {
        try {
            int id = Integer.parseInt(selectedStocId);
            repo.updateStoc(id,
                    Integer.parseInt(cantitate.get()),
                    Boolean.parseBoolean(disponibilitate.get()));
            mesaj.set("Stoc actualizat cu succes.");
            clear();
            loadStocuri();
        } catch (Exception e) {
            mesaj.set("Eroare la actualizare!");
        }
    }

    public void deleteStoc() {
        try {
            int idStoc = repo.getIdStoc(Integer.parseInt(idParfum.get()), Integer.parseInt(idParfumerie.get()));
            if (idStoc != -1) {
                repo.updateStoc(idStoc, 0, false);
                mesaj.set("Stoc marcat ca indisponibil.");
                clear();
                loadStocuri();
            } else {
                mesaj.set("Stocul nu a fost găsit!");
            }
        } catch (Exception e) {
            mesaj.set("Eroare la ștergere!");
        }
    }

    public void searchStoc() {
        try {
            int idStoc = repo.getIdStoc(Integer.parseInt(searchParfumId.get()), Integer.parseInt(searchParfumerieId.get()));
            Optional<Stoc> found = repo.getAll().stream()
                    .filter(s -> s.getIdStoc() == idStoc)
                    .findFirst();

            if (found.isPresent()) {
                Stoc s = found.get();
                idParfum.set(String.valueOf(s.getIdParfum()));
                idParfumerie.set(String.valueOf(s.getIdParfumerie()));
                cantitate.set(String.valueOf(s.getCantitate()));
                disponibilitate.set(String.valueOf(s.isDisponibilitate()));
                selectedStocId = String.valueOf(s.getIdStoc());
                mesaj.set("Stoc găsit cu ID: " + s.getIdStoc());
            } else {
                mesaj.set("Stocul nu a fost găsit!");
            }
        } catch (Exception e) {
            mesaj.set("Eroare la căutare!");
        }
    }

    public void filtrareEpuizate() {
        List<Stoc> lista = repo.getAll().stream()
                .filter(s -> s.getCantitate() == 0)
                .toList();

        stocuri.clear();
        for (Stoc s : lista) {
            stocuri.add(List.of(
                    String.valueOf(s.getIdStoc()),
                    String.valueOf(s.getIdParfumerie()),
                    String.valueOf(s.getIdParfum()),
                    String.valueOf(s.getCantitate()),
                    String.valueOf(s.isDisponibilitate())));
        }
        mesaj.set("Stocuri epuizate: " + lista.size());
    }

    public void selectStoc(List<String> data) {
        if (data == null || data.size() < 5) return;
        selectedStocId = data.get(0);
        idParfumerie.set(data.get(1));
        idParfum.set(data.get(2));
        cantitate.set(data.get(3));
        disponibilitate.set(data.get(4));
    }

    private boolean isValid() {
        return !idParfum.get().isBlank()
                && !idParfumerie.get().isBlank()
                && !cantitate.get().isBlank()
                && !disponibilitate.get().isBlank();
    }

    private boolean hasSelection() {
        return selectedStocId != null && !selectedStocId.isBlank();
    }

    private boolean hasSearchCriteria() {
        return searchParfumId.get() != null && !searchParfumId.get().isBlank()
                && searchParfumerieId.get() != null && !searchParfumerieId.get().isBlank();
    }

    private void clear() {
        selectedStocId = "";
        idParfum.set("");
        idParfumerie.set("");
        cantitate.set("");
        disponibilitate.set("");
    }

    private String selectedStocId = "";
}
