package manon.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import manon.model.user.RegistrationState;

import java.time.LocalDateTime;

@Data
public class UserSnapshotDto {

    private long id;

    private UserDto user;

    private String userUsername;

    private String userAuthorities;

    private RegistrationState userRegistrationState;

    private String userNickname;

    private String userEmail;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;
}
