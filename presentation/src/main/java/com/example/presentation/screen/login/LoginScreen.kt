package com.example.presentation.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.presentation.R
import com.example.presentation.component.AppButton
import com.example.presentation.component.AppTextField
import com.example.presentation.component.AppTitle
import com.example.presentation.component.AuthSubtitle
import com.example.presentation.component.PasswordTextField
import com.example.presentation.component.ShapeAppTextField

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTitle(text = stringResource(R.string.sign_in_title))

            Spacer(modifier = Modifier.height(12.dp))

            AuthSubtitle(
                mainText = stringResource(R.string.dont_have_an_account),
                clickableText = stringResource(R.string.offer_to_register),
                onClick = onRegister
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTextField(
                    shape = ShapeAppTextField.TOP_ROUNDING,
                    value = state.email,
                    onValueChange = { viewModel.processCommand(LoginCommand.InputEmail(it)) },
                    placeholderText = stringResource(R.string.email),
                    leadingIcon = rememberVectorPainter(Icons.Default.Email)
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                PasswordTextField(
                    shape = ShapeAppTextField.BOTTOM_ROUNDING,
                    value = state.password,
                    onValueChange = { viewModel.processCommand(LoginCommand.InputPassword(it)) },
                    placeholderText = stringResource(R.string.password),
                    leadingIcon = rememberVectorPainter(Icons.Default.Password),
                    passwordVisibility = state.passwordVisibility,
                    onChangePasswordVisibility = { viewModel.processCommand(LoginCommand.ChangePasswordVisibility) },
                )
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = state.error!!,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = stringResource(R.string.sign_in_button),
                onClick = { viewModel.processCommand(LoginCommand.Submit) },
                enabled = state.isSubmitButtonEnabled
            )
        }
    }
}