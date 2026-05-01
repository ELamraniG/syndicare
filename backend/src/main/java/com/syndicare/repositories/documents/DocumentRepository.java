package com.syndicare.repositories.documents;

import com.syndicare.domain.entities.documents.Document;
import com.syndicare.domain.entities.documents.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByOrderByUploadedAtDesc();
    List<Document> findByCategoryOrderByUploadedAtDesc(DocumentCategory category);
    List<Document> findByBuildingIdOrderByUploadedAtDesc(Long buildingId);
}
