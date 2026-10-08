package com.example.communitystore.vendor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communitystore.R

@Composable
fun AccountVerifiedDialogCard(
    modifier: Modifier = Modifier,
    onStartExploringClick: () -> Unit = {},
) {
    val primaryOrange = Color(0xFFFFA726)
    val darkTitleColor = Color(0xFF0F172A)
    val subtitleColor = Color(0xFF64748B)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(primaryOrange),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(width = 2.5.dp, color = Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.18f, size.height * 0.52f)
                            lineTo(size.width * 0.42f, size.height * 0.76f)
                            lineTo(size.width * 0.82f, size.height * 0.24f)
                        }
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.account_verified),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = darkTitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.account_verified_message),
                fontSize = 14.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartExploringClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
            ) {
                Text(
                    text = stringResource(id = R.string.start_exploring),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun VendorVerificationScreen(
    modifier: Modifier = Modifier,
    showVerifiedDialog: Boolean = true,
    onBackClick: () -> Unit = {},
    onStartExploringClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
) {
    val primaryOrange = Color(0xFFFFA726)
    val buttonOrange = Color(0xFFD47A18)
    val beigeCardBg = Color(0xFFD4C5B3)
    val beigeBorder = Color(0xFF8E7D6D)
    val darkText = Color(0xFF1E1E1E)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E2E2)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = darkText,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_verified),
                contentDescription = null,
                tint = primaryOrange,
                modifier = Modifier.size(110.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.vendor_verification),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(beigeCardBg)
                    .border(width = 1.dp, color = beigeBorder, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "📎",
                        fontSize = 28.sp,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(id = R.string.tap_to_upload_pdf),
                        fontSize = 14.sp,
                        color = darkText,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(beigeCardBg)
                    .border(width = 1.dp, color = beigeBorder, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 14.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.review_time_info),
                    fontSize = 13.sp,
                    color = Color(0xFF4A4A4A),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonOrange),
            ) {
                Text(
                    text = stringResource(id = R.string.submit_for_review),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        if (showVerifiedDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                AccountVerifiedDialogCard(
                    onStartExploringClick = onStartExploringClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VendorVerificationScreenPreview() {
    MaterialTheme {
        VendorVerificationScreen()
    }
}
