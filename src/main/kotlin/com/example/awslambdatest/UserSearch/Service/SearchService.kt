package com.iotabuild.campuscircle.UserSearch.Service

import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMode
import com.iotabuild.campuscircle.FeedService.Service.ReplyService
import com.iotabuild.campuscircle.FeedService.Service.toResponse
import com.iotabuild.campuscircle.UserSearch.Repository.PostSearchRepository
import com.iotabuild.campuscircle.UserSearch.Repository.UserSearchRepository
import com.iotabuild.campuscircle.UserSearch.domain.dto.SearchResponse
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(private val searchRepository: UserSearchRepository,private val postSearchRepository: PostSearchRepository){


    fun searchUserByName(query: String,page: Int,size: Int): Page<SearchResponse>{
        val pageable = PageRequest.of(page, size)
        val result =  searchRepository.searchUsers(query = query, pageable = pageable)
        return result.map {
            SearchResponse(
                uid = it.uid,
                name = it.name,
                image = it.image,
                tagline = it.tagline,
                about = it.about
            )
        }
    }

    fun postSearch(query: String,page: Int,size: Int): Page<PostResponse>{

        val pageable = PageRequest.of(page,size)

        val result = postSearchRepository.searchPostsWithMedia(query, feedMode = FeedMode.OPEN,pageable)

        return result.map { toResponse(post = it, userId = "") }

    }


}