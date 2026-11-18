package com.composecraft.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.model.UIElementNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerTreeDrawer(
    rootNode: UIElementNode,
    selectedNodeId: String?,
    onDismiss: () -> Unit,
    onSelectNode: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = "Layers", tint = Color(0xFF00F2FE))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Layer & Hierarchy Tree 🌲",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Text(
                text = "Structured view of all nested UI elements and containers",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                renderNodeItem(rootNode, depth = 0, selectedNodeId = selectedNodeId) { id ->
                    onSelectNode(id)
                    onDismiss()
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.renderNodeItem(
    node: UIElementNode,
    depth: Int,
    selectedNodeId: String?,
    onSelect: (String) -> Unit
) {
    item(key = node.id) {
        val isSelected = node.id == selectedNodeId
        val paddingStart = (depth * 20).dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = paddingStart)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31))
                .border(
                    width = if (isSelected) 1.5.dp else 0.5.dp,
                    color = if (isSelected) Color(0xFF00F2FE) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onSelect(node.id) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = resolvePaletteIcon(node.type),
                contentDescription = node.name,
                tint = if (isSelected) Color(0xFF00F2FE) else Color(0xFF818CF8),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.name,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                )
                Text(
                    text = node.type.displayName,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            if (node.type.isContainer) {
                Surface(
                    color = Color(0xFF334155),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${node.children.size} items",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }

    // Recursively render children
    node.children.forEach { child ->
        renderNodeItem(child, depth = depth + 1, selectedNodeId = selectedNodeId, onSelect = onSelect)
    }
}
