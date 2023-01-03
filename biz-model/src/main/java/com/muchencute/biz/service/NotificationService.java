package com.muchencute.biz.service;


import com.muchencute.biz.model.Notification;
import com.muchencute.biz.model.NotificationRead;
import com.muchencute.biz.model.NotificationRead_;
import com.muchencute.biz.model.Notification_;
import com.muchencute.biz.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("bizTransactionManager")
public class NotificationService {

  private final NotificationRepository notificationRepository;

  @Autowired
  public NotificationService(NotificationRepository notificationRepository) {

    this.notificationRepository = notificationRepository;
  }

  private static Specification<Notification> byUserId(String userId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.isNull(root.get(Notification_.USER_ID)),
            criteriaBuilder.equal(root.get(Notification_.USER_ID), userId)
    );
  }

  private static Specification<Notification> hasBeenReadByUserId(String userId) {

    return (root, query, criteriaBuilder) -> {
      final var subQuery = query.subquery(NotificationRead.class);
      final var subQueryRoot = subQuery.from(NotificationRead.class);
      subQuery.select(subQueryRoot)
              .where(criteriaBuilder.and(
                      criteriaBuilder.equal(root, subQueryRoot.get(NotificationRead_.NOTIFICATION)),
                      criteriaBuilder.equal(subQueryRoot.get(NotificationRead_.USER_ID), userId)
              ));
      return criteriaBuilder.not(criteriaBuilder.exists(subQuery));
    };
  }

  public void createUserNotification(String userId, String title, String content) {

    notificationRepository.save(
            Notification
                    .builder()
                    .title(title)
                    .userId(userId)
                    .content(content)
                    .build()
    );
  }

  public Page<Notification> getNotifications(@NonNull String userId, boolean unreadOnly,
                                             Pageable pageable) {

    final var specification = unreadOnly ?
            Specification.where(byUserId(userId)).and(hasBeenReadByUserId(userId)) :
            Specification.where(byUserId(userId));
    final var page = notificationRepository.findAll(specification, pageable);
    page.getContent().forEach(it -> it.setRead(it.getReads().stream()
            .anyMatch(notificationRead -> userId.equals(notificationRead.getUserId()))));
    return page;
  }

  public void markAllAsRead(String userId) {

    final var notifications = notificationRepository.findAll(
            Specification.where(byUserId(userId)).and(hasBeenReadByUserId(userId)));
    notifications.forEach(notification -> notification.getReads()
            .add(NotificationRead.builder().userId(userId).notification(notification).build()));
    notificationRepository.saveAll(notifications);
  }
}
