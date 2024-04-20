package com.example.profilecardlayout

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.profilecardlayout.ui.theme.MyTheme
import com.example.profilecardlayout.ui.theme.lightGreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                UserApplication()
            }
        }
    }
}

@Composable
fun UserApplication(userProfiles: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "users_list") {
        composable("users_list") {
            UserListScreen(userProfiles, navController)
        }
        composable(route = "user_details/{userId}", arguments = listOf(navArgument("userId") {
            type = NavType.IntType
        })) { navBackStackEntry ->
            UserProfileDetailsScreen(navBackStackEntry.arguments!!.getInt("userId"), navController)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserListScreen(userProfiles: List<UserProfile>, navController: NavController?) {
    Scaffold(topBar = {
        AppBar(
            title = "User List",
            icon = Icons.Filled.Home
        ){}
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn {
                items(userProfiles) { userProfile ->
                    ProfileCard(userProfile = userProfile) {
                        navController?.navigate("user_details/${userProfile.id}")
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileDetailsScreen(userId: Int, navController: NavHostController?) {
    val userProfile = userProfileList.first { userProfile -> userId == userProfile.id }
    Scaffold(topBar = {
        AppBar(
            title = "User profile details",
            icon = Icons.Filled.ArrowBack
        ){
            navController?.navigateUp()
        }
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(userProfile.pictureUrl, userProfile.status, 240.dp)
                ProfileContent(userProfile.name, userProfile.status, Alignment.CenterHorizontally)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, icon: ImageVector, iconClickAction: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(
                onClick = {iconClickAction.invoke()},
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable(onClick = {}),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Localized description",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(title)
        }
    )
}

@Composable
fun ProfileCard(userProfile: UserProfile, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .background(color = Color.White)
            .clickable(onClick = { clickAction.invoke() }),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(userProfile.pictureUrl, userProfile.status, 72.dp)
            ProfileContent(userProfile.name, userProfile.status, Alignment.Start)
        }
    }
}

@Composable
fun ProfilePicture(pictureUrl: String, onlineStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (onlineStatus)
                MaterialTheme.colorScheme.lightGreen else Color.Red,
        ),
        modifier = Modifier
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = pictureUrl),
            contentDescription = "Profile picture description",
            modifier = Modifier.size(imageSize),
            contentScale = ContentScale.Crop
        )
//        Image(
//            bitmap = ImageBitmap.imageResource(drawableId),
//            contentDescription = "test",
//            modifier = Modifier.size(72.dp),
//            contentScale = ContentScale.Crop
//        )
    }

}

@Composable
fun ProfileContent(userName: String, onlineStatus: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        CompositionLocalProvider(
            LocalContentColor provides
                    if (onlineStatus) MaterialTheme.colorScheme.onSurface.copy(1f)
                    else MaterialTheme.colorScheme.onSurface.copy(0.5f)
        ) {
            Text(
                userName,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ) {
            Text(
                if (onlineStatus)
                    "Active now" else "Offline",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserProfileDetailsPreview() {
    MyTheme {
        UserProfileDetailsScreen(userId = 0, null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    MyTheme {
        UserListScreen(userProfiles = userProfileList, null)
    }
}