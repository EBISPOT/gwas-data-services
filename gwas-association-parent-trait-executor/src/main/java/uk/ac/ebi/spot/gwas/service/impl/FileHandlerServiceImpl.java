package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.service.FileHandlerService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    public List<String> readFileInput(String path) {

        File file;
        FileReader fileReader = null;
        List<String> efoShortForms = new ArrayList<>();
        try {
            file = new File(path);
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                log.info("shortform is {}",str);
                efoShortForms.add(str);
            }
        }catch(FileNotFoundException ex){
            log.error("File not found->"+ex.getMessage(),ex);
        }catch(IOException ex){
            log.error("IO Exception->"+ex.getMessage(),ex);
        }
        return efoShortForms;
    }
}
