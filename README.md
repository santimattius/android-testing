# 🧨​ Android Testing [update in progress] 🧨​
<!-- 
This is a template to build an Android app applying good practices and testable architecture.

![pyramid](https://github.com/santimattius/android-testing/assets/22333101/2c4b30e2-b31c-4fe4-bb67-c947dedb4ca8)


## Screenshot
 -->
<p align="center">

  <img wight="280" src="https://github.com/santimattius/android-testing/blob/master/screenshoot/entertainment_app.png?raw=true" alt="App Capture"/>

</p>

<!-- 
## Unit Tests to Avoid
Some unit tests should be avoided because of their low value:

- Tests that verify the correct operation of the framework or a library, not your code.
- Framework entry points such as activities, fragments, or services should not have business logic so unit testing shouldn't be a priority. Unit tests for activities have little value, because they would cover mostly framework code and they require a more involved setup. Instrumented tests such as UI tests can cover these classes.
- 
[Intro to Unit Testing on Android (Spanish)](https://github.com/santimattius/android-testing/files/11521079/Intro.a.Unit.Testing.en.Android-1.pdf)

## Integration Testing
Integration or intermediate level tests are tests that validate the interactions between stack levels within a module or the interactions between related modules.

Check more content, here: [Intro to Integration Testing on Android (Spanish)](https://github.com/santimattius/android-testing/files/11521077/Intro.a.Tests.de.Integracion.en.Android-1.pdf)
 -->
## Content

TheMovieDB API: Check this [documentation](https://www.themoviedb.org/documentation/api).

## Setup

Using local properties for define api key:

```properties
apiKey="{your-api-key}"
```

## Verification

Run check project:

```shell
> ./gradlew check
```

Run tests project:

```shell
> ./gradlew test
```
<!-- 
## Dependencies

Below you will find the libraries used to build the template and according to my criteria the most
used in android development so far.

- **[Retrofit](https://square.github.io/retrofit/)**, networking.
- **[Gson](https://github.com/google/gson)**, json parser.
- **[Glide](https://github.com/bumptech/glide)**, with image loader.
- **[Coil](https://coil-kt.github.io/coil/compose/)**, with image loader for Jetpack compose.
- **[Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)**.
- Testing
  -  [Mockk](https://mockk.io/)
  -  [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
  -  [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)
  -  [Android Testing Library](https://developer.android.com/training/testing/local-tests)
  -  [Robolectric](https://robolectric.org/)
  -  [Espresso](https://developer.android.com/training/testing/espresso)
  -  [Jetpack Compose Testing API](https://developer.android.com/jetpack/compose/testing) 
-->
## References

- [Guide to app architecture](https://developer.android.com/jetpack/guide)
- [Android developers](https://developer.android.com/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Code](https://blog.cleancoder.com/)
