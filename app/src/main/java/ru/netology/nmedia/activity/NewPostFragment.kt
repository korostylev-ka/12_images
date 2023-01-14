package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.edit.requestFocus()

        //регистрируем обработчик для контракта
        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    //если ошибка, выводим уведомление
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    //если нет ошибки, получаем адрес картинки и передаем во viewmodel
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }
        //обработка нажатия на кнопку выбрать фото из галереи
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                    //передаем ссылку на лаунч контракта
                .createIntent(pickPhotoLauncher::launch)
        }

        //обработка нажатия на кнопку сделать снимок с камеры
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                    //формируем интент, который запускается через контракт
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }
        //отображаем меню menu_new_post, меню собирается в activity
        //для метода передаем интерфей, который реализует menuprovider
        requireActivity().addMenuProvider(object : MenuProvider {
            //создаем меню(1 параметр - ссылка на меню, 2 - меню, куда добавляется
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }
            //обработка нажатия
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    //обработчик сохранения
                    R.id.save -> {
                        fragmentBinding?.let {
                            viewModel.changeContent(it.edit.text.toString())
                            viewModel.save()
                            AndroidUtils.hideKeyboard(requireView())
                        }
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}