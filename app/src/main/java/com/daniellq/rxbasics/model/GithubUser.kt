package com.daniellq.rxbasics.model

/**
 * Created by dani on 13/9/17.
 */
class GithubUser(val name: String,
                 val login: String,
                 val location: String,
                 val email: String,
                 val bio: String,
                 val public_repos: Int,
                 val public_gists: Int,
                 val followers: Int,
                 val following: Int)