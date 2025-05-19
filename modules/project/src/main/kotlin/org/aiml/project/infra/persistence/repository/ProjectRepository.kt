package org.aiml.project.infra.persistence.repository

import org.aiml.project.infra.persistence.ProjectEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ProjectRepository : JpaRepository<ProjectEntity, UUID> {
  @Query(
    """
    SELECT p FROM ProjectEntity p
    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(p.subtitle) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
    """
  )
  fun findByQuery(query: String, pageable: Pageable): Page<ProjectEntity>
}
