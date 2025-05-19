package org.aiml.project.application.service

import org.aiml.project.application.dto.ProjectDTO
import org.aiml.project.domain.port.inbound.ProjectQueryService
import org.aiml.project.domain.port.outbound.ProjectPersistencePort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectQueryServiceImpl(
  private val projectPersistencePort: ProjectPersistencePort
) : ProjectQueryService {
  override fun findById(id: UUID): ProjectDTO {
    return projectPersistencePort.findById(id).getOrThrow().let { ProjectDTO.from(it) }
  }

  override fun findByIds(ids: List<UUID>): List<ProjectDTO> {
    return projectPersistencePort.findByIds(ids).getOrThrow().map { ProjectDTO.from(it) }
  }

  override fun findAll(): List<ProjectDTO> {
    return projectPersistencePort.findAll().getOrThrow().map { ProjectDTO.from(it) }
  }

  override fun searchByQuery(query: String, pageable: Pageable): Page<ProjectDTO> {
    return projectPersistencePort.searchByQuery(query, pageable).map { ProjectDTO.from(it) }
  }
}
