package com.exemplo.task.helper

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Funções comuns do app
 */
open class BaseFragment : Fragment() {

    fun hideKeyboard() {
        val view = activity?.currentFocus
        if(view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}