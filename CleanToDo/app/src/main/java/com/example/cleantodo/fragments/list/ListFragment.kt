package com.example.cleantodo.fragments.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.cleantodo.R
import com.example.cleantodo.data.models.ToDoData
import com.example.cleantodo.databinding.FragmentListBinding
import com.example.cleantodo.fragments.SharedViewModel
import com.example.cleantodo.fragments.list.adapter.ListAdapter
import com.example.cleantodo.fragments.list.adapter.SwipeToDelete
import com.example.cleantodo.utils.hideKeyboard
import com.example.cleantodo.utils.obserbeOnce
import com.example.cleantodo.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), View.OnClickListener, MenuProvider, SearchView.OnQueryTextListener{

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val msharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.msharedViewModel = msharedViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        //binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        //Animation of Libreria jp.wasabeef
        /*binding.recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }*/
        swipeToDelete(binding.recyclerView)

        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->

            msharedViewModel.checkIfDatabaseEmpty(data)
            adapter.dataList = data
            binding.recyclerView.scheduleLayoutAnimation()

            /*if(data.isNullOrEmpty()) {

                no_data_imageView.visibility = View.VISIBLE
                no_data_textView.visibility = View.VISIBLE

                adapter.dataList = emptyList()

            }else{

                no_data_imageView.visibility = View.GONE
                no_data_textView.visibility = View.GONE

                adapter.dataList = data

            }*/

        })

        /*msharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer { emptyDatabase ->

            showEmptyDatabaseView(emptyDatabase)

        })*/

        //floatingActionButton.setOnClickListener(this)
        //listLayout.setOnClickListener(this)

    }

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete(){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedItem: ToDoData = adapter.dataList[viewHolder.adapterPosition]

                //Delete Item
                mToDoViewModel.deleteData(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                //Toast.makeText(requireContext(), "Successfully Removed: '${deletedItem.title}'", Toast.LENGTH_SHORT).show()

                //Restore Deleted Item
                restoreDeletedData(
                    viewHolder.itemView,
                    deletedItem
                )

            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData){
        val snackBar: Snackbar = Snackbar.make(
            view,
            "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            mToDoViewModel.insert(deletedItem)
        }
        snackBar.show()
    }

    /*private fun showEmptyDatabaseView(emptyDatabase: Boolean) {

        if(emptyDatabase){

            binding.noDataImageView.visibility = View.VISIBLE
            binding.noDataTextView.visibility = View.VISIBLE

        }else{

            binding.noDataImageView.visibility = View.GONE
            binding.noDataTextView.visibility = View.GONE

        }

    }*/

    override fun onClick(v: View?) {

        when (v?.id) {

            /*R.id.floatingActionButton -> {

                findNavController().navigate(R.id.action_listFragment_to_addFragment)

            }*/

            /*R.id.listLayout -> {

                findNavController().navigate(R.id.action_listFragment_to_updateFragment)

            }*/

        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView: SearchView? = search.actionView as? SearchView
        //searchView?.maxWidth = Integer.MAX_VALUE
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {

            R.id.menu_delete_all -> {

                confirmDeleteAll()

                true
            }

            R.id.menu_priority_high -> {

                mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner, Observer{
                    adapter.dataList = it
                })

                true
            }

            R.id.menu_priority_low -> {

                mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner, Observer{
                    adapter.dataList = it
                })

                true
            }

            else -> false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchThroughtDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchThroughtDatabase(query)
        }
        return true
    }

    private fun searchThroughtDatabase(query: String) {
        val searchQuery: String = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).obserbeOnce(viewLifecycleOwner, Observer{ list ->

            list?.let{

                adapter.dataList = it

            }

        })
    }

    private fun confirmDeleteAll() {

        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->

            mToDoViewModel.deleteAllData()

            Toast.makeText(requireContext(), "Successfully Delete All!", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton("No"){_, _ ->

        }
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all?")
        builder.create().show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}