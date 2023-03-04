![Quality Checks](https://github.com/santimattius/android-testing//actions/workflows/main.yml/badge.svg)

# Android Testing

This is a template to build an Android app applying good practices and using a clean architecture,
you will see that the code is super decoupled with external frameworks and even with the same
Android framework, this will help you to model your domain purely in Kotlin without generating
external dependencies.

## Screenshot

<p align="center">

  <img wight="280" src="https://github.com/santimattius/android-testing/blob/master/screenshoot/entertainment_app.png?raw=true" alt="App Capture"/>

</p>

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

## Dependencies

Below you will find the libraries used to build the template and according to my criteria the most
used in android development so far.

- **[Retrofit](https://square.github.io/retrofit/)**, networking.
- **[Moshi](https://github.com/square/moshi)**, json parser.
- **[Glide](https://github.com/bumptech/glide)**, with image loader.
- **[Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)**.

## References

- [Guide to app architecture](https://developer.android.com/jetpack/guide)
- [Android developers](https://developer.android.com/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Code](https://blog.cleancoder.com/)
