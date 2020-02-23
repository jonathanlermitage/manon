package manon.service.msg.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.app.config.MessagingRabbitmqConfig;
import manon.msgqueue.Message;
import manon.service.msg.MessageService;
import manon.util.Tools;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void send(String msg, long from, long to) {
        rabbitTemplate.convertAndSend(
            MessagingRabbitmqConfig.QUEUE_NAME,
            toJson(new Message(msg, from, to, Tools.now())));
    }

    @SneakyThrows
    @Override
    public String toJson(Message message) {
        return objectMapper.writeValueAsString(message);
    }

    @SneakyThrows
    @Override
    public Message fromJson(String json) {
        return objectMapper.readValue(json, Message.class);
    }

    @Override
    public void startQueue() {
        rabbitTemplate.start();
        log.info("started RabbitMQ queue");
    }

    @Override
    public void stopQueue() {
        rabbitTemplate.stop();
        log.warn("stopped RabbitMQ queue");
    }

    @Override
    public void resetQueue() {
        stopQueue();
        startQueue();
    }
}
