package com.example.phocap.utils

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

sealed interface StringValue {

    data object Empty : StringValue

    class StringResource(@StringRes val resId: Int, vararg val args: Any?) :
        StringValue {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringResource) return false

            return resId == other.resId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    class PluralResource(
        @PluralsRes val resId: Int,
        val quantity: Int,
        vararg val args: Any?
    ) : StringValue {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PluralResource) return false

            return resId == other.resId && quantity == other.quantity && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + quantity
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    @Composable
    fun asString(): String = when (this) {
        is Empty -> ""
        is StringResource -> stringResource(
            resId,
            *args.mapNotNull { it.getValue() }.toTypedArray()
        )

        is PluralResource -> pluralStringResource(
            resId,
            quantity,
            *(args.mapNotNull { it.getValue() }.toTypedArray())
        )
    }

    @Composable
    private fun Any?.getValue() = if (this is StringValue) this.asString() else this

}
