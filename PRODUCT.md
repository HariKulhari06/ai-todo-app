# My First App Using AI (Codex)

## Product
Todo App is a multi-module Android application built with Jetpack Compose.

## Current Scope
- 3-screen experience based on a modern dark UI:
  - Onboarding
  - Dashboard
  - Task detail
- Slide-to-start onboarding interaction
- Edge-to-edge safe layout with proper system insets
- Dynamic task cards and detail state updates
- Mark-as-complete task flow

## Tech Stack
- Kotlin
- Jetpack Compose (Material 3)
- Android Gradle Plugin 9
- Multi-module architecture
  - `app`
  - `core:designsystem`
  - `core:navigation`
  - `feature:todo`
  - `feature:todo:domain`
  - `feature:todo:data`

## Architecture
- Presentation: Compose + ViewModel
- Domain: use cases + repository contract
- Data: in-memory repository provider
- Navigation: centralized route definitions in `core:navigation`

## Next Milestones
- Persist data with Room
- Add add/edit task form flow
- Add Settings/Profile feature module
- Add UI and unit tests
