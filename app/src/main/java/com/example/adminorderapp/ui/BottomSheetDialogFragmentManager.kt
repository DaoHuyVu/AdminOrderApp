package com.example.adminorderapp.ui

class BottomSheetDialogFragmentManager private constructor() {

    fun getDialog(
        name : String,
        details : () -> Unit,
        delete : () -> Unit
    ) : OptionBottomSheetDialogFragment{
        return OptionBottomSheetDialogFragment(name,details,delete)
    }
    companion object {
        @Volatile
        private var manager : BottomSheetDialogFragmentManager? = null
        fun getInstance() : BottomSheetDialogFragmentManager {
            return manager ?: synchronized(this){
                manager ?: BottomSheetDialogFragmentManager().also { manager = it}
            }
        }
    }
}