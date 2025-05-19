package org.aiml.project.infra.adapter

import org.aiml.project.domain.model.Project
import org.aiml.project.domain.port.outbound.ProjectPersistencePort
import org.aiml.project.infra.persistence.ProjectEntity
import org.aiml.project.infra.persistence.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProjectPersistenceAdapter(
  private val projectRepository: ProjectRepository
) : ProjectPersistencePort {

  override fun save(project: Project): Result<Project> = runCatching {
    val entity = ProjectEntity.from(project)
    projectRepository.save(entity).toDomain()
  }

  override fun delete(id: UUID): Result<Unit> = runCatching {
    projectRepository.deleteById(id)
  }

  override fun deleteByIds(ids: List<UUID>): Result<Unit> = runCatching {
    projectRepository.deleteAllById(ids)
  }

  override fun findById(id: UUID): Result<Project> = runCatching {
    projectRepository.findById(id).get().toDomain()
  }

  override fun findByIds(ids: List<UUID>): Result<List<Project>> = runCatching {
    projectRepository.findAllById(ids).map { it.toDomain() }
  }

  override fun findAll(): Result<List<Project>> = runCatching {
    projectRepository.findAll().map { it.toDomain() }
  }

  override fun existsById(id: UUID): Boolean {
    return projectRepository.existsById(id)
  }

  override fun deleteAll(): Result<Unit> = runCatching {
    projectRepository.deleteAll()
  }

  override fun searchByQuery(query: String, pageable: Pageable): Page<Project> {
    return projectRepository.findByQuery(query, pageable).map { it.toDomain() }
  }
}
