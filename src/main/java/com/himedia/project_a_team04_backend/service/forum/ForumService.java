package com.himedia.project_a_team04_backend.service.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumDto;
import com.himedia.project_a_team04_backend.dto.forum.ForumMediaDto;
import com.himedia.project_a_team04_backend.dto.forum.ForumTranslationDto;
import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumMediaEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumTranslationEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
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

    @Transactional
    public ForumDto.Response create(String email, ForumDto.CreateRequest request) {
        UserEntity user = findUserByEmail(email);

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

        return toResponse(forum, savedTranslations, savedMedia);
    }

    @Transactional
    public ForumDto.Response update(String email, Long forumId, ForumDto.CreateRequest request) {
        findUserByEmail(email);

        ForumEntity forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found: " + forumId));

        forum.update(request.getSlug(), request.getStatus(), request.getEventDate(),
                request.getThumbnailUrl(), request.getMaxParticipants());

        // 번역/미디어 전체 교체
        forumTranslationRepository.deleteAll(forumTranslationRepository.findByForum_Id(forumId));
        forumMediaRepository.deleteAll(forumMediaRepository.findByForum_Id(forumId));

        List<ForumTranslationEntity> savedTranslations = saveTranslations(forum, request.getTranslations());
        List<ForumMediaEntity> savedMedia = saveMedia(forum, request.getMedia());

        return toResponse(forum, savedTranslations, savedMedia);
    }

    @Transactional
    public void delete(String email, Long forumId) {
        findUserByEmail(email);

        ForumEntity forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found: " + forumId));

        forumRepository.delete(forum);
    }

    @Transactional(readOnly = true)
    public List<ForumDto.ListResponse> getList(String locale) {
        return forumRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(forum -> {
                    ForumTranslationEntity translation = forumTranslationRepository
                            .findByForum_IdAndLocale(forum.getId(), locale)
                            .orElseGet(() -> forumTranslationRepository.findByForum_Id(forum.getId())
                                    .stream().findFirst().orElse(null));
                    String title = translation != null ? translation.getTitle() : "";
                    String location = translation != null ? translation.getLocation() : "";
                    return new ForumDto.ListResponse(forum, title, location);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ForumDto.Response getDetail(Long forumId) {
        ForumEntity forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found: " + forumId));

        List<ForumTranslationEntity> translations = forumTranslationRepository.findByForum_Id(forumId);
        List<ForumMediaEntity> media = forumMediaRepository.findByForum_Id(forumId);

        return toResponse(forum, translations, media);
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    private ForumDto.Response toResponse(ForumEntity forum,
                                          List<ForumTranslationEntity> translations,
                                          List<ForumMediaEntity> media) {
        List<ForumTranslationDto.Response> translationResponses = translations.stream()
                .map(ForumTranslationDto.Response::new)
                .collect(Collectors.toList());
        List<ForumMediaDto.Response> mediaResponses = media.stream()
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
