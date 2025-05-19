package org.aiml.api.controller

import org.aiml.api.common.response.*
import org.aiml.api.dto.project.*
import org.aiml.api.dto.scene.*
import org.aiml.project.domain.port.inbound.ProjectQueryService
import org.aiml.project_user.application.facade.ProjectCommandFacade
import org.aiml.project_user.application.facade.UserProjectQueryFacade
import org.aiml.scene.application.facade.SceneCommandFacade
import org.aiml.scene.application.facade.SceneQueryFacade
import org.aiml.user.infra.security.CustomUserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/project")
class ProjectController(
  private val projectCommandFacade: ProjectCommandFacade,
  private val projectQueryService: ProjectQueryService,
  private val userProjectQueryFacade: UserProjectQueryFacade,
  private val sceneCommandFacade: SceneCommandFacade,
  private val sceneQueryFacade: SceneQueryFacade,
) {

  @GetMapping
  fun getProjects(
    @AuthenticationPrincipal principal: CustomUserPrincipal
  ): ResponseEntity<ApiResponse<List<ProjectBaseResponse>>> {
    val pInfos = userProjectQueryFacade.loadProjects(principal.userId)
    return ok(pInfos.map { ProjectBaseResponse.from(it) })
  }

  @PostMapping
  fun createProject(
    @AuthenticationPrincipal principal: CustomUserPrincipal,
    @RequestBody request: ProjectRequest
  ): ResponseEntity<ApiResponse<ProjectBaseResponse>> {
    val pInfo = projectCommandFacade.createProject(principal.userId, request.toDTO())
    // FIXME initiate in api handler?
    sceneCommandFacade.initiate(principal.userId, pInfo.id!!)
    return created(ProjectBaseResponse.from(pInfo))
  }

  @GetMapping("/{projectId}")
  fun getProject(
    @AuthenticationPrincipal principal: CustomUserPrincipal,
    @PathVariable("projectId") projectId: UUID
  ): ResponseEntity<ApiResponse<ProjectBaseResponse>> {
    val pInfo = userProjectQueryFacade.loadProjectById(principal.userId, projectId)
    return ok(ProjectBaseResponse.from(pInfo))
  }

  @PutMapping("/{projectId}")
  fun updateProject(
    @AuthenticationPrincipal principal: CustomUserPrincipal,
    @PathVariable("projectId") projectId: UUID,
    @RequestBody request: ProjectRequest
  ): ResponseEntity<ApiResponse<ProjectBaseResponse>> {
    val pInfo = projectCommandFacade.updateProject(principal.userId, request.toDTO(projectId))
    return ok(ProjectBaseResponse.from(pInfo))
  }

  @DeleteMapping("/{projectId}")
  fun deleteProject(
    @AuthenticationPrincipal principal: CustomUserPrincipal,
    @PathVariable("projectId") projectId: UUID,
  ): ResponseEntity<ApiResponse<Nothing>> {
    projectCommandFacade.deleteProject(principal.userId, projectId)
    return deleted()
  }

  @GetMapping("/{projectId}/scenes")
  fun getProjectScenes(
    @AuthenticationPrincipal principal: CustomUserPrincipal,
    @PathVariable("projectId") projectId: UUID,
  ): ResponseEntity<ApiResponse<List<SceneResponse>>> {
    val scenes = sceneQueryFacade.loadScenesByProject(principal.userId, projectId)
    return ok(scenes.map { SceneResponse.fromDTO(it) })
  }

  @GetMapping("/search")
  fun searchProjects(
    @RequestParam("query", required = true) query: String,
    @RequestParam("pageNum", defaultValue = "0") page: Int,
    @RequestParam("pageSize", defaultValue = "10") size: Int,
  ): ResponseEntity<ApiResponse<Page<ProjectBaseResponse>>> {
    val pInfosPage = projectQueryService.searchByQuery(query, PageRequest.of(page, size))
    return ok(pInfosPage.map { ProjectBaseResponse.from(it) })
  }
}
