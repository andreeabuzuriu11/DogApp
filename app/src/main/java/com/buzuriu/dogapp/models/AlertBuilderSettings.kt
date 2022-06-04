package com.buzuriu.dogapp.models

class AlertBuilderSettings(
    itemsName: Array<CharSequence>,
    itemAction: HashMap<String, () -> Unit>?
) {
    var itemsName: Array<CharSequence>? = itemsName
    var itemActions: HashMap<String, () -> Unit>? = itemAction
}