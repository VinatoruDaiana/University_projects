package Model.ViewModel;

import Controller.dto.StocDTO;
import Controller.dto.StocMapper;
import Model.Observable;
import Model.Stoc;
import Model.Repository.StocRepository;
import java.util.stream.Collectors;
import java.util.List;

public class StocViewModel extends Observable {
    private final StocRepository repository;
    private List<StocDTO> currentStocuri;

    public StocViewModel(StocRepository repository) {
        this.repository = repository;
    }

    public void loadStocuri() {
        List<Stoc> stocuri = repository.getTableContent();
        currentStocuri = stocuri.stream()
                .map(StocMapper::toDTO)
                .collect(Collectors.toList());
        notifyObservers();
    }

    public List<StocDTO> getCurrentStocuri() {
        return currentStocuri;
    }

    public void addStoc(StocDTO dto) {
        repository.insert(StocMapper.toEntity(dto));
        loadStocuri();
    }

    public void updateStoc(StocDTO dto) {
        repository.update(StocMapper.toEntity(dto));
        loadStocuri();
    }

    public void deleteStoc(StocDTO dto) {
        repository.deleteById(dto.getStoc_id());
        loadStocuri();
    }
}

