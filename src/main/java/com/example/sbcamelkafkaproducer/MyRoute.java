package com.example.sbcamelkafkaproducer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration()
                .enableCORS(true)
                .component("jetty")
                .host("0.0.0.0")
                .port(8988)
                .bindingMode(RestBindingMode.json);

        rest()
                //.get("/hello")
               // .to("direct:hello")
                .get("/hello-kafka-high")
                .to("direct:hello-kafka-high")
                .get("/hello-kafka-medium")
                .to("direct:hello-kafka-medium")
                .get("/hello-kafka-low")
                .to("direct:hello-kafka-low");


        from("direct:hello-kafka-high")
                .routeId("KafkaGreetingRouteA")
                .setBody(simple("HIGH PRIORITY"))
                .setHeader("kafka.PARTITION_KEY" , simple("0"))
                .to("kafka:{{topic}}?brokers={{broker}}&key=high");

        from("direct:hello-kafka-medium")
                .routeId("KafkaGreetingRouteB")
                .setBody(simple("Medium PRIORITY"))
                .setHeader("kafka.PARTITION_KEY" , simple("1"))
                .to("kafka:{{topic}}?brokers={{broker}}&key=medium");


        from("direct:hello-kafka-low")
                .routeId("KafkaGreetingRouteG")
                .setBody(simple("LOW PRIORITY"))
                .setHeader("kafka.PARTITION_KEY" , simple("2"))
                .to("kafka:{{topic}}?brokers={{broker}}&key=low");

        // Kafka Consumer
        from("kafka:{{topic}}?brokers={{broker}}&groupId=G1")
               // .log("Message received from Kafka by group1 c1 : ${body}")
                .routeId("1 - G1 C1")
                .log("-- topic ${headers[kafka.TOPIC]} -- partition ${headers[kafka.PARTITION]}" +
                        " -- offset ${headers[kafka.OFFSET]} -- key ${headers[kafka.KEY]} ");

        from("kafka:{{topic}}?brokers={{broker}}&groupId=G1")
                //.log("Message received from Kafka by group1 c2 : ${body}")
                .routeId("2 - G1 C2")
                .log("-- topic ${headers[kafka.TOPIC]} -- partition ${headers[kafka.PARTITION]}" +
                        " -- offset ${headers[kafka.OFFSET]} -- key ${headers[kafka.KEY]} ");

        // Kafka Consumer
        from("kafka:{{topic}}?brokers={{broker}}&groupId=G2")
               // .log("Message received from Kafka by group2 c1 : ${body}")
                .routeId("3 - G2 C1")
                .log("-- topic ${headers[kafka.TOPIC]} -- partition ${headers[kafka.PARTITION]}" +
                        " -- offset ${headers[kafka.OFFSET]} -- key ${headers[kafka.KEY]} ");

        from("kafka:{{topic}}?brokers={{broker}}&groupId=G2")
               // .log("Message received from Kafka by group2 c2 : ${body}")
                .routeId("4 - G2 C2")
                .log("-- topic ${headers[kafka.TOPIC]} -- partition ${headers[kafka.PARTITION]}" +
                        " -- offset ${headers[kafka.OFFSET]} -- key ${headers[kafka.KEY]} ");

       /* from("kafka:{{topic.debezium}}?brokers={{broker}}")
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}");*/

    }

}