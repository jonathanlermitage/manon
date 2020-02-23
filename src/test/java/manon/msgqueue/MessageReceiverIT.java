package manon.msgqueue;

import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class MessageReceiverIT extends AbstractIT {

    @BeforeEach
    @AfterEach
    public void reset() {
        messageReceiver.resetLatch();
        messageService.resetQueue();
    }

    @Test
    public void shouldReceiveMessage() throws InterruptedException {
        for (int i = 0; i < MessageReceiver.TEST_LATCH; i++) {
            messageService.send("hello", 1, 2);
        }
        messageReceiver.getLatch().await(2, TimeUnit.SECONDS);
    }
}
