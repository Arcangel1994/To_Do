package com.example.cleantodo.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cleantodo.R
import com.example.cleantodo.data.models.ToDoData
import com.example.cleantodo.databinding.FragmentUpdateBinding
import com.example.cleantodo.fragments.SharedViewModel
import com.example.cleantodo.viewmodel.ToDoViewModel

class UpdateFragment : Fragment(), MenuProvider {

    private val args by navArgs<UpdateFragmentArgs>()
    //private lateinit var currentItem: ToDoData

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val msharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        binding.args = args

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //currentItem = arguments?.getParcelable<ToDoData>("currentItem")!!
        //currentItem = args.currentItem

        //binding.currentTitleEt.setText(currentItem.title)
        //binding.currentPrioritiesSpinner.setSelection(msharedViewModel.parsePriorityToInt(currentItem.priority))
        binding.currentPrioritiesSpinner.onItemSelectedListener = msharedViewModel.listener
        //binding.currentDescriptionEt.setText(currentItem.description)

    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {

            R.id.menu_save -> {

                updateDataToDb()

                true
            }

            R.id.menu_delete -> {

                confirmDeleteDataToDb()

                true
            }

            else -> false
        }
    }

    private fun updateDataToDb() {

        val mcTitle = binding.currentTitleEt.text.toString()
        val mcPriority = binding.currentPrioritiesSpinner.selectedItem.toString()
        val mcDescription = binding.currentDescriptionEt.text.toString()

        val validation = msharedViewModel.verifyDataFromUser(mcTitle, mcDescription)

        if (validation){
            //Update Data
            val updateData: ToDoData = ToDoData(
                args.currentItem.id,
                mcTitle,
                msharedViewModel.parsePriority(mcPriority),
                mcDescription
            )

            mToDoViewModel.updateData(updateData)
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()

            //Navigation Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }else{

            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()

        }

    }

    //Show Dialog
    private fun confirmDeleteDataToDb(){

        val builder :AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->

            mToDoViewModel.deleteData(args.currentItem)

            Toast.makeText(requireContext(), "Successfully Delete!: ${args.currentItem.title}", Toast.LENGTH_SHORT).show()

            //Navigation Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }
        builder.setNegativeButton("No"){_, _ ->

        }
        builder.setTitle("Delete '${args.currentItem.title}'?")
        builder.setMessage("Are you sure you want to delete '${args.currentItem.title}'?")
        builder.create().show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}