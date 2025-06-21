package com.example.fruit_quality

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Showprofile : AppCompatActivity() {

    private var database: FirebaseDatabase? = null
    private var mDatabase: DatabaseReference? = null
    var dname: String? = null
    var hname: String? = null
    var address: String? = null
    var desi: String? = null
    var contact: String? = null
    var udname: String? = null
    var uhname: String? = null
    var uhaddress: String? = null
    var udesi: String? = null
    var ucontact: String? = null
    var username: String? = null


    var sharedpreferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_showprofile)

        sharedpreferences = getSharedPreferences("pdfdata", MODE_PRIVATE)
        val name =  sharedpreferences!!.getString("name","")
        val key = sharedpreferences!!.getString("key","")
        Toast.makeText(applicationContext,key.toString(),Toast.LENGTH_LONG).show()

        Toast.makeText(applicationContext,name.toString(),Toast.LENGTH_LONG).show()
        database = FirebaseDatabase.getInstance()
        mDatabase = database!!.getReference("Profile")

        val txtdname = findViewById<TextView>(R.id.txtdname)
        val txthname = findViewById<TextView>(R.id.txthname)
        val txthaddress = findViewById<TextView>(R.id.txtadress)

        val image1= findViewById<ImageView>(R.id.img1)
        val mDatabaseRef = FirebaseDatabase.getInstance().getReference("Profile")

        val query: Query = mDatabaseRef.orderByChild("Name").equalTo(name)

        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    println(data)
                    val models: User? = data.getValue(User::class.java)

//                    val latitude: String = models!!.doctorname
//                    val longitude: String = models!!.Contactnumber

                    if (models != null) {
                        desi = models.Name
                    }
                    if (models != null) {
                        dname = models.Mobileno
                    }
                    if (models != null) {
                        hname = models.Address
                    }

                    txtdname.setText("Name -$desi")
                    txthname.setText("Mobile No -"+dname)
                    txthaddress.setText("Address -"+hname)

                    if (models != null) {
                        Glide.with(image1.getContext()).load(models.imageurl)
                            .into(image1)
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })



    }
}