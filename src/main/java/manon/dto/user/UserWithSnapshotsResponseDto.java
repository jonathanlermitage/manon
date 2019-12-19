package manon.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import manon.model.user.RegistrationState;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithSnapshotsResponseDto {

    private long id;

    private String username;

    private String authorities;

    private RegistrationState registrationState;

    private String nickname;

    private String email;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateDate;

    private List<UserSnapshotResponsetDto> userSnapshots;
}
