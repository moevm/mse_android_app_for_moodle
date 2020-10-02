package com.eltech.moevmmoodle

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // tell the Dalvik VM to run your code in addition to the existing code in
        // the onCreate() of the parent class. If you leave out this line,
        // then only your code is run. The existing code is ignored completely.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Represents a standard navigation menu for application.
        // The menu contents can be populated by a menu resource file.
        // https://developer.android.com/reference/com/google/android/material/navigation/NavigationView?authuser=1
        val navView: NavigationView = findViewById(R.id.nav_view)

        // rule app navigation in NavHost
        // move content from...to during user interaction
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }
}
