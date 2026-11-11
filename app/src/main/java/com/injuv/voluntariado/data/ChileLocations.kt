package com.injuv.voluntariado.data

/** Regiones de Chile con sus principales ciudades y comunas (subconjunto representativo). */
object ChileLocations {

    val data: Map<String, Map<String, List<String>>> = mapOf(
        "Arica y Parinacota" to mapOf(
            "Arica" to listOf("Arica", "Camarones"),
            "Parinacota" to listOf("Putre", "General Lagos"),
        ),
        "Tarapacá" to mapOf(
            "Iquique" to listOf("Iquique", "Alto Hospicio"),
            "Tamarugal" to listOf("Pozo Almonte", "Pica", "Huara", "Camiña", "Colchane"),
        ),
        "Antofagasta" to mapOf(
            "Antofagasta" to listOf("Antofagasta", "Mejillones", "Taltal", "Sierra Gorda"),
            "Calama" to listOf("Calama", "Ollagüe", "San Pedro de Atacama"),
            "Tocopilla" to listOf("Tocopilla", "María Elena"),
        ),
        "Atacama" to mapOf(
            "Copiapó" to listOf("Copiapó", "Caldera", "Tierra Amarilla"),
            "Vallenar" to listOf("Vallenar", "Alto del Carmen", "Freirina", "Huasco"),
            "Chañaral" to listOf("Chañaral", "Diego de Almagro"),
        ),
        "Coquimbo" to mapOf(
            "La Serena" to listOf("La Serena", "Coquimbo", "Andacollo", "Vicuña", "La Higuera", "Paihuano"),
            "Ovalle" to listOf("Ovalle", "Monte Patria", "Punitaqui", "Combarbalá", "Río Hurtado"),
            "Illapel" to listOf("Illapel", "Salamanca", "Los Vilos", "Canela"),
        ),
        "Valparaíso" to mapOf(
            "Valparaíso" to listOf("Valparaíso", "Viña del Mar", "Concón", "Quintero", "Puchuncaví", "Casablanca", "Juan Fernández"),
            "Quillota" to listOf("Quillota", "La Calera", "La Cruz", "Hijuelas", "Nogales"),
            "Quilpué" to listOf("Quilpué", "Villa Alemana", "Limache", "Olmué"),
            "San Antonio" to listOf("San Antonio", "Cartagena", "El Quisco", "El Tabo", "Algarrobo", "Santo Domingo"),
            "San Felipe" to listOf("San Felipe", "Catemu", "Llaillay", "Panquehue", "Putaendo", "Santa María"),
            "Los Andes" to listOf("Los Andes", "Calle Larga", "Rinconada", "San Esteban"),
            "Isla de Pascua" to listOf("Isla de Pascua"),
        ),
        "Metropolitana" to mapOf(
            "Santiago" to listOf(
                "Santiago", "Cerrillos", "Cerro Navia", "Conchalí", "El Bosque", "Estación Central",
                "Huechuraba", "Independencia", "La Cisterna", "La Florida", "La Granja", "La Pintana",
                "La Reina", "Las Condes", "Lo Barnechea", "Lo Espejo", "Lo Prado", "Macul", "Maipú",
                "Ñuñoa", "Pedro Aguirre Cerda", "Peñalolén", "Providencia", "Pudahuel", "Quilicura",
                "Quinta Normal", "Recoleta", "Renca", "San Joaquín", "San Miguel", "San Ramón", "Vitacura",
            ),
            "Puente Alto" to listOf("Puente Alto", "Pirque", "San José de Maipo"),
            "San Bernardo" to listOf("San Bernardo", "Buin", "Calera de Tango", "Paine"),
            "Colina" to listOf("Colina", "Lampa", "Tiltil"),
            "Talagante" to listOf("Talagante", "El Monte", "Isla de Maipo", "Padre Hurtado", "Peñaflor"),
            "Melipilla" to listOf("Melipilla", "Alhué", "Curacaví", "María Pinto", "San Pedro"),
        ),
        "O'Higgins" to mapOf(
            "Rancagua" to listOf(
                "Rancagua", "Machalí", "Graneros", "Mostazal", "Codegua", "Doñihue", "Las Cabras",
                "Malloa", "Olivar", "Peumo", "Pichidegua", "Quinta de Tilcoco", "Rengo", "Requínoa", "San Vicente",
            ),
            "San Fernando" to listOf("San Fernando", "Chimbarongo", "Nancagua", "Palmilla", "Placilla", "Santa Cruz"),
            "Pichilemu" to listOf("Pichilemu", "Litueche", "Marchigüe", "Navidad", "Paredones", "Peralillo"),
        ),
        "Maule" to mapOf(
            "Talca" to listOf(
                "Talca", "Constitución", "Curepto", "Empedrado", "Maule", "Pelarco",
                "Pencahue", "Río Claro", "San Clemente", "San Rafael",
            ),
            "Curicó" to listOf("Curicó", "Hualañé", "Licantén", "Molina", "Rauco", "Romeral", "Sagrada Familia", "Teno", "Vichuquén"),
            "Linares" to listOf("Linares", "Colbún", "Longaví", "Parral", "Retiro", "San Javier", "Villa Alegre", "Yerbas Buenas"),
            "Cauquenes" to listOf("Cauquenes", "Chanco", "Pelluhue"),
        ),
        "Ñuble" to mapOf(
            "Chillán" to listOf(
                "Chillán", "Chillán Viejo", "Bulnes", "Cobquecura", "Coelemu", "Ninhue", "Portezuelo",
                "Quirihue", "Ránquil", "San Carlos", "San Fabián", "San Nicolás", "Treguaco",
            ),
            "San Carlos" to listOf("Quillón", "El Carmen", "Pemuco", "Pinto", "Yungay", "Coihueco", "Ñiquén", "San Ignacio"),
        ),
        "Biobío" to mapOf(
            "Concepción" to listOf(
                "Concepción", "Coronel", "Chiguayante", "Florida", "Hualpén", "Hualqui", "Lota",
                "Penco", "San Pedro de la Paz", "Santa Juana", "Talcahuano", "Tomé",
            ),
            "Los Ángeles" to listOf(
                "Los Ángeles", "Antuco", "Cabrero", "Laja", "Mulchén", "Nacimiento", "Negrete",
                "Quilaco", "Quilleco", "San Rosendo", "Santa Bárbara", "Tucapel", "Yumbel", "Alto Biobío",
            ),
            "Arauco" to listOf("Arauco", "Cañete", "Contulmo", "Curanilahue", "Lebu", "Los Álamos", "Tirúa"),
        ),
        "La Araucanía" to mapOf(
            "Temuco" to listOf(
                "Temuco", "Carahue", "Cholchol", "Cunco", "Curarrehue", "Freire", "Galvarino", "Gorbea",
                "Lautaro", "Loncoche", "Melipeuco", "Nueva Imperial", "Padre Las Casas", "Perquenco",
                "Pitrufquén", "Pucón", "Saavedra", "Teodoro Schmidt", "Toltén", "Vilcún", "Villarrica",
            ),
            "Angol" to listOf(
                "Angol", "Collipulli", "Curacautín", "Ercilla", "Lonquimay", "Los Sauces",
                "Lumaco", "Purén", "Renaico", "Traiguén", "Victoria",
            ),
        ),
        "Los Ríos" to mapOf(
            "Valdivia" to listOf(
                "Valdivia", "Corral", "Futrono", "La Unión", "Lago Ranco", "Lanco", "Los Lagos",
                "Máfil", "Mariquina", "Paillaco", "Panguipulli", "Río Bueno",
            ),
        ),
        "Los Lagos" to mapOf(
            "Puerto Montt" to listOf(
                "Puerto Montt", "Calbuco", "Cochamó", "Fresia", "Frutillar", "Llanquihue",
                "Los Muermos", "Maullín", "Puerto Varas",
            ),
            "Osorno" to listOf("Osorno", "Purranque", "Río Negro", "San Juan de la Costa", "San Pablo", "Puyehue", "Puerto Octay"),
            "Castro" to listOf("Castro", "Ancud", "Chonchi", "Curaco de Vélez", "Dalcahue", "Puqueldón", "Queilén", "Quemchi", "Quinchao"),
            "Palena" to listOf("Chaitén", "Futaleufú", "Hualaihué", "Palena"),
        ),
        "Aysén" to mapOf(
            "Coyhaique" to listOf("Coyhaique", "Lago Verde", "Aysén", "Cisnes", "Guaitecas"),
            "Cochrane" to listOf("Cochrane", "O'Higgins", "Tortel"),
            "Chile Chico" to listOf("Chile Chico", "Río Ibáñez"),
        ),
        "Magallanes" to mapOf(
            "Punta Arenas" to listOf("Punta Arenas", "Laguna Blanca", "Río Verde", "San Gregorio"),
            "Puerto Natales" to listOf("Puerto Natales", "Torres del Paine"),
            "Porvenir" to listOf("Porvenir", "Primavera", "Timaukel"),
            "Cabo de Hornos" to listOf("Cabo de Hornos", "Antártica"),
        ),
    )

    val regions: List<String> get() = data.keys.toList()

    fun citiesOf(region: String): List<String> = data[region]?.keys?.toList() ?: emptyList()

    fun communesOf(region: String, city: String): List<String> =
        data[region]?.get(city) ?: emptyList()
}
