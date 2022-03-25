#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.drudotstech.filterfactory)

float dY;
float dCb;
float dCr;
int isBW;

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

uchar4 __attribute__((kernel)) root(uchar4 input) {

    float4 pixal = rsUnpackColor8888(input);

    // RGB values here have the range of 0.00 - 1.00
    float r = pixal.r;
    float g = pixal.g;
    float b = pixal.b;

    // change the range from 0-1 to 0-255
    r = r * 255.0;
    g = g * 255.0;
    b = b * 255.0;

    // ----------------------------  converting RGB to YCbCr  --------------------------------------

    // from RGB (range 0-255) to YCbCr
    // Y  = (   0.257 R   +   0.504 G   +   0.098 B  ) +   16 // range = 16 - 235 (220 levels) 16 is black & 235 is white
    // Cb = ( - 0.148 R   -   0.291 G   +   0.439 B  ) +  128 // range = 16 - 240 (128 being the middle point)
    // Cr = (   0.439 R   -   0.368 G   -   0.071 B  ) +  128 // range = 16 - 240 (128 being the middle point)

    float Y = ( 0.257f * r  +  0.504f * g  +  0.098f * b) + 16;

    // Blue Cb (0 - 128)
    float Cb = ( -0.148f * r  -  0.291f * g  +  0.439f * b) + 128;

    // Red Cr (0 - 128)
    float Cr = ( 0.439f * r  -  0.368f * g  -  0.070f * b) + 128 ;


    // ---------------------------------- applying filter ------------------------------------------
    Y = Y + dY;
    boundNum(Y,16.0,235.0);

    if(isBW == 0){
        Cb = Cb + dCb;
        Cr = Cr + dCr;

        // bound within specified range
        Cb = boundNum(Cb,16.0,240.0);
        Cr = boundNum(Cr,16.0,240.0);

        // to convert back to RGB, these values should be in proper range
        Y = Y - 16;
        Cb = Cb - 128;
        Cr = Cr - 128;
    } else {
        Cb = 0;
        Cr = 0;
    }

    // ------------------------------  converting YCbCr back to RGB  ------------------------------------
    // R  = (   1.164 Y   +   0.000 Cb   +   1.596 Cr  ) *  Y- 16
    // G  = (   1.164 Y   -   0.392 Cb   -   0.813 Cr   ) +  Cb - 128
    // B  = (   1.164 R   +   2.017 Cb   +   0.000 Cr   ) +  Cr - 128

    float R,G,B;

    // Red R (0 - 255)
    R = ( 1.164f * Y  +  1.596f * Cr );

    // Green G (0 - 255)
    G = ( 1.164f * Y  -  0.392f * Cb  -  0.813f * Cr ) ;

    // BLue B (0 - 255)
    B = ( 1.164f * Y  +  2.017f * Cb  ) ;

    R = boundColor(R);
    G = boundColor(G);
    B = boundColor(B);

    //Put the values in the output uchar4, note that we keep the alpha value
    R = R / 255.0;
    G = G / 255.0;
    B = B / 255.0;

    return rsPackColorTo8888(R, G, B);
}