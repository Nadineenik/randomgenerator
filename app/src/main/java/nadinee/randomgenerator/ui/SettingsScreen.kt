package nadinee.randomgenerator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.model.Rule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Автоматическое обновление состояния галочки и списка правил
    var enabledSpecial by remember { mutableStateOf(Repository.enabledSpecial) }
    val rules by snapshotFlow { Repository.rules }
        .collectAsState(initial = Repository.rules)

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Настройки",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Галочка "Включить особые условия"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = enabledSpecial,
                    onCheckedChange = { checked ->
                        enabledSpecial = checked
                        Repository.setEnabled(checked, context)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Включить особые условия",
                    fontSize = 18.sp
                )
            }

            if (enabledSpecial) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Правила фиксации позиций",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (rules.isEmpty()) {
                    Text(
                        text = "Нет добавленных правил",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                } else {
                    LazyColumn {
                        items(rules) { rule ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Число: ${rule.number}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Позиция: ${rule.minPos} — ${rule.maxPos}",
                                            fontSize = 16.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            Repository.deleteRule(rules.indexOf(rule), context)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Удалить правило",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("+ Добавить правило", fontSize = 18.sp)
                }
            }
        }
    }

    // === Диалог добавления правила ===
    if (showAddDialog) {
        var numberText by remember { mutableStateOf("") }
        var minPosText by remember { mutableStateOf("") }
        var maxPosText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("Новое правило")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = numberText,
                        onValueChange = { numberText = it },
                        label = { Text("Число (например, 13)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = minPosText,
                        onValueChange = { minPosText = it },
                        label = { Text("От позиции") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = maxPosText,
                        onValueChange = { maxPosText = it },
                        label = { Text("До позиции") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val number = numberText.toIntOrNull()
                        val minPos = minPosText.toIntOrNull()
                        val maxPos = maxPosText.toIntOrNull()

                        when {
                            number == null -> {
                                scope.launch { snackbarHostState.showSnackbar("Введите корректное число") }
                            }
                            minPos == null || maxPos == null -> {
                                scope.launch { snackbarHostState.showSnackbar("Введите корректные позиции") }
                            }
                            minPos > maxPos -> {
                                scope.launch { snackbarHostState.showSnackbar("От позиции должно быть ≤ До позиции") }
                            }
                            else -> {
                                val newRule = Rule(number, minPos, maxPos)
                                Repository.addRule(newRule, context)
                                showAddDialog = false
                                numberText = ""
                                minPosText = ""
                                maxPosText = ""
                            }
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}