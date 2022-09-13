package ca.uwaterloo.ofn.model

interface SalesCallback {
    fun onCallback(value: MutableList<Sales>)

}