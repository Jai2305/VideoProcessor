package com.example.videoEditor.repositories;

import com.example.videoEditor.entitites.SharedVideoLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedVideoLinkRepository extends JpaRepository<SharedVideoLink, Long> {
    Optional<SharedVideoLink> findByShareToken(String shareToken);
}
