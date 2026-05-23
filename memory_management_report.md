# Memory Management Analysis - G45 Kotlin

## Objective
This section evaluates the app from a memory-management perspective, focusing on RAM stability after repeated navigation, heavy-screen loading, and back-navigation to `Home`. The goal is to identify whether the app behaves like a catastrophic leak, a transient retention pattern, or a normal burst-and-recovery mobile workload.

## Profiling Setup
- Tool: Android Studio Memory Profiler
- Build: debug
- Device: physical Android device
- Method:
  - first run used as warm-up,
  - second run used as the reference capture,
  - memory was observed at stable start-up and after several queries plus loading a heavy screen and returning to `Home`.

## Real Captures Used
Two real captures were used as evidence:

1. **Baseline capture after opening the app**
   - Total RAM: approximately `332.5 MB`
   - Java heap: approximately `23.2 MB`
   - Native memory: approximately `83 MB`
   - Graphics: approximately `38.6 MB`

2. **Post-navigation capture after several queries, opening a heavy screen, and returning to Home**
   - Total RAM: approximately `521.4 MB`
   - Java heap: approximately `39.1 MB`
   - Native memory: approximately `214 MB`
   - Graphics: approximately `75.9 MB`

## Interpretation of the Captures
The most important observation is that the strongest growth occurs in **Native** and **Graphics** memory, not in Java/Kotlin heap alone.

This suggests that the app is not primarily suffering from a classic Java leak. Instead, the dominant pressure is more consistent with:

- image and bitmap retention,
- graphical resources associated with Compose rendering,
- state retained across navigation,
- and screen stacks that remain alive longer than expected.

The second capture also shows that memory does not immediately return close to the initial baseline after returning to `Home`. This does not prove a catastrophic leak, but it does indicate a **retention plateau**: memory remains elevated longer than ideal after leaving a heavy screen.

## Code-Level Findings
The source code shows two likely contributors to this behavior:

### 1. Navigation stack growth
In `MainScreen.kt`, the bottom navigation was previously using direct navigation calls:

```kotlin
navController.navigate(it.label)
```

This pattern may create multiple instances of the same destination in the back stack, especially after repeated tab switching. In practice, this can retain:

- previous composable trees,
- previous `ViewModel` instances,
- `LazyListState`,
- collectors,
- and Firestore listeners indirectly tied to destinations.

### 2. Multiple in-memory caches without trim strategy
The app already uses several useful caches:

- `TutorRepository.kt`
- `ReservationDetailCacheManager.kt`
- `ReviewLruCache.kt`

These caches are positive for responsiveness, but before optimization they did not react to Android memory-pressure callbacks. In long sessions, this can preserve more memory than necessary when the system is already under pressure.

## Memory Behavior Assessment
Based on the captures and the code review, the app currently behaves as follows:

- It does **not** show immediate evidence of an irreversible Java heap leak.
- It **does** show a meaningful increase in native/graphics memory after heavy navigation.
- It likely retains screen and cache state longer than ideal after returning to lightweight destinations.

Therefore, the current behavior is better classified as:

> **temporary retention under navigation and image-heavy load, with insufficient recovery after back-navigation**

## Implemented Micro-optimizations
Two low-risk micro-optimizations were implemented.

### 1. Bottom-navigation memory optimization
The bottom navigation was updated to use:

- `launchSingleTop = true`
- `restoreState = true`
- `popUpTo(findStartDestination()) { saveState = true }`

Expected effect:
- avoids piling multiple copies of the same tab,
- reduces unnecessary retained destinations,
- improves memory recovery after repeated navigation loops.

### 2. Memory-pressure-aware cache trimming
A new `MemoryPressureManager` was added and wired into:

- `MainActivity.onTrimMemory()`
- `MainActivity.onLowMemory()`

When Android signals pressure, the app now:

- trims caches under moderate pressure,
- clears caches under stronger pressure.

This strategy was connected to:

- `TutorRepository`
- `ReservationDetailCacheManager`
- `ReviewLruCache`

Expected effect:
- lower retained working set during long sessions,
- better survivability on lower-memory devices,
- fewer late-session spikes after screen changes.

## Expected Post-optimization Trend
The following chart is **illustrative** and represents the expected direction after the optimizations above. It is **not** a profiler screenshot and should not be presented as raw measurement evidence.

```text
Illustrative memory trend

Before optimization:
Start baseline        ~332 MB
Heavy navigation      ~521 MB
Return to Home        high retention plateau

Expected after optimization:
Start baseline        ~332 MB
Heavy navigation      lower peak than before
Return to Home        faster drop toward steady state
```

```text
Illustrative comparison

Scenario                     Before            Expected after
Baseline open                332.5 MB          ~similar baseline
Heavy screen + back          521.4 MB          lower retained total
Recovery after return        slow plateau      faster stabilization
Native/Graphics share        high              reduced relative pressure
```

## Strengths
- The app already uses bounded LRU caches instead of unbounded collections.
- The review-detail screen uses cache-based navigation instead of redundant network fetches.
- Firestore listeners are built with `callbackFlow` and `awaitClose`, which is safer than unmanaged listeners.

## Weaknesses
- The app is image-heavy, especially in tutor and home flows.
- Bottom navigation originally allowed unnecessary destination stacking.
- Caches existed, but without aggressive release under memory pressure.

## Conclusion
The memory profile suggests that the app’s main risk is not a catastrophic JVM leak, but rather **retained UI/native/graphics state after heavy navigation**. The implemented optimizations target exactly that problem while keeping the architecture stable and avoiding risky refactors.

The recommended next step is to re-run the same profiler experiment and compare:

- total RAM peak,
- recovery after returning to `Home`,
- and the relative growth of `Native` and `Graphics` memory.
