package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.FragmentImageAttachmentBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class ImageAttachmentFragment : Fragment() {

    companion object {
        private const val IMAGE = "image"
        //для получения ссылки на изображение
        fun createArguments(url: String): Bundle {
            return bundleOf(IMAGE to url)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageAttachmentBinding.inflate(
            inflater,
            container,
            false
        )
        //получаем ссылку на изображение
        var imageUrl = requireArguments().getString(IMAGE)
        val url ="http://10.0.2.2:9999/media/${imageUrl}"
        //загружаем изображение из вложения
        Glide.with(binding.imageAttachment)
            .load(url)
            .timeout(10_000)
            .into(binding.imageAttachment)

    return binding.root
    }
}