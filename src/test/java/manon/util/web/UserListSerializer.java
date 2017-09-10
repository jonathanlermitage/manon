package manon.util.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import manon.user.document.User;

import java.io.IOException;
import java.util.Collection;

public class UserListSerializer extends JsonSerializer<Collection<User>> {
    
    @Override
    public void serialize(Collection<User> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray(value.size());
        for (User val : value) {
            jgen.writeObject(val);
        }
        jgen.writeEndArray();
    }
}
