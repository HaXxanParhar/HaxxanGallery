package com.drudotstech.customgallery.croppy.croppylib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drudotstech.customgallery.R
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.AspectRatioItemViewState
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.model.AspectRatio
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.model.AspectRatioItem
import com.drudotstech.customgallery.croppy.croppylib.main.*
import com.drudotstech.customgallery.croppy.croppylib.state.CropFragmentViewState
import com.drudotstech.customgallery.croppy.croppylib.util.delegate.inflate
import com.drudotstech.customgallery.databinding.FragmentImageCropBinding


class ImageCropFragment : Fragment(), SelectAspectRatioCallback, BlurBitmapCallback {

    private lateinit var ratioList: MutableList<AspectRatioItemViewState>
    private val binding: FragmentImageCropBinding by inflate(R.layout.fragment_image_crop)

    private lateinit var viewModel: ImageCropViewModel

    var onApplyClicked: ((CroppedBitmapData) -> Unit)? = null

    var onCancelClicked: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ImageCropViewModel::class.java)

        val cropRequest = arguments?.getParcelable(KEY_BUNDLE_CROP_REQUEST) ?: CropRequest.empty()
        viewModel.setCropRequest(cropRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getCropRequest()?.let {
            binding.cropView.setTheme(it.croppyTheme)
        }

        val metrics = context?.resources?.displayMetrics
        val w = metrics?.widthPixels?.toFloat()
        val h = metrics?.heightPixels?.toFloat()

        ratioList = mutableListOf<AspectRatioItemViewState>()
//        val a0 = AspectRatioItem(R.string.fullscreen, AspectRatio.ASPECT_FULLSCREEN)
//        ratioList.add(AspectRatioItemViewState(a0, true))
        val a1 = AspectRatioItem(R.string.square, AspectRatio.ASPECT_SQUARE)
        ratioList.add(AspectRatioItemViewState(a1, true))
        val a2 = AspectRatioItem(R.string.portrait, AspectRatio.ASPECT_PORTRAIT)
        ratioList.add(AspectRatioItemViewState(a2, false))
        val a3 = AspectRatioItem(R.string.landscape, AspectRatio.ASPECT_LANDSCAPE)
        ratioList.add(AspectRatioItemViewState(a3, false))
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = AspectRatioAdapter(context, ratioList, this)


//        binding.recyclerViewAspectRatios.setItemSelectedListener {
//            binding.cropView.setAspectRatio(it.aspectRatioItem.aspectRatio)
//            viewModel.onAspectRatioChanged(it.aspectRatioItem.aspectRatio)
//        }

        binding.ivBack.setOnClickListener {
            onCancelClicked?.invoke()
        }

        binding.tvNext.setOnClickListener {
            binding.rlLoading.visibility = VISIBLE;
            onApplyClicked?.invoke(binding.cropView.getCroppedData())
        }

        with(binding.cropView) {

            onInitialized = {
                viewModel.updateCropSize(binding.cropView.getCropSizeOriginal())
            }

            observeCropRectOnOriginalBitmapChanged = {
                viewModel.updateCropSize(binding.cropView.getCropSizeOriginal())
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel
            .getCropViewStateLiveData()
            .observe(viewLifecycleOwner, Observer(this@ImageCropFragment::renderViewState))

        viewModel
            .getResizedBitmapLiveData()
            .observe(viewLifecycleOwner, Observer {
                binding.cropView.setBitmap(it.bitmap)
                //then create a copy of bitmap bmp1 into bmp2
                binding.rlLoading.visibility = View.VISIBLE

                val newBitmap = it.bitmap?.copy(it.bitmap.config, true)
                BlurTask(context, newBitmap, 22, this).execute()
            })
    }

    override fun onResume() {
        super.onResume()
    }

    private fun renderViewState(cropFragmentViewState: CropFragmentViewState) {
        binding.viewState = cropFragmentViewState
        binding.executePendingBindings()
    }


    companion object {

        private const val KEY_BUNDLE_CROP_REQUEST = "KEY_BUNDLE_CROP_REQUEST"

        @JvmStatic
        fun newInstance(cropRequest: CropRequest): ImageCropFragment {
            return ImageCropFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BUNDLE_CROP_REQUEST, cropRequest)
                }
            }
        }
    }

    override fun onAspectRatioSelected(aspectRatioItem: AspectRatioItem?) {
        aspectRatioItem?.let {
            binding.cropView.setAspectRatio(it.aspectRatio)
            viewModel.onAspectRatioChanged(it.aspectRatio)
        }
    }

    override fun onBlurCompleted(bitmapResult: BitmapResult?) {
        binding.rlLoading.visibility = View.GONE

        if (bitmapResult!!.isStatus) {
            binding.ivBackground.setImageBitmap(bitmapResult.bitmap)
            binding.cropView.setBlurredBitmap(bitmapResult.bitmap)
        }

        // to change the crop Rect according to the first item.
        if (ratioList.size > 0) {
            binding.cropView.setAspectRatio(ratioList[0].aspectRatioItem.aspectRatio)
            viewModel.onAspectRatioChanged(ratioList[0].aspectRatioItem.aspectRatio)
        }
    }

}