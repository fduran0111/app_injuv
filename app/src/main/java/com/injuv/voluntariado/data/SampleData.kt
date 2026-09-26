package com.injuv.voluntariado.data

// Datos de muestra tomados 1:1 del diseño original (Figma Make).
// La vista Home usa los primeros 2 elementos de "opportunities" como destacados.
object SampleData {

    val opportunities = listOf(
        Opportunity(
            id = 1,
            title = "Tutoría Ambiental",
            org = "Ministerio del Ambiente",
            location = "Lima",
            dateRange = "15 Sep – 30 Oct 2026",
            hours = 40,
            spots = 12,
            deadline = "30 Sep 2026",
            tags = listOf("Ambiente", "Educación"),
            description = "Apoya en talleres de educación ambiental para estudiantes de primaria en zonas vulnerables de Lima.",
            imageUrl = "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=400&h=220&fit=crop&auto=format",
        ),
        Opportunity(
            id = 2,
            title = "Alfabetización Digital",
            org = "MINEDU",
            location = "Cusco",
            dateRange = "1 Oct – 15 Nov 2026",
            hours = 60,
            spots = 8,
            deadline = "15 Oct 2026",
            tags = listOf("Tecnología", "Educación"),
            description = "Enseña competencias digitales básicas a adultos mayores y comunidades rurales del Cusco.",
            imageUrl = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=400&h=220&fit=crop&auto=format",
        ),
        Opportunity(
            id = 3,
            title = "Salud Comunitaria",
            org = "MINSA",
            location = "Arequipa",
            dateRange = "1 Oct – 20 Oct 2026",
            hours = 80,
            spots = 20,
            deadline = "20 Oct 2026",
            tags = listOf("Salud"),
            description = "Participa en brigadas de salud preventiva en comunidades de Arequipa.",
            imageUrl = "https://images.unsplash.com/photo-1584820927498-cfe5211fd8bf?w=400&h=220&fit=crop&auto=format",
        ),
        Opportunity(
            id = 4,
            title = "Arte y Cultura Urbana",
            org = "Cultura Lima",
            location = "Lima",
            dateRange = "1 Oct – 5 Oct 2026",
            hours = 30,
            spots = 15,
            deadline = "5 Oct 2026",
            tags = listOf("Arte"),
            description = "Colabora en murales y talleres de arte urbano en espacios públicos de Lima.",
            imageUrl = "https://images.unsplash.com/photo-1547891654-e66ed7ebb968?w=400&h=220&fit=crop&auto=format",
        ),
        Opportunity(
            id = 5,
            title = "Reforestación Andina",
            org = "SERNANP",
            location = "Puno",
            dateRange = "1 Oct – 12 Oct 2026",
            hours = 48,
            spots = 25,
            deadline = "12 Oct 2026",
            tags = listOf("Ambiente"),
            description = "Únete a jornadas de reforestación en las laderas del lago Titicaca.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=400&h=220&fit=crop&auto=format",
        ),
    )

    val categories = listOf("Todos", "Educación", "Ambiente", "Salud", "Tecnología", "Arte")

    val activePrograms = listOf(
        ActiveProgram(1, "Tutoría Ambiental", "Min. del Ambiente", 65, 26, 40, "30 Oct 2026"),
        ActiveProgram(2, "Brigada de Salud", "MINSA Arequipa", 20, 16, 80, "15 Nov 2026"),
    )

    val historyPrograms = listOf(
        HistoryProgram(3, "Reforestación Lima Norte", "SERNANP", 48, "2025", true, 5),
        HistoryProgram(4, "Alfabetización Básica", "MINEDU", 60, "2025", true, 5),
        HistoryProgram(5, "Arte Comunitario", "Cultura Lima", 30, "2024", true, 4),
        HistoryProgram(6, "Monitores Ambientales", "Min. del Ambiente", 40, "2024", false, 4),
    )

    val pendingApplicants = listOf(
        Applicant(1, "Carlos Mamani", "Alfabetización Digital", "29 Ago 2026", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format"),
        Applicant(2, "Ana Lucía Torres", "Tutoría Ambiental", "28 Ago 2026", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&auto=format"),
        Applicant(3, "Diego Quispe", "Salud Comunitaria", "27 Ago 2026", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&auto=format"),
        Applicant(4, "Valeria Salas", "Arte Comunitario", "26 Ago 2026", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=80&h=80&fit=crop&auto=format"),
    )

    val programsCapacity = listOf(
        ProgramCapacity("Tutoría Ambiental", 18, 20),
        ProgramCapacity("Alfabetización Digital", 5, 8),
        ProgramCapacity("Salud Comunitaria", 14, 20),
        ProgramCapacity("Arte Comunitario", 9, 15),
        ProgramCapacity("Reforestación Andina", 22, 25),
    )

    val news = listOf(
        NewsItem(1, "Convocatoria Nacional de Voluntariado 2026 alcanza récord histórico", "Más de 5,000 jóvenes se postularon en las primeras 48 horas de apertura de la convocatoria.", "29 Ago 2026", "Institucional", "3 min", true, "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=500&h=280&fit=crop&auto=format"),
        NewsItem(2, "Nuevos programas en alianza con universidades privadas", "El programa amplía su red con 12 universidades para el segundo semestre.", "26 Ago 2026", "Alianzas", "2 min", false, "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=300&h=180&fit=crop&auto=format"),
        NewsItem(3, "Voluntarios peruanos ganan premio iberoamericano", "El equipo de reforestación recibió el premio Iberoamérica Verde 2026.", "22 Ago 2026", "Reconocimientos", "4 min", false, "https://images.unsplash.com/photo-1567427018141-0584cfcbf1b8?w=300&h=180&fit=crop&auto=format"),
        NewsItem(4, "Actualización del reglamento de horas certificadas", "El MINEDU actualiza los criterios para la certificación de horas voluntarias.", "18 Ago 2026", "Normativa", "5 min", false, "https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=300&h=180&fit=crop&auto=format"),
    )

    val newsCategories = listOf("Todas", "Institucional", "Alianzas", "Reconocimientos", "Normativa")

    val documents = listOf(
        DocumentItem(1, "Reglamento de Voluntariado Juvenil 2026", "Normativa", "2.4 MB", "15 Ago 2026", "PDF", 1240),
        DocumentItem(2, "Formulario de Inscripción — Programas Presenciales", "Formularios", "340 KB", "1 Ago 2026", "DOCX", 876),
        DocumentItem(3, "Guía del Voluntario — Edición 2026", "Guías", "5.1 MB", "20 Jul 2026", "PDF", 3412),
        DocumentItem(4, "Modelo de Carta de Presentación Institucional", "Formularios", "128 KB", "10 Jul 2026", "DOCX", 654),
        DocumentItem(5, "Protocolo de Seguridad en Campo", "Seguridad", "1.8 MB", "5 Jul 2026", "PDF", 425),
        DocumentItem(6, "Planilla de Registro de Horas", "Formularios", "96 KB", "1 Jul 2026", "XLSX", 2087),
        DocumentItem(7, "Manual de Comunicación Institucional", "Guías", "3.2 MB", "15 Jun 2026", "PDF", 318),
        DocumentItem(8, "Convenio Marco de Colaboración — Plantilla", "Normativa", "210 KB", "1 Jun 2026", "DOCX", 204),
    )

    val documentCategories = listOf("Todos", "Normativa", "Formularios", "Guías", "Seguridad")

    val courses = listOf(
        Course(1, "Liderazgo Transformacional para Jóvenes", "Dr. Marco Villanueva", "6 semanas", 12, 75, true, "Intermedio", "Liderazgo", "https://images.unsplash.com/photo-1552664730-d307ca884978?w=300&h=180&fit=crop&auto=format", 4.8, 342),
        Course(2, "Gestión de Proyectos Sociales", "Lic. Patricia Salas", "4 semanas", 8, 0, false, "Básico", "Gestión", "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=300&h=180&fit=crop&auto=format", 4.6, 218),
        Course(3, "Comunicación Comunitaria Efectiva", "Mgtr. Rosa Chávez", "3 semanas", 6, 33, true, "Básico", "Comunicación", "https://images.unsplash.com/photo-1573164713714-d95e436ab8d6?w=300&h=180&fit=crop&auto=format", 4.9, 512),
        Course(4, "Primeros Auxilios y Emergencias", "Dr. Felipe Torres", "2 semanas", 5, 0, false, "Básico", "Salud", "https://images.unsplash.com/photo-1584820927498-cfe5211fd8bf?w=300&h=180&fit=crop&auto=format", 4.7, 681),
    )

    val courseLevels = listOf("Todos", "Básico", "Intermedio", "Avanzado")

    val books = listOf(
        Book(1, "El Poder del Voluntariado Juvenil", "Carmen Ruiz Díaz", 2025, 248, "Liderazgo", false, "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=120&h=160&fit=crop&auto=format"),
        Book(2, "Innovación Social en América Latina", "Jorge Medina P.", 2024, 312, "Gestión", true, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=120&h=160&fit=crop&auto=format"),
        Book(3, "Comunidad, Identidad y Territorio", "Luz Elena Vargas", 2025, 186, "Sociedad", false, "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=120&h=160&fit=crop&auto=format"),
        Book(4, "Psicología del Servicio Comunitario", "Rafael Soto M.", 2023, 274, "Psicología", true, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=120&h=160&fit=crop&auto=format"),
        Book(5, "Medioambiente y Acción Colectiva", "Ana Pacheco R.", 2026, 198, "Ambiente", false, "https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=120&h=160&fit=crop&auto=format"),
        Book(6, "Tecnología para el Bien Común", "Diego Villanueva", 2026, 156, "Tecnología", false, "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=120&h=160&fit=crop&auto=format"),
    )

    val bookCategories = listOf("Todos", "Liderazgo", "Gestión", "Sociedad", "Psicología", "Ambiente", "Tecnología")
}
