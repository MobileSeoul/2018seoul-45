package com.threes.scenespotinseoul.ui.search.widget

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.threes.scenespotinseoul.R
import kotlinx.android.synthetic.main.persistent_search_view.view.*

class PersistentSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var iconMode = 0
    private var isExpanded = false
    private var autoCompleteAdapter: SearchAutoCompleteAdapter
    private var searchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            expandSearchView()
            editBeforeTextChangeListener?.invoke(s.toString(), start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editOnTextChangeListener?.invoke(s.toString(), start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            editAfterTextChangeListener?.invoke(s)
        }
    }

    var autoCompleteSelectListener: ((String) -> Unit)? = null
    var editActionListener: ((String) -> Unit)? = null
    var editBeforeTextChangeListener: ((String, Int, Int, Int) -> Unit)? = null
    var editOnTextChangeListener: ((String, Int, Int, Int) -> Unit)? = null
    var editAfterTextChangeListener: ((Editable?) -> Unit)? = null
    var backButtonClickListener: ((View) -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SearchView,
                0, 0).apply {
            try {
                iconMode = getInteger(R.styleable.SearchView_iconMode, 0)
            } finally {
                recycle()
            }
        }

        View.inflate(context, R.layout.persistent_search_view, this)

        when (iconMode) {
            BACK -> showBackButton()
            SEARCH -> hideBackButton()
            else -> hideBackButton()
        }

        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)
        useCompatPadding = true
        isFocusable = true
        isFocusableInTouchMode = true

        autoCompleteAdapter = SearchAutoCompleteAdapter()
        autoCompleteAdapter.itemSelectListener = {
            setAutoCompletedText(it)
            collapseSearchView()
            clearAutoComplete()
            hideKeyboard()
            clearEditTextFocus()
            autoCompleteSelectListener?.invoke(it)
        }

        list_search_autocomplete.layoutManager = LinearLayoutManager(context)
        list_search_autocomplete.adapter = autoCompleteAdapter

        edit_search.addTextChangedListener(searchTextWatcher)

        edit_search.setOnEditorActionListener { view, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    collapseSearchView()
                    clearAutoComplete()
                    hideKeyboard()
                    clearEditTextFocus()
                    editActionListener?.invoke(view.text.toString())
                    true
                }
                else -> false
            }
        }

        edit_search.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showBackButton()
            }
        }

        btn_back.setOnClickListener {
            collapseSearchView()
            clearAutoComplete()
            hideKeyboard()
            hideBackButton()
            edit_search.clearFocus()
            setAutoCompletedText("")
            backButtonClickListener?.invoke(it)
        }
    }

    fun setAutoCompleteData(lifeCycleOwner: LifecycleOwner, data: LiveData<List<String>>) {
        data.observe(lifeCycleOwner, Observer {
            autoCompleteAdapter.submitList(it!!)
        })
    }

    fun setIconMode(@IconMode newIconMode: Int) {
        this.iconMode = newIconMode
        invalidate()
        requestLayout()
    }

    fun requestEditTextFocus() {
        edit_search.requestFocus()
    }

    fun setAutoCompletedText(autoCompletedText: String) {
        edit_search.removeTextChangedListener(searchTextWatcher)
        edit_search.setText(autoCompletedText)
        edit_search.addTextChangedListener(searchTextWatcher)
    }

    private fun clearEditTextFocus() {
        edit_search.clearFocus()
    }

    private fun showBackButton() {
        if (iconMode == NONE || iconMode == BACK) {
            iv_search_icon.visibility = INVISIBLE
            btn_back.visibility = VISIBLE
        }
    }

    private fun hideBackButton() {
        if (iconMode == NONE || iconMode == SEARCH) {
            iv_search_icon.visibility = VISIBLE
            btn_back.visibility = INVISIBLE
        }
    }

    private fun clearAutoComplete() {
        autoCompleteAdapter.submitList(listOf())
    }

    private fun expandSearchView() {
        if (!isExpanded) {
            isExpanded = true
            list_search_autocomplete.visibility = VISIBLE
            view_divider.visibility = VISIBLE
            showBackButton()
        }
    }

    private fun collapseSearchView() {
        if (isExpanded) {
            isExpanded = false
            list_search_autocomplete.visibility = GONE
            view_divider.visibility = INVISIBLE
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
            NONE,
            SEARCH,
            BACK
    )
    annotation class IconMode

    companion object {
        const val NONE = 0
        const val SEARCH = 1
        const val BACK = 2
    }
}