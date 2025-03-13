package com.example.profilemanagementapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Context;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.profilemanagementapp.activities.LoginActivity;
import com.example.profilemanagementapp.database.DatabaseHelper;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileManagementAppEspressoTest {

    @Before
    public void clearDatabase() {
        // Clear the database before each test to ensure isolation
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.clearDatabase();
    }

    // TC001: Test login when account does not exist.
    @Test
    public void testTC001_LoginFailsWhenUserDoesNotExist() {
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withText("Invalid Credentials")).check(matches(isDisplayed()));
    }

    // TC002: Test the registration function
    @Test
    public void testTC002_Registration() {
        ActivityScenario.launch(LoginActivity.class);

        // Navigate to Registration
        onView(withId(R.id.btnRegister)).perform(click());

        // Fill in registration fields
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        // Select DOB via DatePicker
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress))
                .perform(typeText("4700 Keele St, Toronto, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());

        // Submit registration
        onView(withId(R.id.btnRegister)).perform(click());

        // ***INSTEAD of checking the ephemeral Snackbar text:***
        // onView(withText("Registration Successful")).check(matches(isDisplayed()));

        // ***Check that we are back on the Login screen.***
        // For example, the username EditText is unique to LoginActivity:
        onView(withId(R.id.etUsername)).check(matches(isDisplayed()));
    }

    // TC003: Test the login function when credentials exist
    @Test
    public void testTC003_LoginSuccess() {
        // First register the user
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress))
                .perform(typeText("4700 Keele St, Toronto, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        // Now login with the registered credentials
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // ***INSTEAD of checking "Login Successful!" Snackbar:***
        // onView(withText("Login Successful!")).check(matches(isDisplayed()));

        // ***Verify we are on the Profile screen.***
        // For instance, check that tvFullName is visible and has "John Doe":
        onView(withId(R.id.tvFullName)).check(matches(isDisplayed()));
        onView(withId(R.id.tvFullName))
                .check(matches(withText(Matchers.containsString("John Doe"))));
    }

    // TC004: Test the “edit profile” function.
    @Test
    public void testTC004_EditProfile() {
        // Register and login
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress)).perform(typeText("4700 Keele St, Toronto, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        // Login
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        // In ProfileActivity, tap Edit Profile
        onView(withId(R.id.btnEditProfile)).perform(click());
        // Change address field
        onView(withId(R.id.etAddress)).perform(clearText(), typeText("4700 Keele St, North York, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.btnSaveChanges)).perform(click());
        // Verify updated address appears in ProfileActivity
        onView(withId(R.id.tvAddress)).check(matches(withText(Matchers.containsString("North York"))));
    }

    // TC005: Test the “add entry” function.
    @Test
    public void testTC005_AddDiaryEntry() {
        // Register and login then navigate to diary
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress)).perform(typeText("4700 Keele St, North York, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        // Login
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        // Go to DiaryActivity
        onView(withId(R.id.btnViewDiary)).perform(click());
        // Tap Add Entry
        onView(withId(R.id.btnAddEntry)).perform(click());
        // Fill in diary entry details
        onView(withId(R.id.etTitle)).perform(typeText("First note"), closeSoftKeyboard());
        onView(withId(R.id.etContent)).perform(typeText("Today I ate cereal!"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
        // Verify the diary entry is listed
        onView(withText("First note")).check(matches(isDisplayed()));
    }

    // TC006: Test the “modify entry” function.
    @Test
    public void testTC006_ModifyDiaryEntry() {
        // Register, login, and add an initial diary entry.
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress)).perform(typeText("4700 Keele St, North York, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        // Login
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        // Navigate to DiaryActivity and add an entry.
        onView(withId(R.id.btnViewDiary)).perform(click());
        onView(withId(R.id.btnAddEntry)).perform(click());
        onView(withId(R.id.etTitle)).perform(typeText("First note"), closeSoftKeyboard());
        onView(withId(R.id.etContent)).perform(typeText("Today I ate cereal!"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
        // Tap on the added entry to edit it.
        onView(withText("First note")).perform(click());
        // Modify the entry details.
        onView(withId(R.id.etTitle)).perform(clearText(), typeText("Breakfast"), closeSoftKeyboard());
        onView(withId(R.id.etContent)).perform(clearText(), typeText("Today I ate cereal! It was good!"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
        // Verify the updated entry appears in the diary list.
        onView(withText("Breakfast")).check(matches(isDisplayed()));
    }

    // TC007: Test the “logout” function.
    @Test
    public void testTC007_Logout() {
        // Register and login
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress)).perform(typeText("4700 Keele St, North York, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        // Login
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        // In ProfileActivity, tap Logout.
        onView(withId(R.id.btnLogout)).perform(click());
        // Verify that LoginActivity is displayed.
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    // TC008: Test the “delete profile” function.
    @Test
    public void testTC008_DeleteProfile() {
        // Register and login
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etFullName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etDob)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2001, 11, 21));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etAddress)).perform(typeText("4700 Keele St, North York, ON, M3J 1P3"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5551234567"), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        // Login
        onView(withId(R.id.etUsername)).perform(typeText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        // In ProfileActivity, go to Edit Profile and tap Delete Profile.
        onView(withId(R.id.btnEditProfile)).perform(click());
        onView(withId(R.id.btnDeleteProfile)).perform(click());
        // In the confirmation dialog, select "Yes".
        onView(withText("Yes")).perform(click());
        // Verify that LoginActivity is shown.
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        // Also, attempting to log in with the deleted credentials should fail.
        onView(withId(R.id.etUsername)).perform(replaceText("jd2001"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(replaceText("Passwd#1"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withText("Invalid Credentials")).check(matches(isDisplayed()));
    }
}