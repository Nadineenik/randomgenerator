package nadinee.randomgenerator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.model.Rule
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var enabledSpecial by remember { mutableStateOf(Repository.enabledSpecial) }

    // Безопасный collect с lifecycle
    val rules by snapshotFlow { Repository.rules }
        .collectAsStateWithLifecycle(initialValue = Repository.rules)

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
                        itemsIndexed(rules) { index, rule ->
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
                                            Repository.deleteRule(index, context)
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

    // Диалог добавления правила
    if (showAddDialog) {
        var numberText by remember { mutableStateOf("") }
        var minPosText by remember { mutableStateOf("") }
        var maxPosText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить правило") },
            text = {
                Column {
                    OutlinedTextField(
                        value = numberText,
                        onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) numberText = it },
                        label = { Text("Число (например, 13)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = minPosText,
                        onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) minPosText = it },
                        label = { Text("От позиции") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = maxPosText,
                        onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) maxPosText = it },
                        label = { Text("До позиции") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                        if (number == null || numberText.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Введите число") }
                            return@TextButton
                        }
                        if (minPos == null || minPosText.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Введите 'От позиции'") }
                            return@TextButton
                        }
                        if (maxPos == null || maxPosText.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Введите 'До позиции'") }
                            return@TextButton
                        }
                        if (minPos > maxPos) {
                            scope.launch { snackbarHostState.showSnackbar("От позиции должно быть ≤ До позиции") }
                            return@TextButton
                        }

                        Repository.addRule(Rule(number, minPos, maxPos), context)
                        showAddDialog = false
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