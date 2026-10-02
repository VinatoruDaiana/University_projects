package Controller.dto;

import Model.Stoc;

public class StocMapper {

    public static StocDTO toDTO(Stoc stoc) {
        return new StocDTO(
                stoc.getId_stoc(),
                stoc.getId_parfum(),
                stoc.getId_parfumerie(),
                stoc.getCantitate(),
                stoc.isDisponibilitate()
        );
    }

    public static Stoc toEntity(StocDTO dto) {
        return new Stoc(
                dto.getStoc_id(),
                dto.getId_parfum(),
                dto.getId_parfumerie(),
                dto.getCantitate(),
                dto.isDisponibilitate()
        );
    }
}