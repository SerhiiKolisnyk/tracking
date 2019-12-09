package com.kolisnyk.serhii.kn313.option1.tracking.taskdetail

import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.databinding.TaskdetailFragBinding
import com.kolisnyk.serhii.kn313.option1.tracking.util.*

class TaskDetailFragment : Fragment() {

    private var mViewModel: TaskDetailViewModel? = null
    private var mSnackbarCallback: Observable.OnPropertyChangedCallback? = null

    fun setViewModel(taskViewModel: TaskDetailViewModel) {
        mViewModel = taskViewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupFab()

        setupSnackbar()
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
        val fab = activity.findViewById<FloatingActionButton>(R.id.fab_edit_task)

        fab.setOnClickListener { v -> mViewModel?.startEditTask() }
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.start(arguments.getString(ARGUMENT_TASK_ID))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.taskdetail_frag, container, false)

        val viewDataBinding = TaskdetailFragBinding.bind(view)
        viewDataBinding.viewmodel = mViewModel

        setHasOptionsMenu(true)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_delete -> {
                mViewModel?.deleteTask()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    companion object {

        val ARGUMENT_TASK_ID = "TASK_ID"

        val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String): TaskDetailFragment {
            val arguments = Bundle()
            arguments.putString(ARGUMENT_TASK_ID, taskId)
            val fragment = TaskDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
