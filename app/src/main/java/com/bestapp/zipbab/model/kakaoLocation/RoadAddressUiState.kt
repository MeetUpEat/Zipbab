package com.bestapp.zipbab.model.kakaoLocation

import com.bestapp.zipbab.data.model.remote.kakaomap.RoadAddressResponse

data class RoadAddressUiState(
    val addressName: String = "",
    val longitude: String = "",
    val latitude: String = "",
    val regionDepthName1: String = "",
    val regionDepthName2: String = "",
    val regionDepthName3: String = "",
    val roadName: String = "",
    val undergroundYn: String = "",
    val mainBuildingNo: String = "",
    val subBuildingNo: String = "",
    val buildingName: String = "",
    val zoneNo: String = "",
)

fun RoadAddressResponse.toUiState() = RoadAddressUiState(
    addressName = addressName.orEmpty(),
    longitude = longitude.orEmpty(),
    latitude = latitude.orEmpty(),
    regionDepthName1 = regionDepthName1.orEmpty(),
    regionDepthName2 = regionDepthName2.orEmpty(),
    regionDepthName3 = regionDepthName3.orEmpty(),
    roadName = roadName.orEmpty(),
    undergroundYn = undergroundYn.orEmpty(),
    mainBuildingNo = mainBuildingNo.orEmpty(),
    subBuildingNo = subBuildingNo.orEmpty(),
    buildingName = buildingName.orEmpty(),
    zoneNo = zoneNo.orEmpty()
)