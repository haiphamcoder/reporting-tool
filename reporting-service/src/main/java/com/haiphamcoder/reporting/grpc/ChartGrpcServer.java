package com.haiphamcoder.reporting.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.haiphamcoder.reporting.service.impl.ChartServiceGrpcImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChartGrpcServer {
    private Server server;
    private final ChartServiceGrpcImpl chartServiceGrpcImpl;

    @Value("${chart.grpc.server.port:9092}")
    private int port;

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(chartServiceGrpcImpl)
                .build()
                .start();
        log.info("gRPC Server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ChartGrpcServer.this.stop();
            } catch (InterruptedException e) {
                log.error("Error stopping gRPC server", e);
                Thread.currentThread().interrupt();
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
