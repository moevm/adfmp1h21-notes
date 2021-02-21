package eltech.semoevm.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import eltech.semoevm.notes.note.NotePage
import eltech.semoevm.notes.start.StartPage
import eltech.semoevm.notes.ui.NotesTheme
import java.io.Serializable

class MainActivity : AppCompatActivity(), AppStateRenderer {
    private var currentSate: AppState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentSate = if (savedInstanceState?.containsKey(SAVED_STATE) == true) {
            savedInstanceState.get(SAVED_STATE) as AppState
        } else {
            AppState.StartPage
        }

        render(currentSate!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentSate?.let {
            outState.putSerializable(SAVED_STATE, it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == ACTION_NEW_STATE) {
            currentSate = intent.getSerializableExtra(NEW_STATE_KEY) as AppState
            render(currentSate!!)
        }
    }

    override fun onBackPressed() {
        if (currentSate != AppState.StartPage) {
            currentSate = AppState.StartPage
            render(currentSate!!)
        } else {
            super.onBackPressed()
        }
    }

    override fun render(state: AppState) {
        setContent {
            NotesTheme {
                RenderState(state)
            }
        }
    }

    companion object {
        private const val ACTION_NEW_STATE = "ACTION_NEW_STATE"
        private const val NEW_STATE_KEY = "NEW_STATE_KEY"
        private const val SAVED_STATE = "SAVED_STATE"

        fun createStateIntent(context: Context, state: AppState): Intent {
            return Intent(context, MainActivity::class.java)
                .setAction(ACTION_NEW_STATE)
                .putExtra(NEW_STATE_KEY, state)
        }
    }
}

interface AppStateRenderer {
    fun render(state: AppState)
}

enum class AppState(private var initObj: Serializable? = null) {
    StartPage, Note; // ...

    fun setInitObj(obj: Serializable): AppState {
        this.initObj = obj
        return this
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RenderState(state: AppState) {
    when (state) {
        AppState.StartPage -> StartPage()
        AppState.Note -> NotePage()
        // .. TODO
    }
}