package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.GENRE;
import mk.ukim.finki.librardf.properties.AuthorProperties;
import mk.ukim.finki.librardf.properties.BookProperties;
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
import java.util.Arrays;
import java.util.List;

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
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
                "WHERE { " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:dob ?dob . " +
                "    ?author finki:dod ?dod . " +
                "    ?author finki:nationality ?nationality . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?author finki:biography ?biography . " +
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Author author = new Author();

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setDob(LocalDate.parse(soln.get("dob").toString()));
                author.setDod(LocalDate.parse(soln.get("dod").toString()));
                author.setNationality(soln.get("nationality").toString());
                author.setImageUrl(soln.get("image_url").toString());
                author.setBiography(soln.get("biography").toString());

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
                "SELECT ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
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
                "} GROUP BY ?id ?name ?surname ?dob ?dod ?nationality ?image_url ?biography";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setDob(LocalDate.parse(soln.get("dob").toString()));
                author.setDod(LocalDate.parse(soln.get("dod").toString()));
                author.setNationality(soln.get("nationality").toString());
                author.setImageUrl(soln.get("image_url").toString());
                author.setBiography(soln.get("biography").toString());
            }
            else return null;
        }
        return author;
    }

    public boolean insert(Author author){
        try{
            Model model = loadModel();

            Resource authorRes = model.createResource(AuthorProperties.BASE + author.getId())
                    .addProperty(id, String.valueOf(author.getId()))
                    .addProperty(name, author.getName())
                    .addProperty(surname, author.getSurname())
                    .addProperty(dob, author.getDob().toString())
                    .addProperty(dod, author.getDod().toString())
                    .addProperty(nationality, author.getNationality())
                    .addProperty(imageUrl, author.getImageUrl())
                    .addProperty(biography, author.getBiography());

            for(GENRE g: author.getGenres()){
                authorRes.addProperty(genres, g.toString());
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Author author){
        try{
            Model model = loadModel();
            Resource authorRes = model.getResource(AuthorProperties.BASE + author.getId());

            authorRes.removeAll(id).addProperty(id, String.valueOf(author.getId()));
            authorRes.removeAll(name).addProperty(name, author.getName());
            authorRes.removeAll(surname).addProperty(surname, author.getSurname());
            authorRes.removeAll(dob).addProperty(dob, author.getDob().toString());
            authorRes.removeAll(dod).addProperty(dod, author.getDod().toString());
            authorRes.removeAll(nationality).addProperty(nationality, author.getNationality());
            authorRes.removeAll(imageUrl).addProperty(imageUrl, author.getImageUrl());
            authorRes.removeAll(biography).addProperty(biography, author.getBiography());;

            authorRes.removeAll(genres);
            for(GENRE g: author.getGenres()){
                authorRes.addProperty(genres, g.toString());
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
