package com.bestapp.zipbab.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bestapp.zipbab.BuildConfig
import com.bestapp.zipbab.R
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.ui.theme.LocalCustomColorsPalette
import com.bestapp.zipbab.ui.theme.MainColor
import com.bestapp.zipbab.ui.theme.PretendardBold
import com.bestapp.zipbab.ui.theme.PretendardRegular
import com.bestapp.zipbab.ui.theme.SquareButton
import com.bestapp.zipbab.ui.theme.ZipbabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZipbabTheme {
                    SettingScreen()
                }
            }
        }
    }
}

@Composable
fun SettingScreen(settingViewModel: SettingViewModel = viewModel()) {
    val userUiState by settingViewModel.userUiState.collectAsState()

    AppBar(userUiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    userUiState: UserUiState,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarColors(
                    containerColor = LocalCustomColorsPalette.current.defaultBackgroundColor,
                    scrolledContainerColor = LocalCustomColorsPalette.current.defaultBackgroundColor,
                    navigationIconContentColor = LocalCustomColorsPalette.current.defaultForegroundColor,
                    titleContentColor = LocalCustomColorsPalette.current.defaultForegroundColor,
                    actionIconContentColor = MainColor,
                ),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        stringResource(id = R.string.setting_app_bar),
                        maxLines = 1,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    ) { innerPadding ->
        ScrollContent(innerPadding, userUiState)
    }
}

@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    userUiState: UserUiState,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 20.dp)

    ) {
        ProfileStatus(userUiState)
        Text(
            text = stringResource(id = R.string.header_for_setting_row),
            modifier = Modifier.padding(top = 24.dp)
        )
        SettingItem(
            iconResource = R.drawable.baseline_person_24,
            title = stringResource(id = R.string.setting_profile_row_title),
            description = stringResource(id = R.string.setting_profile_row_description)
        )
        SettingItem(
            iconResource = R.drawable.baseline_people_24,
            title = stringResource(id = R.string.setting_meeting_row_title),
            description = stringResource(id = R.string.setting_meeting_row_description)
        )
        SettingItem(
            iconResource = R.drawable.baseline_notifications_none_24,
            title = stringResource(id = R.string.setting_alert_row_title),
            description = stringResource(id = R.string.setting_alert_row_description)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = stringResource(id = R.string.header_for_etc_row),
            modifier = Modifier.padding(top = 12.dp)
        )
        SettingItem(
            iconResource = R.drawable.baseline_remove_red_eye_24,
            title = stringResource(id = R.string.setting_privacy_policy_row_title),
            description = stringResource(id = R.string.setting_privacy_policy_row_description)
        )
        SettingItem(
            iconResource = R.drawable.baseline_my_location_24,
            title = stringResource(id = R.string.setting_location_policy_row_title),
            description = stringResource(id = R.string.setting_location_policy_row_description)
        )
        SettingItem(
            iconResource = R.drawable.baseline_code_24,
            title = stringResource(id = R.string.setting_version_row_title),
            description = stringResource(id = R.string.version_format, BuildConfig.VERSION_NAME)
        )
        SquareButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), text = stringResource(id = R.string.logout)
        ) {

        }
        SquareButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), text = stringResource(id = R.string.register)
        ) {

        }
    }
}

@Composable
fun ProfileStatus(
    userUiState: UserUiState,
) {
    val clipboardManager = LocalClipboardManager.current
    val isShowClipboardToastMessage = remember {
        mutableStateOf(false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userUiState.profileImage, contentDescription = null,
            placeholder = painterResource(
                id = R.drawable.sample_profile_image
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(44.dp)
                .width(44.dp),
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = userUiState.nickname,
            fontFamily = PretendardBold,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    clipboardManager.setText(AnnotatedString(userUiState.userDocumentID))
                    isShowClipboardToastMessage.value = true
                },
            text = stringResource(
                id = R.string.profile_distinguish_format_8,
                userUiState.userDocumentID
            ),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = Color.Gray,
            fontFamily = PretendardRegular,
            fontSize = 16.sp
        )

        Image(
            modifier = Modifier.padding(start = 4.dp),
            painter = painterResource(id = R.drawable.baseline_info_outline_24),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Gray),
        )
    }
    if (isShowClipboardToastMessage.value) {
        ToastMessage(stringResource(id = R.string.user_document_id_is_copied))
        isShowClipboardToastMessage.value = false
    }
}

@Composable
fun ToastMessage(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SettingItem(
    @DrawableRes iconResource: Int,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .height(24.dp)
                .width(24.dp),
            painter = painterResource(id = iconResource),
            contentDescription = ""
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = PretendardBold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = PretendardRegular,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .height(24.dp)
                .width(24.dp),
            painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
            contentDescription = ""
        )
    }
}


@Preview
@Composable
fun SettingScreenPreview() {
    SettingScreen()
}