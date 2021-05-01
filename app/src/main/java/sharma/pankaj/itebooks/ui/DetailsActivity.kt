package sharma.pankaj.itebooks.ui

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.databinding.ActivityDetailsBinding
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.CustomLoading
import sharma.pankaj.itebooks.util.CustomTabHelper
import sharma.pankaj.itebooks.util.toast
import sharma.pankaj.itebooks.viewmodel.BookDetailViewModel


class DetailsActivity : AppCompatActivity(), HomeRequestListener, KodeinAware {

    override val kodein by kodein()
    private val factory: DetailsViewModelFactory by instance()
    private var dialog: Dialog? = null
    lateinit var binding: ActivityDetailsBinding
    private var customTabHelper: CustomTabHelper = CustomTabHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_details
        )
        val viewModel = ViewModelProvider(this, factory).get(BookDetailViewModel::class.java)
        binding.lifecycleOwner = this
        binding.detailviewmodel = viewModel
        viewModel.listener = this

        dialog = CustomLoading.progressDialog(this)
        val value: Bundle = intent.extras!!
        val id: String = value.getString("ID").toString()
        if (id.isNotEmpty()) {
            viewModel.sendBookDetailsRequest(id)
        } else {
            toast("Error")
        }

//        binding.chipsGroup.setOnCheckedChangeListener { chipGroup, i ->
//
//            val chip: Chip = chipGroup.findViewById(i)
//            toast("${chip.text}")
//
//        }

    }

    override fun onMessage(msg: String) {
        toast(msg)
    }

    override fun onStartRequest() {
        dialog?.show()

    }

    override fun onStopRequest() {
        dialog?.dismiss()

    }

    override fun onHomeResponse(data: List<Data>) {

    }

    override fun onHomeResponse(data: sharma.pankaj.itebooks.data.network.responses.Data) {
        dialog?.dismiss()

        Picasso.get().load(data.imageUrl)
            .error(R.drawable.ic_placeholder_image_24)
            .tag("loading...")
            .into(binding.image)

        binding.title.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>Title : </b>${data.title}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>Title : </b>${data.title}")
        }


        binding.author.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>Author : </b>${data.author}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>Author : </b>${data.author}")
        }

        binding.isbn.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>ISBN No. : </b>${data.isbnNumber}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>ISBN No. : </b>${data.isbnNumber}")
        }

        binding.year.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>Year : </b>${data.year}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>Year : </b>${data.year}")
        }

        binding.language.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>Language : </b>${data.language}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>Language : </b>${data.language}")
        }

        binding.pages.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>No. Of Pages : </b>${data.pages}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>No. Of Pages : </b>${data.pages}")
        }

        binding.fileSize.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>File Size : </b>${data.fileSize}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>File Size : </b>${data.fileSize}")
        }

        binding.fileFormat.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>File Format : </b>${data.fileFormat}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("<b>File Format : </b>${data.fileFormat}")
        }

        binding.description.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                "<p align=\"justify\">${data.description}</p>",
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            Html.fromHtml("<p align=\"justify\">${data.description}</p>")
        }

        if (data.fileFormat.contains(",")) {
            val download = data.pdfUrl.split(",").toTypedArray()
            download.forEachIndexed { index, s ->
                addChips("Download $index", binding.chipsGroup)
            }
        } else {
            addChips("Download", binding.chipsGroup)
        }

        binding.chipsGroup.setOnCheckedChangeListener { chipGroup, i ->
            val chip: Chip = chipGroup.findViewById(i)
            val download = data.pdfUrl.split(",").toTypedArray()
            download.forEachIndexed { index, s ->
                if (download[index].contains(".html") || download[index].contains(".htm")) {
                    web(s)
                } else {
                    web(s)
                }
            }
        }
    }

    private fun addChips(menu: String?, chipsGroup: ChipGroup) {
        val chip = Chip(this)
        chip.text = menu
        chip.setChipBackgroundColorResource(R.color.purple_500)
        chip.isCheckable = true
        chip.setTextColor(resources.getColor(R.color.white))
        chipsGroup.addView(chip)

    }

    private fun web(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@DetailsActivity, R.color.purple_700))
        builder.addDefaultShareMenuItem()
        val anotherCustomTab = CustomTabsIntent.Builder().build()
        val intent = anotherCustomTab.intent
        intent.data = Uri.parse(url)
        builder.setShowTitle(true)
        builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
        builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
        val customTabsIntent = builder.build()
        val packageName = customTabHelper.getPackageNameToUse(this, url)
        customTabsIntent.intent.setPackage(packageName)
        customTabsIntent.launchUrl(this, Uri.parse(url))

    }
}