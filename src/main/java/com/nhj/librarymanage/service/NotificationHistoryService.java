package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.admin.notification.NotificationTemplate;
import com.nhj.librarymanage.domain.entity.NotificationHistory;
import com.nhj.librarymanage.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationHistoryService {

    private final NotificationHistoryRepository notificationHistoryRepository;

    public void notified(NotificationTemplate notificationTemplate) {
        NotificationHistory notificationHistory = NotificationHistory.notified(notificationTemplate);

        notificationHistoryRepository.save(notificationHistory);
    }

    public void failed(NotificationTemplate notificationTemplate, String failureReason) {
        NotificationHistory notificationHistory = NotificationHistory.failed(notificationTemplate, failureReason);

        notificationHistoryRepository.save(notificationHistory);
    }

    public void canceled(NotificationTemplate notificationTemplate) {
        NotificationHistory notificationHistory = NotificationHistory.canceled(notificationTemplate);

        notificationHistoryRepository.save(notificationHistory);
    }


}
