package com.kolisnyk.serhii.kn313.option1.tracking.statistics

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.android.architecture.blueprints.todoapp.databinding.StatisticsActBinding

import com.kolisnyk.serhii.kn313.option1.tracking.Injection
import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.ViewModelHolder
import com.kolisnyk.serhii.kn313.option1.tracking.databinding.StatisticsActBinding
import com.kolisnyk.serhii.kn313.option1.tracking.tasks.TasksActivity
import com.kolisnyk.serhii.kn313.option1.tracking.util.*


class StatisticsActivity : AppCompatActivity() {

    lateinit var mBinding: StatisticsActBinding

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mBinding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.statistics_act)

        setupToolbar()

        setupNavigationDrawer()

        val statisticsFragment = findOrCreateViewFragment()

        val statisticsViewModel = findOrCreateViewModel()

        // Link View and ViewModel
        statisticsFragment.setViewModel(statisticsViewModel)
    }

    private fun findOrCreateViewModel(): StatisticsViewModel {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        val retainedViewModel = supportFragmentManager
                .findFragmentByTag(STATS_VIEWMODEL_TAG) as? ViewModelHolder<*>


        if (retainedViewModel?.viewmodel != null) {
            // If the model was retained, return it.
            return retainedViewModel.viewmodel as StatisticsViewModel
        } else {
            // There is no ViewModel yet, create it.
            val viewModel = StatisticsViewModel(applicationContext,
                    Injection.provideTasksRepository(applicationContext))

            // and bind it to this Activity's lifecycle using the Fragment Manager.
            supportFragmentManager.addFragmentToActivity(
                    ViewModelHolder.createContainer(viewModel),
                    STATS_VIEWMODEL_TAG)
            return viewModel
        }
    }

    private fun findOrCreateViewFragment(): StatisticsFragment {
        var statisticsFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as? StatisticsFragment

        if (statisticsFragment == null) {
            statisticsFragment = StatisticsFragment.newInstance()
            supportFragmentManager.addFragmentToActivity(statisticsFragment, R.id.contentFrame)
        }
        return statisticsFragment
    }

    private fun setupNavigationDrawer() {
        mBinding.drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)
        setupDrawerContent(mBinding.navView)

    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding.toolbar)
        val ab = supportActionBar
        ab!!.setTitle(R.string.statistics_title)
        ab.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    val intent = Intent(this@StatisticsActivity, TasksActivity::class.java)
                    startActivity(intent)
                }
                R.id.statistics_navigation_menu_item -> {
                }
                else -> {
                }
            }// Do nothing, we're already on that screen
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            mBinding.drawerLayout.closeDrawers()
            true
        }
    }

    companion object {

        val STATS_VIEWMODEL_TAG = "ADD_EDIT_VIEWMODEL_TAG"
    }
}
