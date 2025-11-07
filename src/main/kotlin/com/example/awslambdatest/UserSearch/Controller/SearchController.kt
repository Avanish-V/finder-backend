package com.iotabuild.campuscircle.UserSearch.Controller

import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostResponse
import com.iotabuild.campuscircle.UserSearch.Service.SearchService
import com.iotabuild.campuscircle.UserSearch.domain.dto.SearchResponse
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.function.EntityResponse

@RestController
@RequestMapping("/api/v1/search")
class SearchController(private val searchService: SearchService) {

    @GetMapping("/users")
    fun searchUsers(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<SearchResponse>> {
        val result = searchService.searchUserByName(q, page, size)
        return ResponseEntity.ok(result )
    }

    @GetMapping("/posts")
    fun searchPost(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostResponse>> {
        val result = searchService.postSearch(q, page, size)
        return ResponseEntity.ok(result )
    }


}
