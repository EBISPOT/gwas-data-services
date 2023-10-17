package uk.ac.ebi.spot.gwas.util;

import uk.ac.ebi.spot.gwas.service.FileHandlerService;
import uk.ac.ebi.spot.gwas.service.impl.FileHandlerServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestRsidListComparison {

    public static void main(String[] args) {
        FileHandlerService fileHandlerService = new FileHandlerServiceImpl();
       List<String> rsidMapped = fileHandlerService.readFileInput("/Users/sajo/Documents/proj_files/Mapped-Rsid.tsv");
        Set<String> rsidMappedSet = new HashSet<>();
        System.out.println("the Rsid which are to be mapped are");
        rsidMapped.forEach(rsId -> { if(rsId.contains(",")) {
            rsidMappedSet.addAll(Arrays.asList(rsId.split(",")));
        }else {
            rsidMappedSet.add(rsId);
        }
        });
        rsidMappedSet.forEach(rsId -> System.out.println(rsId));
        List<String> rsidDaniel = fileHandlerService.readFileInput("/Users/sajo/Documents/proj_files/Incorrectly_mapped_variants_snapshot.tsv");
        List<String> rsIdMappedSuccess = rsidDaniel.stream().filter(rsId -> !rsidMappedSet.contains(rsId)).collect(Collectors.toList());
        System.out.println("the Rsid which are to mapped with out error are");
        rsIdMappedSuccess.forEach(rsId -> System.out.println(rsId));
        List<String> rsIdMappedError = rsidDaniel.stream().filter(rsId -> rsidMappedSet.contains(rsId)).collect(Collectors.toList());
        System.out.println("the Rsid which are to mapped with  error are");
        rsIdMappedError.forEach(rsId -> System.out.println(rsId));
    }
}
