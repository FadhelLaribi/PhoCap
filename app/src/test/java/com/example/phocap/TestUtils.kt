package com.example.phocap

import io.mockk.MockKVerificationScope
import io.mockk.coVerify

fun coVerifyNever(block: suspend MockKVerificationScope.() -> Unit) =
    coVerify(exactly = 0, verifyBlock = block)

fun coVerifyCalled(block: suspend MockKVerificationScope.() -> Unit) =
    coVerify(exactly = 1, verifyBlock = block)
