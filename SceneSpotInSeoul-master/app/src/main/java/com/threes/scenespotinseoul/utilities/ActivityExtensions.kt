package com.threes.scenespotinseoul.utilities

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.addFragment(contentViewId: Int, fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(contentViewId, fragment)
    transaction.commit()
}

fun AppCompatActivity.replaceFragment(contentViewId: Int, fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(contentViewId, fragment)
    transaction.commit()
}