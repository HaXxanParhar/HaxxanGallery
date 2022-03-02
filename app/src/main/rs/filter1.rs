#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.drudotstech.customgallery)

#include "rs_debug.rsh"

//Method to keep the result between 0 and 255
static float boundColor (float val) {
    float m = fmax(0.0f, val);
    return fmin(255.0f, m);
}

//Method to keep the result between 0 and 255
static float boundNum(float val,float small, float large) {
    float m = fmax(small, val);
    return fmin(large, m);
}

uchar4 __attribute__((kernel)) root(uchar4 original, uchar4 filter, uint32_t x, uint32_t y) {
    //Convert input uchar4 to float4
    float4 originalColor = rsUnpackColor8888(original);
    float4 filterColor = rsUnpackColor8888(filter);


    float R = boundColor(originalColor.r + filterColor.r);
    float G = boundColor(originalColor.g + filterColor.g);
    float B = boundColor(originalColor.b + filterColor.b);

    return rsPackColorTo8888(R, G, B);
}
