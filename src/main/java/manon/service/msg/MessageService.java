package manon.service.msg;

import manon.msgqueue.Message;

public interface MessageService {

    void send(String msg, long from, long to);

    String toJson(Message message);

    Message fromJson(String json);

    void startQueue();

    void stopQueue();

    void resetQueue();
}
