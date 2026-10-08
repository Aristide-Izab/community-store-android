package com.example.communitystore.vendor

import androidx.compose.foundation.BorderStroke
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
fun VendorIncomeScreen(
    modifier: Modifier = Modifier,
) {
    val primaryOrange = Color(0xFFFFA726)
    val successGreen = Color(0xFF10B981)
    val lightBorderColor = Color(0xFFE2E8F0)

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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(id = R.string.income_revenue),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterButton(
                                text = stringResource(id = R.string.this_week),
                                selected = true,
                                modifier = Modifier.weight(1f)
                            )
                            FilterButton(
                                text = stringResource(id = R.string.this_month),
                                selected = true,
                                modifier = Modifier.weight(1f)
                            )
                            FilterButton(
                                text = stringResource(id = R.string.this_year),
                                selected = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, lightBorderColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total_income),
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = stringResource(id = R.string.total_sales_value),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "↑",
                                        color = successGreen,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(id = R.string.percentage_18),
                                        color = successGreen,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, lightBorderColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total_orders),
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "128",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "↑",
                                                color = successGreen,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = stringResource(id = R.string.percentage_12),
                                                color = successGreen,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, lightBorderColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.pending_payout),
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = stringResource(id = R.string.amount_340),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.recent_income),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IncomeItemRow(
                                title = stringResource(id = R.string.sales_orders),
                                date = stringResource(id = R.string.date_29_aug_2026),
                                amount = stringResource(id = R.string.amount_1600)
                            )
                            HorizontalDivider(color = lightBorderColor, thickness = 1.dp)
                            IncomeItemRow(
                                title = stringResource(id = R.string.sales_orders),
                                date = stringResource(id = R.string.date_29_aug_2026),
                                amount = stringResource(id = R.string.amount_1600)
                            )
                            HorizontalDivider(color = lightBorderColor, thickness = 1.dp)
                            IncomeItemRow(
                                title = stringResource(id = R.string.refund),
                                date = stringResource(id = R.string.date_29_aug_2026),
                                amount = stringResource(id = R.string.amount_350)
                            )
                            HorizontalDivider(color = lightBorderColor, thickness = 1.dp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrivateBottomNavItem(
                                icon = R.drawable.ic_home,
                                label = stringResource(id = R.string.nav_home),
                                selected = false,
                                selectedColor = primaryOrange
                            )
                            PrivateBottomNavItem(
                                icon = R.drawable.ic_listing,
                                label = stringResource(id = R.string.nav_listing),
                                selected = false,
                                selectedColor = primaryOrange
                            )
                            PrivateBottomNavItem(
                                icon = R.drawable.ic_sales,
                                label = stringResource(id = R.string.nav_sales),
                                selected = true,
                                selectedColor = primaryOrange
                            )
                            PrivateBottomNavItem(
                                icon = R.drawable.ic_messages,
                                label = stringResource(id = R.string.nav_messages),
                                selected = false,
                                selectedColor = primaryOrange
                            )
                            PrivateBottomNavItem(
                                icon = R.drawable.ic_profile,
                                label = stringResource(id = R.string.nav_profile),
                                selected = false,
                                selectedColor = primaryOrange
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val primaryOrange = Color(0xFFFFA726)
    Button(
        onClick = {},
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) primaryOrange else Color(0xFFF1F5F9),
            contentColor = if (selected) Color.White else Color.DarkGray
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun IncomeItemRow(
    title: String,
    date: String,
    amount: String,
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = date,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun PrivateBottomNavItem(
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
fun VendorIncomeScreenPreview() {
    MaterialTheme {
        VendorIncomeScreen()
    }
}
