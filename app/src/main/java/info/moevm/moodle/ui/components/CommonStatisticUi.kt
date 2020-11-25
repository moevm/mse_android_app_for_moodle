package info.moevm.moodle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import java.text.DecimalFormat

/**
 * A row representing the basic information of an Account.
 */
@Composable
fun SuccessCoursesRow(name: String, number: Int, percent: Float, color: Color) {
    BaseRow(
        color = color,
        title = name,
        subtitle = stringResource(R.string.account_redacted) + " " + AccountDecimalFormat.format(number),
        amount = percent,
        negative = false
    )
}

@Composable
fun SuccessStudentsRow(name: String, mark: String, amount: Int, color: Color) {
    BaseRow(
        color = color,
        title = name,
        subtitle = stringResource(R.string.mark) + mark,
        amount = amount * 1f,
        negative = true
    )
}

@Suppress("unused")
@Composable
private fun BaseRow(
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    negative: Boolean
) {
    Row(
        modifier = Modifier.preferredHeight(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        AccountIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.preferredWidth(12.dp))
        Column(Modifier) {
            Providers(
                AmbientContentAlpha provides ContentAlpha.high,
                children = {
                    Text(text = title, style = typography.body1)
                }
            )
            Providers(
                AmbientContentAlpha provides ContentAlpha.high,
                children = {
                    Text(text = subtitle, style = typography.subtitle1)
                }
            )
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = " = ",
                // text = if (negative) "–$ " else "$ ",
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = formatAmount(
                    amount
                ),
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.preferredWidth(16.dp))

        Providers(
            AmbientContentAlpha provides ContentAlpha.high,
            children = {
                Icon(
                    asset = Icons.Filled.ChevronRight,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .preferredSize(24.dp)
                )
            }
        )
    }
    StatisticsDivider()
}

/**
 * A vertical colored line that is used in a [BaseRow] to differentiate accounts.
 */
@Composable
private fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(modifier.preferredSize(4.dp, 36.dp).background(color = color))
}

@Composable
fun StatisticsDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumByDouble { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}
