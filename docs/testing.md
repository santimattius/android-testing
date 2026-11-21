# Testing guide

This project follows the official Android testing guidance. The strategy favors many small, fast,
deterministic tests and fewer larger ones, as in the
[testing pyramid](https://developer.android.com/training/testing/fundamentals/strategies).

## Test layers

| Layer | Where | Runs on | What it proves |
|---|---|---|---|
| Local unit | `*/src/test` | JVM | Logic of repositories, data sources, view models and fakes |
| Local integration | `*/src/test` | JVM (Robolectric, MockWebServer) | Several real classes together, such as repository plus fake sources or Room |
| Component UI | `app/src/androidTest` (`HomeScreenContentTest`) | Device | One composable rendered from controlled state |
| Instrumented integration | `app/src/androidTest` (activity tests) | Device | Activity, DI wiring and UI together, with fake data sources |
| Device-backed data | `core/src/androidTest` (`MovieDaoTest`) | Device | Room queries against a real SQLite database |

Pick the lowest layer that can prove the behavior
([what to test](https://developer.android.com/training/testing/fundamentals/what-to-test)).
Framework entry points such as activities hold no business logic, so they are covered by
instrumented tests instead of unit tests.

## Conventions

- **Prefer fakes over mocks.** `FakeMovieLocalDataSource`, `FakeMovieNetworkDataSource` and
  `FakeMovieRepository` in `shared-test` behave like the real thing. `TMDbRepositoryTest` shows the
  preferred style; Mockito and MockK are deliberately not project dependencies
  ([test doubles](https://developer.android.com/training/testing/fundamentals/test-doubles)).
- **Fakes publish immutable snapshots.** Collectors never see a list change after receiving it.
- **Hermetic tests.** No test reaches the network. HTTP goes through `MockWebServerRule`, images
  through `FakeImageLoaderRule` (`coil-test`, shared by unit and instrumented tests), and the app graph through `FakeDataModule`.
- **Never swallow cancellation.** Code that catches `Throwable` must rethrow
  `CancellationException`; `RetrofitMovieNetworkDataSourceTest` guards this.
- **No sleeps.** Wait for a condition, not for time. Compose tests that show the loading spinner
  drive the clock manually because the indeterminate progress animation never lets Compose go idle
  ([test stability](https://developer.android.com/training/testing/instrumented-tests/stability)).
- **Test the smallest UI contract.** Prefer a plain composable with state and callbacks over the
  full activity when only layout or a callback is under test.
- **Fixtures.** Use `MovieMother` and `MovieBuilder` instead of inline JSON or repeated setup.

## Coroutine and refresh decisions

- **Main-safe data access.** Room DAO and Retrofit service calls use suspend APIs, so the data
  sources do not inject a dispatcher just to call them. If a source later performs blocking or
  expensive work itself, it must move that work off the main thread and inject the dispatcher
  ([data-layer threading](https://developer.android.com/topic/architecture/data-layer#threading)).
- **ViewModel-owned work.** ViewModels start screen business-logic coroutines and expose state;
  inject their dispatcher for deterministic tests. Use `runTest` and test dispatchers sharing one
  scheduler ([coroutine best practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)).
- **Cancellation is not a failure result.** Rethrow `CancellationException` when catching broad
  exceptions, so cancellation reaches the caller
  ([exception guidance](https://developer.android.com/kotlin/coroutines/coroutines-best-practices#watch-out-for-exceptions)).
- **Refresh replaces; save adds.** The app atomically upserts incoming movies and deletes stale
  entries in a Room 2 `@Transaction`, preserving favorites for matching IDs. An empty refresh
  removes all cached entries; a failed transaction leaves the previous cache intact. Ordinary
  `save` remains additive. This is the app's policy, not a universal Room requirement: Android's
  [Room 2 guide](https://developer.android.com/training/data-storage/room/v2) shows `@Transaction`,
  and its [network/database guide](https://developer.android.com/topic/libraries/architecture/paging/v3-network-db)
  groups refresh database writes in a transaction for consistency. `RoomMovieRefreshTest` checks
  replacement, favorite retention and rollback.

## Coverage

Coverage uses AGP's built-in reports (`enableUnitTestCoverage`, `enableAndroidTestCoverage`).
Unit and instrumented coverage are tracked **separately**, because instrumented tests have different
goals ([CI guidance](https://developer.android.com/training/testing/continuous-integration/automation)).

```shell
./gradlew createDebugUnitTestCoverageReport      # build/reports/coverage/test/debug
./gradlew createDebugAndroidTestCoverageReport   # build/reports/coverage/androidTest/debug/connected
./gradlew coverageRegressionCheck                # compare against the baseline
```

`coverageRegressionCheck` fails when line coverage drops more than one point below
`config/coverage/baseline.properties`. This is a regression check rather than an absolute
threshold, as the guidance recommends. Raise a baseline when coverage improves and never lower it to
make a check pass. Select a kind with `-PcoverageKinds=test` or `-PcoverageKinds=androidTest`, and
generate the reports first.

Reports include generated code (Koin, data binding), which lowers the percentages. Read the trend,
not the absolute number.

## Instrumented tests

- Tests run under Android Test Orchestrator, so each test has its own `Instrumentation` and a crash
  does not stop the suite. `clearPackageData` is intentionally **not** set: it deletes the coverage
  data from the device.
- `pixel9api35` is a [Gradle-managed device](https://developer.android.com/studio/test/gradle-managed-devices)
  (Pixel 9, API 35, `aosp-atd` image), so no emulator has to be started by hand.
- Room is tested on a device in `MovieDaoTest` with an in-memory database.

## Continuous integration

Defined in `.github/workflows/main.yml`:

| Job | When | What |
|---|---|---|
| Detekt | Pull requests and `master` | Static analysis |
| Unit tests, lint and coverage | Pull requests and `master` | `test lint`, coverage report, regression check |
| Instrumented tests | `master`, nightly, manual | `pixel9api35DebugAndroidTest` with KVM and a software GPU |

Small tests run on every change; slower device tests run less often. Reports are uploaded as build
artifacts.

## Known limitations

- Instrumented coverage regression is checked locally only. The managed-device job runs the 14
  instrumented tests on GitHub-hosted runners (about 10 minutes), but it does not generate a
  coverage report, and its results land under `build/outputs/androidTest-results/managedDevice/`
  rather than `connected/`, so the regression check does not read them yet.
- Activity and fragment tests exist both under Robolectric (`app/src/test`) and as instrumented
  tests (`app/src/androidTest`).
