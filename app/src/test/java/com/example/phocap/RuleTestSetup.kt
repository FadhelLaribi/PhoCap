package com.example.phocap

import io.mockk.junit4.MockKRule
import org.junit.Rule

abstract class RuleTestSetup {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    val mockkRule by lazy { MockKRule(this) }
}