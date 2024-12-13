package com.example.trashissue.models

import com.google.gson.annotations.SerializedName

data class PredictResponse(
	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("suggestion")
	val suggestion: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null
)