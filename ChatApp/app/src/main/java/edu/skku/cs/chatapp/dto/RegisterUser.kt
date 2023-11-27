package edu.skku.cs.chatapp.dto

data class RegisterUser(var UserName: String, var Email: String, var Password: String)
data class RegisterUserResponse(var Status: String)