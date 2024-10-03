package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;

public class BaseRepository {
    protected final RdfConfig rdfConfig;

    public BaseRepository(RdfConfig rdfConfig) {
        this.rdfConfig = rdfConfig;
    }

    protected Model loadModel() {
        Model model = ModelFactory.createDefaultModel();
        try (InputStream in = new FileInputStream(rdfConfig.getRdfFilePath())) {
            model.read(in, null, "TTL");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    protected void saveModel(Model modifiedModel) {
        try (OutputStream out = new FileOutputStream(rdfConfig.getRdfFilePath())) {
            modifiedModel.write(out, "TTL");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}