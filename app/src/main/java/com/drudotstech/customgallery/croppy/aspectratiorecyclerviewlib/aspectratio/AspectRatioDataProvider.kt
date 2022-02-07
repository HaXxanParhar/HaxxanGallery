package com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio

import com.drudotstech.customgallery.R
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.model.AspectRatio
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.model.AspectRatioItem

object AspectRatioDataProvider {

    private val aspectRatioViewStateList = arrayListOf<AspectRatioItemViewState>()

    fun getAspectRatioViewStateList(
        activeColor: Int,
        passiveColor: Int,
        socialActiveColor: Int,
        socialPassiveColor: Int
    ): List<AspectRatioItemViewState> {
        if (aspectRatioViewStateList.isEmpty()) {
            loadAspects(activeColor, passiveColor, socialActiveColor, socialPassiveColor)
        }
        return aspectRatioViewStateList
    }

    private fun loadAspects(
        activeColor: Int,
        passiveColor: Int,
        socialActiveColor: Int,
        socialPassiveColor: Int
    ) {
        aspectRatioViewStateList.clear()
        getAspectRatioList(
            activeColor,
            passiveColor,
            socialActiveColor,
            socialPassiveColor
        ).forEach {
            aspectRatioViewStateList.add(
                AspectRatioItemViewState(
                    aspectRatioItem = it,
                    isSelected = false
                )
            )
        }
    }

    private fun getAspectRatioList(
        activeColor: Int,
        passiveColor: Int,
        socialActiveColor: Int,
        socialPassiveColor: Int
    ): List<AspectRatioItem> {


        return arrayListOf(

            // Original
            AspectRatioItem(
                aspectRatioSelectedWidthRes = R.dimen.width_aspect_square,
                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_square,
                aspectRatioNameRes = R.string.original,
                aspectRatio = AspectRatio.ASPECT_SQUARE,
                activeColor = activeColor,
                passiveColor = passiveColor,
                socialActiveColor = socialActiveColor,
                socialPassiveColor = socialPassiveColor
            ),

            // Square
            AspectRatioItem(
                aspectRatioSelectedWidthRes = R.dimen.width_aspect_square,
                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_square,
                aspectRatioNameRes = R.string.square,
                aspectRatio = AspectRatio.ASPECT_SQUARE,
                activeColor = activeColor,
                passiveColor = passiveColor,
                socialActiveColor = socialActiveColor,
                socialPassiveColor = socialPassiveColor
            ),

            // Portrait
            AspectRatioItem(
                aspectRatioSelectedWidthRes = R.dimen.width_aspect_portrait,
                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_portrait,
                aspectRatioNameRes = R.string.portrait,
                aspectRatio = AspectRatio.ASPECT_PORTRAIT,
                activeColor = activeColor,
                passiveColor = passiveColor,
                socialActiveColor = socialActiveColor,
                socialPassiveColor = socialPassiveColor
            ),

            // Landscape
            AspectRatioItem(
                aspectRatioSelectedWidthRes = R.dimen.width_aspect_landscape,
                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_landscape,
                aspectRatioNameRes = R.string.landscape,
                aspectRatio = AspectRatio.ASPECT_LANDSCAPE,
                activeColor = activeColor,
                passiveColor = passiveColor,
                socialActiveColor = socialActiveColor,
                socialPassiveColor = socialPassiveColor
            )
//            ,
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_ins_1_1,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_ins_1_1,
//                aspectRatioNameRes = R.string.aspect_ins_1_1,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_instagram,
//                aspectRatio = AspectRatio.ASPECT_INS_1_1,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_ins_4_5,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_ins_4_5,
//                aspectRatioNameRes = R.string.aspect_ins_4_5,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_instagram,
//                aspectRatio = AspectRatio.ASPECT_INS_4_5,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_ins_story,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_ins_story,
//                aspectRatioNameRes = R.string.aspect_ins_story,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_instagram,
//                aspectRatio = AspectRatio.ASPECT_INS_STORY,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_5_4,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_5_4,
//                aspectRatioNameRes = R.string.aspect_5_4,
//                aspectRatio = AspectRatio.ASPECT_5_4,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_3_4,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_3_4,
//                aspectRatioNameRes = R.string.aspect_3_4,
//                aspectRatio = AspectRatio.ASPECT_3_4,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_4_3,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_4_3,
//                aspectRatioNameRes = R.string.aspect_4_3,
//                aspectRatio = AspectRatio.ASPECT_4_3,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_face_post,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_face_post,
//                aspectRatioNameRes = R.string.aspect_face_post,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_facebook,
//                aspectRatio = AspectRatio.ASPECT_FACE_POST,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_face_cover,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_face_cover,
//                aspectRatioNameRes = R.string.aspect_face_cover,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_facebook,
//                aspectRatio = AspectRatio.ASPECT_FACE_COVER,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_pin_post,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_pin_post,
//                aspectRatioNameRes = R.string.aspect_pin_post,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_pinterest,
//                aspectRatio = AspectRatio.ASPECT_PIN_POST,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_3_2,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_3_2,
//                aspectRatioNameRes = R.string.aspect_3_2,
//                aspectRatio = AspectRatio.ASPECT_3_2,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_9_16,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_9_16,
//                aspectRatioNameRes = R.string.aspect_9_16,
//                aspectRatio = AspectRatio.ASPECT_9_16,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_16_9,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_16_9,
//                aspectRatioNameRes = R.string.aspect_16_9,
//                aspectRatio = AspectRatio.ASPECT_16_9,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_1_2,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_1_2,
//                aspectRatioNameRes = R.string.aspect_1_2,
//                aspectRatio = AspectRatio.ASPECT_1_2,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_you_cover,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_you_cover,
//                aspectRatioNameRes = R.string.aspect_you_cover,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_youtube,
//                aspectRatio = AspectRatio.ASPECT_YOU_COVER,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_twit_post,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_twit_post,
//                aspectRatioNameRes = R.string.aspect_twit_post,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_twitter,
//                aspectRatio = AspectRatio.ASPECT_TWIT_POST,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_twit_header,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_twit_header,
//                aspectRatioNameRes = R.string.aspect_twit_header,
//                socialMediaImageRes = R.drawable.ic_aspect_icon_twitter,
//                aspectRatio = AspectRatio.ASPECT_TWIT_HEADER,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_a_4,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_a_4,
//                aspectRatioNameRes = R.string.aspect_a_4,
//                aspectRatio = AspectRatio.ASPECT_A_4,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            ),
//            AspectRatioItem(
//                aspectRatioSelectedWidthRes = R.dimen.width_aspect_a_5,
//                aspectRatioUnselectedHeightRes = R.dimen.height_aspect_a_5,
//                aspectRatioNameRes = R.string.aspect_a_5,
//                aspectRatio = AspectRatio.ASPECT_A_5,
//                activeColor = activeColor,
//                passiveColor = passiveColor,
//                socialActiveColor = socialActiveColor,
//                socialPassiveColor = socialPassiveColor
//            )
        )
    }
}