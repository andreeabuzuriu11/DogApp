package com.buzuriu.dogapp.models

class AlertDialogTextObj(
    options: Array<String>,
    var action: Map<String, () -> Unit>?
) {
    var options: Array<String>? = options
}