package manon.user.document;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

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
    
    @JsonFormat(pattern = DATE_FORMAT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;
    
    @PrePersist
    public void prePersist() {
        Date now = Tools.now();
        if (creationDate == null) {
            creationDate = now;
        }
    }
    
    @NoArgsConstructor(access = PRIVATE)
    public static final class Validation {
        public static final int MAX_EVENTS_PER_USER = 30;
    }
}
