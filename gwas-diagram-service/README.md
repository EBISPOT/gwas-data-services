# GWAS Diagram Service

GWAS Diagram Service is a Spring Boot application designed to manage and serve data for the GWAS (Genome-Wide Association Studies) diagram. It synchronizes data from a GWAS database into a high-performance Solr search engine and provides a REST API for visual discovery of genotype-phenotype associations across the human genome.

## Table of Contents
- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [CLI Operations](#cli-operations)
- [Deployment Profiles](#deployment-profiles)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

The GWAS Diagram Service acts as a data provider for the gwas diagram in the GWAS Catalog UI. It indexes complex genetic association data, mapping them to cytogenetic regions and chromosomes, and makes them accessible via optimized REST endpoints.

### What is the GWAS Diagram?
The [GWAS Diagram](https://www.ebi.ac.uk/gwas/diagram) is the user interface that utilizes this backend API. This diagram tool visualizes associations in the Catalog according to their genomic location and trait in a more intuitive and visually appealing manner. It enables users to easily see which regions of the genome have the most associations with a trait category, providing visualization for these association "hotspots" and growth in the total number of associations over time.

**The Diagram Features:**
- **Karyotype display**: Dots group associations for a particular EFO term within a particular cytoband (genomic region).
- **Interactive dots**: Multiple associations may be in the same dot if their genomic locations are in the same cytoband. Clicking a dot reveals individual associations and links directly to Catalog data.
- **Trait categories**: Each dot is colored according to one of 17 parent categories that the EFO term belongs to. Color definitions are visible on the left-hand side of the page.
- **Hotspot tracking**: Visualizes the growth of associations over time in specific genomic regions.
- **Integration with other tools**: These colors are used consistently across the Catalog, e.g., in LocusZoom plots and LD plots.

### Parent Categories
The `ParentTrait` entity holds all parent categories for the diagram. The presentation is clustered, indexed, and filterable by these categories. This backend API makes this possible:
1. **UI Sidebar**: Used in the left bar of the UI.
2. **Color Coding**: Handled by JavaScript logic (see [goci-diagram-v2.js](https://github.com/EBISPOT/gwas-ui/blob/e4c04bf4c3191e76885d69ff1e3cf89c3e56d1cd/goci-interfaces/goci-ui/src/main/resources/static/js/goci-diagram-v2.js#L64)).
3. **Filtering**: The REST API can be filtered, e.g., `http://<api-server>/gwas/diagram-api/chromosomes/1?parent=nervous system disease`.

### Changing Parent Traits in the Diagram Loader
Parent traits and their corresponding children traits are pre-built into Solr for fast search during diagram loading.
- **Management**: The list of terms is stored in an external CSV file (`src/main/resources/parent_traits.csv`) to allow management without modifying the software.
- **OLS Integration**: The OLS API is interrogated during this process. If an EFO ID becomes deprecated in OLS, the CSV must be updated with the new ID.

---

### Key Features: 2 Modes of using this Diagram Service

- **Data Indexing**: Automated pipeline to extract, transform, and load GWAS association data from Oracle DB to Apache Solr.
- **Data API**: Optimized retrieval of association data by chromosome and cytogenetic region.

---

### CLI Operations

The application includes a built-in CLI for data management operations.

| Mode                             | Command | Description                                                               |
|----------------------------------|---------|---------------------------------------------------------------------------|
| **Data Indexing - Refresh Data** | `start --mode refresh-data` | Cleans Solr indexes and performs a full re-indexing from the DB and stops |
| **Data API - API Mode**          | `start --mode api-mode` | Starts the application as a REST API server for the gwas-ui to call       |

---

## Quick Start

<details>
<summary>Click to expand</summary>

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd gwas-diagram-service
   ```

2. Download Solr 8+ on your machine.

3. Start your solr:
   ```bash
   <solr-directory>/bin/solr start
   ```

4. Create core:
   ```bash
   ./solr create_core -c gwas_diagram
   ```

5. Configure Solr Managed Schema Using the Schema API
-----------------------------------------------------
The 9 fields required for indexing the diagram in Solr are parent_trait, cytogenetic_region, chromosome, facet, type, data, resource_name, traits, and category. Unlike the old school static schema management via manual xml edit, The project uses the modern Solr feature that allows Managing Schema using Schema API. Add fields dynamically via REST which updates the managed-schema automatically.

```bash
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"parent_trait",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"cytogenetic_region",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"chromosome",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"facet",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"type",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"data",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"resource_name",
     "type":"string",
     "indexed":true,
     "stored":true,
     "multiValued":false }
}' http://localhost:8983/solr/gwas_diagram/schema


curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"traits",
     "type":"string",
     "indexed":false,
     "stored":true,
     "docValues":false,
     "multiValued":true 
     }
}' http://localhost:8983/solr/gwas_diagram/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"category",
     "type":"text_general",
     "indexed":false,
     "stored":true,
     "docValues":false,
     "multiValued":false 
     }
}' http://localhost:8983/solr/gwas_diagram/schema
```

6. Build project:
   ```bash
   mvn clean install
   ```

7. Starting as a Data Loader for Full Re-indexing (Local):
   ```bash
   DB_URL=jdbc:postgresql://host:port/name \
   DB_USER=username DB_PASSWORD=password \
   java -Dspring.profiles.active=local -jar gwas-diagram-service.jar start --mode refresh-data
   ```

8. Starting as an API in Server Mode for the UI to call (Local):
   ```bash
   DB_URL=jdbc:postgresql://host:port/name \
   DB_USER=username DB_PASSWORD=password \
   java -Dspring.profiles.active=local -jar gwas-diagram-service.jar start --mode api-mode
   ```

</details>

---


## API Documentation

<details>
<summary>Click to expand</summary>

The API is accessible under the context path `/gwas/diagram-api`.

### 📖 Swagger UI
The service includes an interactive Swagger UI for API exploration and testing. It is automatically generated from the code using Springdoc OpenAPI.

- **URL**: [http://localhost:8989/gwas/diagram-api/swagger-ui/index.html](http://localhost:8989/gwas/diagram-api/swagger-ui/index.html)

### 🧬 Association Endpoint

This is the endpoint that powers the result that is seen whenever any of the colored trait dot is clicked in the UI, corresponding associations are retrieved and the UI displays it in a modal window
<details>
<summary><strong>GET /associations - Get Association Details</strong></summary>

Returns detailed SNP association data for a specific EFO trait and region.

**Query Parameters**
- `efo`: EFO Trait label/ID
- `region`: Cytogenetic region (e.g., 1q21)

**Example**
```bash
curl "http://localhost:8685/gwas/diagram-api/associations?efo=type+2+diabetes&region=1q21"
```
</details>

### 🗺️ Chromosome Endpoint
This is the endpoint that powers the retrieval and display of chromosomes (1-22, X and Y) data displayed on the diagram landing page and also when filtered by Parent Trait Category
<details>
<summary><strong>GET /chromosomes/{chrId} - Get Chromosomal Data</strong></summary>

Returns Solr documents mapped to a specific chromosome, used for rendering the diagram.

**Path Parameters**
- `chrId`: Chromosome ID (1-22, X, Y)

**Query Parameters**
- `parent`: (Optional) Filter by parent trait

**Example**
```bash
curl "http://localhost:8685/gwas/diagram-api/chromosomes/1?parent=Neoplasm"
```
</details>

### 📊 Parent Trait Statistics Count Endpoint

This is the endpoint that powers the statistical facet count of the traits belong to each Parent category, the gwas-diagram ui uses this on teh sidebar
<details>
<summary><strong>GET /stats - Get Global Statistics</strong></summary>

Returns summarized statistics about the indexed GWAS data.

**Example**
```bash
curl "http://localhost:8685/gwas/diagram-api/stats"
```
</details>

</details>

---

## Tech Stack

<details>
<summary>Click to expand</summary>

- **Backend**: Spring Boot 2.x (Java 8)
- **Database**: Postgres DB (Source)
- **Search Engine**: Apache Solr (Storage & Search)
- **CLI**: PicoCLI for command-line operations
- **Libraries**: Lombok, Jackson, Spring Data JPA, RestTemplate

</details>

---

## Requirements

<details>
<summary>Click to expand</summary>

- **Java 8** (LTS)
- **Maven 3.8+**
- **Oracle Database Access** (for data indexing)
- **Apache Solr 8.x+**
    - **Local**: [http://localhost:8983/solr](http://localhost:8983/solr)
    - **Dev**: [http://gwas-snoopy.ebi.ac.uk:8987/solr](http://gwas-snoopy.ebi.ac.uk:8987/solr)
    - **Pre-staging**: [http://gwas-garfield.ebi.ac.uk:8990/solr](http://gwas-garfield.ebi.ac.uk:8990/solr)
    - **Staging**: [http://ves-hx-7f.ebi.ac.uk:8990/solr](http://ves-hx-7f.ebi.ac.uk:8990/solr)
    - **Production**: [http://ves-pg-7f.ebi.ac.uk:8990/solr](http://ves-pg-7f.ebi.ac.uk:8990/solr)
    - **Fallback**: [http://ves-oy-7f.ebi.ac.uk:8990/solr](http://ves-oy-7f.ebi.ac.uk:8990/solr)
    - **Solr Core Name**: gwas_diagram

</details>

---


## Project Structure

<details>
<summary>Click to expand</summary>

The project follows a **Vertical Slice Architecture (Package by Feature)**, where each package contains the logic, data access, and DTOs related to a specific domain feature:

```text
src/main/java/uk/ac/ebi/spot/gwas/
├── association/    # Association retrieval and PostgreSQL interactions
├── chromosome/     # Chromosome-specific data and Solr integration
├── statistics/     # Diagram statistics and reporting
├── diagram_loader/ # Data indexing and Solr loading logic
├── ontology/       # EFO and OLS integration for trait hierarchies
├── solr/           # Shared Solr infrastructure and models
├── config/         # Shared application configurations
├── Application.java # Main Spring Boot entry point
└── Cli.java        # CLI entry point (PicoCLI)
```

</details>

---

## Configuration

<details>
<summary>Click to expand</summary>

The application is configured via standard Spring YAML files located in `src/main/resources`.

- `application.yml`: Base configuration (server port, context-path, Springdoc settings).
- `application-local.yml`: Local development settings (PostgreSQL driver, local Solr).
- `application-dev.yml`: Development environment settings.
- `api-docs.yaml`: Static OpenAPI specification (located in `src/main/resources/static`).

Key environment variables required for indexing:
- `DB_URL`: PostgreSQL JDBC connection string (e.g., `jdbc:postgresql://host:port/db`).
- `DB_USER`: Database username.
- `DB_PASSWORD`: Database password.

</details>

---

## Deployment Environments & Profiles

<details>
<summary>Click to expand</summary>

The application supports multiple deployment environments via Spring profiles:

### Environments
- **Dev**: [http://gwas-snoopy.ebi.ac.uk:8989/diagram-api](http://gwas-snoopy.ebi.ac.uk:8989/diagram-api)
- **Staging**: [http://gwas-garfield.ebi.ac.uk:8989/gwas/diagram-api](http://gwas-garfield.ebi.ac.uk:8989/gwas/diagram-api)
- **Production**: [https://www.ebi.ac.uk/gwas/diagram-api](https://www.ebi.ac.uk/gwas/diagram-api)

### Profiles
- **local**: Uses local Solr and DB settings.
- **dev**: Target development environment.
- **prestaging**: Target pre-staging environment.
- **staging**: Target staging environment.

Example running in dev:
```bash
java -jar -Dspring.profiles.active=dev gwas-diagram-service.jar start --mode api-mode
```

</details>

---

## Troubleshooting

<details>
<summary>Click to expand</summary>

- **Solr Connection Failure**: Verify the `url.server` and `url.solr` properties in the active profile's YAML file.
- **Database Driver Errors**: Ensure the Oracle JDBC driver is available and environment variables (`DB_URL`, etc.) are correctly set.
- **Port Conflict**: The default port is `8685`. Override using `-Dserver.port=XXXX` if needed.
- **Circular References**: If you encounter bean creation errors, ensure `spring.main.allow-circular-references` is set to `true` (default in `application.yml`).

</details>
