package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Employee
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.BizlyViewModel

@Composable
fun EmployeesScreen(viewModel: BizlyViewModel) {
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<Employee?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BizlyNavyDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Team & Permissions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${employees.size} Active Team Members",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("employee_add_btn")
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Role Permission Matrix Card (Requirement 15)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, null, tint = BizlyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Role-Based Access Control (RBAC)", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        RoleDescriptionRow("Owner", "Full access: System settings, billing, financial P&L, team management.")
                        RoleDescriptionRow("Manager", "Sales POS, stock adjustments, customer ledger, and analytics.")
                        RoleDescriptionRow("Salesperson", "Process sales checkout, view catalog stock, print/share receipts.")
                        RoleDescriptionRow("Accountant", "Manage expenses, audit debt ledger, review profit reports.")
                        RoleDescriptionRow("Staff", "Inventory counts, receiving supplier stock consignments.")
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text("Team Members", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = BizlyTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(employees) { emp ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    when (emp.role) {
                                        "Owner" -> BizlyNavy.copy(alpha = 0.15f)
                                        "Manager" -> BizlyPrimary.copy(alpha = 0.15f)
                                        "Accountant" -> BizlySuccess.copy(alpha = 0.15f)
                                        else -> Color(0xFFE2E8F0)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (emp.role) {
                                    "Owner" -> Icons.Default.Security
                                    "Manager" -> Icons.Default.Badge
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = when (emp.role) {
                                    "Owner" -> BizlyNavy
                                    "Manager" -> BizlyPrimary
                                    "Accountant" -> BizlySuccess
                                    else -> BizlyTextPrimary
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = emp.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = emp.role)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${emp.email} • ${emp.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = BizlyTextSecondary
                            )
                        }

                        if (emp.role != "Owner") {
                            IconButton(onClick = { employeeToEdit = emp }) {
                                Icon(Icons.Default.Edit, "Edit", tint = BizlyPrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.deleteEmployee(emp) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = BizlyDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Employee Dialog
    if (showAddDialog) {
        EmployeeFormDialog(
            title = "Add Team Member",
            initialEmployee = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, email, phone, role ->
                viewModel.addEmployee(name, email, phone, role)
                showAddDialog = false
            }
        )
    }

    // Edit Employee Dialog
    employeeToEdit?.let { emp ->
        EmployeeFormDialog(
            title = "Edit Employee: ${emp.name}",
            initialEmployee = emp,
            onDismiss = { employeeToEdit = null },
            onSave = { name, email, phone, role ->
                viewModel.updateEmployee(emp.copy(name = name, email = email, phone = phone, role = role))
                employeeToEdit = null
            }
        )
    }
}

@Composable
private fun RoleDescriptionRow(role: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• $role: ",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = BizlyPrimary
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.labelSmall,
            color = BizlyTextSecondary
        )
    }
}

@Composable
fun EmployeeFormDialog(
    title: String,
    initialEmployee: Employee?,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, phone: String, role: String) -> Unit
) {
    var name by remember { mutableStateOf(initialEmployee?.name ?: "") }
    var email by remember { mutableStateOf(initialEmployee?.email ?: "") }
    var phone by remember { mutableStateOf(initialEmployee?.phone ?: "+254 7") }
    var role by remember { mutableStateOf(initialEmployee?.role ?: "Salesperson") }

    val roles = listOf("Manager", "Salesperson", "Accountant", "Staff")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Work Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Assign Role", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    roles.forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, email, phone, role)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Employee")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
