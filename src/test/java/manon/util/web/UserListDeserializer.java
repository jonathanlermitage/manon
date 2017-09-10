package manon.util.web;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import manon.user.document.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static manon.util.Tools.JSON;

public class UserListDeserializer extends JsonDeserializer<Collection<User>> {
    
    @Override
    public List<User> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<User> values = new ArrayList<>();
        JsonNode node = jp.getCodec().readTree(jp);
        for (int i = 0; i < node.size(); i++) {
            JsonNode on = node.get(i);
            values.add(JSON.readValue(on.toString(), User.class));
        }
        return values;
    }
}
