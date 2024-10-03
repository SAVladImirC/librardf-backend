package mk.ukim.finki.librardf.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RdfConfig {
    @Value("${rdf.file.path}")
    private String rdfFilePath;

    public String getRdfFilePath() {
        return rdfFilePath;
    }
}
