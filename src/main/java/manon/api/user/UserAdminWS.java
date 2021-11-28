package manon.api.user;

import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.document.user.UserEntity;
import manon.dto.user.UserDto;
import manon.mapper.user.UserMapper;
import manon.model.user.UserSimpleDetails;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_USER_ADMIN;

/** User admin API. */
@Tag(name = "User administration tasks. Used by: admin.")
@RestController
@RequestMapping(value = API_USER_ADMIN)
@RequiredArgsConstructor
@Slf4j
public class UserAdminWS {

    private final RegistrationService registrationService;
    private final UserService userService;

    /** Get all users. */
    @Operation(summary = "Get all users. Result is paginated.")
    @GetMapping(value = "/all")
    public Page<UserDto> findAll(@AuthenticationPrincipal UserSimpleDetails admin,
                                 Pageable pageable) {
        log.debug("admin {} finds all users pageable {}", admin.getUsername(), pageable);
        Page<UserEntity> res = userService.findAll(pageable);
        return UserMapper.MAPPER.toUserDtoPage(res, pageable);
    }

    /** Activate a user. */
    @Operation(summary = "Activate a user. Returns registration state name.")
    @PostMapping(value = "/{userId}/activate")
    public String activate(@AuthenticationPrincipal UserSimpleDetails admin,
                           @PathVariable("userId") long userId) {
        log.warn("admin {} activates user {}", admin.getUsername(), userId);
        return registrationService.activate(userId).getRegistrationState().name();
    }

    /** Suspend a user. */
    @Operation(summary = "Suspend a user. Returns registration state name.")
    @PostMapping(value = "/{userId}/suspend")
    public String suspend(@AuthenticationPrincipal UserSimpleDetails admin,
                          @PathVariable("userId") long userId) {
        log.warn("admin {} suspends user {}", admin.getUsername(), userId);
        return registrationService.suspend(userId).getRegistrationState().name();
    }

    /** Ban a user. */
    @Operation(summary = "Ban a user. Returns registration state name.")
    @PostMapping(value = "/{userId}/ban")
    public String ban(@AuthenticationPrincipal UserSimpleDetails admin,
                      @PathVariable("userId") long userId) {
        log.warn("admin {} bans user {}", admin.getUsername(), userId);
        return registrationService.ban(userId).getRegistrationState().name();
    }

    @Operation(summary = "Search users via Querydsl.")
    @PostMapping(value = "/search")
    public Page<UserDto> search(@AuthenticationPrincipal UserSimpleDetails admin,
                                @QuerydslPredicate(root = UserEntity.class) Predicate predicate,
                                Pageable pageable) {
        log.debug("admin {} uses Querydsl to search users with predicate {}, page {}",
            admin.getUsername(), predicate, pageable);
        Page<UserEntity> res = userService.search(predicate, pageable);
        return UserMapper.MAPPER.toUserDtoPage(res, pageable);
    }

    @Operation(summary = "Search users via username, nickname or email.")
    @PostMapping(value = "/search/identity")
    public Page<UserDto> searchByIdentity(@AuthenticationPrincipal UserSimpleDetails admin,
                                          @RequestParam(name = "username", required = false) String username,
                                          @RequestParam(name = "nickname", required = false) String nickname,
                                          @RequestParam(name = "email", required = false) String email,
                                          Pageable pageable) {
        log.debug("admin {} uses Querydsl to search users with username {}, nickname {}, email {}, page {}",
            admin.getUsername(), username, nickname, email, pageable);
        Page<UserEntity> res = userService.searchByIdentity(username, nickname, email, pageable);
        return UserMapper.MAPPER.toUserDtoPage(res, pageable);
    }
}
