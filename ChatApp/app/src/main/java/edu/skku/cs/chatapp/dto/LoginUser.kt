package edu.skku.cs.chatapp.dto

data class LoginUser(var Email: String, var Password: String)
data class LoginUserResponse(var Status: String, var Id: Integer)