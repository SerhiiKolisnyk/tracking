package com.kolisnyk.serhii.kn313.option1.tracking


import android.os.Bundle
import android.support.v4.app.Fragment

class ViewModelHolder<VM> : Fragment() {

    var viewmodel: VM? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun setViewModel(viewModel: VM) {
        viewmodel = viewModel
    }

    companion object {

        fun <M> createContainer(viewModel: M): ViewModelHolder<*> {
            val viewModelContainer = ViewModelHolder<M>()
            viewModelContainer.setViewModel(viewModel)
            return viewModelContainer
        }
    }
}
