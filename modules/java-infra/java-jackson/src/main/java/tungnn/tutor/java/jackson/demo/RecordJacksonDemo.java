package tungnn.tutor.java.jackson.demo;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class RecordJacksonDemo {

    static void main() {
        // ObjectMapper is the core Jackson class responsible for
        // converting between Java objects and JSON
        ObjectMapper mapper = JsonMapper.builder().build();

        // --------------------
        // Serialization
        // --------------------
        User user = new User("Aditi", 27);

        // Convert Java record to JSON string
        String jsonStr = mapper.writeValueAsString(user);
        System.out.println(jsonStr); // Output: {"name":"Aditi","age":27}

        // --------------------
        // Deserialization
        // --------------------
        // Convert JSON string back to Java record
        User read = mapper.readValue(jsonStr, User.class);

        // Access record components via accessor methods
        System.out.println(read.name() + " - " + read.age());
    }

    record User(String name, int age) {}
}
