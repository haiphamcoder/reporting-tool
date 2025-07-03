package com.haiphamcoder.integrated.grpc;

import com.haiphamcoder.integrated.service.impl.EmailServiceGrpcImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailGrpcServer {

    private Server server;
    private final EmailServiceGrpcImpl emailServiceGrpcImpl;

    @Value("${email.grpc.server.port:9093}")
    private int port;

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(emailServiceGrpcImpl)
                .build()
                .start();
        log.info("gRPC Server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                EmailGrpcServer.this.stop();
            } catch (InterruptedException e) {
                log.error("Error stopping gRPC server", e);
            }
        }));
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}