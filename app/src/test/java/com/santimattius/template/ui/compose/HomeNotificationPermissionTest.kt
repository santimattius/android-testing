package com.santimattius.template.ui.compose

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.santimattius.test.compose.HomeRobot
import com.santimattius.test.compose.scenario
import com.santimattius.test.rules.FakeImageLoaderRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.di.FakeDataModule
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.plugin.module.dsl.modules
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * The app's side of the notification permission, described in docs/features/notifications.feature.
 * The system dialog is never driven: the request is read from the Robolectric shadow and the answer
 * is delivered to the activity, as the platform would do.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], application = Application::class)
class HomeNotificationPermissionTest {

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        modules(DataModule::class, FakeDataModule::class)
    }

    @get:Rule(order = 1)
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HomeComposeActivity>()

    private val robot by lazy { HomeRobot(composeTestRule) }

    @Test
    fun `the app declares the notification permission`() {
        val activity = composeTestRule.activity
        val declared = activity.packageManager
            .getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .orEmpty()

        assertTrue(Manifest.permission.POST_NOTIFICATIONS in declared)
    }

    @Test
    fun `enabling notifications requests the permission`() = scenario(robot) {
        whenever { tapEnableNotifications() }
        then {
            assertArrayEquals(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                lastPermissionRequest()?.requestedPermissions,
            )
        }
    }

    @Test
    fun `granting the permission confirms that notifications are enabled`() = scenario(robot) {
        given { tapEnableNotifications() }
        whenever { answerPermissionRequest(PackageManager.PERMISSION_GRANTED) }
        then { assertMessageDisplayed("Notificaciones activadas") }
    }

    @Test
    fun `denying the permission explains that it was denied`() = scenario(robot) {
        given { tapEnableNotifications() }
        whenever { answerPermissionRequest(PackageManager.PERMISSION_DENIED) }
        then { assertMessageDisplayed("Permiso de notificaciones denegado") }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S_V2])
    fun `before Android 13 enabled notifications are confirmed without a request`() =
        scenario(robot) {
            whenever { tapEnableNotifications() }
            then {
                assertMessageDisplayed("Notificaciones activadas")
                assertNull(lastPermissionRequest())
            }
        }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S_V2])
    fun `before Android 13 disabled notifications are reported without a request`() =
        scenario(robot) {
            given { turnOffNotificationsInSystemSettings() }
            whenever { tapEnableNotifications() }
            then {
                assertMessageDisplayed("Las notificaciones están desactivadas en los ajustes")
                assertNull(lastPermissionRequest())
            }
        }

    private fun turnOffNotificationsInSystemSettings() {
        val manager = composeTestRule.activity.getSystemService(NotificationManager::class.java)
        shadowOf(manager).setNotificationsEnabled(false)
    }

    private fun lastPermissionRequest() =
        shadowOf(composeTestRule.activity).lastRequestedPermission

    // The Activity Result API receives platform answers through this callback.
    @Suppress("DEPRECATION")
    private fun answerPermissionRequest(grantResult: Int) {
        val request = checkNotNull(lastPermissionRequest()) { "No permission was requested" }
        composeTestRule.runOnUiThread {
            composeTestRule.activity.onRequestPermissionsResult(
                request.requestCode,
                request.requestedPermissions,
                intArrayOf(grantResult),
            )
        }
    }
}
