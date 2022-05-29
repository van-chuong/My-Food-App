package com.example.foodapp.helper

import java.text.FieldPosition

interface OnItemViewManagerClick {
    fun OnItemClick(position: Int)
    fun OnDeleteClick(position: Int)
}