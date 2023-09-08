package uk.ac.ebi.spot.gwas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.service.FileHandlerService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileHandlerServiceImpl implements FileHandlerService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public List<String> readFileInput(String path) {

    File file;
    FileReader fileReader = null;
    List<String> rsids = new ArrayList<>();
        try {
            file = new File(path);
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                log.info("Rsid is ->"+str);
                rsids.add(str);
            }
        }catch(FileNotFoundException ex){
            log.error("File not found->"+ex.getMessage(),ex);
        }catch(IOException ex){
            log.error("IO Exception->"+ex.getMessage(),ex);
        }
        return rsids;
    }
}
