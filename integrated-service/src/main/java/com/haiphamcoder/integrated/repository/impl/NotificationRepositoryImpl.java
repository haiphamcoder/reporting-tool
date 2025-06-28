package com.haiphamcoder.integrated.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.integrated.domain.entity.Notification;
import com.haiphamcoder.integrated.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    // Find notifications by user ID with pagination
    Page<Notification> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    // Find all notifications with pagination (for admin purposes)
    Page<Notification> findAllByOrderByTimestampDesc(Pageable pageable);

    // Count unread notifications by user ID
    Long countByUserIdAndReadFalse(String userId);

    // Find notifications by IDs
    List<Notification> findByIdIn(List<Long> ids);

    // Mark notifications as read by IDs
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id IN :ids")
    void markAsReadByIds(@Param("ids") List<Long> ids);

    // Mark all notifications as read by user ID
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") String userId);

    // Delete notifications by user ID
    void deleteByUserId(String userId);

    // Delete notifications by IDs
    void deleteByIdIn(List<Long> ids);
}

@Component
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Page<Notification> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable) {
        return notificationJpaRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Override
    public Page<Notification> findAllByOrderByTimestampDesc(Pageable pageable) {
        return notificationJpaRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    public Long countByUserIdAndReadFalse(String userId) {
        return notificationJpaRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public List<Notification> findByIdIn(List<Long> ids) {
        return notificationJpaRepository.findByIdIn(ids);
    }

    @Override
    public void markAsReadByIds(List<Long> ids) {
        notificationJpaRepository.markAsReadByIds(ids);
    }

    @Override
    public void markAllAsReadByUserId(String userId) {
        notificationJpaRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public void deleteByUserId(String userId) {
        notificationJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByIdIn(List<Long> ids) {
        notificationJpaRepository.deleteByIdIn(ids);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public Notification save(Notification entity) {
        return notificationJpaRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        notificationJpaRepository.deleteById(id);
    }
}
