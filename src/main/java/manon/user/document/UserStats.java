package manon.user.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.util.Tools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import static manon.util.Tools.DATE_FORMAT;

/** Statistics on all users. */
@Entity
@Getter
@ToString
@EqualsAndHashCode(exclude = "creationDate")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserStats implements Serializable {
    
    private static final long serialVersionUID = -6351299219725037369L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false)
    private long nbUsers;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;
    
    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = Tools.now();
        }
    }
}
