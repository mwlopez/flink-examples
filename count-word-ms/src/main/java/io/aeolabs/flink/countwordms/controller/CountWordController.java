package io.aeolabs.flink.countwordms.controller;

import io.aeolabs.flink.countwordms.entity.WordEntity;
import io.aeolabs.flink.countwordms.producer.CountKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
public class CountWordController {
    final CountKafkaProducer countKafkaProducer;

    public CountWordController(CountKafkaProducer countKafkaProducer) {
        this.countKafkaProducer = countKafkaProducer;
    }


    @PostMapping("/flink")
    public void addWordToFlink(@RequestBody WordEntity entity) {
        log.info("mensaje {}", entity.getMessage());
        countKafkaProducer.enviarMensaje("countIn", entity.getMessage());
    }

}
