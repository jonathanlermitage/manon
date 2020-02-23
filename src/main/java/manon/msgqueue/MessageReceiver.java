package manon.msgqueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.service.msg.MessageService;
import manon.util.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * A receiver that only logs incoming message from RabbitMQ.
 * In a real-life application, this receiver would do something useful.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageReceiver {

    public static final String LISTENER_METHOD = "receiveMessage";

    /** For test purpose only. */
    public static final int TEST_LATCH = 10;

    /** For test purpose only. */
    private CountDownLatch latch = new CountDownLatch(TEST_LATCH);

    private final MessageService messageService;

    @SuppressWarnings("unused") // see MessagingRabbitmqConfig:listenerAdapter
    public void receiveMessage(String msgAsJson) {
        Message msg = messageService.fromJson(msgAsJson);
        latch.countDown();
        log.info("received [{}] from [{}] to [{}] at [{}]",
            msg.getMessage(), msg.getUserFrom(), msg.getUserTo(), msg.getActionDate().toString());
    }

    @VisibleForTesting
    public void resetLatch() {
        latch = new CountDownLatch(TEST_LATCH);
    }

    @VisibleForTesting
    public CountDownLatch getLatch() {
        return latch;
    }
}
