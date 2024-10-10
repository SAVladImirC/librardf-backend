package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.properties.AuthorProperties;
import mk.ukim.finki.librardf.properties.GenreProperties;
import mk.ukim.finki.librardf.requests.Author.InsertRequest;
import mk.ukim.finki.librardf.requests.Author.UpdateRequest;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Repository
public class AuthorRepository extends BaseRepository{
    Property id;
    Property name;
    Property surname;
    Property genres;
    Property dob;
    Property dod;
    Property nationality;
    Property imageUrl;
    Property biography;
    Property books;

    public AuthorRepository(RdfConfig rdfConfig) {
        super(rdfConfig);
        this.id = ResourceFactory.createProperty(AuthorProperties.ID);
        this.name = ResourceFactory.createProperty(AuthorProperties.NAME);
        this.surname = ResourceFactory.createProperty(AuthorProperties.SURNAME);
        this.genres = ResourceFactory.createProperty(AuthorProperties.GENRES);
        this.dob = ResourceFactory.createProperty(AuthorProperties.DOB);
        this.dod = ResourceFactory.createProperty(AuthorProperties.DOD);
        this.nationality = ResourceFactory.createProperty(AuthorProperties.NATIONALITY);
        this.imageUrl = ResourceFactory.createProperty(AuthorProperties.IMAGE_URL);
        this.biography = ResourceFactory.createProperty(AuthorProperties.BIOGRAPHY);
        this.books = ResourceFactory.createProperty(AuthorProperties.BOOKS);
    }

    public List<Author> getAllAuthors(){
        List<Author> authors = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:dob ?dob . " +
                "    ?author finki:dod ?dod . " +
                "    ?author finki:genres ?genres . " +
                "    ?author finki:nationality ?nationality . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?author finki:biography ?biography . " +
                "    ?author finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Author author = new Author();

                // Set author properties
                setAuthor(soln, author);

                // Set the author in the book
                authors.add(author);
            }
        }
        return authors;
    }

    public List<Author> getAllAuthorsFiltered(String filter){
        List<Author> authors = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:dob ?dob . " +
                "    ?author finki:dod ?dod . " +
                "    ?author finki:nationality ?nationality . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?author finki:biography ?biography . " +
                "    ?author finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "    FILTER (CONTAINS(LCASE(?name), LCASE(\"" + filter + "\")) || " +
                "            CONTAINS(LCASE(?surname), LCASE(\"" + filter + "\"))) " +
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Author author = new Author();

                // Set author properties
                setAuthor(soln, author);

                // Set the author in the book
                authors.add(author);
            }
        }
        return authors;
    }

    public List<Author> getAllAuthorsByGenre(int[] genreIds){
        List<Author> authors = new ArrayList<>();
        Model model = loadModel();

        StringBuilder genresFilter = new StringBuilder("VALUES ?genres {");

        for (int genreId : genreIds) {
            Resource g = model.getResource(GenreProperties.BASE + genreId);
            genresFilter.append("<").append(g.toString()).append("> ");
        }

        genresFilter.append("}");

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:dob ?dob . " +
                "    ?author finki:dod ?dod . " +
                "    ?author finki:nationality ?nationality . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?author finki:biography ?biography . " +
                "    ?author finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                genresFilter +
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Author author = new Author();

                // Set author properties
                setAuthor(soln, author);

                // Set the author in the book
                authors.add(author);
            }
        }
        return authors;
    }

    public Author getAuthorById(int id){
        Model model = loadModel();
        Author author = new Author();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?author finki:id ?id . " +
                "    ?author finki:id \"" + id + "\" . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:dob ?dob . " +
                "    ?author finki:dod ?dod . " +
                "    ?author finki:nationality ?nationality . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?author finki:biography ?biography . " +
                "    ?author finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                // Set author properties
                setAuthor(soln, author);
            }
        }
        return author;
    }

    public boolean insert(InsertRequest request){
        try{
            Model model = loadModel();

            Random random = new Random();
            int randomId = random.nextInt(1000000) + 1;

            Resource authorRes = model.createResource(AuthorProperties.BASE + randomId)
                    .addProperty(id, String.valueOf(randomId))
                    .addProperty(name, request.name)
                    .addProperty(surname, request.surname)
                    .addProperty(dob, (request.dob != null) ? request.dob.toString() : "")
                    .addProperty(dod, (request.dod != null) ? request.dod.toString() : "")
                    .addProperty(nationality, request.nationality)
                    .addProperty(imageUrl, request.imageUrl)
                    .addProperty(biography, request.biography);

            for(int genreId: request.genres){
                Resource genre = model.getResource(GenreProperties.BASE + genreId);
                authorRes.addProperty(genres, genre);
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(UpdateRequest request){
        try{
            Model model = loadModel();
            Resource authorRes = model.getResource(AuthorProperties.BASE + request.id);

            authorRes.removeAll(name).addProperty(name, request.name);
            authorRes.removeAll(surname).addProperty(surname, request.surname);
            authorRes.removeAll(dob).addProperty(dob, (request.dob != null) ? request.dob.toString() : "");
            authorRes.removeAll(dod).addProperty(dod, (request.dod != null) ? request.dod.toString() : "");
            authorRes.removeAll(nationality).addProperty(nationality, request.nationality);
            authorRes.removeAll(imageUrl).addProperty(imageUrl, request.imageUrl);
            authorRes.removeAll(biography).addProperty(biography, request.biography);;

            authorRes.removeAll(genres);
            for(int genreId: request.genres){
                Resource genre = model.getResource(GenreProperties.BASE + genreId);
                authorRes.addProperty(genres, genre);
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void setAuthor(QuerySolution soln, Author author) {
        String[] genreNames = soln.get("genre_names").toString().split(",");
        String[] genreIds = soln.get("genre_ids").toString().split(",");
        List<Genre> genres = new ArrayList<>();

        author.setId(soln.get("id").asLiteral().getInt());
        author.setName(soln.get("name").toString());
        author.setSurname(soln.get("surname").toString());
        author.setDob(LocalDate.parse(soln.get("dob").toString()));
        author.setDod(LocalDate.parse(soln.get("dod").toString()));
        author.setNationality(soln.get("nationality").toString());
        author.setImageUrl(soln.get("image_url").toString());
        author.setBiography(soln.get("biography").toString());

        for(int i = 0; i < genreNames.length; i++){
            Genre genre = new Genre();
            genre.setId(Integer.parseInt(genreIds[i]));
            genre.setName(genreNames[i]);
            genres.add(genre);
        }

        author.setGenres(genres);
    }
}
