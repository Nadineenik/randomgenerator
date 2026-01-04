package nadinee.randomgenerator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.utils.generateNumbers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var from by remember { mutableStateOf("1") }
    var to by remember { mutableStateOf("24") }
    var quantity by remember { mutableStateOf("24") }
    var noDuplicates by remember { mutableStateOf(true) }
    var sortOrder by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf(listOf<Int>()) }

    val enabledSpecial = Repository.enabledSpecial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Генератор случайных чисел, рандомайзер",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("В диапазоне", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("от", modifier = Modifier.width(40.dp))
                    OutlinedTextField(
                        value = from,
                        onValueChange = { from = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("до")
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = to,
                        onValueChange = { to = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Получить", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("числа")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = noDuplicates, onCheckedChange = { noDuplicates = it })
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Без повторов")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = sortOrder, onCheckedChange = { sortOrder = it })
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Сортировать по порядку")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val fromInt = from.toIntOrNull() ?: 1
                        val toInt = to.toIntOrNull() ?: 24
                        val qty = quantity.toIntOrNull() ?: 24

                        if (fromInt >= toInt || qty <= 0 || (noDuplicates && qty > (toInt - fromInt + 1))) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Проверьте параметры диапазона и количества")
                            }
                            return@Button
                        }

                        var numbers = generateNumbers(fromInt, toInt, qty, enabledSpecial)

                        if (sortOrder) {
                            numbers = numbers.sorted()
                        }

                        result = numbers
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF))
                ) {
                    Text("Сгенерировать", color = Color.White, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (result.isNotEmpty()) {
            Text(
                text = "${result.size} числа в диапазоне от $from до $to, ${if (noDuplicates) "без" else "с"} повторами${if (sortOrder) ", отсортировано" else ""}",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(result) { number ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .border(2.dp, Color(0xFFBBDEFB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}