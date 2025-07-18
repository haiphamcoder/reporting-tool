package com.haiphamcoder.usermanagement.grpc;

import com.haiphamcoder.usermanagement.service.impl.UserServiceGrpcImpl;

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
public class UserGrpcServer {

    private Server server;
    private final UserServiceGrpcImpl userServiceGrpcImpl;

    @Value("${grpc.server.port:9090}")
    private int port;

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(userServiceGrpcImpl)
                .build()
                .start();
        log.info("gRPC Server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UserGrpcServer.this.stop();
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