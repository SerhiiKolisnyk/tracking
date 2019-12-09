package com.kolisnyk.serhii.kn313.option1.tracking.statistics

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.databinding.StatisticsFragBinding

class StatisticsFragment : Fragment() {

    lateinit var mViewDataBinding: StatisticsFragBinding

    private var mStatisticsViewModel: StatisticsViewModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate<StatisticsFragBinding>(
                inflater!!, R.layout.statistics_frag, container, false)
        return mViewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewDataBinding.stats = mStatisticsViewModel
    }

    override fun onResume() {
        super.onResume()
        mStatisticsViewModel?.start()
    }

    fun setViewModel(statisticsViewModel: StatisticsViewModel) {
        mStatisticsViewModel = statisticsViewModel
    }

    val isActive: Boolean
        get() = isAdded

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
