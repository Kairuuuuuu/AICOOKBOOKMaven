package com.cookbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookbook.data.model.PantryItem
import com.cookbook.ui.theme.*
import com.cookbook.ui.viewmodel.CookbookViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: CookbookViewModel,
    onBack: () -> Unit,
    onNavigateToMainMenu: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showSideMenu by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(1) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.refreshPantry()
    }

    val filteredItems = remember(state.pantryItems, searchQuery) {
        if (searchQuery.isBlank()) state.pantryItems
        else state.pantryItems.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamLight)
        ) {
            // Top bar
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GreenDark, GreenPrimary)
                        )
                    ),
                title = {
                    Text(
                        text = "Dirk's CookBook",
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showSideMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { showBudgetDialog = true }) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Budget", tint = White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search pantry...", color = LightGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MediumGray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = LightGray
                )
            )

            // Pantry grid
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Kitchen,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = LightGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Your pantry is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = MediumGray
                        )
                        Text(
                            "Tap + to add items",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightGray
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        PantryCard(item = item)
                    }
                }
            }

            BottomNavBar(selectedTab = selectedTab, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigateToMainMenu()
                    1 -> { }
                    2 -> onNavigateToChat()
                }
            })
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            containerColor = GreenPrimary,
            contentColor = White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add item", modifier = Modifier.size(28.dp))
        }

        // Side menu
        if (showSideMenu) {
            SideMenuOverlay(
                viewModel = viewModel,
                onDismiss = { showSideMenu = false },
                onChangePassword = { },
                onLogout = { }
            )
        }

        // Budget dialog
        if (showBudgetDialog) {
            AddBudgetDialog(viewModel = viewModel, onDismiss = { showBudgetDialog = false })
        }
    }

    // Add item dialog
    if (showAddDialog) {
        AddPantryItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, qty, expDate ->
                viewModel.addPantryItem(name, qty, expDate)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PantryCard(item: PantryItem) {
    val status = remember(item.expDate) { getExpiryStatus(item.expDate) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Egg,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                textAlign = TextAlign.Center
            )

            if (item.qty.isNotBlank()) {
                Text(
                    text = item.qty,
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                ExpiryStatus.FRESH -> SuccessGreen
                                ExpiryStatus.EXPIRING -> WarningOrange
                                ExpiryStatus.EXPIRED -> ErrorRed
                            }
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (status) {
                        ExpiryStatus.FRESH -> "Fresh"
                        ExpiryStatus.EXPIRING -> "Expiring"
                        ExpiryStatus.EXPIRED -> "Expired"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (status) {
                        ExpiryStatus.FRESH -> SuccessGreen
                        ExpiryStatus.EXPIRING -> WarningOrange
                        ExpiryStatus.EXPIRED -> ErrorRed
                    }
                )
            }
        }
    }
}

enum class ExpiryStatus { FRESH, EXPIRING, EXPIRED }

private fun getExpiryStatus(expDate: String): ExpiryStatus {
    if (expDate.isBlank()) return ExpiryStatus.FRESH
    return try {
        val date = LocalDate.parse(expDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        val daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), date)
        when {
            daysUntilExpiry < 0 -> ExpiryStatus.EXPIRED
            daysUntilExpiry <= 3 -> ExpiryStatus.EXPIRING
            else -> ExpiryStatus.FRESH
        }
    } catch (_: Exception) {
        ExpiryStatus.FRESH
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPantryItemDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, qty: String, expDate: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var expDate by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item to Pantry", textAlign = TextAlign.Center) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expDate,
                    onValueChange = {
                        expDate = it
                        dateError = false
                    },
                    label = { Text("Expiry Date (MM/DD/YYYY)") },
                    singleLine = true,
                    isError = dateError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (dateError) {
                    Text("Invalid date format. Use MM/DD/YYYY",
                        color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (expDate.isNotBlank()) {
                    try {
                        LocalDate.parse(expDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                    } catch (_: Exception) {
                        dateError = true
                        return@TextButton
                    }
                }
                onAdd(name, qty, expDate)
            }) {
                Text("Add to Pantry", color = GreenPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
