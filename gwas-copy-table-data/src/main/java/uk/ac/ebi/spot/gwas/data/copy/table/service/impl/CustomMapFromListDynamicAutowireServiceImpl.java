package uk.ac.ebi.spot.gwas.data.copy.table.service.impl;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.data.copy.table.service.DataTableService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomMapFromListDynamicAutowireServiceImpl {
    private final Map<String, DataTableService> servicesByTableName;


    @Autowired
    public CustomMapFromListDynamicAutowireServiceImpl(List<DataTableService> dataTableServices) {
        servicesByTableName = dataTableServices.stream()
                .collect(Collectors.toMap(DataTableService::getTableName, Function.identity()));
    }


    public DataTableService getTableService(String tableName) {
       return servicesByTableName.get(tableName);
    }


}
