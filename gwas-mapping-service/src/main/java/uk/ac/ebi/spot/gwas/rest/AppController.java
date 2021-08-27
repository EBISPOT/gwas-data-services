package uk.ac.ebi.spot.gwas.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblRunnner;

@Slf4j
@RestController
public class AppController {

    @Autowired
    private EnsemblRunnner ensemblRunnner;

    @GetMapping("/{studyId}")
    public String mapByStudy(@PathVariable Long studyId) {
        String performer = "user";
        log.info("Study ID : {}", studyId);
        ensemblRunnner.mapAssociationsByStudy(studyId, performer);
        return "success";
    }
}
