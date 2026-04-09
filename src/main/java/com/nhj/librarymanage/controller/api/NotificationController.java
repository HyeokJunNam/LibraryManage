package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.dto.NotificationRequest;
import com.nhj.librarymanage.security.member.SecurityUser;
import com.nhj.librarymanage.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Description(value = "도서 알림 신청")
    @PostMapping("/books/{id}/notifications")
    public ResponseEntity<Void> createNotify(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id, @RequestBody NotificationRequest.Create create) {
        notificationService.createNotify(id, securityUser.getId(), create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
