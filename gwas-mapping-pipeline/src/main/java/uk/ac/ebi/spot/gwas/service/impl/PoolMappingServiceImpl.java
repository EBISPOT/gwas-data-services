/*
package uk.ac.ebi.spot.gwas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.component.JobstatusMap;
import uk.ac.ebi.spot.gwas.component.PollFreeSlotThread;
import uk.ac.ebi.spot.gwas.component.PoolMappingServiceThread;
import uk.ac.ebi.spot.gwas.dto.JobStatusDto;
import uk.ac.ebi.spot.gwas.service.PoolMappingService;

import java.util.HashMap;
import java.util.Map;

@Service
public class PoolMappingServiceImpl implements PoolMappingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static Integer poolMapperCall = 30;



    @Override
    public void poolMappingService() {
        PoolMappingServiceThread poolMappingServiceThread = new PoolMappingServiceThread();
        poolMappingServiceThread.start();
        PollFreeSlotThread freeSlotThread = new PollFreeSlotThread();
        freeSlotThread.start();

    }






}
*/
