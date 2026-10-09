package com.example.communitystore.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communitystore.R

@Composable
fun VendorDashboardScreen(
    modifier: Modifier = Modifier,
) {
    val primaryOrange = Color(0xFFFFA726)
    val cardBackground = Color(0xFFFFEDD5)
    val deliveredGreen = Color(0xFF10B981)
    val processingOrange = Color(0xFFD97706)
    val processingBg = Color(0xFFFEF3C7)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(primaryOrange)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.good_morning_vendor),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.vendor_label),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_verified),
                            contentDescription = stringResource(id = R.string.verified_label),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.verified_label),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = stringResource(id = R.string.nav_profile),
                        tint = primaryOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_diamond),
                                        contentDescription = stringResource(id = R.string.loyalty_points),
                                        tint = primaryOrange,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.loyalty_points_value),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = stringResource(id = R.string.loyalty_points),
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = stringResource(id = R.string.loyalty_points_monthly),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = stringResource(id = R.string.view_rewards),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(id = R.string.total_sales),
                            value = stringResource(id = R.string.total_sales_value),
                            backgroundColor = cardBackground,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(id = R.string.orders_label),
                            value = stringResource(id = R.string.orders_value),
                            backgroundColor = cardBackground,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(id = R.string.products_label),
                            value = stringResource(id = R.string.products_value),
                            backgroundColor = cardBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.recent_orders),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = stringResource(id = R.string.more_label),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                    OrderRow(
                        title = stringResource(id = R.string.textbook_title),
                        price = stringResource(id = R.string.textbook_price),
                        statusText = stringResource(id = R.string.status_processing),
                        statusBg = processingBg,
                        statusColor = processingOrange
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                    OrderRow(
                        title = stringResource(id = R.string.textbook_title),
                        price = stringResource(id = R.string.textbook_price),
                        statusText = stringResource(id = R.string.status_delivered),
                        statusBg = deliveredGreen,
                        statusColor = Color.White
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                    OrderRow(
                        title = stringResource(id = R.string.textbook_title),
                        price = stringResource(id = R.string.textbook_price),
                        statusText = stringResource(id = R.string.status_delivered),
                        statusBg = deliveredGreen,
                        statusColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun OrderRow(
    title: String,
    price: String,
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(statusBg)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: Int,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
) {
    val tint = if (selected) selectedColor else Color.Gray
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VendorDashboardScreenPreview() {
    MaterialTheme {
        VendorDashboardScreen()
    }
}
