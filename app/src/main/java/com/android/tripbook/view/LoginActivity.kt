package com.android.tripbook.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.data.LoginBody
import com.android.tripbook.data.ValidateEmailBody
import com.android.tripbook.repository.AuthRepository
import com.android.tripbook.utils.APIService
import com.android.tripbook.utils.VibrateView
import com.android.tripbook.view_model.LoginActivityViewModel
import com.android.tripbook.view_model.LoginActivityViewModelFactory
import com.example.tripbook.R
import com.example.tripbook.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener , View.OnFocusChangeListener , View.OnKeyListener {
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.loginWithGoogleTn.setOnClickListener(this)
        mBinding.loginBtn.setOnClickListener(this)
        mBinding.registerBtn.setOnClickListener(this)
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.passwordEt.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this,LoginActivityViewModelFactory(AuthRepository(APIService.getService()),application)).get(LoginActivityViewModel::class.java)

        setupObservers()
    }
    private fun setupObservers() {

        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }







        mViewModel.getErrorMessage().observe(this) {
//FULLNAME, email, password
            val formErrorKeys = arrayOf("fullName", "email", "password")
            val message = StringBuilder()

            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when (entry.key) {


                        "email" -> {
                            mBinding.emailTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }

                        "password" -> {
                            mBinding.passwordTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                    }


                } else {
                    message.append(entry.value).append("\n")
                }
                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                        .show()

                }
            }


        }
        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }


    }

    private fun validateEmail(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val value = mBinding.emailEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null

    }

    private fun validatePassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {

        var errorMessage: String? = null
        val value = mBinding.passwordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) {
            errorMessage = "Password must be  6 character long"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }


        return errorMessage == null


    }
    private fun validate(): Boolean {
        var isValid = true

        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false

        if (!isValid) VibrateView.vibrate(this, mBinding.cardView)
        return isValid

    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.loginBtn ->{
                    submitForm()
                }
                R.id.registerBtn -> {
                    startActivity(Intent(this,RegisterActivity::class.java))
                }
            }


        }

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {

            when (view.id) {


                R.id.emailEt -> {
                    if (hasFocus) {
                        if (mBinding.emailTil.isErrorEnabled) {
                            mBinding.emailTil.isErrorEnabled = false
                        }

                    } else {
                        (validateEmail())
                    }

                }

                R.id.passwordEt -> {
                    if (hasFocus) {
                        if (mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }

                    } else {
                         validatePassword()
                    }

                }


            }


        }


    }

    private  fun  submitForm(){
        if (validate()){
            // verify user credentials
     mViewModel.loginUser(LoginBody(mBinding.emailEt.text!!.toString(), mBinding.passwordEt.text!!.toString()))
        }

    }

    override fun onKey(view: View?, KeyCode: Int, keyEvent: KeyEvent?): Boolean {
   if (KeyCode == KeyEvent.KEYCODE_ENTER && keyEvent!!.action == KeyEvent.ACTION_UP){
submitForm()

   }

    return false
    }
}