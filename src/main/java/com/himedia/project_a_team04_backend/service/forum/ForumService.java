package com.himedia.project_a_team04_backend.service.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumDto;
import com.himedia.project_a_team04_backend.dto.forum.ForumMediaDto;
import com.himedia.project_a_team04_backend.dto.forum.ForumTranslationDto;
import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumMediaEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumTranslationEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import com.himedia.project_a_team04_backend.exception.forum.ForumAccessDeniedException;
import com.himedia.project_a_team04_backend.repository.forum.ForumMediaRepository;
import com.himedia.project_a_team04_backend.repository.forum.ForumRepository;
import com.himedia.project_a_team04_backend.repository.forum.ForumTranslationRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumRepository forumRepository;
    private final ForumTranslationRepository forumTranslationRepository;
    private final ForumMediaRepository forumMediaRepository;
    private final UserRepository userRepository;

    // TODO: Security 적용 후 userId 제거, @AuthenticationPrincipal UserEntity로 교체
    @Transactional
    public ForumDto.Response create(Long userId, ForumDto.CreateRequest request) {
        UserEntity user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (user.getRole() != UserRole.ROLE_ADMIN) {
            throw new ForumAccessDeniedException("Only ROLE_ADMIN can create a forum");
        }

        ForumEntity forum = forumRepository.save(ForumEntity.builder()
                .slug(request.getSlug())
                .status(request.getStatus())
                .eventDate(request.getEventDate())
                .thumbnailUrl(request.getThumbnailUrl())
                .maxParticipants(request.getMaxParticipants())
                .createdByUser(user)
                .build());

        List<ForumTranslationEntity> savedTranslations = saveTranslations(forum, request.getTranslations());
        List<ForumMediaEntity> savedMedia = saveMedia(forum, request.getMedia());

        List<ForumTranslationDto.Response> translationResponses = savedTranslations.stream()
                .map(ForumTranslationDto.Response::new)
                .collect(Collectors.toList());
        List<ForumMediaDto.Response> mediaResponses = savedMedia.stream()
                .map(ForumMediaDto.Response::new)
                .collect(Collectors.toList());

        return new ForumDto.Response(forum, translationResponses, mediaResponses);
    }

    private List<ForumTranslationEntity> saveTranslations(ForumEntity forum,
                                                           List<ForumTranslationDto.Request> requests) {
        if (requests == null || requests.isEmpty()) return Collections.emptyList();

        List<ForumTranslationEntity> entities = requests.stream()
                .map(t -> ForumTranslationEntity.builder()
                        .forum(forum)
                        .locale(t.getLocale())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .location(t.getLocation())
                        .speakers(t.getSpeakers())
                        .build())
                .collect(Collectors.toList());

        return forumTranslationRepository.saveAll(entities);
    }

    private List<ForumMediaEntity> saveMedia(ForumEntity forum,
                                              List<ForumMediaDto.Request> requests) {
        if (requests == null || requests.isEmpty()) return Collections.emptyList();

        List<ForumMediaEntity> entities = requests.stream()
                .map(m -> ForumMediaEntity.builder()
                        .forum(forum)
                        .mediaType(m.getMediaType())
                        .url(m.getUrl())
                        .thumbnailUrl(m.getThumbnailUrl())
                        .sortOrder(m.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return forumMediaRepository.saveAll(entities);
    }
}
