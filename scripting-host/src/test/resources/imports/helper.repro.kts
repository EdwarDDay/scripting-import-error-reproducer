inline fun <reified T> T.typeName(): String = T::class.simpleName ?: "?"
