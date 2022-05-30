package com.example.foodapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.foodapp.databinding.ActivityLoginBinding
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.view.MenuItem
import com.example.foodapp.MainActivity
import com.example.foodapp.WelcomeActivity
import com.example.foodapp.model.User
import com.facebook.*
import com.facebook.login.widget.LoginButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding:ActivityLoginBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //ProgressDiaLog
    private lateinit var progressDiaLog: ProgressDialog
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseDatabase
    private lateinit var database: FirebaseFirestore
    //FireBaseGoogleAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }
    //FireBaseFacebookAuth
    private lateinit var callBackManager: CallbackManager

    private var email=""
    private var password=""
    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.hide()
        //configure progress dialog
        progressDiaLog = ProgressDialog(this)
        progressDiaLog.setTitle("Please Wait!!")
        progressDiaLog.setMessage("Logging In..")
        progressDiaLog.setCanceledOnTouchOutside(false)
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        //init firebase datavase
        database = FirebaseFirestore.getInstance()
        checkUser()
        //handle click login
        binding.loginBtn.setOnClickListener {
            validateData()
        }
        binding.loginGgBtn.setOnClickListener{
            firebaseLoginWithGg()
        }
        binding.loginFbBtn.setOnClickListener {
            binding.loginFbutton.performClick()
            firebaseLoginWithFb()
        }
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Configure Google sign in
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        // Initialize Facebook Login button
        callBackManager = CallbackManager.Factory.create()
    }

    //Login with Facebook

    private fun firebaseLoginWithFb(){
        val loginFbBtn : LoginButton = binding.loginFbutton
        loginFbBtn.setPermissions("email", "public_profile")
//        loginFbBtn.setReadPermissions("email", "public_profile")
        LoginManager.getInstance().registerCallback(callBackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                }
            })
    }

    private fun handleFacebookAccessToken(token : AccessToken?){
        val  credential = FacebookAuthProvider.getCredential(token?.token!!)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    // Sign in success,
                    Log.d(TAG, "signInWithCredential:success")
                    val currentUser = firebaseAuth.currentUser
                    val name = currentUser?.displayName
                    val email = currentUser?.email
                    val uid = currentUser?.uid
                    val img = currentUser?.photoUrl
                    val phone = currentUser?.phoneNumber
                    val userModel = User(uid,name,email,false,now, img.toString(),"",phone)
                    if (uid != null) {
                        database.collection("Users")
                            .document(uid)
                            .get().addOnSuccessListener { document ->
                                if (document == null) {
                                    database.collection("Users")
                                        .document(uid)
                                        .set(userModel)
                                }
                            }
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Login with Google
    private fun firebaseLoginWithGg() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager?.onActivityResult(requestCode,resultCode,data)
        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }catch (e: Exception){
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: logged in")
                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser?.uid
                val email = firebaseUser?.email
                val name = firebaseUser?.displayName
                val img = firebaseUser?.photoUrl
                val phone = firebaseUser?.phoneNumber
                Log.d(TAG, "firebaseAuthWithGoogleAccount: UID: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                if (authResult.additionalUserInfo!!.isNewUser){
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created")
                    val userModel = User(uid,name,email,false,now, img.toString(),"",phone)
                    if (uid != null) {
                        database.collection("Users")
                            .document(uid)
                            .set(userModel)
                    }
                    Toast.makeText(this,"Account created....\n $email",Toast.LENGTH_LONG).show()
                }
                else{
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing User")
                    Toast.makeText(this,"Existing User... \n $email",Toast.LENGTH_LONG).show()
                }

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener {e ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Login failed due to ${e.message}")
                Toast.makeText(this," Login failed due to ${e.message}",Toast.LENGTH_LONG).show()
            }
    }

    //Validate Data
    private fun validateData() {
        email = binding.emailET.editText?.text.toString().trim()
        password = binding.passwordET.editText?.text.toString().trim()
        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailET.error = "Invalid email format"
        }else if(TextUtils.isEmpty(password)){
            binding.passwordET.error = "Please enter the password"
        }else{
            firebaseLogin()
        }
    }

    //Login with email and pass
    private fun firebaseLogin() {
        progressDiaLog.show()
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                progressDiaLog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"LoggedIn as $email",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                progressDiaLog.dismiss()
                Toast.makeText(this,"Login Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    //Check Current user
    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser!= null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    //onclick Register Intent
    fun register(view: android.view.View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    //onclick Home button on actionbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            // app icon in action bar clicked; go home
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    //Onclick Forgot Password TV
    fun forgot_password(view: android.view.View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }


}