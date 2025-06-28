package com.haiphamcoder.integrated.repository;

import com.haiphamcoder.integrated.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Page<Notification> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    Page<Notification> findAllByOrderByTimestampDesc(Pageable pageable);

    Long countByUserIdAndReadFalse(String userId);

    List<Notification> findByIdIn(List<Long> ids);

    void markAsReadByIds(@Param("ids") List<Long> ids);

    void markAllAsReadByUserId(@Param("userId") String userId);

    void deleteByUserId(String userId);

    void deleteByIdIn(List<Long> ids);

    Optional<Notification> findById(Long id);

    Notification save(Notification entity);

    void deleteById(Long id);

}