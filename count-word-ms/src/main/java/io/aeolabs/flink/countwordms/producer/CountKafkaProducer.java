package io.aeolabs.flink.countwordms.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CountKafkaProducer {

    private final KafkaTemplate<String, String> template;

    public CountKafkaProducer(KafkaTemplate<String, String> template) {
        this.template = template;
    }

    /**
     * Permite enviar un mensaje al stream.
     * El mensaje enviado es de tipo String y se envia sin transformaciones al topico.
     *
     * <pre>
     *     String s = "mensaje ha ser enviado";
     *     enviarMensaje("miTopico", s);
     * </pre>
     *
     * @param topic Topico a enviar el mensaje
     * @param mensaje mensaje ha ser enviado.
     */
    public void enviarMensaje(String topic, String mensaje) {
        log.info("topic {} message {}", topic, mensaje);
        this.template.send(topic, mensaje);
    }


}
