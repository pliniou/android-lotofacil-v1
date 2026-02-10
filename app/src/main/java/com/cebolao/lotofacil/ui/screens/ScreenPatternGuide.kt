package com.cebolao.lotofacil.ui.screens

/**
 * SCREEN PATTERN GUIDE - Cebolão Lotofácil
 * 
 * This is a reference pattern that all screens should follow for consistency,
 * testability, and performance optimization.
 * 
 * ==============================================================================
 * PATTERN STRUCTURE
 * ==============================================================================
 * 
 * Every screen is composed of TWO parts:
 * 
 * 1. STATEFUL SCREEN (Main Composable)
 *    - Collects state from ViewModel
 *    - Handles navigation
 *    - Manages side effects (events)
 *    - Delegates rendering to Content composable
 * 
 * 2. STATELESS CONTENT (Presentation Composable)
 *    - Pure UI rendering
 *    - No ViewModel dependency
 *    - Fully testable and previewable
 *    - Reusable in different contexts
 * 
 * ==============================================================================
 * TEMPLATE IMPLEMENTATION
 * ==============================================================================
 * 
 * FILE: MyFeatureScreen.kt
 * 
 * ```kotlin
 * package com.cebolao.lotofacil.ui.screens.myfeature
 * 
 * import androidx.compose.foundation.layout.*
 * import androidx.compose.material3.*
 * import androidx.compose.runtime.*
 * import androidx.compose.ui.Modifier
 * import androidx.compose.ui.res.stringResource
 * import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
 * import androidx.lifecycle.compose.collectAsStateWithLifecycle
 * import com.cebolao.lotofacil.R
 * import com.cebolao.lotofacil.ui.components.*
 * import com.cebolao.lotofacil.ui.theme.AppSpacing
 * import com.cebolao.lotofacil.viewmodels.MyFeatureViewModel
 * 
 * // ==================== STATEFUL SCREEN ====================
 * /**
 *  * Main screen entry point (STATEFUL)
 *  * - Collects ViewModel state
 *  * - Handles events and navigation
 *  * - Delegates UI to Content composable
 *  */
 * @Composable
 * fun MyFeatureScreen(
 *     modifier: Modifier = Modifier,
 *     viewModel: MyFeatureViewModel = hiltViewModel(),
 *     onNavigateBack: () -> Unit,
 *     onNavigateToX: () -> Unit
 * ) {
 *     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 *     
 *     // Handle events (navigation, snackbar, etc.)
 *     LaunchedEffect(Unit) {
 *         viewModel.events.collect { event ->
 *             when (event) {
 *                 is MyFeatureEvent.NavigateToX -> onNavigateToX()
 *                 is MyFeatureEvent.ShowMessage -> { /* show snackbar */ }
 *             }
 *         }
 *     }
 *     
 *     // Delegate all rendering to Content composable
 *     MyFeatureScreenContent(
 *         state = uiState,
 *         modifier = modifier,
 *         onAction = { viewModel.handleAction(it) }
 *     )
 * }
 * 
 * // ==================== STATELESS CONTENT ====================
 * /**
 *  * Screen content (STATELESS)
 *  * - No ViewModel dependency
 *  * - All state passed as parameters
 *  * - Fully testable and previewable
 *  */
 * @Composable
 * fun MyFeatureScreenContent(
 *     state: MyFeatureUiState,
 *     modifier: Modifier = Modifier,
 *     onAction: (MyFeatureAction) -> Unit
 * ) {
 *     Scaffold(
 *         modifier = modifier.fillMaxSize(),
 *         topBar = {
 *             StandardScreenHeader(
 *                 title = stringResource(id = R.string.my_feature_title),
 *                 onBackClick = { onAction(MyFeatureAction.BackClicked) }
 *             )
 *         }
 *     ) { innerPadding ->
 *         // Use StatusScreen to handle Loading/Error/Empty/Success states
 *         StatusScreen(
 *             status = state.status,
 *             loadingMessage = stringResource(id = R.string.loading_data),
 *             onRetry = { onAction(MyFeatureAction.Retry) },
 *             onEmpty = { MyFeatureEmptyState() }
 *         ) { data ->
 *             MyFeatureContent(
 *                 data = data,
 *                 modifier = Modifier
 *                     .fillMaxSize()
 *                     .padding(innerPadding),
 *                 onAction = onAction
 *             )
 *         }
 *     }
 * }
 * 
 * // ==================== HELPER COMPOSABLES ====================
 * @Composable
 * private fun MyFeatureContent(
 *     data: MyFeatureData,
 *     modifier: Modifier = Modifier,
 *     onAction: (MyFeatureAction) -> Unit
 * ) {
 *     LazyColumn(
 *         modifier = modifier,
 *         contentPadding = PaddingValues(AppSpacing.lg)
 *     ) {
 *         items(data.items) { item ->
 *             MyFeatureItem(item = item, onAction = onAction)
 *         }
 *     }
 * }
 * 
 * @Composable
 * private fun MyFeatureEmptyState() {
 *     EmptyState(
 *         title = stringResource(id = R.string.my_feature_empty_title),
 *         message = stringResource(id = R.string.my_feature_empty_message)
 *     )
 * }
 * 
 * // ==================== PREVIEW ====================
 * @Preview
 * @Composable
 * fun MyFeatureScreenPreview() {
 *     CebolaoTheme {
 *         MyFeatureScreenContent(
 *             state = MyFeatureUiState.Preview.success,
 *             onAction = {}
 *         )
 *     }
 * }
 * 
 * @Preview(showBackground = true)
 * @Composable
 * fun MyFeatureScreenLoadingPreview() {
 *     CebolaoTheme {
 *         MyFeatureScreenContent(
 *             state = MyFeatureUiState.Preview.loading,
 *             onAction = {}
 *         )
 *     }
 * }
 * ```
 * 
 * ==============================================================================
 * KEY PRINCIPLES
 * ==============================================================================
 * 
 * 1. SINGLE RESPONSIBILITY
 *    - Screen: State collection + navigation
 *    - Content: Pure UI rendering
 *    - Helpers: Specific UI domains
 * 
 * 2. TESTABILITY
 *    - Content is 100% testable (no ViewModel)
 *    - Can be previewed independently
 *    - All state is explicit
 * 
 * 3. REUSABILITY
 *    - Content can be reused in different contexts
 *    - No tight coupling to specific ViewModel
 *    - Flexible state passing
 * 
 * 4. PERFORMANCE
 *    - StatusScreen handles state efficiently
 *    - Only Content recomposes when state changes
 *    - Screen wrapper remains stable
 * 
 * 5. CONSISTENCY
 *    - All screens follow same pattern
 *    - Headers use StandardScreenHeader
 *    - Status management via StatusScreen
 *    - Actions defined in sealed Action classes
 * 
 * ==============================================================================
 * COMMON PATTERNS
 * ==============================================================================
 * 
 * HANDLING ACTIONS:
 * ```kotlin
 * sealed class MyFeatureAction {
 *     data class ItemClicked(val id: Int) : MyFeatureAction()
 *     object Retry : MyFeatureAction()
 *     object BackClicked : MyFeatureAction()
 * }
 * ```
 * 
 * STATE MANAGEMENT:
 * ```kotlin
 * data class MyFeatureUiState(
 *     val status: ScreenStatus<MyFeatureData> = ScreenStatus.Loading(),
 *     val scrollPosition: Int = 0
 * )
 * ```
 * 
 * VIEWMODEL EVENT HANDLING:
 * ```kotlin
 * sealed class MyFeatureEvent {
 *     data class NavigateTo(val destination: String) : MyFeatureEvent()
 *     data class ShowMessage(val messageId: Int) : MyFeatureEvent()
 * }
 * ```
 * 
 * ==============================================================================
 * ANTI-PATTERNS (DO NOT DO)
 * ==============================================================================
 * 
 * ❌ Mixing ViewModel logic in Content
 * ❌ Using remember for data that should come from state
 * ❌ Inline LaunchedEffect in Content composable
 * ❌ Custom state handling instead of StatusScreen
 * ❌ Different header styles across screens
 * ❌ Navigation logic in Content
 * 
 * ==============================================================================
 * MIGRATION CHECKLIST (From Old Pattern)
 * ==============================================================================
 * 
 * For existing screens, follow these steps:
 * 
 * 1. [ ] Create MyFeatureUiState data class with status field
 * 2. [ ] Create MyFeatureAction sealed class
 * 3. [ ] Extract Content composable from main Screen
 * 4. [ ] Move LaunchedEffect to Screen level
 * 5. [ ] Replace custom status handling with StatusScreen
 * 6. [ ] Add @Preview for Content
 * 7. [ ] Test: build + preview + run
 * 8. [ ] Remove old dead code\n * 9. [ ] Update imports in navigation layer\n */
