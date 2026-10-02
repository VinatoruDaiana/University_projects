package Model.ViewModel;

import Model.Repository.StatisticiRepository;
import java.util.Map;

public class StatisticiViewModel {
    private final StatisticiRepository repository;

    public StatisticiViewModel(StatisticiRepository repository) {
        this.repository = repository;
    }

    public Map<String, Integer> genereazaStatisticiPeDescriere() {
        return repository.getNrParfumuriPeDescriere();
    }
}