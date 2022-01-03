package com.buzuriu.dogapp.models

class AlertBuilderSettings {
    var itemsName:Array<CharSequence>?=null
    var itemActions: HashMap<String, () -> Unit>? = null

    constructor(itemsName:Array<CharSequence>,itemAction: HashMap<String, () -> Unit>?) {
        this.itemsName=itemsName
        this.itemActions = itemAction
    }
}