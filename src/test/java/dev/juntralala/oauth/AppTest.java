package dev.juntralala.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.StringWriter;


@SpringBootTest(classes = App.class)
public class AppTest {

    private static class Name {
        public String fisrtName;
        public String middleName;
        public String lastName;
    }

    @Autowired
    ObjectMapper json;

    @Test
    public void json() throws JsonProcessingException {
//        json.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Name name = new Name();
        name.fisrtName = "Muhammad";
        name.middleName = null;
        name.lastName = "Junaidi";

        String result = json.writeValueAsString(name);

        System.out.println(result);
    }

}
