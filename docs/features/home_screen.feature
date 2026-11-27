# Living documentation. No runner executes this file.
# Each scenario title is the exact name of a test in
# app/src/sharedTest/java/com/santimattius/template/ui/HomeScreenContentScenarios.kt,
# which runs on the JVM (app/src/test) and on a device (app/src/androidTest).
# Keep titles and test names in sync by hand.

Feature: Compose home screen content

  @P1
  Scenario: showsLoadingIndicatorWhileLoading
    Given the home screen state is loading
    Then the loading indicator is displayed

  @P1
  Scenario: showsErrorMessageWhenLoadingFails
    Given the home screen state has a loading error
    Then the loading error message is displayed

  @P2
  Scenario: showsEmptyMessageWhenThereAreNoMovies
    Given the home screen state has no movies
    Then the empty result message is displayed

  @P1
  Scenario: reportsFavoriteActionForTheClickedMovie
    Given the home screen state has one movie
    When the user taps the favorite button of that movie
    Then a favorite action is reported for that movie
