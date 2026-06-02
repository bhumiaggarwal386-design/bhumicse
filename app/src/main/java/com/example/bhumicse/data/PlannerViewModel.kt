package com.example.bhumicse.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow


class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WardrobeRepository(
        AppDatabase.getInstance(application)
    )

    // ── All outfits (to show in picker dialog) ─
    val allOutfits: StateFlow<List<OutfitWithItems>> =
        repository.getAllOutfitsWithItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ── All planner entries ────────────────────
    val allEntries: StateFlow<List<PlannerEntryEntity>> =
        repository.getAllPlannerEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    // ── Outfit item refs for preview ───────────
    private val _outfitItemRefs = MutableStateFlow<List<OutfitItemCrossRef>>(emptyList())
    val outfitItemRefs: StateFlow<List<OutfitItemCrossRef>> = _outfitItemRefs

    fun loadRefsForOutfit(outfitId: Long) {
        viewModelScope.launch {
            _outfitItemRefs.value = repository.getOutfitItemRefs(outfitId)
        }
    }

    // ── Add a new planner entry ────────────────
    fun addEntry(outfitId: Long, date: String) {
        viewModelScope.launch {
            repository.insertPlannerEntry(
                PlannerEntryEntity(
                    outfitId = outfitId,
                    date     = date
                )
            )
        }
    }

    // ── Delete a planner entry ─────────────────
    fun deleteEntry(entry: PlannerEntryEntity) {
        viewModelScope.launch {
            repository.deletePlannerEntry(entry)
        }
    }

    // ── Factory ────────────────────────────────
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return PlannerViewModel(application) as T
                }
            }
        }
    }
}
