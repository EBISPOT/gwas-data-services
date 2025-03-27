package uk.ac.ebi.spot.gwas.service;

import java.util.List;

public interface FileHandlerService {

    List<String> readFileInput(String path);
}
