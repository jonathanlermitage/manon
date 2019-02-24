package manon.user.document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.user.model.FriendshipEventCode;
import manon.util.Tools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@ToString
@EqualsAndHashCode(exclude = "creationDate")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipEvent implements Serializable {
    
    private static final long serialVersionUID = 5177929765264927516L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User friend;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipEventCode code;
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime creationDate;
    
    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = Tools.now();
        }
    }
    
    @NoArgsConstructor(access = PRIVATE)
    public static final class Validation {
        public static final int MAX_EVENTS_PER_USER = 30;
    }
}
