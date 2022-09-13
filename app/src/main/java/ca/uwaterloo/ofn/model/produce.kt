package ca.uwaterloo.ofn.model

class Produce {

    var id: String? = null
    var name: String? = null
    var image: String? = null
    var price: String? = null
    var quantity: String? = null
    var description: String? = null
    var sellerId: String? = null

    constructor() {}

    constructor(id: String, name: String, image: String?, price: String, quantity: String, description: String, sellerId: String) {
        this.id = id
        this.name = name
        this.image = image
        this.price = price
        this.quantity = quantity
        this.description = description
        this.sellerId = sellerId
    }

}