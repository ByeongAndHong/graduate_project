package edu.skku.cs.chatapp.dto

data class RegisterUser(var UserName: String, var Password: String, var Email: String)
data class RegisterUserResponse(var Status: String)