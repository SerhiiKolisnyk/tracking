package com.kolisnyk.serhii.kn313.option1.tracking.addedittask

import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.databinding.AddtaskFragBinding
import com.kolisnyk.serhii.kn313.option1.tracking.util.*

class AddEditTaskFragment : Fragment() {

    private var mViewModel: AddEditTaskViewModel? = null

    private lateinit var mViewDataBinding: AddtaskFragBinding

    private var mSnackbarCallback: Observable.OnPropertyChangedCallback? = null

    override fun onResume() {
        super.onResume()
        if (arguments != null) {
            mViewModel?.start(arguments.getString(ARGUMENT_EDIT_TASK_ID))
        } else {
            mViewModel?.start(null)
        }
    }

    fun setViewModel(viewModel: AddEditTaskViewModel) {
        mViewModel = viewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupFab()

        setupSnackbar()

        setupActionBar()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = container?.inflate(R.layout.addtask_frag)

        mViewDataBinding = AddtaskFragBinding.bind(root)

        mViewDataBinding.viewmodel = mViewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return mViewDataBinding.root
    }

    override fun onDestroy() {
        if (mSnackbarCallback != null) {
            mViewModel?.snackbarText?.removeOnPropertyChangedCallback(mSnackbarCallback)
        }
        super.onDestroy()
    }

    private fun setupSnackbar() {
        mSnackbarCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                view?.snack(mViewModel?.getSnackbarTextString()!!)
            }
        }
        mViewModel?.snackbarText?.addOnPropertyChangedCallback(mSnackbarCallback)
    }

    private fun setupFab() {
        val fab = activity.findViewById<View>(R.id.fab_edit_task_done) as FloatingActionButton
        fab.setImageResource(R.drawable.ic_done)
        fab.setOnClickListener { mViewModel?.saveTask() }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (arguments != null) {
            actionBar?.setTitle(R.string.edit_task)
        } else {
            actionBar?.setTitle(R.string.add_task)
        }
    }

    companion object {

        val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(): AddEditTaskFragment {
            return AddEditTaskFragment()
        }
    }
}// Required empty public constructor
