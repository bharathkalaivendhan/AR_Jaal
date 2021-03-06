package com.bk.arjaal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.bk.arjaal.ui.theme.ARJaalTheme

//needed
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.bk.arjaal.ui.theme.ui.theme.Ljust1
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

val JewelleryCategoryList = mutableListOf<String>(
    "ALL",
    "Ear rings",
    "Necklaces",
    "Rings",
    "Bangles"
)
var favourites = mutableListOf<Jewellery>()
private lateinit var auth : FirebaseAuth

class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = JewelleryViewModel()

        setContent {

            auth = Firebase.auth

            val jewellerylist = viewModel.jewelleriesList

            ARJaalTheme {
                // Remember a SystemUiController
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight

                SideEffect {
                    // Update all of the system bar colors to be transparent, and use
                    // dark icons if we're in light theme
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )


                }

                MainScreen(jewellerylist,viewModel,this, JewelleryCategoryList)
            }

        }
    }
}


@ExperimentalMaterialApi
@Composable
fun MainScreen(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity,
    JewelleryCategoryList: MutableList<String>
) {
    val items =  listOf(
        NavigationItem(
            name = "Home",
            route = "home",
            icon = Icons.Default.Home
        ),
        NavigationItem(
            name = "Favourite",
            route = "favourite",
            icon = Icons.Default.Favorite
        ),
        NavigationItem(
            name = "Account",
            route = "account",
            icon = Icons.Default.AccountBox
        ))

    val navController  = rememberNavController()
    Scaffold(
        topBar = {
            //TopBar()
        },
        bottomBar = {
            BottomNavigationBar(items = items,
                navController = navController,
                onItemClick = { navController.navigate(it.route)}) }
    ) {

            innerPadding ->
        // Apply the padding globally to the whole BottomNavScreensController
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(
                jewellerylist,
                navController,
                viewModel,
                mainActivity,
                JewelleryCategoryList,
            )
        }}
}


@Composable
fun BottomNavigationBar(
    items : List<NavigationItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (NavigationItem) -> Unit
)
{
    val backStackEntry = navController.currentBackStackEntryAsState()

    BottomNavigation(
        modifier = modifier,
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = colorResource(id = R.color.black)
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                icon = { Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = item.icon, contentDescription = item.name)
                    if(selected) {
                        Text(
                            text = item.name,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp
                        )
                    }
                }
                },
                //label = { Text(text = item.name) },
                selectedContentColor = Ljust1,
                unselectedContentColor = colorResource(id = R.color.black),
                alwaysShowLabel = true,
                selected = selected,
                onClick = {
                    onItemClick(item)
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun Navigation(
    jewellerylist: MutableList<Jewellery>,
    navController: NavHostController,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity,
    JewelleryCategoryList: MutableList<String>
)
{
    NavHost( navController = navController,startDestination = "home") {
        composable("home") {
            HomeScreen(
                jewellerylist,
                viewModel = viewModel,
                mainActivity = mainActivity,
                JewelleryCategoryList = JewelleryCategoryList
            )

        }
        composable("favourite") {
            FavouriteScreen(
                jewellerylist,
                viewModel = viewModel,
                mainActivity = mainActivity,
            )
        }
        composable("account") {
            AccountScreen(
                jewellerylist,
                viewModel = viewModel,
                mainActivity = mainActivity,
                JewelleryCategoryList = JewelleryCategoryList
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AccountScreen(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity,
    JewelleryCategoryList: MutableList<String>
) {

    auth = FirebaseAuth.getInstance()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Name : "+ auth.currentUser?.displayName.toString(),fontSize = 20.sp)
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Email : "+ auth.currentUser?.email.toString(),fontSize = 20.sp)
        Spacer(modifier = Modifier.size(20.dp))
        /*
        Text(text = "Total Number Of Orders : $numOfOrder",fontSize = 20.sp)
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Total Amount Purchased : $TotalAmount",fontSize = 20.sp)
        Spacer(modifier = Modifier.size(20.dp))
         */
        Button(onClick = { signOut(mainActivity = mainActivity)},shape = MaterialTheme.shapes.medium) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.ic_logout), contentDescription = "Logout")
                Text(text = "LogOut",color = Color.Black)
            }
        }
    }
}

fun signOut(mainActivity: MainActivity) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("650846603505-s02l4835eujpnfkekt5957ripm4ruci6.apps.googleusercontent.com")
        .requestEmail()
        .build()
    mGoogleSignInClient= GoogleSignIn.getClient(mainActivity,gso)

    auth.signOut()
    mGoogleSignInClient.signOut().addOnCompleteListener {
        Log.d("SignInActivity", "signInWithCredential:success")
        val intent = Intent(mainActivity, LoginActivity::class.java)
        mainActivity.startActivity(intent)
    }
}

@ExperimentalMaterialApi
@Composable
fun FavouriteScreen(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity
)
{
    FavouriteCardListView(jewellerylist = jewellerylist, viewModel = viewModel, mainActivity = mainActivity)
}

@ExperimentalMaterialApi
@Composable
fun FavouriteCardListView(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity
)
{
    LazyColumn(
        modifier = Modifier,

        contentPadding = PaddingValues(horizontal = 8.dp)
    ){

        itemsIndexed(
            items = favourites,
            itemContent = { index, item ->
                if(item.Name!="") {
                    FavouriteCard(
                        jewellery = item,
                        viewModel = viewModel,
                        mainActivity = mainActivity
                    )
                }
            }
        )

    }
}

@ExperimentalMaterialApi
@Composable
fun FavouriteCard(
    jewellery: Jewellery,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity
)
{
    val context = LocalContext.current

    val painter = rememberImagePainter(data = jewellery.ImageURL)

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(corner = CornerSize(6.dp)),
        onClick = {takeToNextActivity(mainActivity,context,jewellery)}
    ) {

        Column {

            Image(
                painter = painter,
                contentDescription = "jewelly image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    //.padding(8.dp)
                    .fillMaxWidth(1f)
                    .height(200.dp)
                //.clip(RoundedCornerShape(corner = CornerSize(10.dp)))
            )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {

                    Text(
                        text = "Rs." + jewellery.Price.toString(),
                        style = MaterialTheme.typography.h6,
                        color = colorResource(id = R.color.black),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 12.dp)

                    )
                    Text(
                        text = jewellery.Name,
                        style = MaterialTheme.typography.h5,
                        color = colorResource(id = R.color.black),
                        fontWeight = FontWeight.Medium
                    )
                }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity,
    JewelleryCategoryList: MutableList<String>
)
{
    Column {

        LazyRow(
            modifier = Modifier.padding(start =10.dp,end=10.dp,top=4.dp,bottom=2.dp)
        )
        {

            itemsIndexed(
                items = JewelleryCategoryList,
                itemContent = { index, item -> CategoryCard(Category = item, viewModel) }
            )

        }

        JewelleryCardListView(jewellerylist,viewModel, mainActivity)
    }
}

@ExperimentalMaterialApi
@Composable
fun CategoryCard(Category: String, viewModel: JewelleryViewModel)
{
    var isSelected = false
    Card(
        modifier = Modifier
            .padding(start = 10.dp, bottom = 4.dp, end = 5.dp, top = 12.dp)
            .width(100.dp)
            .height(50.dp)
            .clickable {
                Log.d("CHECKING", "inside category card click")
                viewModel.onchangeList(Category)
                isSelected = true
            },
        elevation = 3.dp,
        backgroundColor =
        if(isSelected)
        { MaterialTheme.colors.surface }
        else{
            MaterialTheme.colors.primaryVariant
        }
        ,
        shape = MaterialTheme.shapes.small

    )
    {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = Category,color = Color.Black)
        }

    }
}

@ExperimentalMaterialApi
@Composable
fun JewelleryCardListView(
    jewellerylist: MutableList<Jewellery>,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity
)
{
    LazyColumn(
        modifier = Modifier,

        contentPadding = PaddingValues(horizontal = 8.dp)
    ){

        itemsIndexed(
            items = jewellerylist,
            itemContent = { index, item ->
                JewelleryCard(
                    jewellery = item,
                    viewModel = viewModel,
                    mainActivity = mainActivity
                )
            }
        )

    }
}

@ExperimentalMaterialApi
@Composable
fun JewelleryCard(
    jewellery: Jewellery,
    viewModel: JewelleryViewModel,
    mainActivity: MainActivity
)
{
    val context = LocalContext.current

    val painter = rememberImagePainter(data = jewellery.ImageURL)

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(corner = CornerSize(6.dp)),
        onClick = {takeToNextActivity(mainActivity,context,jewellery)}
    ) {

        Column {

            Image(
                painter = painter,
                contentDescription = "jewelly image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    //.padding(8.dp)
                    .fillMaxWidth(1f)
                    .height(200.dp)
                //.clip(RoundedCornerShape(corner = CornerSize(10.dp)))
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {

                Text(
                    text = "Rs."+jewellery.Price.toString(),
                    style = MaterialTheme.typography.h6,
                    color = colorResource(id = R.color.black)
                    ,
                    fontWeight = FontWeight.ExtraBold
                    , modifier = Modifier.padding(bottom=12.dp)

                )
                Text(
                    text = jewellery.Name,
                    style = MaterialTheme.typography.h5,
                    color = colorResource(id = R.color.black)
                    ,
                    fontWeight = FontWeight.Medium
                )
            }

        }

    }
}

fun takeToNextActivity(mainActivity: MainActivity, context: Context, jewellery: Jewellery,) {
    val intent = Intent(mainActivity,DetailsActivity::class.java)
    intent.putExtra( "jewelleryInfo", jewellery )
    context.startActivity(intent)
}
