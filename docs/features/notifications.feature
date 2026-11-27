# Living documentation. No runner executes this file.
# Each scenario title is the exact name of a test in
# app/src/test/java/com/santimattius/template/ui/compose/HomeNotificationPermissionTest.kt.
# Keep titles and test names in sync by hand.
# The app only asks for the permission; it does not send notifications.

Feature: Enable notifications

  @P2
  Scenario: the app declares the notification permission
    Given the installed app
    Then it requests the POST_NOTIFICATIONS permission in its manifest

  @P1
  Scenario: enabling notifications requests the permission
    Given a device running Android 13 or later
    When the user taps the enable notifications action
    Then the notification permission is requested

  @P1
  Scenario: granting the permission confirms that notifications are enabled
    Given the user tapped the enable notifications action
    When the permission is granted
    Then a message confirms that notifications are enabled

  @P1
  Scenario: denying the permission explains that it was denied
    Given the user tapped the enable notifications action
    When the permission is denied
    Then a message says that the permission was denied

  @P2
  Scenario: before Android 13 enabled notifications are confirmed without a request
    Given a device running a version earlier than Android 13
    When the user taps the enable notifications action
    Then a message confirms that notifications are enabled
    And no permission is requested

  @P2
  Scenario: before Android 13 disabled notifications are reported without a request
    Given a device running a version earlier than Android 13
    And notifications turned off for the app in system settings
    When the user taps the enable notifications action
    Then a message says that notifications are turned off in settings
    And no permission is requested
