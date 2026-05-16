package com.cookbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookbook.backend.AIChatBot
import com.cookbook.data.model.ParsedResponse
import com.cookbook.ui.theme.*
import com.cookbook.ui.viewmodel.CookbookViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isThinking: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: CookbookViewModel,
    onNavigateToMainMenu: () -> Unit,
    onNavigateToPantry: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf<ParsedResponse?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showBudgetWarning by remember { mutableStateOf(false) }
    var warningMessage by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(2) }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var showSideMenu by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val initialGreeting = "Hello! I'm Chef Dirk, your personal AI chef. " +
            "Ask me to suggest a recipe or tell me what ingredients you have!"

    suspend fun sendMessage(
        text: String,
        currentMessages: List<ChatMessage>,
        updateMessages: (List<ChatMessage>) -> Unit,
        setThinking: (Boolean) -> Unit,
        setAiResponse: (ParsedResponse?) -> Unit,
        scrollState: androidx.compose.foundation.lazy.LazyListState
    ) {
        val updated = currentMessages + ChatMessage(text, isUser = true)
        updateMessages(updated)

        setThinking(true)
        updateMessages(updated + ChatMessage("", isUser = false, isThinking = true))

        val result = withContext(Dispatchers.IO) {
            AIChatBot.askChefAI(text, viewModel.state.value.currentBudget)
        }

        setThinking(false)
        val finalMessages = if (result.hasRecipe) {
            updated + ChatMessage(
                "Here's what I found: **${result.recipeName}**\n\n" +
                        result.ingredients.joinToString("\n") + "\n\n" +
                        "Estimated cost: Php %.2f\nCalories: ${result.calories}\nProtein: ${result.protein}"
                    .format(result.totalEstimatedCost),
                isUser = false
            )
        } else {
            updated + ChatMessage(
                result.recipeName.ifBlank { "I couldn't find a recipe for that. Try something else!" },
                isUser = false
            )
        }
        updateMessages(finalMessages)

        if (result.hasRecipe) {
            setAiResponse(result)
        }

        kotlinx.coroutines.delay(100)
        scrollState.animateScrollToItem(finalMessages.size - 1)
    }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(ChatMessage(initialGreeting, isUser = false))
        }
        if (state.pendingPantryPrompt != null) {
            inputText = state.pendingPantryPrompt!!
            scope.launch {
                sendMessage(viewModel.state.value.pendingPantryPrompt!!,
                    messages, { messages = it }, { isThinking = it },
                    { aiResponse = it }, listState)
            }
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
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
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

            // Chat history
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
            }

            // Input bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type a message...", color = LightGray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank() && !isThinking) {
                                    val text = inputText.trim()
                                    inputText = ""
                                    focusManager.clearFocus()
                                    scope.launch {
                                        sendMessage(text, messages,
                                            { messages = it }, { isThinking = it },
                                            { aiResponse = it }, listState)
                                    }
                                }
                            }
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isThinking) {
                                val text = inputText.trim()
                                inputText = ""
                                focusManager.clearFocus()
                                scope.launch {
                                    sendMessage(text, messages,
                                        { messages = it }, { isThinking = it },
                                        { aiResponse = it }, listState)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(GreenPrimary),
                        enabled = inputText.isNotBlank() && !isThinking
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            BottomNavBar(selectedTab = selectedTab, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigateToMainMenu()
                    1 -> onNavigateToPantry()
                    2 -> { }
                }
            })
        }

        // Add to shopping list FAB
        if (aiResponse?.hasRecipe == true) {
            FloatingActionButton(
                onClick = {
                    val analysis = com.cookbook.backend.Chatbackend.analyzeRecipe(
                        aiResponse!!, state.currentBudget)
                    when (analysis.status) {
                        com.cookbook.data.model.BudgetStatus.NO_BUDGET -> {
                            warningMessage = "You haven't set a budget. Want to add to the shopping list anyway?"
                            showBudgetWarning = true
                        }
                        com.cookbook.data.model.BudgetStatus.INSUFFICIENT_FUNDS -> {
                            warningMessage = "This recipe costs Php %.2f but your budget is Php %.2f. Add anyway?"
                                .format(analysis.finalOutOfPocketCost, analysis.currentBudget)
                            showBudgetWarning = true
                        }
                        com.cookbook.data.model.BudgetStatus.OK -> {
                            showConfirmDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 140.dp),
                containerColor = GreenPrimary,
                contentColor = White
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Add to list")
            }
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

    // Confirm add to shopping list
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Add to Shopping List") },
            text = { Text("Do you want to add missing ingredients to the shopping list?") },
            confirmButton = {
                TextButton(onClick = {
                    aiResponse?.let { viewModel.saveRecipeToMenu(it) }
                    showConfirmDialog = false
                    aiResponse = null
                }) {
                    Text("Yes", color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Budget warning dialog
    if (showBudgetWarning) {
        AlertDialog(
            onDismissRequest = { showBudgetWarning = false },
            title = { Text("Budget Warning") },
            text = { Text(warningMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showBudgetWarning = false
                    showConfirmDialog = true
                }) {
                    Text("Add Anyway", color = WarningOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetWarning = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bgColor = if (message.isUser) ChatUserBubble else ChatAiBubble
    val textColor = if (message.isUser) White else DarkGray

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = bgColor
        ) {
            if (message.isThinking) {
                Row(modifier = Modifier.padding(12.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = GreenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("thinking...", color = MediumGray, fontSize = 13.sp)
                }
            } else {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}
