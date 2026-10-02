package Controller.dto;

import Model.Parfum;

public class ParfumMapper {

    public static ParfumDTO toDTO(Parfum parfum) {
        return new ParfumDTO(
                parfum.getId_parfum(),
                parfum.getNume(),
                parfum.getProducator(),
                parfum.getDescriere()

        );
    }

    public static Parfum toEntity(ParfumDTO dto) {
        return new Parfum(
                dto.getParfum_id(),
                dto.getNume(),
                dto.getProducator(),
                dto.getDescriere()

        );
    }
}