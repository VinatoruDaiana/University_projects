package Model.ViewModel;

import Controller.dto.ParfumerieDTO;
import Controller.dto.ParfumerieMapper;
import Model.Observable;
import Model.Parfumerie;
import Model.Repository.ParfumerieRepository;
import java.util.stream.Collectors;
import java.util.List;

public class ParfumerieViewModel extends Observable {
    private final ParfumerieRepository repository;
    private List<ParfumerieDTO> currentParfumerii;

    public ParfumerieViewModel(ParfumerieRepository repository) {
        this.repository = repository;
    }

    public void loadParfumerii() {
        List<Parfumerie> parfumerii = repository.getTableContent();
        currentParfumerii = parfumerii.stream()
                .map(ParfumerieMapper::toDTO)
                .collect(Collectors.toList());
        notifyObservers();
    }

    public List<ParfumerieDTO> getCurrentParfumerii() {
        return currentParfumerii;
    }

    public void addParfumerie(ParfumerieDTO dto) {
        repository.insert(ParfumerieMapper.toEntity(dto));
        loadParfumerii();
    }

    public void updateParfumerie(ParfumerieDTO dto) {
        repository.update(ParfumerieMapper.toEntity(dto));
        loadParfumerii();
    }

    public void deleteParfumerie(ParfumerieDTO dto) {
        repository.deleteById(dto.getParfumerie_id());
        loadParfumerii();
    }
}
