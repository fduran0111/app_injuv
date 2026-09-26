# INJUV Voluntariado — App Android nativa (Kotlin + Jetpack Compose)

Conversión 1:1 del diseño Figma Make (React/TypeScript) a un proyecto nativo de Android
Studio, usando **Kotlin + Jetpack Compose** (Material 3) y **Navigation Compose**.

## Pasos para abrirlo

1. Descomprime este .zip.
2. **Agrega las tipografías** (obligatorio, si no el proyecto no compila):
   - Descarga *Space Grotesk* y *Outfit* desde Google Fonts.
   - Copia los archivos `.ttf` dentro de `app/src/main/res/font/` con los nombres
     exactos indicados en `app/src/main/res/font/README_FUENTES.txt`.
3. Abre la carpeta del proyecto con **Android Studio (Koala o superior)**.
4. Deja que Android Studio sincronice Gradle (te pedirá regenerar el `gradle-wrapper.jar`
   si no está presente; acepta la sugerencia o ejecuta `gradle wrapper` una vez con
   Gradle instalado localmente).
5. Ejecuta en un emulador o dispositivo con Android 7.0 (API 24) o superior.

## Qué se tradujo y decisiones tomadas

- **Stack**: Jetpack Compose (no XML/Views) porque tu diseño original ya está armado
  como componentes declarativos con un tema claro/oscuro — es el equivalente más
  directo a React + Tailwind/inline-styles.
- **Estructura de pantallas**: cada pantalla de `src/screens/*.tsx` tiene su
  equivalente en `app/.../screens/*.kt`, con los mismos textos, datos de ejemplo
  y jerarquía visual (headers con degradado, tarjetas, chips de filtro, etc).
- **Navegación**: `App.tsx` (estado manual de pantalla) se tradujo a
  **Navigation Compose** (`AppNavigation.kt`), con rutas por nombre y el detalle
  de postulación recibiendo el `id` como argumento de navegación.
- **Tema claro/oscuro**: `theme.ts` y `ThemeContext.tsx` se tradujeron a
  `ui/theme/Theme.kt` (colores idénticos, con alfas recalculados a hex ARGB).
  Por defecto la app inicia en modo oscuro, igual que el diseño original
  (`MainActivity.kt` → `ProvideAppTheme(isDark = true)`).
- **Imágenes remotas**: se usa Coil (`AsyncImage`) para cargar las mismas URLs
  de Unsplash que usaba el diseño original.
- **Iconos**: se reemplazaron los SVG originales por Material Icons equivalentes
  (Home, Assignment, Article, School, Person, etc.) — visualmente muy cercanos,
  pero no son el mismo trazado SVG exacto.
- **Marco de teléfono / barra de estado simulada**: el diseño en Figma Make
  incluía una simulación de celular (para verse en el navegador). Se omitió
  a propósito: una app Android real ya tiene su propia barra de estado del
  sistema operativo.
- **Datos**: son de ejemplo (`SampleData.kt`), igual que en el proyecto original.
  Los botones de acción (postular, aceptar/rechazar postulante, descargar
  documento) están marcados con `// TODO` para conectarlos a tu backend
  FastAPI cuando integres el flujo real.

## Estructura del proyecto

```
app/src/main/java/com/injuv/voluntariado/
├── MainActivity.kt
├── navigation/AppNavigation.kt      (NavHost + bottom bar + alerta de estado)
├── ui/theme/Theme.kt                (colores claro/oscuro + tipografías)
├── data/Models.kt                   (data classes)
├── data/SampleData.kt               (datos de ejemplo)
├── components/CommonComponents.kt   (chips de filtro, bottom nav icons)
├── components/StatusAlertSheet.kt   (alerta de cambio de estado)
└── screens/
    ├── HomeScreen.kt
    ├── ApplicationsScreen.kt
    ├── ApplicationDetailScreen.kt
    ├── ProfileScreen.kt
    ├── AdminScreen.kt
    ├── NewsScreen.kt
    ├── DocumentsScreen.kt
    ├── AcademyScreen.kt
    └── LibraryScreen.kt
```
