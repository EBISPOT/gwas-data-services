package uk.ac.ebi.spot.gwas.data.copy.table.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.data.copy.table.config.DepositionCurationConfig;
import uk.ac.ebi.spot.gwas.data.copy.table.model.Author;
import uk.ac.ebi.spot.gwas.data.copy.table.model.Housekeeping;
import uk.ac.ebi.spot.gwas.data.copy.table.model.Publication;
import uk.ac.ebi.spot.gwas.data.copy.table.repository.*;

import uk.ac.ebi.spot.gwas.data.copy.table.service.DataTableService;
import uk.ac.ebi.spot.gwas.data.copy.table.service.PublicationAuthorService;
import uk.ac.ebi.spot.gwas.deposition.constants.PublicationStatus;
import uk.ac.ebi.spot.gwas.deposition.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PublicationDataTableServiceImpl implements DataTableService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PublicationMongoRepository publicationRepository;

    @Autowired
    uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository.PublicationRepository publicationOracleRepository;

    @Autowired
    PublicationAuthorRepository publicationAuthorRepository;

    @Autowired
    uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository.StudyRepository studyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DiseaseTraitMongoRepository diseaseTraitRepository;

    @Autowired
    EfoTraitMongoRepository efoTraitRepository;

    @Autowired
    CurationStatusMongoRepository curationStatusRepository;

    @Autowired
    CuratorMongoRepository curatorRepository;

    @Autowired
    DepositionCurationConfig depositionCurationConfig;

    @Autowired
    PublicationAuthorService publicationAuthorService;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void copyDataToMongoTables(List<Long> ids) {
        log.info("Inside copyDataToMongoTables()");
        ids.forEach(id ->  {
            log.info("The id is {}", id);
            Publication publication =   publicationOracleRepository.findById(id).get();
            String pmid = publication.getPubmedId();
            User user =userRepository.findByName("Migration User").get();
            if (!publicationRepository.findByPmid(pmid).isPresent()) {
                log.debug("Inside Pmid not in Mongo DB block");
                log.info("Pmid is {}", pmid);
                uk.ac.ebi.spot.gwas.deposition.domain.Publication mongoPublication = new uk.ac.ebi.spot.gwas.deposition.domain.Publication();
                mongoPublication.setPmid(pmid);
                mongoPublication.setJournal(publication.getPublication());
                mongoPublication.setStatus(PublicationStatus.ELIGIBLE.name());
                mongoPublication.setFirstAuthor(publication.getFirstAuthor().getFullname());
                mongoPublication.setTitle(publication.getTitle());
                Author author = publication.getFirstAuthor();
                PublicationAuthor publicationAuthor = findUniqueAuthor(author);
                mongoPublication.setFirstAuthorId(publicationAuthor.getId());
                mongoPublication.setCreated(new Provenance(new DateTime(publication.getCreatedAt()), user.getId()));
                mongoPublication.setUpdated(new Provenance(new DateTime(publication.getUpdatedAt()), user.getId()));
                List<String> pubAuthors = new ArrayList<>();
                mongoPublication.setPublicationDate(new LocalDate(publication.getPublicationDate()));
                publication.getAuthors().forEach(author1 -> {
                    PublicationAuthor pubAuthor = findUniqueAuthor(author1);
                    pubAuthors.add(pubAuthor.getId());
                });
                mongoPublication.setAuthors(pubAuthors);

                Optional.ofNullable(publication.getCorrespondingAuthor()).ifPresent(corrAuthor ->
                        mongoPublication.setCorrespondingAuthor(new CorrespondingAuthor(
                                corrAuthor.getCorrespondingAuthorName(), corrAuthor.getCorrespondingAuthorEmail())));
                addCurationDetails(mongoPublication, publication);
                publicationRepository.save(mongoPublication);
            } else {
                uk.ac.ebi.spot.gwas.deposition.domain.Publication mongoPublication = Optional.ofNullable(publicationRepository.findByPmid(pmid))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .orElse(null);
                log.debug("Inside Pmid  in Mongo DB block");
                log.info("Pmid is {}", pmid);
                addCurationDetails(mongoPublication, publication);
                publicationRepository.save(mongoPublication);
            }

        });
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public uk.ac.ebi.spot.gwas.deposition.domain.Publication addCurationDetails(uk.ac.ebi.spot.gwas.deposition.domain.Publication mongoPublication, Publication publication) {
        for (uk.ac.ebi.spot.gwas.data.copy.table.model.Study study : publication.getStudies()) {
            Housekeeping housekeeping = study.getHousekeeping();
            uk.ac.ebi.spot.gwas.data.copy.table.model.CurationStatus curationStatus = housekeeping.getCurationStatus();
            uk.ac.ebi.spot.gwas.data.copy.table.model.Curator curator = housekeeping.getCurator();
            String status = curationStatus.getStatus();

            if (!status.equals("Requires Review")) {
                log.info("status from oracle is {}", status);
                String mongoStatus = depositionCurationConfig.getCurationStatusMap().get(status);
               log.info("Inside addCurationDetails status is {}",mongoStatus);
                mongoPublication.setCurationStatusId(Optional.ofNullable(curationStatusRepository.findByStatus(mongoStatus))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(CurationStatus::getId)
                        .orElse(null));
                mongoPublication.setCuratorId(getCuratorDetails(curator));
                break;
            }
        }
            return mongoPublication;
    }
    @Override
    public String getTableName() {
        return "publication";
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public String  saveCuratorDetails(uk.ac.ebi.spot.gwas.data.copy.table.model.Curator curator) {
        Curator mongCurator = new Curator(curator.getLastName(),
                curator.getFirstName(),
                curator.getUserName(),
                curator.getEmail());
        Curator updatedCurator =  curatorRepository.save(mongCurator);
        return updatedCurator.getId();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String getCuratorDetails(uk.ac.ebi.spot.gwas.data.copy.table.model.Curator curator) {
        if (curator.getEmail() != null ) {
           Optional<Curator> optionalCurator = curatorRepository.findByEmailIgnoreCase(curator.getEmail());
           if( optionalCurator.isPresent() ) {
               return optionalCurator.get().getId();
           } else {
               log.debug("Inside email not null block");
               return saveCuratorDetails(curator);
           }
        }else if(curator.getFirstName() != null && curator.getLastName() != null) {
            Optional<Curator> optionalCurator = curatorRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(curator.getFirstName(), curator.getLastName());
            if( optionalCurator.isPresent() ) {
                return optionalCurator.get().getId();
            } else {
                log.debug("Inside first name & last name  not null block");
                return saveCuratorDetails(curator);
            }
        }else if(curator.getLastName() != null) {
            Optional<Curator> optionalCurator = curatorRepository.findByLastNameIgnoreCase( curator.getLastName());
            if( optionalCurator.isPresent() ) {
                return optionalCurator.get().getId();
            } else {
                log.debug("Inside  last name  not null block");
                return saveCuratorDetails(curator);
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PublicationAuthor findUniqueAuthor(Author author) {
       Optional<PublicationAuthor> optionalPublicationAuthor = publicationAuthorService.findUniqueAuthor
               (author.getFullname(), author.getFirstName(), author.getLastName(),
                author.getInitials(), author.getAffiliation());
       if(optionalPublicationAuthor.isPresent()) {
           return optionalPublicationAuthor.get();
       } else {
           PublicationAuthor publicationAuthor = new PublicationAuthor(author.getFullname(), author.getFullnameStandard()
                   , author.getFirstName(), author.getLastName(), author.getInitials(),
                   author.getAffiliation(), author.getOrcid(), null, null);
           return publicationAuthorRepository.save(publicationAuthor);
       }
    }
}
