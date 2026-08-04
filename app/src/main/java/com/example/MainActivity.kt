package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ModuleTabRow
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.modules.ApkAuditorModule
import com.example.ui.modules.DevatorLabModule
import com.example.ui.modules.EvaluatorModule
import com.example.ui.modules.FreeAiModule
import com.example.ui.modules.GeminiAiModule
import com.example.ui.modules.ImageToolsModule
import com.example.ui.modules.KnowledgeModule
import com.example.ui.modules.MandelaCoreModule
import com.example.ui.modules.MatrixCoreModule
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BuilderViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BuilderAppScreen()
      }
    }
  }
}

@Composable
fun BuilderAppScreen(
    viewModel: BuilderViewModel = viewModel()
) {
  val selectedTabIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()

  // Matrix Core States
  val cpuUsage by viewModel.cpuUsage.collectAsStateWithLifecycle()
  val ramUsage by viewModel.ramUsage.collectAsStateWithLifecycle()

  // Devator Lab States
  val devInput by viewModel.devInput.collectAsStateWithLifecycle()
  val devOutput by viewModel.devOutput.collectAsStateWithLifecycle()
  val codeSnippets by viewModel.codeSnippets.collectAsStateWithLifecycle()

  // Evaluator States
  val expression by viewModel.expression.collectAsStateWithLifecycle()
  val evalResult by viewModel.evalResult.collectAsStateWithLifecycle()

  // MandelaCore States
  val quantumNodes by viewModel.quantumNodes.collectAsStateWithLifecycle()

  // Knowledge States
  val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

  // Settings & Keystore States
  val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
  val keystoreAlias by viewModel.keystoreAlias.collectAsStateWithLifecycle()
  val keystorePass by viewModel.keystorePass.collectAsStateWithLifecycle()
  val buildVariant by viewModel.buildVariant.collectAsStateWithLifecycle()

  // Gemini AI & App Builder States
  val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
  val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
  val userApiKey by viewModel.userApiKey.collectAsStateWithLifecycle()
  val builderPrompt by viewModel.builderPrompt.collectAsStateWithLifecycle()
  val isBuildingApp by viewModel.isBuildingApp.collectAsStateWithLifecycle()
  val buildLogs by viewModel.buildLogs.collectAsStateWithLifecycle()
  val generatedAppSpec by viewModel.generatedAppSpec.collectAsStateWithLifecycle()
  val apkArtifact by viewModel.apkArtifact.collectAsStateWithLifecycle()

  // Free AI States
  val freeAiInput by viewModel.freeAiInput.collectAsStateWithLifecycle()
  val freeAiResult by viewModel.freeAiResult.collectAsStateWithLifecycle()

  // Image Tools States
  val imgWidth by viewModel.imgWidth.collectAsStateWithLifecycle()
  val imgHeight by viewModel.imgHeight.collectAsStateWithLifecycle()
  val aspectRatioResult by viewModel.aspectRatioResult.collectAsStateWithLifecycle()

  if (showSettingsDialog) {
    SettingsDialog(
        userApiKey = userApiKey,
        keystoreAlias = keystoreAlias,
        keystorePass = keystorePass,
        buildVariant = buildVariant,
        onApiKeyChanged = { viewModel.updateUserApiKey(it) },
        onKeystoreAliasChanged = { viewModel.updateKeystoreAlias(it) },
        onKeystorePassChanged = { viewModel.updateKeystorePass(it) },
        onBuildVariantChanged = { viewModel.updateBuildVariant(it) },
        onDismiss = { viewModel.toggleSettingsDialog(false) }
    )
  }

  Scaffold(
      modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
      TopHeaderBar(
          onOpenSettings = { viewModel.toggleSettingsDialog(true) }
      )
      ModuleTabRow(
          selectedTabIndex = selectedTabIndex,
          onTabSelected = { viewModel.selectTab(it) }
      )

      when (selectedTabIndex) {
        0 -> MatrixCoreModule(
            cpuUsage = cpuUsage,
            ramUsage = ramUsage
        )
        1 -> DevatorLabModule(
            inputCode = devInput,
            outputCode = devOutput,
            savedSnippets = codeSnippets,
            onInputChanged = { viewModel.updateDevInput(it) },
            onFormatJson = { viewModel.formatJson() },
            onConvertCurl = { viewModel.convertCurlToKotlin() },
            onSaveSnippet = { title, lang -> viewModel.saveSnippet(title, lang) },
            onDeleteSnippet = { viewModel.deleteSnippet(it) }
        )
        2 -> EvaluatorModule(
            expression = expression,
            result = evalResult,
            onExpressionChanged = { viewModel.updateExpression(it) },
            onEvaluate = { viewModel.evaluateExpression() }
        )
        3 -> MandelaCoreModule(
            quantumNodes = quantumNodes,
            onCycleState = { viewModel.cycleMandelaState() }
        )
        4 -> KnowledgeModule(
            bookmarks = bookmarks,
            onAddBookmark = { title, cat, content -> viewModel.addBookmark(title, cat, content) },
            onDeleteBookmark = { viewModel.removeBookmark(it) }
        )
        5 -> GeminiAiModule(
            chatMessages = chatMessages,
            isLoading = aiLoading,
            apiKeySetting = userApiKey,
            builderPrompt = builderPrompt,
            isBuildingApp = isBuildingApp,
            buildLogs = buildLogs,
            generatedAppSpec = generatedAppSpec,
            apkArtifact = apkArtifact,
            onApiKeyChanged = { viewModel.updateUserApiKey(it) },
            onSendPrompt = { viewModel.sendGeminiPrompt(it) },
            onBuilderPromptChanged = { viewModel.updateBuilderPrompt(it) },
            onGenerateAndBuildApp = { viewModel.generateAndBuildFullApp(it) }
        )
        6 -> FreeAiModule(
            input = freeAiInput,
            result = freeAiResult,
            onInputChanged = { viewModel.updateFreeAiInput(it) },
            onSummarize = { viewModel.runFreeAiSummarize() },
            onToneAnalysis = { viewModel.runFreeAiToneAnalysis() }
        )
        7 -> ImageToolsModule(
            widthStr = imgWidth,
            heightStr = imgHeight,
            ratioResult = aspectRatioResult,
            onDimensionsChanged = { w, h -> viewModel.updateImgDimensions(w, h) }
        )
        8 -> ApkAuditorModule()
      }
    }
  }
}
