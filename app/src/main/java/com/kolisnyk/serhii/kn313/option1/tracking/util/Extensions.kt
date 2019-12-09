package com.kolisnyk.serhii.kn313.option1.tracking.util

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun FragmentManager.addFragmentToActivity(fragment: Fragment, frameId: Int) {
    val transaction = this.beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commit()
}

fun FragmentManager.addFragmentToActivity(fragment: Fragment, tag: String) {
    val transaction = this.beginTransaction()
    transaction.add(fragment, tag)
    transaction.commit()
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun View.snack(snackbarText: String) {
    Snackbar.make(this, snackbarText, Snackbar.LENGTH_LONG).show()
}
