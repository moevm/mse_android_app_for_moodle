package info.moevm.moodle.model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Immutable // Optimization for Compose
data class ExpandableCardModel(val id: Int, val title: String)

const val EXPAND_ANIMATION_DURATION = 500
const val FADE_IN_ANIMATION_DURATION = 500
const val FADE_OUT_ANIMATION_DURATION = 500
const val COLLAPSE_ANIMATION_DURATION = 500

class CardsViewModel(titles: List<String>) : ViewModel() {

    private val _cards = MutableStateFlow(listOf<ExpandableCardModel>())
    val cards: StateFlow<List<ExpandableCardModel>> get() = _cards

    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val testList = arrayListOf<ExpandableCardModel>()
            var cnt = 0
            for (title in titles) {
                testList += ExpandableCardModel(id = cnt++, title = title)
            }
            _cards.emit(testList)
        }
    }

    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }
}
