package org.aiml.project.domain.port.inbound

import org.aiml.project.application.dto.ProjectDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ProjectQueryService {
  fun findById(id: UUID): ProjectDTO
  fun findByIds(ids: List<UUID>): List<ProjectDTO>

  fun searchByQuery(query: String, pageable: Pageable): Page<ProjectDTO>

  fun findAll(): List<ProjectDTO>
}
