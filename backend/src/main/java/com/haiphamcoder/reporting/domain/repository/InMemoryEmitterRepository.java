package com.haiphamcoder.reporting.domain.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryEmitterRepository {

    private final Map<String, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    public Map<String, List<SseEmitter>> getEmitterMap() {
        return emitterMap;
    }

    public Optional<SseEmitter> addEmitter(String id, SseEmitter emitter) {
        log.info("Adding emitter for id: {}", id);
        emitterMap.putIfAbsent(id, new CopyOnWriteArrayList<>());
        emitterMap.get(id).add(emitter);
        return Optional.of(emitter);
    }

    public Optional<SseEmitter> removeEmitter(String id, SseEmitter emitter) {
        if (emitterMap.containsKey(id)) {
            if (emitterMap.get(id).contains(emitter)){
                log.info("Removing emitter for id: {}", id);
                emitterMap.get(id).remove(emitter);
                if (emitterMap.get(id).isEmpty()) {
                    emitterMap.remove(id);
                }
            }
            return Optional.of(emitter);
        }
        return Optional.empty();
    }

    public void removeAllEmitters(String id) {
        log.info("Removing all emitters for id: {}", id);
        emitterMap.remove(id);
    }

    public List<SseEmitter> getEmitters(String id) {
        log.info("Getting emitters for id: {}", id);
        return emitterMap.getOrDefault(id, Collections.emptyList());
    }

}
