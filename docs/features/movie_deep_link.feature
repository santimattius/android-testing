# Living documentation. No runner executes this file.
# Each scenario title is the exact name of a test in
# app/src/test/java/com/santimattius/template/ui/compose/HomeMovieDeepLinkTest.kt.
# Keep titles and test names in sync by hand.
# Links have the form androidtesting://movies/{id} and are untrusted input.

Feature: Movie deep link

  @P1
  Scenario: a browsable movie link resolves to the Compose home screen
    Given the installed app
    When a browsable movie link is resolved
    Then the Compose home screen is the only match

  @P1
  Scenario: opening a movie link shows the detail of that movie
    When the user opens a link to a cached movie
    Then the detail of that movie is displayed

  @P2
  Scenario: an open detail survives a configuration change
    Given a movie detail opened from a link
    When the screen is recreated
    Then the detail is still displayed

  @P1
  Scenario: a dismissed detail is not shown again after a configuration change
    Given a movie detail opened from a link and then closed
    When the screen is recreated
    Then no movie detail is displayed

  @P2
  Scenario: the activity is reused for links received while it is on top
    Given the installed app
    Then the Compose home screen uses the singleTop launch mode

  @P1
  Scenario: a movie link received while the screen is open shows that movie
    Given a movie detail opened from a link and then closed
    When a link to another cached movie is received
    Then the detail of that movie is displayed

  @P2
  Scenario: an unrelated intent received while a detail is open keeps it
    Given a movie detail opened from a link
    When an intent without a movie link is received
    Then the detail is still displayed

  @P1
  Scenario: a movie link relaunched from recents shows no detail
    When a finished movie link task is reopened from Recents
    Then the movie list is displayed without a detail

  @P2
  Scenario: a movie link redelivered from recents does not reopen a dismissed detail
    Given a movie detail opened from a link and then closed
    When the same link is received flagged as launched from Recents
    Then no movie detail is displayed

  @P2
  Scenario: a link with an unknown id shows no detail
    When the user opens a link to a movie that is not cached
    Then the movie list is displayed without a detail

  @P1
  Scenario: a link with a non numeric id shows no detail
    When the user opens a link whose id is not a number
    Then the movie list is displayed without a detail

  @P2
  Scenario: a link with an oversized id shows no detail
    When the user opens a link whose id does not fit in a 64 bit number
    Then the movie list is displayed without a detail

  @P2
  Scenario: a link with extra path segments shows no detail
    When the user opens a link with more than one path segment
    Then the movie list is displayed without a detail

  @P2
  Scenario: a link with a different host shows no detail
    When an explicit intent carries a link with another host
    Then the movie list is displayed without a detail

  @P2
  Scenario: an opaque link shows no detail
    When an explicit intent carries a non hierarchical link
    Then the movie list is displayed without a detail

  @P2
  Scenario: a movie link sent with another action shows no detail
    When an explicit intent carries a movie link with an action other than view
    Then the movie list is displayed without a detail
