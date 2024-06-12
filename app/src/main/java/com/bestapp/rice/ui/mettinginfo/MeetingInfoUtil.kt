package com.bestapp.rice.ui.mettinginfo

import android.view.View

fun View.setOnSingleClickListener(
    onClickListener: (view: View) -> Unit
) {
    setOnClickListener(OnSingleClickListener(onClickListener))
}