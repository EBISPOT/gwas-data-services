package uk.ac.ebi.spot.gwas.data.copy.table.service;

import java.util.List;

public interface DataTableService {

  void copyDataToMongoTables(List<Long> ids);

  String getTableName();
}
