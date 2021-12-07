package uk.co.droidinactu.araclists

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import uk.co.droidinactu.araclists.data.TodoItem
import uk.co.droidinactu.araclists.data.Todolist
import uk.co.droidinactu.araclists.ui.theme.AracListsTheme
import java.io.IOException
import java.nio.charset.StandardCharsets

const val LOG_TAG = "AracTodoLists"

class MainActivity : ComponentActivity() {

    var todolistModel: TodolistViewModel = TodolistViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todolistModel.initialise(this)
        setContent {
            AracListsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TodoListView(todolistModel)
                }
            }
        }
    }
}

class TodolistViewModel() : ViewModel() {
    var listItemsStack = ArrayDeque<List<TodoItem>>()
    var todolists: MutableList<Todolist> = mutableListOf()

    fun initialise(ctx: Context) {
        if (todolists.size == 0) {
            Log.d(LOG_TAG, "reading default lists from json files")
            initialiseLists(ctx)
        } else {
            Log.d(LOG_TAG, "Lists already populated")
        }
    }

    fun getCurrentList(): List<TodoItem>? {
        return listItemsStack.lastOrNull()
    }

    fun getListFromList(list: Todolist): List<TodoItem>? {
        listItemsStack.addLast(list.items)
        return getCurrentList()
    }

    fun getListFromItem(item: TodoItem): List<TodoItem>? {
        listItemsStack.addLast(item.items)
        return getCurrentList()
    }

    fun getPreviousList(): List<TodoItem>? {
        try {
            listItemsStack.removeLast()
        } catch (sne: NoSuchElementException) {

        }
        return getCurrentList()
    }

    private fun initialiseLists(ctx: Context) {
        Log.d("AracLists", "TodolistViewModel() :initialisePrompts() starting")
        val assetMgr = ctx.assets
        val assetFiles = assetMgr.list("lists");
        assetFiles!!.forEach { af ->
            run {
                var jsonStr = readJSONFromAsset(ctx, "lists/" + af);
                val gson = Gson()
                var todolist = gson.fromJson(jsonStr, Todolist::class.java);
                todolists.add(todolist)
            }
        }
        Log.d(
            "AracLists",
            "TodolistViewModel() :initialisePrompts() read in " + todolists.size + " lists"
        )
    }

    private fun readJSONFromAsset(ctx: Context, filename: String): String? {
        val json: String = try {
            val `is` = ctx.assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}

@Composable
fun TodoListView(mdl: TodolistViewModel) {
    Box() {
        if (mdl.getCurrentList() == null) {
            Log.d(LOG_TAG, "TodoListView(): using list of lists")
            TodoList(items = mdl.todolists)
        } else {
            Log.d(LOG_TAG, "TodoListView(): using currentListItems")
            TodoListItems(items = mdl.getCurrentList()!!)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoList(@PreviewParameter(TodoListPreviewParameterProvider::class) items: List<Todolist>) {
    Column {
        items.forEach { item ->
            TodoListRow(item)
        }
    }
}

class TodoListPreviewParameterProvider : PreviewParameterProvider<List<Todolist>> {
    override val values = sequenceOf(
        listOf(
            Todolist.Builder("1test list 1").description("test list 1 description").build(),
            Todolist.Builder("1test list 2").build(),
            Todolist.Builder("1test list 3").build()
        ),
        listOf(
            Todolist.Builder("2test list 1").description("test list 2 description").build(),
            Todolist.Builder("2test list 2").build()
        )
    )
}

@Composable
fun TodoListRow(item: Todolist) {
    val ctx: Context = LocalContext.current
    // TODO : Add onclick handling
    LazyRow(Modifier
        .padding(12.dp)
        .clickable(onClick = { /* Ignoring onClick */ })
    ) {
    item() {
            if (item.icon != null) {
                //display icon from list
                //Image()
            } else {//display default icon
                Image(
                    painterResource(R.drawable.ic_default_list_icon),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredSize(36.dp)
                        .padding(all = 8.dp)
                )
            }
        }
        item() {
            Text(text = "List: " + item.name,
                modifier = Modifier
                    .padding(8.dp) // margin
                    .border(2.dp, Color.Gray) // outer border
                    .padding(18.dp) // margin
                    .clickable { })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TodoListItems(@PreviewParameter(TodoListItemsPreviewParameterProvider::class) items: List<TodoItem>) {
    Column {
        items.forEach { item ->
            TodoItemRow(item)
        }
    }
}

class TodoListItemsPreviewParameterProvider : PreviewParameterProvider<List<TodoItem>> {
    override val values = sequenceOf(
        listOf(
            TodoItem.Builder("1test item 1").build(),
            TodoItem.Builder("1test item 2").build(),
            TodoItem.Builder("1test item 3").build()
        ),
        listOf(
            TodoItem.Builder("2test item 1").build(),
            TodoItem.Builder("2test item 2").build(),
            TodoItem.Builder("2test item 3").build()
        )
    )
}

@Composable
fun TodoItemRow(item: TodoItem) {
    LazyRow(Modifier
        .padding(8.dp)
        .clickable(onClick = { /* Ignoring onClick */ })
    ) {
        item() {
            if (item.icon != null) {
                //display icon from list
                //Image()
            } else {//display default icon
                Image(
                    painterResource(R.drawable.ic_default_item_icon),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredSize(24.dp)
                        .padding(all = 8.dp)
                )
            }
        }
        item() {
            Column() {
                Text(text = item.name!!)
                Text(text = item.description!!)
            }
        }
    }
}
