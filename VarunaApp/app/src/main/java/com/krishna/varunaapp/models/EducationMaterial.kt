package com.krishna.varunaapp.models

data class EducationMaterial(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val fileUrl: String = "",      // Base64 string
    val fileType: String = "",     // pdf or image
    val uploadedAt: Long = 0L,
    val uploadedBy: String = ""
)


//package com.krishna.varunaapp.models
//
//data class EducationMaterial(
//    val id: String = "",
//    val title: String = "",
//    val description: String = "",
//    val fileUrl: String = "",
//    val fileType: String = "",
//    val uploadedAt: Long = 0L,
//    val uploadedBy: String = ""
//)
