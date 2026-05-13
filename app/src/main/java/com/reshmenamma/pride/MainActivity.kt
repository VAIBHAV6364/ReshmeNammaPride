package com.reshmenamma.pride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.reshmenamma.pride.data.*
import com.reshmenamma.pride.logic.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "sericulture-db")
            .fallbackToDestructiveMigration()
            .build()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF064E3B),
                    onPrimary = Color.White,
                    secondary = Color(0xFF059669),
                    tertiary = Color(0xFFD97706),
                    background = Color(0xFFF1F5F9)
                )
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainApp(db.dao(), windowSizeClass)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(dao: SericultureDao, windowSize: WindowSizeClass) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val activeBatches by dao.getActiveBatches().collectAsState(initial = emptyList())
    
    var selectedBatchId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("Reshme-Namma Pride", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; selectedBatchId = null },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Batches") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AddCircle, null) },
                    label = { Text("New") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("Info") }
                )
            }
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding).fillMaxSize()
        
        Crossfade(targetState = selectedTab, modifier = contentModifier) { tab ->
            when (tab) {
                0 -> {
                    if (selectedBatchId == null) {
                        BatchListScreen(activeBatches, dao, windowSize) { selectedBatchId = it }
                    } else {
                        val batch = activeBatches.find { it.id == selectedBatchId }
                        if (batch != null) {
                            BatchDetailScreen(batch, dao, onBack = { selectedBatchId = null })
                        } else {
                            selectedBatchId = null
                        }
                    }
                }
                1 -> NewBatchScreen(onCreated = { id -> 
                    selectedTab = 0
                    selectedBatchId = id
                }, dao)
                2 -> InformationScreen()
            }
        }
    }
}

@Composable
fun BatchListScreen(batches: List<BatchEntity>, dao: SericultureDao, windowSize: WindowSizeClass, onSelect: (String) -> Unit) {
    if (batches.isEmpty()) {
        EmptyStateView()
    } else {
        val columns = when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 1
            WindowWidthSizeClass.Medium -> 2
            else -> 3
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(columns) }) {
                Text("Your Active Batches", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.DarkGray)
            }
            items(batches) { batch ->
                BatchCard(batch, dao, onSelect)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchCard(batch: BatchEntity, dao: SericultureDao, onSelect: (String) -> Unit) {
    val logs by dao.getLogsForBatch(batch.id).collectAsState(initial = emptyList())
    val currentStage = InstarEngine.calculateCurrentStage(batch.startStage, batch.startDate)
    val latestLog = logs.firstOrNull()
    
    val statusColor = latestLog?.let { InstarEngine.getStatusColor(it.temperature, it.humidity, currentStage) } ?: Color.Gray

    Card(
        onClick = { onSelect(batch.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).background(statusColor, CircleShape))
                Spacer(Modifier.width(12.dp))
                // Display custom Batch Name as the primary title
                Text(batch.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(currentStage.displayName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            // Display Variety as a secondary detail
            Text(batch.variety, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            
            Divider(Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Default.Thermostat, "${latestLog?.temperature?.toInt() ?: "--"}°C", "Temp")
                InfoItem(Icons.Default.WaterDrop, "${latestLog?.humidity?.toInt() ?: "--"}%", "Humid")
                InfoItem(Icons.Default.Event, "Day ${TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - batch.startDate) + 1}", "Age")
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun EmptyStateView() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.BugReport, null, Modifier.size(100.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text("No active batches found", fontWeight = FontWeight.Bold, color = Color.Gray)
        Text("Start a new batch to begin monitoring", color = Color.LightGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(batch: BatchEntity, dao: SericultureDao, onBack: () -> Unit) {
    val logs by dao.getLogsForBatch(batch.id).collectAsState(initial = emptyList())
    val currentStage = InstarEngine.calculateCurrentStage(batch.startStage, batch.startDate)
    val scope = rememberCoroutineScope()
    
    var tempInput by remember { mutableFloatStateOf(25f) }
    var humInput by remember { mutableFloatStateOf(75f) }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(20.dp)) {
                Column {
                    IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    // Display Batch Name prominently in header
                    Text(batch.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("${batch.variety} • Started ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(batch.startDate))}", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        item {
            Column(Modifier.padding(16.dp)) {
                Text("Environment Status", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
                
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusCard("Temp", "${logs.firstOrNull()?.temperature?.toInt() ?: "--"}°C", Modifier.weight(1f), Icons.Default.Thermostat)
                    StatusCard("Humidity", "${logs.firstOrNull()?.humidity?.toInt() ?: "--"}%", Modifier.weight(1f), Icons.Default.WaterDrop)
                }
            }
        }

        item {
            val latestLog = logs.firstOrNull()
            if (latestLog != null) {
                val advice = InstarEngine.getDetailedAdvice(latestLog.temperature, latestLog.humidity, currentStage)
                val statusColor = InstarEngine.getStatusColor(latestLog.temperature, latestLog.humidity, currentStage)
                
                Card(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(statusColor))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (advice.isEmpty()) Icons.Default.CheckCircle else Icons.Default.Warning, null, tint = statusColor)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (advice.isEmpty()) "Conditions are Perfect" else "Action Recommended",
                                fontWeight = FontWeight.Black,
                                color = statusColor
                            )
                        }
                        
                        if (advice.isEmpty()) {
                            Text("Current conditions match the ideal requirements for ${currentStage.displayName}.", modifier = Modifier.padding(top = 8.dp), fontSize = 14.sp)
                        } else {
                            advice.forEach { item ->
                                Column(Modifier.padding(top = 12.dp)) {
                                    Text(item.issue, fontWeight = FontWeight.Bold, color = statusColor, fontSize = 14.sp)
                                    Text(item.action, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(Modifier.padding(16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Update Readings", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Text("Temperature: ${tempInput.toInt()}°C", fontSize = 14.sp)
                    Slider(value = tempInput, onValueChange = { tempInput = it }, valueRange = 15f..40f, colors = SliderDefaults.colors(thumbColor = Color(0xFFEF4444), activeTrackColor = Color(0xFFEF4444)))
                    
                    Text("Humidity: ${humInput.toInt()}%", fontSize = 14.sp)
                    Slider(value = humInput, onValueChange = { humInput = it }, valueRange = 30f..100f, colors = SliderDefaults.colors(thumbColor = Color(0xFF3B82F6), activeTrackColor = Color(0xFF3B82F6)))
                    
                    Button(
                        onClick = {
                            scope.launch {
                                dao.insertLog(ClimateLogEntity(batchId = batch.id, timestamp = System.currentTimeMillis(), temperature = tempInput, humidity = humInput))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Log")
                    }
                }
            }
        }
        
        item {
            Text("History (Last 10)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
        }

        items(logs.take(10)) { log ->
            ListItem(
                headlineContent = { Text("${log.temperature.toInt()}°C / ${log.humidity.toInt()}%") },
                supportingContent = { Text(SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))) },
                leadingContent = { Icon(Icons.Default.History, null, tint = Color.LightGray) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            TextButton(
                onClick = { scope.launch { dao.deleteBatch(batch.id); onBack() } },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.Default.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Delete Batch")
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, modifier: Modifier, icon: ImageVector) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBatchScreen(onCreated: (String) -> Unit, dao: SericultureDao) {
    var batchName by remember { mutableStateOf("") }
    var selectedVariety by remember { mutableStateOf(InstarEngine.Varieties[0]) }
    var selectedStage by remember { mutableIntStateOf(1) }
    var varietyExpanded by remember { mutableStateOf(false) }
    var stageExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(24.dp).fillMaxSize()) {
        Text("Start New Batch", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Text("Configure your rearing cycle", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

        OutlinedTextField(
            value = batchName,
            onValueChange = { batchName = it },
            label = { Text("Batch Name (e.g., Lot A, North Farm)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = varietyExpanded, onExpandedChange = { varietyExpanded = !varietyExpanded }) {
            OutlinedTextField(
                value = selectedVariety,
                onValueChange = {},
                readOnly = true,
                label = { Text("Silkworm Variety") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = varietyExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = varietyExpanded, onDismissRequest = { varietyExpanded = false }) {
                InstarEngine.Varieties.forEach { variety ->
                    DropdownMenuItem(text = { Text(variety) }, onClick = {
                        selectedVariety = variety
                        varietyExpanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = stageExpanded, onExpandedChange = { stageExpanded = !stageExpanded }) {
            OutlinedTextField(
                value = InstarStage.fromId(selectedStage).displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Current Instar Stage") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = stageExpanded, onDismissRequest = { stageExpanded = false }) {
                InstarStage.entries.forEach { stage ->
                    DropdownMenuItem(text = { Text(stage.displayName) }, onClick = {
                        selectedStage = stage.id
                        stageExpanded = false
                    })
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (batchName.isNotBlank()) {
                    val id = UUID.randomUUID().toString()
                    scope.launch {
                        dao.insertBatch(BatchEntity(id, batchName, selectedVariety, selectedStage, System.currentTimeMillis()))
                        onCreated(id)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = batchName.isNotBlank()
        ) {
            Text("Create Rearing Batch", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun InformationScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Knowledge Base", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Text("Ideal conditions for each instar stage", color = Color.Gray)
        }
        
        items(InstarStage.entries) { stage ->
            val req = InstarEngine.Requirements[stage]!!
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text(stage.displayName, fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Text(req.description, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RequirementBadge(Icons.Default.Thermostat, "${req.minTemp}-${req.maxTemp}°C")
                        RequirementBadge(Icons.Default.WaterDrop, "${req.minHum}-${req.maxHum}%")
                    }
                    
                    Divider(Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    
                    Text("Do's", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 14.sp)
                    req.dos.forEach { Text("• $it", fontSize = 12.sp) }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Don'ts", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), fontSize = 14.sp)
                    req.donts.forEach { Text("• $it", fontSize = 12.sp) }
                }
            }
        }
    }
}

@Composable
fun RequirementBadge(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Icon(icon, null, Modifier.size(16.dp), tint = Color.Gray)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
