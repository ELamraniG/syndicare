package com.syndicare.repositories.announcements;

import com.syndicare.domain.entities.announcements.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByPinnedDescCreatedAtDesc();
}
