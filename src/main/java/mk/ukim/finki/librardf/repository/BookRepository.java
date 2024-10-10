package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.properties.AuthorProperties;
import mk.ukim.finki.librardf.properties.BookProperties;
import mk.ukim.finki.librardf.properties.GenreProperties;
import mk.ukim.finki.librardf.requests.Book.InsertRequest;
import mk.ukim.finki.librardf.requests.Book.UpdateRequest;
import mk.ukim.finki.librardf.utilities.RandomStringGenerator;
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

@Repository
public class BookRepository extends BaseRepository{
    Property title;
    Property publisher;
    Property shortDescription;
    Property fullDescription;
    Property language;
    Property isbn;
    Property imageLink;
    Property link;
    Property year;
    Property pages;
    Property country;
    Property authorProp;
    Property genres;
    public BookRepository(RdfConfig rdfConfig) {
        super(rdfConfig);
        this.title = ResourceFactory.createProperty(BookProperties.TITLE);
        this.publisher = ResourceFactory.createProperty(BookProperties.PUBLISHER);
        this.shortDescription = ResourceFactory.createProperty(BookProperties.SHORT_DESCRIPTION);
        this.fullDescription = ResourceFactory.createProperty(BookProperties.FULL_DESCRIPTION);
        this.language = ResourceFactory.createProperty(BookProperties.LANGUAGE);
        this.isbn = ResourceFactory.createProperty(BookProperties.ISBN);
        this.imageLink = ResourceFactory.createProperty(BookProperties.IMAGE_LINK);
        this.link = ResourceFactory.createProperty(BookProperties.LINK);
        this.year = ResourceFactory.createProperty(BookProperties.YEAR);
        this.pages = ResourceFactory.createProperty(BookProperties.PAGES);
        this.country = ResourceFactory.createProperty(BookProperties.COUNTRY);
        this.authorProp = ResourceFactory.createProperty(BookProperties.AUTHOR);
        this.genres = ResourceFactory.createProperty(BookProperties.GENRES);
    }

    public Book getBookByIsbn(String isbn){
        Book book = new Book();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:isbn \"" + isbn + "\" . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?image_link . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?book finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Author author = new Author();

                // Set book properties
                book.setIsbn(soln.get("isbn").toString());
                book.setShortDescription(soln.get("short_description").toString());
                book.setFullDescription(soln.get("full_description").toString());
                book.setTitle(soln.get("title").toString());
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("image_link").toString());
                book.setLanguage(soln.get("language").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setImageUrl(soln.get("image_url").toString());

                String[] genreNames = soln.get("genre_names").toString().split(",");
                String[] genreIds = soln.get("genre_ids").toString().split(",");
                List<Genre> genres = new ArrayList<>();

                for(int i = 0; i < genreNames.length; i++){
                    Genre genre = new Genre();
                    genre.setId(Integer.parseInt(genreIds[i]));
                    genre.setName(genreNames[i]);
                    genres.add(genre);
                }
                // Set the author in the book
                book.setAuthor(author);

                // Set the genres in the book
                book.setGenres(genres);
            } else return null;
        }
        return book;
    }

    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?image_link . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?book finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Book book = new Book();
                Author author = new Author();

                // Set book properties
                book.setIsbn(soln.get("isbn").toString());
                book.setShortDescription(soln.get("short_description").toString());
                book.setFullDescription(soln.get("full_description").toString());
                book.setTitle(soln.get("title").toString());
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("image_link").toString());
                book.setLanguage(soln.get("language").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setImageUrl(soln.get("image_url").toString());

                String[] genreNames = soln.get("genre_names").toString().split(",");
                String[] genreIds = soln.get("genre_ids").toString().split(",");
                List<Genre> genres = new ArrayList<>();

                for(int i = 0; i < genreNames.length; i++){
                    Genre genre = new Genre();
                    genre.setId(Integer.parseInt(genreIds[i]));
                    genre.setName(genreNames[i]);
                    genres.add(genre);
                }

                // Set the author in the book
                book.setAuthor(author);

                // Set the genres in the book
                book.setGenres(genres);

                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getAllBooksFiltered(String filter){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?image_link . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?book finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "    FILTER (CONTAINS(LCASE(?title), LCASE(\"" + filter + "\")) || " +
                "            CONTAINS(LCASE(?isbn), LCASE(\"" + filter + "\"))) " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?link ?pages ?publisher ?year ?id ?name ?surname ?image_url";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Book book = new Book();
                Author author = new Author();

                // Set book properties
                book.setIsbn(soln.get("isbn").toString());
                book.setShortDescription(soln.get("short_description").toString());
                book.setFullDescription(soln.get("full_description").toString());
                book.setTitle(soln.get("title").toString());
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("image_link").toString());
                book.setLanguage(soln.get("language").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setImageUrl(soln.get("image_url").toString());

                String[] genreNames = soln.get("genre_names").toString().split(",");
                String[] genreIds = soln.get("genre_ids").toString().split(",");
                List<Genre> genres = new ArrayList<>();

                for(int i = 0; i < genreNames.length; i++){
                    Genre genre = new Genre();
                    genre.setId(Integer.parseInt(genreIds[i]));
                    genre.setName(genreNames[i]);
                    genres.add(genre);
                }

                // Set the author in the book
                book.setAuthor(author);

                // Set the genres in the book
                book.setGenres(genres);

                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getAllBooksByAuthor(int authorId){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?image_link . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:id \"" + authorId + "\" . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?book finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?image_link ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Book book = new Book();
                Author author = new Author();

                // Set book properties
                book.setIsbn(soln.get("isbn").toString());
                book.setShortDescription(soln.get("short_description").toString());
                book.setFullDescription(soln.get("full_description").toString());
                book.setTitle(soln.get("title").toString());
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("image_link").toString());
                book.setLanguage(soln.get("language").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setImageUrl(soln.get("image_url").toString());

                String[] genreNames = soln.get("genre_names").toString().split(",");
                String[] genreIds = soln.get("genre_ids").toString().split(",");
                List<Genre> genres = new ArrayList<>();

                for(int i = 0; i < genreNames.length; i++){
                    Genre genre = new Genre();
                    genre.setId(Integer.parseInt(genreIds[i]));
                    genre.setName(genreNames[i]);
                    genres.add(genre);
                }

                // Set the author in the book
                book.setAuthor(author);

                // Set the genres in the book
                book.setGenres(genres);

                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getAllBooksByGenre(int[] genreIds){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        StringBuilder genresFilter = new StringBuilder("VALUES ?genres {");

        for (int genreId : genreIds) {
            Resource g = model.getResource(GenreProperties.BASE + genreId);
            genresFilter.append("<").append(g.toString()).append("> ");
        }

        genresFilter.append("}");

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url (GROUP_CONCAT(?genre_id; SEPARATOR=\",\") AS ?genre_ids) (GROUP_CONCAT(?genre_name; SEPARATOR=\",\") AS ?genre_names) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?imageLink . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "    ?author finki:image_url ?image_url . " +
                "    ?book finki:genres ?genres . " +
                "    ?genres finki:genre_id ?genre_id . " +
                "    ?genres finki:genre_name ?genre_name . " +
                genresFilter +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?pages ?publisher ?year ?id ?name ?surname ?image_url";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Book book = new Book();
                Author author = new Author();

                // Set book properties
                book.setIsbn(soln.get("isbn").toString());
                book.setShortDescription(soln.get("short_description").toString());
                book.setFullDescription(soln.get("full_description").toString());
                book.setTitle(soln.get("title").toString());
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("imageLink").toString());
                book.setLanguage(soln.get("language").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());
                author.setImageUrl(soln.get("image_url").toString());

                // Set the author in the book
                book.setAuthor(author);

                books.add(book);
            }
        }
        return books;
    }

    public boolean insert(InsertRequest request){
        try{
            Model model = loadModel();

            Resource author = model.getResource(AuthorProperties.BASE + request.authorId);
            String randomIsbn = RandomStringGenerator.generateRandomString(10);

            Resource bookRes = model.createResource(BookProperties.BASE + randomIsbn)
                    .addProperty(title, request.title)
                    .addProperty(publisher, request.publisher)
                    .addProperty(shortDescription, request.shortDescription)
                    .addProperty(fullDescription, request.fullDescription)
                    .addProperty(language, request.language)
                    .addProperty(isbn, randomIsbn)
                    .addProperty(imageLink, request.imageLink)
                    .addProperty(year, String.valueOf(request.year))
                    .addProperty(pages, String.valueOf(request.pages))
                    .addProperty(country, request.country)
                    .addProperty(authorProp, author);

            for(int genreId: request.genres){
                Resource genre = model.getResource(GenreProperties.BASE + genreId);
                bookRes.addProperty(genres, genre);
            }

            saveModel(model);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(UpdateRequest request) {
        try{
            Model model = loadModel();

            Resource bookRes = model.getResource(BookProperties.BASE + request.isbn);
            Resource author = model.getResource(AuthorProperties.BASE + request.authorId);

            // Update book properties
            bookRes.removeAll(title).addProperty(title, request.title);
            bookRes.removeAll(publisher).addProperty(publisher, request.publisher);
            bookRes.removeAll(shortDescription).addProperty(shortDescription, request.shortDescription);
            bookRes.removeAll(fullDescription).addProperty(fullDescription, request.fullDescription);
            bookRes.removeAll(language).addProperty(language, request.language);
            bookRes.removeAll(isbn).addProperty(isbn, request.isbn);
            bookRes.removeAll(imageLink).addProperty(imageLink, request.imageLink);
            bookRes.removeAll(year).addProperty(year, String.valueOf(request.year));
            bookRes.removeAll(pages).addProperty(pages, String.valueOf(request.pages));
            bookRes.removeAll(country).addProperty(country, request.country);
            bookRes.removeAll(authorProp).addProperty(authorProp, author);

            bookRes.removeAll(genres);
            for(int genreId: request.genres){
                Resource genre = model.getResource(GenreProperties.BASE + genreId);
                bookRes.addProperty(genres, genre);
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}