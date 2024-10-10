package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.properties.GenreProperties;
import mk.ukim.finki.librardf.requests.Genre.UpdateRequest;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class GenreRepository extends BaseRepository{
    Property id;
    Property name;

    private final ReentrantLock lock = new ReentrantLock();

    public GenreRepository(RdfConfig rdfConfig) {
        super(rdfConfig);
        this.id = ResourceFactory.createProperty(GenreProperties.ID);
        this.name = ResourceFactory.createProperty(GenreProperties.NAME);
    }

    public List<Genre> getAllGenres(){
        List<Genre> genres = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name " +
                "WHERE { " +
                "    ?genre finki:genre_id ?id . " +
                "    ?genre finki:genre_name ?name . " +
                "} GROUP BY ?id ?name ";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Genre genre = new Genre();

                // Set genre properties
                genre.setId(soln.get("id").asLiteral().getInt());
                genre.setName(soln.get("name").toString());

                genres.add(genre);
            }
        }
        return genres;
    }

    public Genre getGenreById(int id){
        Model model = loadModel();
        Genre genre = new Genre();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name " +
                "WHERE { " +
                "    ?genre finki:genre_id ?id . " +
                "    ?genre finki:genre_id \"" + id + "\" . " +
                "    ?genre finki:genre_name ?name . " +
                "} GROUP BY ?id ?name ";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                // Set genre properties
                genre.setId(soln.get("id").asLiteral().getInt());
                genre.setName(soln.get("name").toString());

            }
            else return null;
        }
        return genre;
    }

    public boolean insert(String name){
        try{
            lock.lock();
            Model model = loadModel();

            Random random = new Random();
            int randomId = random.nextInt(1000000) + 1;

            model.createResource(GenreProperties.BASE + randomId)
                    .addProperty(id, String.valueOf(randomId))
                    .addProperty(this.name, name);

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            lock.unlock();
        }
    }

    public boolean insertAll(String[] names){
        for (String s : names) {
            insert(s);
        }
        return true;
    }

    public boolean update(UpdateRequest request){
        try{
            Model model = loadModel();
            Resource genreRes = model.getResource(GenreProperties.BASE + request.id);

            genreRes.removeAll(name).addProperty(name, request.name);

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}