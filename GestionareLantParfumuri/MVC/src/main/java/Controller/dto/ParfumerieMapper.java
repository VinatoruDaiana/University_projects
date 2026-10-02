package Controller.dto;

import Model.Parfumerie;

public class ParfumerieMapper {

    public static ParfumerieDTO toDTO(Parfumerie parfumerie) {
        return new ParfumerieDTO(
                parfumerie.getId_parfumerie(),
                parfumerie.getNume(),
                parfumerie.getAdresa(),
                parfumerie.getTelefon()
        );
    }

    public static Parfumerie toEntity(ParfumerieDTO dto) {
        return new Parfumerie(
                dto.getParfumerie_id(),
                dto.getNume(),
                dto.getAdresa(),
                dto.getTelefon()
        );
    }
}