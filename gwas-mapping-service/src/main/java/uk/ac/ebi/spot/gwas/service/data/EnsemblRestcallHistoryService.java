package uk.ac.ebi.spot.gwas.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.model.EnsemblRestcallHistory;
import uk.ac.ebi.spot.gwas.repository.EnsemblRestcallHistoryRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EnsemblRestcallHistoryService {

    private EnsemblRestcallHistoryRepository ensemblRestcallHistoryRepository;


    @Autowired
    public EnsemblRestcallHistoryService(EnsemblRestcallHistoryRepository ensemblRestcallHistoryRepository) {
        this.ensemblRestcallHistoryRepository = ensemblRestcallHistoryRepository;
    }

    //Without release version, the data are not stored.
    public EnsemblRestcallHistory create(RestResponseResult resultResponseResult, String type, String param,
                                         String eRelease) {
        EnsemblRestcallHistory ensemblRestcallHistory = new EnsemblRestcallHistory();
        String restApiError = resultResponseResult.getError();


        if ((resultResponseResult.getStatus() == 200) || (resultResponseResult.getStatus() == 400)) {
            if (eRelease != null) {
                if (!(eRelease.isEmpty())) {
                    try {
                        ensemblRestcallHistory.setEnsemblUrl(resultResponseResult.getUrl());
                        ensemblRestcallHistory.setEnsemblParam(param);
                        ensemblRestcallHistory.setRequestType(type);
                        ensemblRestcallHistory.setEnsemblVersion(eRelease);

                        // Check for any errors
                        if (restApiError != null && !restApiError.isEmpty()) {
                            ensemblRestcallHistory.setEnsemblError(restApiError);
                        } else {
                            ensemblRestcallHistory.setEnsemblResponse(resultResponseResult.getRestResult());
                        }

                        this.ensemblRestcallHistoryRepository.save(ensemblRestcallHistory);
                    } catch (Exception e) {
                        // BEWARE: the following code MUST NOT block Ensembl Rest API Call
                    }
                }
            }
        }
        return ensemblRestcallHistory;
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<EnsemblRestcallHistory> create(EnsemblRestcallHistory ensemblRestcallHistory) {
        EnsemblRestcallHistory history = ensemblRestcallHistoryRepository.save(ensemblRestcallHistory);
        return CompletableFuture.completedFuture(history);
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<List<EnsemblRestcallHistory>> create(List<EnsemblRestcallHistory> ensemblRestcallHistory) {
        List<EnsemblRestcallHistory> history = ensemblRestcallHistoryRepository.saveAll(ensemblRestcallHistory);
        return CompletableFuture.completedFuture(history);
    }

    public EnsemblRestcallHistory build(RestResponseResult result, String type, String param, String eRelease) {

            EnsemblRestcallHistory restcallHistory = new EnsemblRestcallHistory();
            String restApiError = result.getError();

            restcallHistory.setEnsemblUrl(result.getUrl());
            restcallHistory.setEnsemblParam(param);
            restcallHistory.setRequestType(type);
            restcallHistory.setEnsemblVersion(eRelease);
            if (restApiError != null && !restApiError.isEmpty()) {
                restcallHistory.setEnsemblError(restApiError);
            } else {
                restcallHistory.setEnsemblResponse(result.getRestResult());
            }
        return restcallHistory;
    }

    // BEWARE:If the Ensembl release is valid, the system can try to retrieve the data from the table
    public RestResponseResult getEnsemblRestCallByTypeAndParamAndVersion(String type, String param, String eRelease) {
        RestResponseResult restResponseResult = null;

        // Without release it is pointless stores the info.
        if (eRelease != null) {
            if (!(eRelease.isEmpty())) {
                try {
                    Collection<EnsemblRestcallHistory> urls = ensemblRestcallHistoryRepository.findByRequestTypeAndEnsemblParamAndEnsemblVersion(type, param, eRelease);
                    if (urls.size() > 0) {
                        EnsemblRestcallHistory result = urls.iterator().next();
                        restResponseResult = new RestResponseResult();
                        restResponseResult.setUrl(result.getEnsemblUrl());
                        String restApiError = result.getEnsemblError();

                        if (restApiError != null && !restApiError.isEmpty()) {
                            restResponseResult.setError(restApiError);
                        } else {
                            restResponseResult.setRestResult(result.getEnsemblResponse());
                        }
                    }
                } catch (Exception e) {
                    // BEWARE: the following code MUST NOT block Ensembl Rest API Call
                }
            }
        }
        return restResponseResult;
    }
}
