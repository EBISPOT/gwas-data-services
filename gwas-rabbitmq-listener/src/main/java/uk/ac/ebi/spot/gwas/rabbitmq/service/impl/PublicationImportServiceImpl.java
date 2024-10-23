package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationAuthorDto;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;
import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.rabbitmq.dto.AuthorDTOAssembler;
import uk.ac.ebi.spot.gwas.rabbitmq.dto.PublicationRabbitMessageAssembler;
import uk.ac.ebi.spot.gwas.rabbitmq.service.AuthorService;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationImportService;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicationImportServiceImpl implements PublicationImportService {

    private static final Logger log = LoggerFactory.getLogger(PublicationImportServiceImpl.class);
    @Autowired
    PublicationRabbitMessageAssembler publicationRabbitMessageAssembler;

    @Autowired
    AuthorDTOAssembler authorDTOAssembler;
    @Autowired
    AuthorService authorService;
    @Autowired
    PublicationService publicationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void importPublication(PublicationRabbitMessage publicationRabbitMessage){

        log.info("Pmid from rabbit message {}",publicationRabbitMessage.getPmid());
        Publication publication =  publicationRabbitMessageAssembler.disassemble(publicationRabbitMessage);

        if(publicationService.findByPmid(publication.getPubmedId()) != null) {
            return;
        }
        publicationService.save(publication);
        Map<Integer, PublicationAuthorDto> authorMap = publicationRabbitMessage.getAuthors();
        for(Map.Entry<Integer, PublicationAuthorDto> entry : authorMap.entrySet()) {

            Author author = authorDTOAssembler.diassemble(entry.getValue());
            Integer order = entry.getKey();
            log.info("Author details are {},{}",author.getFullnameStandard(),author.getInitials());
            Author authorDB = authorService.findUniqueAuthor(author.getFullname(), author.getFirstName(),
                    author.getLastName(),author.getInitials(), author.getAffiliation());

            if (authorDB == null) {
                authorService.addPublication(author, publication, order);
            } else {
                authorService.addPublication(authorDB, publication, order);
            }
        }
        Author firstAuthor =  authorDTOAssembler.diassemble(publicationRabbitMessage.getFirstAuthor());
        log.info("First Author details are {},{}",firstAuthor.getFullnameStandard(),firstAuthor.getInitials());
        Author firstAuthorDB = authorService.findUniqueAuthor(firstAuthor.getFullname(),firstAuthor.getFirstName(),
                firstAuthor.getLastName(),firstAuthor.getInitials(),
                firstAuthor.getAffiliation());
        publication.setFirstAuthor(firstAuthorDB);
        publicationService.save(publication);
    }
}
