package manon.msgqueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private long userFrom;
    private long userTo;
    private LocalDateTime actionDate;
}
