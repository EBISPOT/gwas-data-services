package uk.ac.ebi.spot.gwas.association;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AssociationController {

    private final AssociationRepository associationRepository;

    public AssociationController(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @GetMapping("/associations")
    public Object getAssociations(@RequestParam String efo, @RequestParam String region) {

        List<Map<String, String>> dataList = associationRepository.findByRegionAndEfoTrait(region, efo);

        List<String> snpIds = new ArrayList<>();
        dataList.forEach(data -> snpIds.add(data.get("RS_ID")));
        log.info(snpIds.toString());

        List<Map<String, String>> dataList2 = associationRepository.findByRsidsAndTrait(snpIds, efo);

        List<AssociationDto> associationDtos = new ArrayList<>();
        dataList.forEach(data -> {
            AssociationDto associationDto = AssociationDto.builder()
                    .snp(data.get("RS_ID"))
                    .pValueMantissa(String.valueOf(data.get("PVALUE_MANTISSA")))
                    .pValueExponent(String.valueOf(data.get("PVALUE_EXPONENT")))
                    .efoId(String.valueOf(data.get("SHORT_FORM")))
                    .efoMapping(efo)
                    .build();

            dataList2.forEach(data2 -> {
                if (data2.get("RS_ID").equals(data.get("RS_ID"))) {
                    associationDto.setStudy(data2.get("ACCESSION_ID"));
                    associationDto.setGwasTraits(data2.get("TRAIT"));
                    associationDto.setPubmedId(data2.get("PUBMED_ID"));
                    associationDto.setAuthor(data2.get("FULLNAME"));
                }
            });
            associationDtos.add(associationDto);
        });
        return associationDtos;
    }


}
