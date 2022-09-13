package ca.uwaterloo.ofn.model

class Sales {

    var id: String? = null
    var name: String? = null
    var revenue: String? = null

    constructor() {}

    constructor(name: String, revenue: String?, id: String?) {
        this.id = id
        this.name = name
        this.revenue = revenue
    }

}