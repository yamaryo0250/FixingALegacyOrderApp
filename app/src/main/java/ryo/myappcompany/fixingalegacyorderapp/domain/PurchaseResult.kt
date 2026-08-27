package ryo.myappcompany.fixingalegacyorderapp.domain

sealed class PurchaseResult

object Success : PurchaseResult()
class Failure(cause: String) : PurchaseResult()