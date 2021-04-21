package utils;


import com.google.common.base.Strings;
import facades.UserFacade;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Populate {
    private final EntityManagerFactory emf;


    public static void main(String[] args) {
        new Populate(EMF_Creator.createEntityManagerFactory()).populateAll();
    }

    public Populate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<String> populateAll() {
        List<String> populated = new ArrayList<>();
        if(populateUsers())
            populated.add("users");

        return populated;
    }

    /**
     *
     * @return Boolean regarding table being populated or not.
     *
     * */
    public boolean populateUsers() throws IllegalArgumentException {
        UserFacade userFacade = UserFacade.getUserFacade(this.emf);

        if (!userFacade.getUsers().isEmpty()) return false;

        // NOTICE: Always set your password as environment variables.
        String password_admin = "test";
        String password_user = "test";

        boolean isDeployed = System.getenv("DEPLOYED") != null;
        if(isDeployed) {
            password_user = System.getenv("PASSWORD_DEFAULT_USER");
            password_admin = System.getenv("PASSWORD_DEFAULT_ADMIN");

            // Do not allow "empty" passwords in production.
            if(Strings.isNullOrEmpty(password_admin.trim()) || Strings.isNullOrEmpty(password_user.trim()))
                throw new IllegalArgumentException("Passwords were empty? Are environment variables: [PASSWORD_DEFAULT_USER, PASSWORD_DEFAULT_ADMIN] set?");
        }

        userFacade.create("user", password_user, new ArrayList<>());
        userFacade.create("admin", password_admin, Collections.singletonList("admin"));

        return true;
    }

}
