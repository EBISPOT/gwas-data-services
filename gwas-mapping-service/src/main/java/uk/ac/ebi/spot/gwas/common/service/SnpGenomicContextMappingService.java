package uk.ac.ebi.spot.gwas.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.*;
import uk.ac.ebi.spot.gwas.common.projection.SnpGeneProjection;
import uk.ac.ebi.spot.gwas.common.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SnpGenomicContextMappingService {

    @Autowired
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private GenomicContextRepository genomicContextRepository;
    @Autowired
    private EnsemblGeneRepository ensemblGeneRepository;
    @Autowired
    private EntrezGeneRepository entrezGeneRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private GeneQueryService geneQueryService;
    @Autowired
    private EnsemblGeneQueryService ensemblGeneQueryService;
    @Autowired
    private EntrezGeneQueryService entrezGeneQueryService;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    @Autowired
    private LocationCreationService locationCreationService;
    @Autowired
    private GenomicContextCreationService genomicContextCreationService;
    @Autowired
    private SnpUpdateService snpUpdateService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    /**
     * Takes genomic context information returned by mapping pipeline and creates a structure that links an rs_id to all
     * its genomic context objects. This ensures we can do a single update based on latest mapping information.
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void processGenomicContext(Collection<GenomicContext> genomicContexts) {

        processGenes(genomicContexts);
        getLog().info("Collate all new genomic context information...");
        Map<String, Set<GenomicContext>> snpToGenomicContextMap = new HashMap<>();

        for (GenomicContext genomicContext : genomicContexts) {
            String snpIdInGenomicContext = genomicContext.getSnp().getRsId();
            if (snpToGenomicContextMap.containsKey(snpIdInGenomicContext)) {
                snpToGenomicContextMap.get(snpIdInGenomicContext).add(genomicContext);
            } else {
                Set<GenomicContext> snpGenomicContext = new HashSet<>();
                snpGenomicContext.add(genomicContext);
                snpToGenomicContextMap.put(snpIdInGenomicContext, snpGenomicContext);
            }
        }
        getLog().debug("Storing new genomic context information...");
        storeSnpGenomicContext(snpToGenomicContextMap);
    }


    /**
     * Extract gene information from genomic contexts returned from mapping pipeline
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void processGenes(Collection<GenomicContext> genomicContexts) {

        log.info("Processing genes...");

        // Need to flatten down genomic context gene information
        // and create structure linking each gene symbol to its
        // complete set of current Ensembl and Entrez IDs
        Map<String, Set<String>> geneToEnsemblIdMap = new HashMap<>();
        Map<String, Set<String>> geneToEntrezIdMap = new HashMap<>();

        // Loop over each genomic context and store information on external IDs linked to gene symbol
        for (GenomicContext genomicContext : genomicContexts) {

            log.warn("processing {} ", genomicContext.getGene().getGeneName());
            // Check gene exists
            String geneName = "undefined";
            try {
                geneName = genomicContext.getGene().getGeneName().trim();
            }catch (Exception e){
                log.error(e.getMessage());
            }

            if (!geneName.equalsIgnoreCase("undefined")) {

                // Retrieve the latest Ensembl/Entrez IDs for the named gene from the latest mapping run
                Collection<EnsemblGene> ensemblGeneIds = genomicContext.getGene().getEnsemblGeneIds();
                for (EnsemblGene ensemblGene : ensemblGeneIds) {

                    log.warn("Ensembl Gene id: {} ", ensemblGene.getGene());
                    String ensemblId = ensemblGene.getEnsemblGeneId();
                    if (ensemblId != null) {
                        if (geneToEnsemblIdMap.containsKey(geneName)) {
                            geneToEnsemblIdMap.get(geneName).add(ensemblId);
                        }

                        else {
                            Set<String> ensemblGeneIdsSet = new HashSet<>();
                            ensemblGeneIdsSet.add(ensemblId);
                            geneToEnsemblIdMap.put(geneName, ensemblGeneIdsSet);
                        }
                    }
                }

                Collection<EntrezGene> entrezGeneIds = genomicContext.getGene().getEntrezGeneIds();
                for (EntrezGene entrezGene : entrezGeneIds) {

                    log.warn("Entrez Gene id: {} ", entrezGene.getGene());
                    String entrezId = entrezGene.getEntrezGeneId();
                    if (entrezId != null) {
                        if (geneToEntrezIdMap.containsKey(geneName)) {
                            geneToEntrezIdMap.get(geneName).add(entrezId);
                        }

                        else {
                            Set<String> entrezGeneIdsSet = new HashSet<>();
                            entrezGeneIdsSet.add(entrezId);
                            geneToEntrezIdMap.put(geneName, entrezGeneIdsSet);
                        }
                    }
                }
            }
        }

        // Store genes, source is required so we know what table to add them to
        log.warn("Storing genetoEnsemblMap {} ", geneToEnsemblIdMap.size());
        if (geneToEnsemblIdMap.size() > 0) {
            storeGenes(geneToEnsemblIdMap, "Ensembl");
        }

        log.warn("Storing genetoEntrezMap {} ", geneToEntrezIdMap.size());
        if (geneToEntrezIdMap.size() > 0) {
            storeGenes(geneToEntrezIdMap, "Entrez");
        }
        log.info("done");
    }

    /**
     * Create/update genes with latest mapping information
     *
     * @param geneToExternalIdMap map of a gene name and all external database IDs from current mapping run
     * @param source              the source of mapping, either Ensembl or Entrez
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void storeGenes(Map<String, Set<String>> geneToExternalIdMap, String source) {

        List<Gene> genesToUpdate = new ArrayList<>();
        List<Gene> genesToCreate = new ArrayList<>();
        List<EntrezGene> entrezGenesToDelete = new ArrayList<>();
        List<EnsemblGene> ensemblGenesToDelete = new ArrayList<>();
        for (String geneName : geneToExternalIdMap.keySet()) {

            log.warn("Storing gene {} for source {} : ", geneName, source);
            Set<String> externalIds = geneToExternalIdMap.get(geneName);

            log.warn("Find any existing database genes that matches {} ", geneName);
            // IgnoreCase query is not used here as we want
            // the exact gene name returned from mapping
            Gene existingGeneInDatabase = geneQueryService.findByGeneName(geneName);

            // If gene is not found in database then create one
            if (existingGeneInDatabase == null) {
                log.warn("Creating gene {} as its not existig in DB", geneName);
                Gene newGene = createGene(geneName, externalIds, source);
                genesToCreate.add(newGene);
            }

            // Update gene
            else {

                log.warn(" processing {} as its existing in db ...", geneName);
                if (source.equalsIgnoreCase("Ensembl")) {

                    // Get a list of current Ensembl IDs linked to existing gene
                    Collection<EnsemblGene> oldEnsemblGenesLinkedToGene = existingGeneInDatabase.getEnsemblGeneIds();
                    Collection<Long> oldEnsemblIdsLinkedToGene = new ArrayList<>();

                    log.warn("processing {} old Ensembl gene linked to {} ", oldEnsemblGenesLinkedToGene.size(), geneName);
                    for (EnsemblGene oldEnsemblGeneLinkedToGene : oldEnsemblGenesLinkedToGene) {
                        log.warn("{} is an old Ensembl gene linked to {} ",
                                 oldEnsemblGeneLinkedToGene.getEnsemblGeneId(), geneName);
                        oldEnsemblIdsLinkedToGene.add(oldEnsemblGeneLinkedToGene.getId());
                    }

                    log.warn("processing {} new Ensembl gene linked to {} ", externalIds.size(), geneName);
                    Collection<EnsemblGene> newEnsemblGenes = new ArrayList<>();
                    for (String id : externalIds) {
                        log.warn("{} is an old Ensembl gene linked to {} ", id, geneName);
                        EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                        newEnsemblGenes.add(ensemblGene);
                    }

                    // Set latest IDs from mapping run
                    existingGeneInDatabase.setEnsemblGeneIds(newEnsemblGenes);

                    // Save changes
                    //geneRepository.save(existingGeneInDatabase);
                    genesToUpdate.add(existingGeneInDatabase);

                    // Clean-up any Ensembl IDs that may now be left without a gene linked
                    for (Long oldEnsemblIdLinkedToGene : oldEnsemblIdsLinkedToGene) {
                        cleanUpEnsemblGenes(oldEnsemblIdLinkedToGene, ensemblGenesToDelete);
                    }
                }

                if (source.equalsIgnoreCase("Entrez")) {

                    // Get a list of of current Entrez IDs linked to existing gene
                    Collection<EntrezGene> oldEntrezGenesLinkedToGene = existingGeneInDatabase.getEntrezGeneIds();
                    Collection<Long> oldEntrezGenesIdsLinkedToGene = new ArrayList<>();

                    log.warn("processing {} old Entrez gene linked to {} ", oldEntrezGenesLinkedToGene.size(), geneName);
                    for (EntrezGene oldEntrezGeneLinkedToGene : oldEntrezGenesLinkedToGene) {
                        log.warn("{} is an old Entrez gene linked to {} ",
                                 oldEntrezGeneLinkedToGene.getEntrezGeneId(), geneName);
                        oldEntrezGenesIdsLinkedToGene.add(oldEntrezGeneLinkedToGene.getId());
                    }

                    log.warn("processing {} new Entrez gene linked to {} ", externalIds.size(), geneName);
                    Collection<EntrezGene> newEntrezGenes = new ArrayList<>();
                    for (String id : externalIds) {
                        log.warn("{} is an old Entrez gene linked to {} ", id, geneName);
                        EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                        newEntrezGenes.add(entrezGene);
                    }

                    existingGeneInDatabase.setEntrezGeneIds(newEntrezGenes);
                    genesToUpdate.add(existingGeneInDatabase);
                    log.warn("Clean up {} Entrez id for gene: {} ", oldEntrezGenesIdsLinkedToGene.size(), geneName);
                    for (Long oldEntrezGenesIdLinkedToGene : oldEntrezGenesIdsLinkedToGene) {
                        cleanUpEntrezGenes(oldEntrezGenesIdLinkedToGene, entrezGenesToDelete);
                    }
                }
            }
        }
        ensemblGeneRepository.deleteAll(ensemblGenesToDelete);
        entrezGeneRepository.deleteAll(entrezGenesToDelete);
        geneRepository.saveAll(genesToCreate);
        geneRepository.saveAll(genesToUpdate);
    }


    /**
     * Saves genomic context information to database
     *
     * @param snpToGenomicContextMap map of rs_id and all genomic context details returned from current mapping run
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void storeSnpGenomicContext(Map<String, Set<GenomicContext>> snpToGenomicContextMap) {

        List<SingleNucleotidePolymorphism> updatedSnps = new ArrayList<>();
        // Go through each rs_id and its associated genomic contexts returned from the mapping pipeline
        for (String snpRsId : snpToGenomicContextMap.keySet()) {

            getLog().debug("Storing genomic context for " + snpRsId);

            Set<GenomicContext> genomicContextsFromMapping = snpToGenomicContextMap.get(snpRsId);

            // Check if the SNP exists
            SingleNucleotidePolymorphism snpInDatabase =
                    singleNucleotidePolymorphismRepository.findByRsId(snpRsId);
            if(snpInDatabase == null){
                snpInDatabase =
                        singleNucleotidePolymorphismQueryService.findByRsIdIgnoreCase(snpRsId);
            }

            if (snpInDatabase != null) {

                Collection<GenomicContext> newSnpGenomicContexts = new ArrayList<>();

                for (GenomicContext genomicContextFromMapping : genomicContextsFromMapping) {

                    // Gene should already have been created
                    String geneName = "undefined";
                    try {
                        geneName = genomicContextFromMapping.getGene().getGeneName().trim();
                    }catch (Exception e){
                        log.error(e.getMessage());
                    }

                    if (!geneName.equalsIgnoreCase("undefined")) {

                        // Create new genomic context
                        Boolean isIntergenic = genomicContextFromMapping.getIsIntergenic();
                        Boolean isUpstream = genomicContextFromMapping.getIsUpstream();
                        Boolean isDownstream = genomicContextFromMapping.getIsDownstream();
                        Long distance = genomicContextFromMapping.getDistance();
                        String source = genomicContextFromMapping.getSource();
                        String mappingMethod = genomicContextFromMapping.getMappingMethod();
                        Boolean isClosestGene = genomicContextFromMapping.getIsClosestGene();

                        // Location details
                        String chromosomeName = genomicContextFromMapping.getLocation().getChromosomeName();
                        Integer chromosomePosition = genomicContextFromMapping.getLocation().getChromosomePosition();
                        Region regionFromMapping = genomicContextFromMapping.getLocation().getRegion();
                        String regionName = null;

                        if (regionFromMapping.getName() != null) {
                            regionName = regionFromMapping.getName().trim();
                        }

                        // Check if location already exists
                        Location location =
                                locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                        chromosomeName,
                                        chromosomePosition,
                                        regionName);

                        if (location == null) {
                            location = locationCreationService.createLocation(chromosomeName,
                                                                              chromosomePosition,
                                                                              regionName);
                        }

                        GenomicContext genomicContext = genomicContextCreationService.createGenomicContext(isIntergenic,
                                                                                                           isUpstream,
                                                                                                           isDownstream,
                                                                                                           distance,
                                                                                                           source,
                                                                                                           mappingMethod,
                                                                                                           geneName,
                                                                                                           snpInDatabase,
                                                                                                           isClosestGene,
                                                                                                           location);

                        newSnpGenomicContexts.add(genomicContext);
                    }

                    else {
                        getLog().warn("Gene name returned from mapping pipeline is 'undefined' for SNP" +
                                              snpInDatabase.getRsId());
                    }
                }

                // Save latest mapped information
                snpInDatabase.setGenomicContexts(newSnpGenomicContexts);
                // Update the last update date
                snpInDatabase.setLastUpdateDate(new Date());
                //singleNucleotidePolymorphismRepository.save(snpInDatabase);
                updatedSnps.add(snpInDatabase);
            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                getLog().error("Adding genomic context for SNP not found in database, RS_ID:" + snpRsId);
                throw new RuntimeException(
                        "Adding genomic context for SNP not found in database, RS_ID: " + snpRsId);
            }
        }
        singleNucleotidePolymorphismRepository.saveAll(updatedSnps);

    }

    /**
     * Method to create a gene
     *
     * @param geneName    gene symbol or name
     * @param externalIds external gene IDs
     * @param source      the source of mapping, either Ensembl or Entrez
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Gene createGene(String geneName, Set<String> externalIds, String source) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);

        if (source.equalsIgnoreCase("Ensembl")) {
            Collection<EnsemblGene> ensemblGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                ensemblGeneIds.add(ensemblGene);
            }
            newGene.setEnsemblGeneIds(ensemblGeneIds);
        }


        if (source.equalsIgnoreCase("Entrez")) {
            // Set Entrez Ids for new gene
            Collection<EntrezGene> entrezGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                entrezGeneIds.add(entrezGene);
            }
            newGene.setEntrezGeneIds(entrezGeneIds);
        }

        // Save gene
        log.info("Creating " + source + " gene, with name " + geneName);
        return newGene;
    }

    /**
     * Method to create an Ensembl gene, this database table holds ensembl gene IDs
     *
     * @param id       Ensembl gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public EnsemblGene createOrRetrieveEnsemblExternalId(String id, String geneName) {
        EnsemblGene ensemblGene = ensemblGeneQueryService.findByEnsemblGeneId(id);

        // Create new entry in ENSEMBL_GENE table for this ID
        if (ensemblGene == null) {
            ensemblGene = new EnsemblGene();
            ensemblGene.setEnsemblGeneId(id);
            ensemblGeneRepository.save(ensemblGene);
        }

        // Check this ID is not linked to a gene with a different name
        else {
            Gene existingGeneLinkedToId = ensemblGene.getGene();

            if (existingGeneLinkedToId != null) {
                if (!Objects.equals(existingGeneLinkedToId.getGeneName(), geneName)) {
                    getLog().warn(
                            "Ensembl ID: " + id + ", is already used in database by a different gene(s): " +
                                    existingGeneLinkedToId.getGeneName() + ". Will update so links to " + geneName);

                    // For gene already linked to this ensembl ID remove the ensembl ID
                    existingGeneLinkedToId.getEnsemblGeneIds().remove(ensemblGene);
                    geneRepository.save(existingGeneLinkedToId);
                }
            }
        }
        return ensemblGene;
    }

    /**
     * Method to create an Entrez gene, this database table holds entrez gene IDs
     *
     * @param id       Entrez gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public EntrezGene createOrRetrieveEntrezExternalId(String id, String geneName) {
        EntrezGene entrezGene = entrezGeneQueryService.findByEntrezGeneId(id);

        // Create new entry in ENTREZ_GENE table for this ID
        if (entrezGene == null) {
            entrezGene = new EntrezGene();
            entrezGene.setEntrezGeneId(id);
            entrezGeneRepository.save(entrezGene);
        }

        // Check this ID is not linked to a gene with a different name
        else {
            Gene existingGeneLinkedToId = entrezGene.getGene();

            if (existingGeneLinkedToId != null) {
                if (!Objects.equals(existingGeneLinkedToId.getGeneName(), geneName)) {
                    getLog().warn(
                            "Entrez ID: " + id + ", is already used in database by a different gene(s): " +
                                    existingGeneLinkedToId.getGeneName() + ". Will update so links to " + geneName);

                    // For gene already linked to this entrez ID remove the entrez ID
                    existingGeneLinkedToId.getEntrezGeneIds().remove(entrezGene);
                    geneRepository.save(existingGeneLinkedToId);
                }
            }
        }
        return entrezGene;
    }

    /**
     * Method to clean-up an Ensembl gene ID in database that has no linked gene
     *
     * @param id Ensembl gene ID to delete
     */
    @Transactional(readOnly = true)
    public void cleanUpEnsemblGenes(Long id, List<EnsemblGene> ensemblGenesToDelete) {

        // Find any genes with this Ensembl ID
        EnsemblGene ensemblGene = ensemblGeneRepository.findById(id).get();
        Gene geneWithEnsemblId = ensemblGene.getGene();

        // If this ID is not linked to a gene then delete it
        if (geneWithEnsemblId == null) {
//            ensemblGeneRepository.delete(id);
            ensemblGenesToDelete.add(ensemblGene);
        }
    }

    /**
     * Method to clean-up an Entrez gene ID in database that has no linked gene
     *
     * @param id Entrez gene ID to delete
     */
    @Transactional(readOnly = true)
    public void cleanUpEntrezGenes(Long id, List<EntrezGene> entrezGenesToDelete) {

        // Find any genes with this Entrez ID
        EntrezGene entrezGene = entrezGeneRepository.findById(id).get();
        Gene geneWithEntrezIds = entrezGene.getGene();

        // If this ID is not linked to a gene then delete it
        if (geneWithEntrezIds == null) {
//            entrezGeneRepository.delete(id);
            entrezGenesToDelete.add(entrezGene);
        }
    }

    /**
     * Method to remove the existing genomic contexts linked to a SNP
     *
     * @param snp SNP from which to remove the associated genomic contexts
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeExistingGenomicContexts(SingleNucleotidePolymorphism snp) {

        // Get a list of locations currently genomic context
        Collection<GenomicContext> snpGenomicContexts = snp.getGenomicContexts();

        if (snpGenomicContexts != null && !snpGenomicContexts.isEmpty()) {
            // Remove old genomic contexts, as these will be updated with latest mapping
            snp.setGenomicContexts(new ArrayList<>());
            singleNucleotidePolymorphismRepository.save(snp);
            Set<Long> oldSnpLocationIds = new HashSet<>();

            for (GenomicContext snpGenomicContext : snpGenomicContexts) {
                if (snpGenomicContext.getLocation() != null) {
                    oldSnpLocationIds.add(snpGenomicContext.getLocation().getId());
                }
            }
            genomicContextRepository.deleteAll(snpGenomicContexts);

            for (Long oldSnpLocationId : oldSnpLocationIds) {
                cleanUpLocations(oldSnpLocationId);
            }

        }
    }

    /**
     * Method to remove any old locations that no longer have snps or genomic contexts linked to them
     *
     * @param id Id of location object
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void cleanUpLocations(Long id) {
        List<SingleNucleotidePolymorphism> snps =
                singleNucleotidePolymorphismRepository.findIdsByLocationId(id);
        List<GenomicContext> genomicContexts = genomicContextRepository.findIdsByLocationId(id);

        if (snps.size() == 0 && genomicContexts.size() == 0) {
            locationRepository.deleteById(id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateSnpMappedGene(Long snpId, List<Gene> mappedGenes) {
        singleNucleotidePolymorphismRepository.findById(snpId).ifPresent(snp -> {
            snp.setMappedSnpGenes(mappedGenes);
            singleNucleotidePolymorphismRepository.save(snp);
        });
    }


    public void updateSnpGeneMapping(List<SnpGeneProjection> snpGeneProjections) {

        Map<Long, List<SnpGeneProjection>> snpGeneMap = snpGeneProjections.
                stream()
                .collect(Collectors.groupingBy(SnpGeneProjection::getSnpId));
        snpGeneMap.keySet().forEach(snpId -> {
            List<Long> mappedGeneIds = snpGeneMap.get(snpId).stream()
                    .map(SnpGeneProjection::getGeneId)
                    .collect(Collectors.toList());
            List<Gene> mappedGenes = geneQueryService.findGenesByIds(mappedGeneIds);
            snpUpdateService.updateSnpMappedGene(snpId, mappedGenes);
        });
    }

}

