package uk.ac.ebi.spot.gwas.rabbitmq.dto;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationAuthorDto;
import uk.ac.ebi.spot.gwas.model.Author;

@Component
public class AuthorDTOAssembler {
    @Transactional(readOnly = true)
    public Author diassemble(PublicationAuthorDto publicationAuthorDto) {
        Author author = new Author();
        author.setFullname(publicationAuthorDto.getFullName());
        author.setAffiliation(publicationAuthorDto.getAffiliation());
        author.setFirstName(publicationAuthorDto.getFirstName());
        author.setLastName(publicationAuthorDto.getLastName());
        author.setFullnameStandard(publicationAuthorDto.getFullNameStandard());
        author.setOrcid(publicationAuthorDto.getOrcid());
        author.setInitials(publicationAuthorDto.getInitials());
        return author;
    }
}
