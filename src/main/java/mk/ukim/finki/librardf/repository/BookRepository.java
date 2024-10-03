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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    Property genre;
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
        this.genre = ResourceFactory.createProperty(BookProperties.GENRE);
    }

    public boolean insert(Book book){
        try{
            Model model = loadModel();

            Resource author = model.getResource(AuthorProperties.BASE + book.getAuthor().getId());

            Resource bookRes = model.createResource(BookProperties.BASE + book.getIsbn())
                    .addProperty(title, book.getTitle())
                    .addProperty(publisher, book.getPublisher())
                    .addProperty(shortDescription, book.getShortDescription())
                    .addProperty(fullDescription, book.getFullDescription())
                    .addProperty(language, book.getLanguage())
                    .addProperty(isbn, book.getIsbn())
                    .addProperty(imageLink, book.getImageLink())
                    .addProperty(link, book.getLink())
                    .addProperty(year, String.valueOf(book.getYear()))
                    .addProperty(pages, String.valueOf(book.getPages()))
                    .addProperty(country, book.getCountry())
                    .addProperty(authorProp, author);

           for(GENRE g: book.getGenres()){
                bookRes.addProperty(genre, g.toString());
           }

           saveModel(model);
           return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Book book) {
        try{
            Model model = loadModel();

            Resource bookRes = model.getResource(BookProperties.BASE + book.getIsbn());
            Resource author = model.getResource(AuthorProperties.BASE + book.getAuthor().getId());

            // Update book properties
            bookRes.removeAll(title).addProperty(title, book.getTitle());
            bookRes.removeAll(publisher).addProperty(publisher, book.getPublisher());
            bookRes.removeAll(shortDescription).addProperty(shortDescription, book.getShortDescription());
            bookRes.removeAll(fullDescription).addProperty(fullDescription, book.getFullDescription());
            bookRes.removeAll(language).addProperty(language, book.getLanguage());
            bookRes.removeAll(isbn).addProperty(isbn, book.getIsbn());
            bookRes.removeAll(imageLink).addProperty(imageLink, book.getImageLink());
            bookRes.removeAll(year).addProperty(year, String.valueOf(book.getYear()));
            bookRes.removeAll(pages).addProperty(pages, String.valueOf(book.getPages()));
            bookRes.removeAll(country).addProperty(country, book.getCountry());
            bookRes.removeAll(authorProp).addProperty(authorProp, author);;

            bookRes.removeAll(authorProp);
            for(GENRE g: book.getGenres()){
                bookRes.addProperty(genre, g.toString());
            }

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Book getBookByIsbn(String isbn){
        Book book = new Book();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:isbn \"" + isbn + "\" . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?imageLink . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:link ?link . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname";

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
                GENRE[] genres = Arrays.stream(soln.get("genres_").toString().split(",")).map(GENRE::valueOf).toArray(GENRE[]::new);
                book.setGenres(genres);
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("imageLink").toString());
                book.setLanguage(soln.get("language").toString());
                book.setLink(soln.get("link").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());

                // Set the author in the book
                book.setAuthor(author);
            } else return null;
        }
        return book;
    }

    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?imageLink . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:link ?link . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname";

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
                GENRE[] genres = Arrays.stream(soln.get("genres_").toString().split(",")).map(GENRE::valueOf).toArray(GENRE[]::new);
                book.setGenres(genres);
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("imageLink").toString());
                book.setLanguage(soln.get("language").toString());
                book.setLink(soln.get("link").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());

                // Set the author in the book
                book.setAuthor(author);

                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getAllBooksByAuthor(int authorId){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?imageLink . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:link ?link . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:id \"" + authorId + "\" . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname";

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
                GENRE[] genres = Arrays.stream(soln.get("genres_").toString().split(",")).map(GENRE::valueOf).toArray(GENRE[]::new);
                book.setGenres(genres);
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("imageLink").toString());
                book.setLanguage(soln.get("language").toString());
                book.setLink(soln.get("link").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());

                // Set the author in the book
                book.setAuthor(author);

                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getAllBooksByGenre(GENRE[] genres_){
        List<Book> books = new ArrayList<>();
        Model model = loadModel();

        StringBuilder genresFilter = new StringBuilder("VALUES ?genres {");

        for (GENRE genre : genres_) {
            genresFilter.append("\"").append(genre).append("\" ");
        }

        genresFilter.append("}");

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname (GROUP_CONCAT(?genres; SEPARATOR=\",\") AS ?genres_) " +
                "WHERE { " +
                "    ?book finki:isbn ?isbn . " +
                "    ?book finki:title ?title . " +
                "    ?book finki:short_description ?short_description . " +
                "    ?book finki:full_description ?full_description . " +
                "    ?book finki:genres ?genres . " +
                genresFilter +
                "    ?book finki:country ?country . " +
                "    ?book finki:image_link ?imageLink . " +
                "    ?book finki:language ?language . " +
                "    ?book finki:link ?link . " +
                "    ?book finki:pages ?pages . " +
                "    ?book finki:publisher ?publisher . " +
                "    ?book finki:year ?year . " +
                "    ?book finki:author ?author . " +
                "    ?author finki:id ?id . " +
                "    ?author finki:name ?name . " +
                "    ?author finki:surname ?surname . " +
                "} GROUP BY ?isbn ?title ?short_description ?full_description ?country ?imageLink ?language ?link ?pages ?publisher ?year ?id ?name ?surname";

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
                GENRE[] genres = Arrays.stream(soln.get("genres_").toString().split(",")).map(GENRE::valueOf).toArray(GENRE[]::new);
                book.setGenres(genres);
                book.setCountry(soln.get("country").toString());
                book.setImageLink(soln.get("imageLink").toString());
                book.setLanguage(soln.get("language").toString());
                book.setLink(soln.get("link").toString());
                book.setPages(soln.get("pages").asLiteral().getInt());
                book.setPublisher(soln.get("publisher").toString());
                book.setYear(soln.get("year").asLiteral().getInt());

                // Set author properties
                author.setId(soln.get("id").asLiteral().getInt());
                author.setName(soln.get("name").toString());
                author.setSurname(soln.get("surname").toString());

                // Set the author in the book
                book.setAuthor(author);

                books.add(book);
            }
        }
        return books;
    }
}