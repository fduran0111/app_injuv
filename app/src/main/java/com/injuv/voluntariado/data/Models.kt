package com.injuv.voluntariado.data

data class Opportunity(
    val id: Int,
    val title: String,
    val org: String,
    val location: String,
    val dateRange: String,
    val hours: Int,
    val spots: Int,
    val deadline: String,
    val tags: List<String>,
    val description: String,
    val imageUrl: String,
)

data class ActiveProgram(
    val id: Int,
    val title: String,
    val org: String,
    val progress: Int,
    val hours: Int,
    val totalHours: Int,
    val deadline: String,
)

data class HistoryProgram(
    val id: Int,
    val title: String,
    val org: String,
    val hours: Int,
    val year: String,
    val certificate: Boolean,
    val rating: Int,
)

data class AppliedProgram(
    val id: Int,
    val title: String,
    val org: String,
    val appliedDate: String,
    val status: String,
)

data class Applicant(
    val id: Int,
    val name: String,
    val program: String,
    val date: String,
    val avatarUrl: String,
)

data class ProgramCapacity(
    val name: String,
    val enrolled: Int,
    val cap: Int,
)

data class NewsItem(
    val id: Int,
    val title: String,
    val excerpt: String,
    val date: String,
    val category: String,
    val readTime: String,
    val featured: Boolean,
    val imageUrl: String,
)

data class DocumentItem(
    val id: Int,
    val title: String,
    val category: String,
    val size: String,
    val date: String,
    val type: String,
    val downloads: Int,
)

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val duration: String,
    val modules: Int,
    val progress: Int,
    val enrolledDefault: Boolean,
    val level: String,
    val category: String,
    val imageUrl: String,
    val rating: Double,
    val students: Int,
)

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int,
    val pages: Int,
    val category: String,
    val savedDefault: Boolean,
    val coverUrl: String,
)
