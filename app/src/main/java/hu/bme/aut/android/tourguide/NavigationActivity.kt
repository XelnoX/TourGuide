package hu.bme.aut.android.tourguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class NavigationActivity : AppCompatActivity() {
    lateinit var password: String
    val PREFS_FILENAME = "hu.bme.aut.android.tourguide.mypreference"
    val PASSWORD = "userPassword"
    private lateinit var navView: BottomNavigationView
    var cityList = mutableListOf<City>()
    var routeList = mutableListOf<Route>()
    private val cityRef = FirebaseDatabase.getInstance().getReference("cities")
    private val routeRef = FirebaseDatabase.getInstance().getReference("routes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        navView = findViewById(R.id.bottom_navigation_view)

        this.supportActionBar?.hide()
        menuItemsPassword()

        cityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snap: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snap: DataSnapshot) {
                for(dataSnap in snap.children){
                    val city = dataSnap.getValue(City::class.java)
                    if(city != null){
                        cityList.add(city)
                    }
                }
            }
        })
        routeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snap: DataSnapshot) {
                for(dataSnap in snap.children){
                    val route = dataSnap.getValue(Route::class.java)
                    if(route != null){
                        routeList.add(route)
                    }
                }
            }
        })

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navigation.setOnNavigationItemSelectedListener(onNavClick)
        replaceFragment(ProfileFragment())
    }

    private val onNavClick = BottomNavigationView.OnNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.navigation_profile ->{
                    replaceFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_tours ->{
                    replaceFragment(RoutesFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map ->{
                    replaceFragment(MapFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
        return@OnNavigationItemSelectedListener true
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, fragment)
        fragmentTransaction.commit()
    }

    private fun isPasswordStrong(): Boolean{
        if(password.length < 8){
            return false
        }
        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var match = pattern.matcher(password)
        if(!match.matches()){
            return false
        }
        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return false
        }
        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return false
        }
        return true
    }

    fun menuItemsPassword(){
        val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        password = prefs.getString(PASSWORD,"")!!
        if(!isPasswordStrong()){
            navView.menu.findItem(R.id.navigation_tours).isEnabled = false
            navView.menu.findItem(R.id.navigation_map).isEnabled = false
        }else{
            navView.menu.findItem(R.id.navigation_tours).isEnabled = true
            navView.menu.findItem(R.id.navigation_map).isEnabled = true
        }
    }
}
