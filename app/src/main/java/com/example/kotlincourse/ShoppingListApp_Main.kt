package com.example.kotlincourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

const val HOME_ROUTE = "home"
const val ADDITEM_ROUTE = "item"

@Composable
fun MainAppView() {
    val userController = viewModel<UserController>()
    //passing this down fixes a weird state sync bug that only affects certain functions
    val dataController = viewModel<DataController>()
    if(userController.userName.value.isEmpty()){
        LoginView(userController, dataController)
    }else {
        MainScaffold(dataController, userController)
    }
}

@Composable
fun MainScaffold(dataController: DataController, userController: UserController) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { HeaderBar(userController) },
        bottomBar = { FooterBar(navController) },
        content = { MainAppContent(navController, dataController, userController) }
    )
}

@Composable
fun MainAppContent(navController: NavHostController, dataController: DataController, userController: UserController) {
    NavHost(navController = navController, startDestination = HOME_ROUTE ){
        composable( route = HOME_ROUTE ){ HomeView(dataController, userController) }
        composable( route = ADDITEM_ROUTE){ DataEntryView(dataController, userController) }
    }
}

@Composable
fun HomeView(dataController: DataController, userController: UserController) {
    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (dataController.items.isEmpty())
        {
            if (dataController.dbErr.value.isEmpty())
            {
                Spacer(modifier = Modifier.fillMaxHeight(0.45f))
                Text(fontSize = 4.em, textAlign = TextAlign.Center, text = "Welcome to ShoppingList!\n" +
                        "Your list seems to be empty, add some items in the bottom right menu.")
            }
            else
            {
                Spacer(modifier = Modifier.fillMaxHeight(0.45f))
                Text(text = dataController.dbErr.value, color = Color.Red)
            }
        }
        else
        {
            dataController.items.forEach {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { dataController.deleteShoppingListItem( userController.userName.value, it.key ) }
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(text = it.value)
                        Spacer(modifier = Modifier.height(25.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_remove_shopping_cart_24),
                            contentDescription = "delete",
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
            }
            if (dataController.itemSuccess.value.isNotEmpty())
            {
                Text(text = dataController.itemSuccess.value, color = Color.Green)
            }
            else if (dataController.dbErr.value.isNotEmpty())
            {
                Text(text = dataController.dbErr.value, color = Color.Red)
            }
        }
    }
}

@Composable
fun DataEntryView(dataController: DataController, userController: UserController) {
    var entryValue by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        OutlinedTextField(
            value = entryValue ,
            onValueChange = { entryValue = it },
            label = { Text(text = "Item name") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = { dataController.addShoppingListItem(userController.userName.value, entryValue); entryValue = "" }
        ) {
            Text(text = "Add item to shopping list")
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (dataController.itemSuccess.value.isNotEmpty())
        {
            Text(text = dataController.itemSuccess.value, color = Color.Green)
        }
        else if (dataController.dbErr.value.isNotEmpty())
        {
            Text(text = dataController.dbErr.value, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider(thickness = 2.dp)
    }
}

@Composable
fun HeaderBar(userController: UserController) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .drawBehind {
            val strokeWidth = 3f
            val y = size.height - strokeWidth / 2
            drawLine(
                Color.LightGray,
                Offset(0f, y),
                Offset(size.width, y),
                strokeWidth
            )
        }
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Row() {
            Text(text = "Logged in as ")
            Text(
                userController.userName.value.substringBefore("@") + ".",
                fontWeight = FontWeight.ExtraBold
            )
        }
        OutlinedButton(onClick = { userController.logout() })
        {
            Text(text = "Log out")
        }
    }
}

@Composable
fun FooterBar(navController: NavHostController) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .drawBehind {
            val strokeWidth = 3f
            val y = -2f
            drawLine(
                Color.LightGray,
                Offset(0f, y),
                Offset(size.width, y),
                strokeWidth
            )
        }
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        var homeButtonTint = if (currentRoute == HOME_ROUTE) {
            Color.Blue;
        } else {
            Color.DarkGray;
        }
        var itemButtonTint = if (currentRoute == ADDITEM_ROUTE) {
            Color.Blue;
        } else {
            Color.DarkGray;
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_shopping_cart_24),
            contentDescription = "home",
            modifier = Modifier
                .clickable { navController.navigate(HOME_ROUTE) }
                .size(35.dp),
            tint = homeButtonTint
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_shopping_cart_24),
            contentDescription = "add item",
            modifier = Modifier
                .clickable { navController.navigate(ADDITEM_ROUTE) }
                .size(35.dp),
            tint = itemButtonTint
        )
    }
}

@Composable
fun LoginView(userController: UserController, dataController: DataController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_shopping_cart_24),
            contentDescription = "icon",
            modifier = Modifier
                .size(65.dp)
        )
        Text(text = "Welcome to ShoppingList!",
            modifier = Modifier
            .padding(vertical = 20.dp))
        OutlinedTextField(
            value = email , 
            onValueChange = { email = it },
            label = { Text(text = "Email") }
        )
        OutlinedTextField(
            value = password ,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Text(text = userController.loginErrorText.value, color = Color.Red, modifier = Modifier.padding(vertical = 10.dp))
        OutlinedButton(
            onClick = { userController.login(email, password); dataController.getShoppingList(email) },
            modifier = Modifier.width(100.dp))
        {
            Text(text = "Login")
        }
    }
}