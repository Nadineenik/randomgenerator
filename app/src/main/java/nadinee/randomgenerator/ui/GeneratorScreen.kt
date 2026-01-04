package nadinee.randomgenerator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState


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

    val enabledSpecial by snapshotFlow { Repository.enabledSpecial }
        .collectAsState(initial = Repository.enabledSpecial)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Генератор случайных чисел, рандомайзер",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
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

                            if (numbers.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Конфликт правил позиций — измените настройки")
                                }
                                return@Button
                            }

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
        }

        if (result.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "${result.size} числа в диапазоне от $from до $to, ${if (noDuplicates) "без" else "с"} повторами${if (sortOrder) ", отсортировано" else ""}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Сетка 5 колонок с маленькой цифрой как ярлычком в левом верхнем углу рамки
            items(result.chunked(4)) { rowNumbers ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    rowNumbers.forEachIndexed { _, number ->
                        val position = result.indexOf(number) + 1  // Порядковый номер в очереди

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            // Основной квадратик с номером человека
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp, start = 8.dp)  // отступ под ярлычок
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = number.toString(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            // Ярлычок с порядковым номером — вынесен в левый верхний угол
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = 4.dp, y = (-4).dp)  // чуть выходит за рамку
                                    .background(
                                        color = Color(0xFF0066FF),
                                        shape = RoundedCornerShape(bottomEnd = 8.dp, topStart = 8.dp)
                                    )
                                    .size(24.dp)
                            ) {
                                Text(
                                    text = position.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    // Заполнитель для неполных рядов
                    repeat(4 - rowNumbers.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}