package tungnn.tutor.java.jackson.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.jackson.JsonException;
import tungnn.tutor.java.jackson.v3.JsonUtils;
import tungnn.tutor.java.jackson.v3.ObjectMapperCreator;

public class JsonDemoRunner {

    static void main(String[] args) {
        ObjectMapper mapper = ObjectMapperCreator.createPretty();

        // =========================
        // 1. Object -> JSON -> Object
        // =========================
        User user = new User("Tung", 25);

        String json = JsonUtils.toJson(mapper, user);
        System.out.println("JSON: " + json);

        User userBack = JsonUtils.fromJson(mapper, json, User.class);
        System.out.println("Object: " + userBack.name() + ", " + userBack.age());

        // =========================
        // 2. List<T> (Generic)
        // =========================
        List<User> users = new ArrayList<>();
        users.add(new User("A", 20));
        users.add(new User("B", 30));

        String listJson = JsonUtils.toJson(mapper, users);
        System.out.println("List JSON: " + listJson);

        List<User> usersBack = JsonUtils.fromJson(mapper, listJson, new TypeReference<>() {});
        System.out.println("List size: " + usersBack.size());

        // =========================
        // 3. File
        // =========================
        Path path = Path.of("storage/build/demo-user.json");

        JsonUtils.writeToFile(mapper, path, user);
        System.out.println("Written to file: " + path);

        User fileUser = JsonUtils.readFromFile(mapper, path, User.class);
        System.out.println("Read from file: " + fileUser);

        // =========================
        // 4. Streaming
        // =========================
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonUtils.writeToStream(mapper, os, user);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        User streamUser = JsonUtils.readFromStream(mapper, is, User.class);

        System.out.println("Stream user: " + streamUser);

        // =========================
        // 5. Exception case
        // =========================
        try {
            JsonUtils.fromJson(mapper, "{invalid-json}", User.class);
        } catch (JsonException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }

    public record User(String name, int age) {}
}
