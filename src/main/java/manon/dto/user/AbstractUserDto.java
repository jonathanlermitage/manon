package manon.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import manon.document.user.UserEntity;
import manon.mapper.user.UserMapper;
import manon.model.user.RegistrationState;

import java.time.LocalDateTime;

/**
 * Workaround for issue <a href="https://github.com/mapstruct/mapstruct/issues/131">mapstruct/issues/131</a>.
 * <p>
 * {@link UserWithSnapshotsDto} should inherit from {@link UserDto} (and {@link UserDto}
 * inherit from nothing and declare fields below), but since {@link UserWithSnapshotsDto} contains a list
 * of {@link UserSnapshotDto} elements, which contain {@link UserDto} items too,
 * {@link UserMapper#toUserWithSnapshotsDto(UserEntity)} fails to populate it.
 * <p>
 * A solution is to write a custom mapper, but I don't want to do MapStruct's job.<br/>
 * An other solution, chosen here, is to play with class names and create a common ancestor for both
 * {@link UserWithSnapshotsDto} and {@link UserDto} classes. {@link UserDto} won't
 * declare any field because everything comes from the ancestor: this is not perfect, but it works.
 */
@Data
@EqualsAndHashCode(exclude = {"id", "creationDate", "updateDate"})
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
