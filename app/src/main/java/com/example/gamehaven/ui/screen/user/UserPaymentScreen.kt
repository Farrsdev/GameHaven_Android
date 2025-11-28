package com.example.gamehaven.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamehaven.entity.Transaction
import com.example.gamehaven.entity.TransactionDetail
import com.example.gamehaven.other.rememberCurrentUser
import com.example.gamehaven.viewmodel.GameViewModel
import com.example.gamehaven.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    gameId: Int,
    navController: NavHostController
) {
    val gameViewModel: GameViewModel = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()
    val allGames by gameViewModel.allGames.observeAsState(initial = emptyList())

    val currentUserManager = rememberCurrentUser()
    val currentUserId = currentUserManager.getUserId()

    val game = remember(gameId, allGames) {
        allGames.find { it.id == gameId }
    }

    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Payment",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (game == null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Game not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                PaymentContent(
                    game = game,
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    expiryDate = expiryDate,
                    onExpiryDateChange = { expiryDate = it },
                    cvv = cvv,
                    onCvvChange = { cvv = it },
                    cardHolder = cardHolder,
                    onCardHolderChange = { cardHolder = it },
                    onPayClick = {
                        // Process payment
                        val transaction = Transaction(
                            id = 0,
                            userId = currentUserId,
                            totalPrice = game.price,
                            date = Date()
                        )

                        val detail = TransactionDetail(
                            id = 0,
                            transactionId = 0,
                            gameId = game.id,
                            price = game.price,
                            qty = 1
                        )

                        transactionViewModel.insertTransaction(transaction, listOf(detail))
                        showSuccessDialog = true
                    }
                )
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Payment Successful!") },
            text = {
                Text("Your purchase of ${game?.title} has been completed successfully. You can now download the game from your my game section.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack("home", false)
                    }
                ) {
                    Text("Go to Home")
                }
            }
        )
    }
}

@Composable
private fun PaymentContent(
    game: com.example.gamehaven.entity.Game,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit,
    cardHolder: String,
    onCardHolderChange: (String) -> Unit,
    onPayClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "US")) }
    val isFormValid = cardNumber.isNotBlank() &&
            expiryDate.isNotBlank() &&
            cvv.isNotBlank() &&
            cardHolder.isNotBlank()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Order Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Order Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(game.title, style = MaterialTheme.typography.bodyMedium)
                    Text(currencyFormat.format(game.price), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(currencyFormat.format(game.price), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Payment Method
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = "Payment")
                    Text(
                        "Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Number
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        if (it.length <= 19 && it.matches(Regex("^[0-9 ]*$"))) {
                            val cleaned = it.replace(" ", "")
                            val formatted = cleaned.chunked(4).joinToString(" ")
                            onCardNumberChange(formatted)
                        }
                    },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("1234 5678 9012 3456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Default.CreditCard, contentDescription = "Card")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Expiry Date
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = {
                            if (it.length <= 5 && it.matches(Regex("^[0-9/]*$"))) {
                                val cleaned = it.replace("/", "")
                                val formatted = if (cleaned.length > 2) {
                                    "${cleaned.substring(0, 2)}/${cleaned.substring(2)}"
                                } else {
                                    cleaned
                                }
                                onExpiryDateChange(formatted)
                            }
                        },
                        label = { Text("Expiry Date") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("MM/YY") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // CVV
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = {
                            if (it.length <= 3 && it.matches(Regex("^[0-9]*$"))) {
                                onCvvChange(it)
                            }
                        },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card Holder
                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = onCardHolderChange,
                    label = { Text("Card Holder Name") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("John Doe") }
                )
            }
        }

        // Security Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = "Security",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Your payment information is secure and encrypted",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Pay Button
        Button(
            onClick = onPayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isFormValid && game.stock > 0,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Payment, contentDescription = "Pay")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pay ${currencyFormat.format(game.price)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}