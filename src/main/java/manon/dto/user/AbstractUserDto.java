package manon.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import manon.document.user.User;
import manon.mapper.user.UserMapper;
import manon.model.user.RegistrationState;

import java.time.LocalDateTime;

/**
 * Workaround for issue <a href="https://github.com/mapstruct/mapstruct/issues/131">mapstruct/issues/131</a>.
 * <p>
 * {@link UserWithSnapshotsResponseDto} should inherit from {@link UserResponseDto} (and {@link UserResponseDto}
 * inherit from nothing and declare fields below), but since {@link UserWithSnapshotsResponseDto} contains a list
 * of {@link UserSnapshotResponseDto} elements, which contain {@link UserResponseDto} items too,
 * {@link UserMapper#userToUserWithSnapshotsResponseDto(User)} fails to populate it.
 * <p>
 * A solution is to write a custom mapper, but I don't want to do MapStruct's job.<br/>
 * An other solution, chosen here, is to play with class names and create a common ancestor for both
 * {@link UserWithSnapshotsResponseDto} and {@link UserResponseDto} classes. {@link UserResponseDto} won't
 * declare any field because everything comes from the ancestor: this is not perfect, but it works.
 */
@Data
public abstract class AbstractUserDto {

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
}
