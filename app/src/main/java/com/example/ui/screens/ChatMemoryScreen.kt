package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.NavigationScreen
import com.example.ui.theme.BorderGlowPurple
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SaraBackground
import com.example.ui.theme.SaraCardBg
import com.example.ui.theme.SaraCardGlow
import com.example.ui.theme.SaraVoid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SaraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMemoryScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.MAIN)
    }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val memories by viewModel.memories.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Dialogue Logging, 1: Long-Term Memory
    var searchQuery by remember { mutableStateOf("") }
    var inputMessage by remember { mutableStateOf("") }
    var showAddMemoryDialog by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SaraVoid, SaraBackground, Color(0xFF100724), SaraVoid)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(NavigationScreen.MAIN) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SaraCardBg)
                        .border(1.dp, CardBorderSubtle, CircleShape)
                        .testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Conversation & Memory",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hindi & English Dialogue Logging with SARA",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }

            // Tabs: Dialogue Logging vs Long-Term Memory
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SaraCardBg,
                contentColor = NeonPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonPurple
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dialogue Logging", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = NeonPurple,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Long-Term Memory", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = NeonCyan,
                    unselectedContentColor = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            // Search Language / Keyword Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (selectedTabIndex == 0) "Search conversation / Hindi, English..." else "Search long-term memory logs...",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonPurple,
                        modifier = Modifier.size(17.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_language_field"),
                shape = RoundedCornerShape(13.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CardBorderSubtle,
                    focusedContainerColor = SaraCardBg,
                    unfocusedContainerColor = SaraCardBg,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TAB 0: DIALOGUE LOGGING
            if (selectedTabIndex == 0) {
                val filteredMessages = chatMessages.filter {
                    searchQuery.isBlank() || it.text.contains(searchQuery, ignoreCase = true)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dialogue History (${filteredMessages.size})",
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Clear History",
                        color = Color(0xFFEF4444),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { viewModel.clearChatHistory() }
                            .padding(4.dp)
                            .testTag("clear_chat_button")
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    items(filteredMessages, key = { it.id }) { message ->
                        DialogueMessageCard(
                            message = message,
                            timeText = timeFormat.format(Date(message.timestamp)),
                            onReplay = { viewModel.replayMessage(message.text) }
                        )
                    }
                }

                // Chat Input bar at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Ask SARA in Hindi / English...", fontSize = 11.5.sp, color = TextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedContainerColor = SaraCardBg,
                            unfocusedContainerColor = SaraCardBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.processUserPrompt(inputMessage)
                                inputMessage = ""
                            }
                        })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.processUserPrompt(inputMessage)
                                inputMessage = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            } else {
                // TAB 1: LONG-TERM MEMORY
                val filteredMemories = memories.filter {
                    searchQuery.isBlank() ||
                            it.key.contains(searchQuery, ignoreCase = true) ||
                            it.value.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stored Knowledge Points (${filteredMemories.size})",
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row {
                        Text(
                            text = "+ Add Memory",
                            color = NeonCyan,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showAddMemoryDialog = true }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("add_memory_button")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clear All",
                            color = Color(0xFFEF4444),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { viewModel.clearMemories() }
                                .padding(horizontal = 4.dp, vertical = 3.dp)
                                .testTag("clear_memories_button")
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    items(filteredMemories, key = { it.id }) { memory ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(13.dp))
                                .background(SaraCardBg)
                                .border(1.dp, CardBorderSubtle, RoundedCornerShape(13.dp))
                                .padding(13.dp)
                                .testTag("memory_card_${memory.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(NeonPurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = memory.category,
                                            color = NeonCyan,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = memory.key,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = memory.value,
                                        color = TextSecondary,
                                        fontSize = 11.5.sp,
                                        lineHeight = 15.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeMemory(memory.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete memory",
                                        tint = TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Memory Dialog
        if (showAddMemoryDialog) {
            var newKey by remember { mutableStateOf("") }
            var newValue by remember { mutableStateOf("") }
            var newCategory by remember { mutableStateOf("Preferences") }

            AlertDialog(
                onDismissRequest = { showAddMemoryDialog = false },
                containerColor = SaraCardGlow,
                title = {
                    Text("Add SARA Long-Term Memory", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newKey,
                            onValueChange = { newKey = it },
                            label = { Text("Memory Topic / Key", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            label = { Text("Details / Fact", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = newCategory,
                            onValueChange = { newCategory = it },
                            label = { Text("Category", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newKey.isNotBlank() && newValue.isNotBlank()) {
                                viewModel.addMemory(newKey, newValue, newCategory)
                                showAddMemoryDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("Save Memory", fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddMemoryDialog = false }) {
                        Text("Cancel", color = TextMuted, fontSize = 12.sp)
                    }
                }
            )
        }
    }
}

@Composable
fun DialogueMessageCard(
    message: ChatMessage,
    timeText: String,
    onReplay: () -> Unit
) {
    val isSara = message.sender == MessageSender.SARA

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSara) SaraCardGlow else SaraCardBg)
            .border(
                width = 1.dp,
                color = if (isSara) BorderGlowPurple else CardBorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(13.dp)
            .testTag("dialogue_card_${message.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.5.dp)
                            .clip(CircleShape)
                            .background(if (isSara) NeonCyan else NeonPurple)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSara) "SARA AI ASSISTANT" else "YOU (USER)",
                        color = if (isSara) NeonCyan else Color(0xFFC084FC),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeText,
                        color = TextMuted,
                        fontSize = 9.5.sp
                    )
                    if (isSara) {
                        Spacer(modifier = Modifier.width(7.dp))
                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Replay audio",
                                tint = NeonPurple,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = message.text,
                color = Color.White,
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Status: ${message.status}",
                    color = TextMuted,
                    fontSize = 8.5.sp
                )
            }
        }
    }
}
