 public static void main(String[] args) {
        log.info("Mapping Pipeline started");
        ApplicationContext ctx = new SpringApplicationBuilder(MappingPipelineApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("Mapping Pipeline finished");
        SpringApplication.exit(ctx);

    }