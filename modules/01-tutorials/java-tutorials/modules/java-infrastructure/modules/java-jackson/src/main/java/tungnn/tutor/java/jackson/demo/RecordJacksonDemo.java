package tungnn.tutor.java.jackson.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordJacksonDemo {

  static void main() throws JsonProcessingException {
    // ObjectMapper is the core Jackson class responsible for
    // converting between Java objects and JSON
    ObjectMapper mapper = new ObjectMapper();

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

  /**
   * Java record used as an immutable DTO. Jackson maps JSON properties to record components via the
   * canonical constructor.
   */
  record User(String name, int age) {}
}
