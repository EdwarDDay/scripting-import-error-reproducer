@file:Import("imports/helper.repro.kts")

class MyData(val value: String)

val result = MyData("hello").typeName()
require(result == "MyData") { "Expected 'MyData' but got '$result'" }
