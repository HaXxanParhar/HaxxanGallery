#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.drudotstech.customgallery)

#include "rs_debug.rsh"

int32_t histo[256];
float remapArray[256];
int size;

//Method to keep the result between 0 and 1
static float bound (float val) {
    float m = fmax(0.0f, val);
    return fmin(1.0f, m);
}

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

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);

    // from RGB to YCbCr
    // Y  = (   0.257 R   +   0.504 G   +   0.098 B  ) *   16
    // Cb = ( - 0.148 R   -   0.291 G   +   0.439 B  ) *  128
    // Cr = (   0.439 R   -   0.368 G   -   0.071 B  ) *  128

    // Brighness Y (0 - 16)
    f4.r = f4.r * 255.0;
    f4.g = f4.g * 255.0;
    f4.b = f4.b * 255.0;

// 125.5225
    float Y = ( 0.257f * f4.r  +  0.504f * f4.g  +  0.098f * f4.b) + 16;
    Y = boundNum(Y,16.0,235.0);

    // Blue Cb (0 - 128)
    // 128
    float Cb = ( -0.148f * f4.r  -  0.291f * f4.g  +  0.439f * f4.b) + 128;
        Cb = boundNum(Cb,16.0,240.0);

//128.12
    // Red Cr (0 - 128)
    float Cr = ( 0.439f * f4.r  -  0.368f * f4.g  -  0.070f * f4.b) + 128 ;
            Cr = boundNum(Cr,16.0,240.0);



// ------------------------------  A P P L Y   F I L T E R  ----------------------------------------

        // increasing Cb - Blueness
       if(f4.b > 200){
        Cb = Cb * 1.02;
       }  if(f4.b >= 150 && f4.b <= 200){
        Cb = Cb * 1.05;
       } else  if(f4.b >= 100 && f4.b < 150){
             Cb = Cb * 1.15;
       } else {
        Cb = Cb * 1.2;
       }

        // Increasing Brightness
      Y = Y * 1.15;

      // decreasing Redness
      Cr = Cr * 0.9;



    // from  YCbCr to RGB
    // R  = (   1.164 Y   +   0.000 Cb   +   1.596 Cr  ) *  Y- 16
    // G  = (   1.164 Y   -   0.392 Cb   -   0.813 Cr   ) +  Cb - 128
    // B  = (   1.164 R   +   2.017 Cb   +   0.000 Cr   ) +  Cr - 128

    Y = Y - 16;
    Cb = Cb - 128;
    Cr = Cr - 128;

    // Red R (0 - 255)
    float R = ( 1.164f * Y  +  1.596f * Cr );
    R = boundColor(R);

    // Green G (0 - 255)
    float G = ( 1.164f * Y  -  0.392f * Cb  -  0.813f * Cr ) ;
        G = boundColor(G);


    // BLue B (0 - 255)
    float B = ( 1.164f * Y  +  2.017f * Cb  ) ;
            B = boundColor(B);

           // rsDebug(" --------------------- x y ------> ",x,y);
           //rsDebug("R ----------------------> ", R);
             //  rsDebug("G ----------------------> ", G);
               //rsDebug("B ----------------------> ", B);


    //Put the values in the output uchar4, note that we keep the alpha value
    R = R / 255.0;
    G = G / 255.0;
    B = B / 255.0;
    return rsPackColorTo8888(R, G, B);
}

uchar4 __attribute__((kernel)) remaptoRGB(uchar4 in, uint32_t x, uint32_t y) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);

    //Get Y value
    float Y = f4.r;
    //Get Y value between 0 and 255 (included)
    int32_t val = Y * 255;
    //Get Y new value in the map array
    Y = remapArray[val];

    //Get value for U and V channel (back to their original values)
   // float U = (2*f4.g)-1;
    float U = f4.g;
    //float V = (2*f4.b)-1;
    float V = f4.b;

    //Compute values for red, green and blue channels
    float red = bound(Y + 1.14f * V);
    float green = bound(Y - 0.395f * U - 0.581f * V);
    float blue = bound(Y + 2.033f * U);

    //Put the values in the output uchar4
    return rsPackColorTo8888(red, green, blue, f4.a);
}

void init() {
    //init the array with zeros
    for (int i = 0; i < 256; i++) {
        histo[i] = 0;
        remapArray[i] = 0.0f;
    }
}

void createRemapArray() {
    //create map for y
    //float sum = 0;
    for (int i = 0; i < 256; i++) {
        //sum += histo[i];
        remapArray[i] = histo[i]; // (size);
    }
}