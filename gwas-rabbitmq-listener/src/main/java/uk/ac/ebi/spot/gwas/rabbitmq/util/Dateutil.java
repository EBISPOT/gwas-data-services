package uk.ac.ebi.spot.gwas.rabbitmq.util;

        import org.joda.time.LocalDate;
        import org.joda.time.format.DateTimeFormatter;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import uk.ac.ebi.spot.gwas.deposition.util.DateTimeCommon;
        import uk.ac.ebi.spot.gwas.rabbitmq.consumer.PublicationRabbitConsumer;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;

public class Dateutil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
    private static final Logger log = LoggerFactory.getLogger(PublicationRabbitConsumer.class);

    public static Date convertLocalDatetoSqlDate(String pubDate) {


        Date sqlPubDate = null;
        try {
            sqlPubDate = sdf.parse(pubDate);
        } catch (ParseException ex) {
            log.error("Exception in parsing date {} {}", ex.getMessage(), ex);
        }
        return sqlPubDate;
    }
}


