package com.arvi.Interfaces

import android.view.View

public interface RecyclerViewItemClicked {
    abstract fun onClick(view: View, position: Int)
}