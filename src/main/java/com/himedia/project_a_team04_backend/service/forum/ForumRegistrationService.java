package com.himedia.project_a_team04_backend.service.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumRegistrationDto;
import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationStatus;
import com.himedia.project_a_team04_backend.entity.forum.ForumStatus;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.forum.ForumRegistrationRepository;
import com.himedia.project_a_team04_backend.repository.forum.ForumRepository;
import com.himedia.project_a_team04_backend.repository.forum.ForumTranslationRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import com.himedia.project_a_team04_backend.service.auth.BrevoEmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumRegistrationService {

    private final ForumRegistrationRepository registrationRepository;
    private final ForumRepository forumRepository;
    private final ForumTranslationRepository forumTranslationRepository;
    private final UserRepository userRepository;
    private final BrevoEmailService brevoEmailService;

    @Transactional
    public ForumRegistrationDto.Response apply(Long forumId, String email,
                                               ForumRegistrationDto.ApplyRequest request) {
        UserEntity user = findUserByEmail(email);

        ForumEntity forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found: " + forumId));

        if (forum.getStatus() == ForumStatus.CLOSED || forum.getStatus() == ForumStatus.FINISHED) {
            throw new IllegalStateException("신청 가능한 포럼이 아닙니다.");
        }

        if (registrationRepository.existsByForum_IdAndUser_Id(forumId, user.getId())) {
            throw new IllegalStateException("이미 신청한 포럼입니다.");
        }

        ForumRegistrationEntity registration = registrationRepository.save(
                ForumRegistrationEntity.builder()
                        .forum(forum)
                        .user(user)
                        .status(ForumRegistrationStatus.WAITING)
                        .note(request.getNote())
                        .build()
        );

        String forumTitle = forumTranslationRepository
                .findByForum_IdAndLocale(forumId, "ko")
                .map(t -> t.getTitle())
                .orElse(forum.getSlug());

        brevoEmailService.sendForumApplyEmail(user.getEmail(), forumTitle);

        return new ForumRegistrationDto.Response(registration);
    }

    @Transactional
    public ForumRegistrationDto.Response review(Long forumId, Long registrationId,
                                                String adminEmail,
                                                ForumRegistrationDto.ReviewRequest request) {
        UserEntity admin = findUserByEmail(adminEmail);

        ForumRegistrationEntity registration = registrationRepository
                .findByIdAndForum_Id(registrationId, forumId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found: " + registrationId));

        if (registration.getStatus() != ForumRegistrationStatus.WAITING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        ForumRegistrationStatus newStatus = request.getStatus();
        if (newStatus != ForumRegistrationStatus.ACCEPTED && newStatus != ForumRegistrationStatus.REJECTED) {
            throw new IllegalArgumentException("ACCEPTED 또는 REJECTED만 설정할 수 있습니다.");
        }

        registration.updateStatus(newStatus, request.getRejectReason(), admin);

        String forumTitle = forumTranslationRepository
                .findByForum_IdAndLocale(forumId, "ko")
                .map(t -> t.getTitle())
                .orElse(registration.getForum().getSlug());

        brevoEmailService.sendForumReviewEmail(
                registration.getUser().getEmail(),
                forumTitle,
                newStatus == ForumRegistrationStatus.ACCEPTED,
                request.getRejectReason()
        );

        return new ForumRegistrationDto.Response(registration);
    }

    @Transactional(readOnly = true)
    public List<ForumRegistrationDto.Response> getList(Long forumId) {
        forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found: " + forumId));

        return registrationRepository.findByForum_Id(forumId).stream()
                .map(ForumRegistrationDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ForumRegistrationDto.Response getMyRegistration(Long forumId, String email) {
        UserEntity user = findUserByEmail(email);

        return registrationRepository.findByForum_IdAndUser_Id(forumId, user.getId())
                .map(ForumRegistrationDto.Response::new)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역이 없습니다."));
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }
}
