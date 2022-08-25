package com.example.cleantodo.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.cleantodo.R
import com.example.cleantodo.data.models.ToDoData
import com.example.cleantodo.databinding.FragmentAddBinding
import com.example.cleantodo.fragments.SharedViewModel
import com.example.cleantodo.viewmodel.ToDoViewModel

class AddFragment : Fragment(), MenuProvider {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val msharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prioritiesSpinner.onItemSelectedListener = msharedViewModel.listener

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {

            R.id.menu_add -> {

                insertDataToDb()

                true
            }

            else -> false
        }
    }

    private fun insertDataToDb() {

        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        val validation = msharedViewModel.verifyDataFromUser(mTitle, mDescription)

        if (validation){
            //Insert Data
            val newData: ToDoData = ToDoData(
                0,
                mTitle,
                msharedViewModel.parsePriority(mPriority),
                mDescription
            )

            mToDoViewModel.insert(newData)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()

            //Navigation Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)

        }else{

            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}