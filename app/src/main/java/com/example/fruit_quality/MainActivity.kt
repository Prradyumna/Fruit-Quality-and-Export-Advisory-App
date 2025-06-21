package com.example.fruit_quality

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val edemail = findViewById<EditText>(R.id.edemail)
        val edpassword = findViewById<EditText>(R.id.edpassword)
        val btnforgot = findViewById<Button>(R.id.btnforgot)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        val btnregister = findViewById<Button>(R.id.btnregister)
        val image = findViewById<ImageView>(R.id.google)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //got to register activity

        btnregister.setOnClickListener {
            val intent = Intent(applicationContext,Register::class.java)
            startActivity(intent)

        }

        image.setOnClickListener {
            signInWithGoogle()
        }

        auth = FirebaseAuth.getInstance()


        //login
        btnlogin.setOnClickListener {

            if(edemail.text.isEmpty() ) {
                edemail.setError("Enter Email Id")
                return@setOnClickListener
            }
            else if (edpassword.text.isEmpty()){
                edpassword.setError("Enter Password")
                return@setOnClickListener
            }



            auth.signInWithEmailAndPassword(edemail.text.toString(),edpassword.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(applicationContext,"successfully Login", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, UserDashbord::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"Failed to login", Toast.LENGTH_LONG).show()
                    }
                }
        }



        //forgot password
        btnforgot.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.dailog_forgot,null)
            val username = view.findViewById<EditText>(R.id.ed_forgot)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                forgotpassword(username)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()

        }



    }

    private fun signInWithGoogle(){

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->
        if(result.resultCode == Activity.RESULT_OK)
        {
            val task =  GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {

        if(task.isSuccessful)
        {
            val account:GoogleSignInAccount? = task.result
            if(account!=null)
            {
                updateUI(account)
            }
        }else
        {
            Toast.makeText(applicationContext,"SignIn Failed",Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount)
    {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful)
            {
                Toast.makeText(applicationContext,"successfully Login", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, UserDashbord::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(applicationContext,"Failed To Login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun forgotpassword(username: EditText) {

        auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(applicationContext,"Email Sent", Toast.LENGTH_LONG).show()
                }
            }

    }

}