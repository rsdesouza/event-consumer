package mpes.cesar.school.event_consumer.service;

import jakarta.annotation.PostConstruct;
import mpes.cesar.school.event_consumer.model.Event;
import mpes.cesar.school.event_consumer.repository.EventRepository;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MqttConsumerService {

    private final MqttClient mqttClient;
    private final EventRepository eventRepository;

    private static final String TOPIC_MOTO = "moto";
    private static final String TOPIC_CARRO = "carro";

    @Autowired
    public MqttConsumerService(MqttClient mqttClient, EventRepository eventRepository) {
        this.mqttClient = mqttClient;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void subscribe() {
        try {
            mqttClient.subscribe(TOPIC_MOTO, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    processMessage(message, "moto");
                }
            });

            mqttClient.subscribe(TOPIC_CARRO, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    processMessage(message, "carro");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(MqttMessage message, String vehicleType) {
        String payload = new String(message.getPayload());
        System.out.println("Received " + vehicleType + " message: " + payload);

        // Salvando evento no banco de dados
        Event event = new Event();
        event.setVeiculo(vehicleType);
        event.setSemaforo(String.valueOf(message));
        event.setTimestamp(LocalDateTime.now());

        eventRepository.save(event);
    }
}
