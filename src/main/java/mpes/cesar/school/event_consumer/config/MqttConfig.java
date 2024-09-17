package mpes.cesar.school.event_consumer.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() {
        try {
            MqttClient client = new MqttClient("tcp://emqx:1883", MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10); // Tentar reconectar por 10 segundos
            client.connect(options);
            return client;
        } catch ( MqttException e) {
            throw new RuntimeException("Failed to connect to MQTT broker", e);
        }
    }
}
