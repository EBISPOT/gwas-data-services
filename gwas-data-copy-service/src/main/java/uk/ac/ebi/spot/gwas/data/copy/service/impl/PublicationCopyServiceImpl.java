package uk.ac.ebi.spot.gwas.data.copy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.ac.ebi.spot.gwas.data.copy.model.Publication;
import uk.ac.ebi.spot.gwas.data.copy.oracle.repository.PublicationRepository;
import uk.ac.ebi.spot.gwas.data.copy.service.PublicationCopyService;
import uk.ac.ebi.spot.gwas.data.copy.service.PublicationMongoDataCopyRunner;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationCopyServiceImpl implements PublicationCopyService {

    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    PublicationMongoDataCopyRunner publicationMongoDataCopyRunner;

    public void copyData(String outputDir, String errorDir) {
        Long pubCount =  publicationRepository.count();
        int batchSize = 2000;
        long bucket = pubCount/5000;
        for(int i =0; i <= bucket ; i++) {
            Pageable pageable = PageRequest.of(i, batchSize);
            List<Long> pubIds = publicationRepository.findAll(pageable)
                    .stream()
                    .map(Publication::getId)
                    .collect(Collectors.toList());
            publicationMongoDataCopyRunner.executeRunner(pubIds, outputDir, errorDir, "executor-"+i);
        }



    }


}
