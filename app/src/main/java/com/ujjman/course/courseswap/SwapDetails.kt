package com.ujjman.course.courseswap

public data class SwapDetails(
    public var courseHave: String?="",
    public var courseWant: String?="",
    public var uid : String?="",
    public var requestsHave: Int?=0,
    public var requestsWant: Int?=0
)