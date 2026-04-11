package com.dr.booking_client.ui.screens.auth


import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dr.booking_client.viewmodel.AuthState
import com.dr.booking_client.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context   = LocalContext.current
    val activity  = context as Activity

    var phone  by remember { mutableStateOf("") }
    var otp    by remember { mutableStateOf("") }

    // Which step we're showing
    val showOtpInput = authState is AuthState.OtpSent
            || authState is AuthState.VerifyingOtp

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showOtpInput) "Enter OTP" else "Login with Phone") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showOtpInput) viewModel.resetState() else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor       = MaterialTheme.colorScheme.primary,
                    titleContentColor    = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showOtpInput) {
                // ── STEP 1: Enter phone number ──────────────────────────
                PhoneInputStep(
                    phone     = phone,
                    onChange  = { phone = it },
                    isLoading = authState is AuthState.SendingOtp,
                    error     = (authState as? AuthState.Error)?.message,
                    onSend    = { viewModel.sendOtp(phone, activity) }
                )
            } else {
                // ── STEP 2: Enter OTP ───────────────────────────────────
                OtpInputStep(
                    phone       = phone,
                    otp         = otp,
                    onChange    = { otp = it },
                    isLoading   = authState is AuthState.VerifyingOtp,
                    error       = (authState as? AuthState.Error)?.message,
                    onVerify    = { viewModel.verifyOtp(otp) },
                    onResend    = { viewModel.sendOtp(phone, activity) }
                )
            }
        }
    }
}

// ── Step 1 composable ────────────────────────────────────────────────────────

@Composable
private fun PhoneInputStep(
    phone: String,
    onChange: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSend: () -> Unit
) {
    Text("Welcome", style = MaterialTheme.typography.displayMedium)
    Text(
        "Enter your mobile number to receive an OTP",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(32.dp))

    OutlinedTextField(
        value         = phone,
        onValueChange = { if (it.length <= 10) onChange(it.filter { c -> c.isDigit() }) },
        label         = { Text("Mobile Number") },
        leadingIcon   = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("+91", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Divider(
                    modifier  = Modifier.height(20.dp).width(1.dp),
                    color     = MaterialTheme.colorScheme.outline
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(12.dp),
        singleLine      = true,
        isError         = error != null,
        supportingText  = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
    )

    Spacer(Modifier.height(24.dp))

    Button(
        onClick  = onSend,
        enabled  = phone.length == 10 && !isLoading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier.size(20.dp),
                color       = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text("Send OTP")
        }
    }
}

// ── Step 2 composable ────────────────────────────────────────────────────────

@Composable
private fun OtpInputStep(
    phone: String,
    otp: String,
    onChange: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Text("OTP Sent", style = MaterialTheme.typography.displayMedium)
    Text(
        "Enter the 6-digit code sent to +91 $phone",
        style     = MaterialTheme.typography.bodyMedium,
        color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(32.dp))

    // 6-box OTP display
    OtpBoxes(otp = otp)

    // Hidden field that captures typing
    OutlinedTextField(
        value         = otp,
        onValueChange = { if (it.length <= 6) onChange(it.filter { c -> c.isDigit() }) },
        modifier      = Modifier
            .size(1.dp)           // invisible but focusable
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine    = true
    )

    if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error, color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium)
    }

    Spacer(Modifier.height(24.dp))

    Button(
        onClick  = onVerify,
        enabled  = otp.length == 6 && !isLoading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier.size(20.dp),
                color       = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text("Verify OTP")
        }
    }

    Spacer(Modifier.height(12.dp))

    TextButton(onClick = onResend) {
        Text("Didn't receive it? Resend OTP")
    }
}

// ── OTP boxes UI ─────────────────────────────────────────────────────────────

@Composable
private fun OtpBoxes(otp: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val char = otp.getOrNull(index)
            val isCurrent = index == otp.length

            Surface(
                shape  = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                ),
                color    = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text  = char?.toString() ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Blinking cursor on active box
                    if (isCurrent && char == null) {
                        Text("|", style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}