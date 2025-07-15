package uk.ac.ebi.spot.gwas.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestMissingAssociation {

    public static void main(String[] args) {
        File sandboxfile;
        File prodFile;
        FileReader sandboxfileReader = null;
        FileReader prodfileReader = null;
        List<String> sandboxefoShortForms = new ArrayList<>();
        List<String> prodefoShortForms = new ArrayList<>();
        try {
            sandboxfile = new File("/Users/sajo/Documents/sajo/proj_files/Sandbox_Asscn_Ids");
            prodFile = new File("/Users/sajo/Documents/sajo/proj_files/Prod_Asscn_Ids");
            sandboxfileReader = new FileReader(sandboxfile);
            BufferedReader bufferedReader = new BufferedReader(sandboxfileReader);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                sandboxefoShortForms.add(str);
            }
            String str1 = "";
            prodfileReader = new FileReader(prodFile);
            BufferedReader bufferedReader1 = new BufferedReader(prodfileReader);
            while ((str1 = bufferedReader1.readLine()) != null) {
                prodefoShortForms.add(str1);
            }
            prodefoShortForms.stream().filter(efo -> !sandboxefoShortForms.contains(efo)).forEach(System.out::println);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
