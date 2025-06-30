package com.haiphamcoder.integrated.shared;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * Add a new emitter to the list.
     * 
     * @return The added emitter.
     */
    public SseEmitter add(Long timeToLive) {
        return add(new SseEmitter(timeToLive));
    }

    /**
     * Add an emitter to the list.
     * 
     * @param emitter The emitter to add.
     * @return The added emitter.
     */
    public SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("Emitter completed: {}", emitter);
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter timed out: {}", emitter);
            emitter.complete();
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    /**
     * Send a comment line to all emitters.
     * Comments start with ':' and are ignored by clients.
     * Useful for keeping connections alive or sending server-side comments.
     * 
     * @param comment The comment to send (without the leading ':')
     */
    public void sendComment(String comment) {
        send(emitter -> emitter.send(SseEmitter.event().comment(comment)));
    }

    /**
     * Send an event to all emitters.
     * 
     * @param eventName The name of the event.
     * @param data      The data to send.
     */
    public void send(String eventName, String data) {
        send(emitter -> emitter.send(
                SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name(eventName)
                        .data(data)));
    }

    /**
     * Send an object to all emitters.
     * 
     * @param obj The object to send.
     */
    private void send(SseEmitterConsumer<SseEmitter> consumer) {
        List<SseEmitter> failedEmitters = new LinkedList<>();

        for (SseEmitter emitter : this.emitters) {
            try {
                consumer.accept(emitter);
            } catch (Exception e) {
                // Handle various types of connection errors
                if (e instanceof IllegalStateException ||
                        (e.getCause() instanceof IOException &&
                                (e.getCause().getMessage().contains("Broken pipe") ||
                                        e.getCause() instanceof SocketException))
                        ||
                        e.getCause() instanceof AsyncRequestNotUsableException) {
                    log.debug("Client disconnected or connection error: {}", emitter);
                } else {
                    log.error("Emitter failed: {}", emitter, e);
                }

                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.debug("Error completing emitter: {}", emitter, ex);
                }
                failedEmitters.add(emitter);
            }
        }

        this.emitters.removeAll(failedEmitters);
    }

    /**
     * Complete all emitters and clear the list.
     */
    public void complete() {
        this.emitters.forEach(emitter -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Error completing emitter: {}", emitter, e);
            }
        });
        this.emitters.clear();
    }

    /**
     * Consumer for SseEmitter.
     * 
     * @param <T> The type of the emitter.
     */
    @FunctionalInterface
    private interface SseEmitterConsumer<T> {

        void accept(T t) throws IOException;

    }

}
