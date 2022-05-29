package com.example.foodapp.model

class OrderModel(var orderId: String ?= null,var uid : String ?= null,var name : String ?= null, var phone : String ?= null,
                 var address: String ?= null, var payment : String ?= null,var status : String ?= null,var date : String ?= null,
                 var total : String ?= null,val cancel_request : Boolean ?= false ) {
}