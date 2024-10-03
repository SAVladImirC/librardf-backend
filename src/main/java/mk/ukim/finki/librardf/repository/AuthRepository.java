package mk.ukim.finki.librardf.repository;

import mk.ukim.finki.librardf.configuration.RdfConfig;
import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.models.GENRE;
import mk.ukim.finki.librardf.models.ROLE;
import mk.ukim.finki.librardf.models.User;
import mk.ukim.finki.librardf.properties.AuthorProperties;
import mk.ukim.finki.librardf.properties.UserProperties;
import mk.ukim.finki.librardf.requests.User.LoginRequest;
import mk.ukim.finki.librardf.requests.User.RegisterRequest;
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
import java.util.Random;

@Repository
public class AuthRepository extends BaseRepository{
    Property id;
    Property name;
    Property surname;
    Property email;
    Property password;
    Property address;
    Property role;
    public AuthRepository(RdfConfig rdfConfig) {
        super(rdfConfig);
        this.id = ResourceFactory.createProperty(UserProperties.ID);
        this.name = ResourceFactory.createProperty(UserProperties.NAME);
        this.surname = ResourceFactory.createProperty(UserProperties.SURNAME);
        this.email = ResourceFactory.createProperty(UserProperties.EMAIL);
        this.password = ResourceFactory.createProperty(UserProperties.PASSWORD);
        this.address = ResourceFactory.createProperty(UserProperties.ADDRESS);
        this.role = ResourceFactory.createProperty(UserProperties.ROLE);
    }

    public boolean register(RegisterRequest request){
        try{
            Model model = loadModel();

            Random random = new Random();
            int randomId = random.nextInt(1000000) + 1;

            model.createResource(UserProperties.BASE + randomId)
                    .addProperty(id, String.valueOf(randomId))
                    .addProperty(name, request.name)
                    .addProperty(surname, request.surname)
                    .addProperty(email, request.email)
                    .addProperty(password, request.password)
                    .addProperty(address, request.address)
                    .addProperty(role, ROLE.USER.name());

            saveModel(model);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public User login(LoginRequest request){
        Model model = loadModel();
        User user = new User();

        String sparqlQuery = "PREFIX finki: <http://finki.ukim.mk/> " +
                "SELECT ?id ?name ?surname ?email ?address ?role " +
                "WHERE { " +
                "    ?user finki:user_id ?id . " +
                "    ?user finki:user_name ?name . " +
                "    ?user finki:user_surname ?surname . " +
                "    ?user finki:role ?role . " +
                "    ?user finki:email ?email . " +
                "    ?user finki:email \"" + request.email +"\" . " +
                "    ?user finki:password \"" + request.password +"\" . " +
                "    ?user finki:address ?address . " +
                "}";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                // Set user properties
                user.setId(soln.get("id").asLiteral().getInt());
                user.setName(soln.get("name").toString());
                user.setRole(ROLE.valueOf(soln.get("role").toString()));
                user.setSurname(soln.get("surname").toString());
                user.setEmail(soln.get("email").toString());
                user.setAddress(soln.get("address").toString());
            }
            else return null;
        }
        return user;
    }
}