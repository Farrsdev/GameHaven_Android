package com.example.gamehaven.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamehaven.entity.Transaction
import com.example.gamehaven.entity.TransactionDetail
import com.example.gamehaven.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticScreen(modif: Modifier = Modifier) {
    val transactionViewModel: TransactionViewModel = viewModel()
    val allTransactions by transactionViewModel.allTransactions.observeAsState(initial = emptyList())

    var selectedTimeRange by remember { mutableStateOf(TimeRange.ALL) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showTransactionDetails by remember { mutableStateOf(false) }

    // Calculate statistics
    val filteredTransactions = when (selectedTimeRange) {
        TimeRange.TODAY -> allTransactions.filter { isToday(it.date) }
        TimeRange.WEEK -> allTransactions.filter { isThisWeek(it.date) }
        TimeRange.MONTH -> allTransactions.filter { isThisMonth(it.date) }
        TimeRange.ALL -> allTransactions
    }

    val totalRevenue = filteredTransactions.sumOf { it.totalPrice }
    val totalTransactions = filteredTransactions.size
    val averageTransaction = if (totalTransactions > 0) totalRevenue / totalTransactions else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics & Transactions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Refresh data */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modif
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Time Range Filter
            TimeRangeFilter(
                selectedRange = selectedTimeRange,
                onRangeSelected = { selectedTimeRange = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Cards
            StatisticsSection(
                totalRevenue = totalRevenue,
                totalTransactions = totalTransactions,
                averageTransaction = averageTransaction
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Transactions List
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredTransactions.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTransactions, key = { it.id }) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onClick = {
                                selectedTransaction = transaction
                                showTransactionDetails = true
                            }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "No Transactions",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Transaction Details Dialog
    if (showTransactionDetails && selectedTransaction != null) {
        TransactionDetailsDialog(
            transaction = selectedTransaction!!,
            onDismiss = {
                showTransactionDetails = false
                selectedTransaction = null
            }
        )
    }
}

@Composable
private fun TimeRangeFilter(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Column {
        Text(
            text = "Time Range",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeRange.entries.forEach { range ->
                FilterChip(
                    selected = selectedRange == range,
                    onClick = { onRangeSelected(range) },
                    label = { Text(range.displayName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticsSection(
    totalRevenue: Double,
    totalTransactions: Int,
    averageTransaction: Double
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Revenue",
                value = currencyFormat.format(totalRevenue),
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )

            StatCard(
                title = "Transactions",
                value = totalTransactions.toString(),
                icon = Icons.Default.Receipt,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        StatCard(
            title = "Average Transaction",
            value = currencyFormat.format(averageTransaction),
            icon = Icons.Default.TrendingUp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Transaction #${transaction.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "User ID: ${transaction.userId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = currencyFormat.format(transaction.totalPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TransactionDetailsDialog(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val transactionViewModel: TransactionViewModel = viewModel()
    val transactionDetails by transactionViewModel.getDetails(transaction.id).observeAsState(initial = emptyList())

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Transaction Details")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Transaction #${transaction.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Date: ${dateFormat.format(transaction.date)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "User ID: ${transaction.userId}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Amount: ${currencyFormat.format(transaction.totalPrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Transaction Items
                Text(
                    text = "Items (${transactionDetails.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (transactionDetails.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        transactionDetails.forEach { detail ->
                            TransactionDetailItem(detail = detail)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading items...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun TransactionDetailItem(detail: TransactionDetail) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Game ID: ${detail.gameId}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Quantity: ${detail.qty}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = currencyFormat.format(detail.price * detail.qty),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Helper functions for date filtering
private fun isToday(date: Date): Boolean {
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)
}

private fun isThisWeek(date: Date): Boolean {
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    val week = today.get(Calendar.WEEK_OF_YEAR)
    return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            week == target.get(Calendar.WEEK_OF_YEAR)
}

private fun isThisMonth(date: Date): Boolean {
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == target.get(Calendar.MONTH)
}

enum class TimeRange(val displayName: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month"),
    ALL("All Time")
}
