/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.subject.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItemsWithLifecycle
import kotlinx.coroutines.launch
import me.him188.ani.app.navigation.LocalNavigator
import me.him188.ani.app.ui.foundation.ImageViewer
import me.him188.ani.app.ui.foundation.LocalPlatform
import me.him188.ani.app.ui.foundation.ifThen
import me.him188.ani.app.ui.foundation.interaction.WindowDragArea
import me.him188.ani.app.ui.foundation.interaction.nestedScrollWorkaround
import me.him188.ani.app.ui.foundation.layout.ConnectedScrollState
import me.him188.ani.app.ui.foundation.layout.PaddingValuesSides
import me.him188.ani.app.ui.foundation.layout.connectedScrollContainer
import me.him188.ani.app.ui.foundation.layout.connectedScrollTarget
import me.him188.ani.app.ui.foundation.layout.currentWindowAdaptiveInfo1
import me.him188.ani.app.ui.foundation.layout.only
import me.him188.ani.app.ui.foundation.layout.paneVerticalPadding
import me.him188.ani.app.ui.foundation.layout.rememberConnectedScrollState
import me.him188.ani.app.ui.foundation.navigation.BackHandler
import me.him188.ani.app.ui.foundation.pagerTabIndicatorOffset
import me.him188.ani.app.ui.foundation.rememberImageViewerHandler
import me.him188.ani.app.ui.foundation.theme.AniThemeDefaults
import me.him188.ani.app.ui.foundation.widgets.LocalToaster
import me.him188.ani.app.ui.foundation.widgets.TopAppBarGoBackButton
import me.him188.ani.app.ui.richtext.RichTextDefaults
import me.him188.ani.app.ui.subject.collection.components.EditableSubjectCollectionTypeButton
import me.him188.ani.app.ui.subject.details.components.CollectionData
import me.him188.ani.app.ui.subject.details.components.DetailsTab
import me.him188.ani.app.ui.subject.details.components.SelectEpisodeButtons
import me.him188.ani.app.ui.subject.details.components.SubjectBlurredBackground
import me.him188.ani.app.ui.subject.details.components.SubjectCommentColumn
import me.him188.ani.app.ui.subject.details.components.SubjectDetailsDefaults
import me.him188.ani.app.ui.subject.details.components.SubjectDetailsHeader
import me.him188.ani.app.ui.subject.details.state.SubjectDetailsState
import me.him188.ani.app.ui.subject.episode.list.EpisodeListDialog
import me.him188.ani.app.ui.subject.rating.EditableRating
import me.him188.ani.utils.platform.isMobile

@Composable
fun SubjectDetailsPage(
    vm: SubjectDetailsViewModel,
    onPlay: (episodeId: Int) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    showBlurredBackground: Boolean = true,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    val stateFlow = vm.stateLoader.subjectDetailsStateFlow
    if (stateFlow != null) {
        val state by stateFlow.collectAsStateWithLifecycle()
        SubjectDetailsPage(
            state,
            onPlay = onPlay,
            modifier,
            showTopBar,
            showBlurredBackground,
            windowInsets,
        )
    }
}

@Composable
fun SubjectDetailsPage(
    state: SubjectDetailsState,
    onPlay: (episodeId: Int) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    showBlurredBackground: Boolean = true,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    val toaster = LocalToaster.current
    val browserNavigator = LocalUriHandler.current

    var showSelectEpisode by rememberSaveable { mutableStateOf(false) }
    if (showSelectEpisode) {
        EpisodeListDialog(
            state.episodeListState,
            title = {
                Text(state.info.displayName)
            },
            onDismissRequest = { showSelectEpisode = false },
        )
    }
    val connectedScrollState = rememberConnectedScrollState()

    // image viewer
    val imageViewer = rememberImageViewerHandler()
    BackHandler(enabled = imageViewer.viewing.value) { imageViewer.clear() }

    SubjectDetailsPageLayout(
        state,
        collectionData = {
            SubjectDetailsDefaults.CollectionData(
                collectionStats = state.info.collectionStats,
            )
        },
        collectionActions = {
            if (state.authState.isKnownExpired) {
                val navigator = LocalNavigator.current
                OutlinedButton({ state.authState.launchAuthorize(navigator) }) {
                    Text("登录后可收藏")
                }
            } else {
                EditableSubjectCollectionTypeButton(state.editableSubjectCollectionTypeState)
            }
        },
        rating = {
            EditableRating(state.editableRatingState)
        },
        selectEpisodeButton = {
            SubjectDetailsDefaults.SelectEpisodeButtons(
                state.subjectProgressState,
                onShowEpisodeList = { showSelectEpisode = true },
                onPlay = onPlay,
            )
        },
        connectedScrollState = connectedScrollState,
        detailsTab = { contentPadding ->
            SubjectDetailsDefaults.DetailsTab(
                info = state.info,
                staff = state.staffPager.collectAsLazyPagingItemsWithLifecycle(),
                exposedStaff = state.exposedStaffPager.collectAsLazyPagingItemsWithLifecycle(),
                totalStaffCount = state.totalStaffCountState.value,
                characters = state.charactersPager.collectAsLazyPagingItemsWithLifecycle(),
                exposedCharacters = state.exposedCharactersPager.collectAsLazyPagingItemsWithLifecycle(),
                totalCharactersCount = state.totalCharactersCountState.value,
                relatedSubjects = state.relatedSubjectsPager.collectAsLazyPagingItemsWithLifecycle(),
                Modifier
                    .nestedScrollWorkaround(state.detailsTabLazyListState, connectedScrollState)
                    .nestedScroll(connectedScrollState.nestedScrollConnection),
                state.detailsTabLazyListState,
                contentPadding = contentPadding,
            )
        },
        commentsTab = { contentPadding ->
            SubjectDetailsDefaults.SubjectCommentColumn(
                state = state.subjectCommentState,
                onClickUrl = {
                    RichTextDefaults.checkSanityAndOpen(it, browserNavigator, toaster)
                },
                onClickImage = { imageViewer.viewImage(it) },
                connectedScrollState,
                Modifier.fillMaxSize(),
                lazyListState = state.commentTabLazyListState,
                contentPadding = contentPadding,
            )
        },
        discussionsTab = {
            LazyColumn(
                Modifier.fillMaxSize()
                    // TODO: Add nestedScrollWorkaround when we implement this tab
                    .nestedScroll(connectedScrollState.nestedScrollConnection),
            ) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("即将上线, 敬请期待", Modifier.padding(16.dp))
                    }
                }
            }
        },
        modifier,
        showTopBar = showTopBar,
        showBlurredBackground = showBlurredBackground,
        windowInsets = windowInsets,
    )

    ImageViewer(imageViewer) { imageViewer.clear() }
}

@Immutable
enum class SubjectDetailsTab {
    DETAILS,
    COMMENTS,
    DISCUSSIONS,
}


/**
 * 一部番的详情页
 */
@Composable
fun SubjectDetailsPageLayout(
    state: SubjectDetailsState,
    collectionData: @Composable () -> Unit,
    collectionActions: @Composable () -> Unit,
    rating: @Composable () -> Unit,
    selectEpisodeButton: @Composable BoxScope.() -> Unit,
    connectedScrollState: ConnectedScrollState,
    detailsTab: @Composable (contentPadding: PaddingValues) -> Unit,
    commentsTab: @Composable (contentPadding: PaddingValues) -> Unit,
    discussionsTab: @Composable (contentPadding: PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    showBlurredBackground: Boolean = true,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    val scope = rememberCoroutineScope()
    val backgroundColor = AniThemeDefaults.pageContentBackgroundColor
    val stickyTopBarColor = AniThemeDefaults.navigationContainerColor

    val urlHandler = LocalUriHandler.current
    val onClickOpenExternal = {
        urlHandler.openUri("https://bgm.tv/subject/${state.info.subjectId}")
    }
    Scaffold(
        topBar = {
            if (showTopBar) {
                WindowDragArea {
                    Box {
                        // 透明背景的, 总是显示
                        TopAppBar(
                            title = {},
                            navigationIcon = { TopAppBarGoBackButton() },
                            actions = {
                                IconButton(onClickOpenExternal) {
                                    Icon(Icons.AutoMirrored.Outlined.OpenInNew, null)
                                }
                            },
                            colors = AniThemeDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
                            windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                        )

                        // 有背景, 仅在滚动一段距离后使用
                        AnimatedVisibility(connectedScrollState.isScrolledTop, enter = fadeIn(), exit = fadeOut()) {
                            TopAppBar(
                                title = {
                                    Text(
                                        state.info.displayName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                navigationIcon = { TopAppBarGoBackButton() },
                                actions = {
                                    IconButton(onClickOpenExternal) {
                                        Icon(Icons.AutoMirrored.Outlined.OpenInNew, null)
                                    }
                                },
                                colors = AniThemeDefaults.topAppBarColors(containerColor = stickyTopBarColor),
                                windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier,
        contentWindowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        containerColor = backgroundColor,
    ) { scaffoldPadding ->
        // 这个页面比较特殊. 背景需要绘制到 TopBar 等区域以内, 也就是要无视 scaffoldPadding.

        // 在背景之上显示的封面和标题等信息
        val headerContentPadding = scaffoldPadding.only(PaddingValuesSides.Horizontal + PaddingValuesSides.Top)
        // 从 tab row 开始的区域
        val remainingContentPadding = scaffoldPadding.only(PaddingValuesSides.Horizontal + PaddingValuesSides.Bottom)

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(Modifier.widthIn(max = 1300.dp).fillMaxHeight()) {
                Box(Modifier.connectedScrollContainer(connectedScrollState)) {
                    // 虚化渐变背景, 需要绘制到 scaffoldPadding 以外区域
                    if (showBlurredBackground) {
                        SubjectBlurredBackground(
                            coverImageUrl = state.coverImageUrl,
                            Modifier.matchParentSize(),
                            backgroundColor = backgroundColor,
                        )
                    }

                    // 标题和封面, 以及收藏数据, 可向上滑动
                    // 需要满足 scaffoldPadding 的 horizontal 和 top
                    Column(
                        Modifier
                            .padding(headerContentPadding)
                            .consumeWindowInsets(headerContentPadding),
                    ) {
                        SubjectDetailsHeader(
                            state.info,
                            state.coverImageUrl,
                            airingLabelState = state.airingLabelState,
                            collectionData = collectionData,
                            collectionAction = collectionActions,
                            selectEpisodeButton = selectEpisodeButton,
                            rating = rating,
                            Modifier
                                .connectedScrollTarget(connectedScrollState)
                                .fillMaxWidth()
                                .ifThen(!showTopBar) { padding(top = 16.dp) }
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
                val pagerState = rememberPagerState(
                    initialPage = SubjectDetailsTab.DETAILS.ordinal,
                    pageCount = { 3 },
                )

                // Pager with TabRow
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(remainingContentPadding)
                        .consumeWindowInsets(remainingContentPadding),
                ) {
                    val tabContainerColor by animateColorAsState(
                        if (connectedScrollState.isScrolledTop) stickyTopBarColor else backgroundColor,
                        tween(),
                    )
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = @Composable { tabPositions ->
                            TabRowDefaults.PrimaryIndicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            )
                        },
                        containerColor = tabContainerColor,
                        contentColor = TabRowDefaults.secondaryContentColor,
                        divider = {},
                        modifier = Modifier,
                    ) {
                        SubjectDetailsTab.entries.forEachIndexed { index, tabId ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = {
                                    Text(text = renderSubjectDetailsTab(tabId))
                                },
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        Modifier.fillMaxHeight(),
                        userScrollEnabled = LocalPlatform.current.isMobile(),
                        verticalAlignment = Alignment.Top,
                    ) { index ->
                        val type = SubjectDetailsTab.entries[index]
                        Column(Modifier.padding()) {
                            val paddingValues =
                                PaddingValues(bottom = currentWindowAdaptiveInfo1().windowSizeClass.paneVerticalPadding)
                            when (type) {
                                SubjectDetailsTab.DETAILS -> detailsTab(paddingValues)
                                SubjectDetailsTab.COMMENTS -> commentsTab(paddingValues)
                                SubjectDetailsTab.DISCUSSIONS -> discussionsTab(paddingValues)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Stable
private fun renderSubjectDetailsTab(tab: SubjectDetailsTab): String {
    return when (tab) {
        SubjectDetailsTab.DETAILS -> "详情"
        SubjectDetailsTab.COMMENTS -> "评论"
        SubjectDetailsTab.DISCUSSIONS -> "讨论"
    }
}