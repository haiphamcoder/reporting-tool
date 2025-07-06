package com.haiphamcoder.dataprocessing.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.haiphamcoder.dataprocessing.service.impl.DataProcessingServiceGrpcImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataProcessingGrpcServer {

    private Server server;
    private final DataProcessingServiceGrpcImpl dataProcessingServiceGrpcImpl;

    @Value("${dataprocessing.grpc.server.port:9094}")
    private int port;

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(dataProcessingServiceGrpcImpl)
                .build()
                .start();
        log.info("gRPC Server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DataProcessingGrpcServer.this.stop();
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
