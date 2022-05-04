package com.exemplo.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentDoneBinding
import com.exemplo.task.helper.FirebaseHelper
import com.exemplo.task.model.Task
import com.exemplo.task.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment: Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private val tasksList = mutableListOf<Task>()

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentDoneBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTasks()
    }

    private fun getTasks() {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tasksList.clear()

                    if(snapshot.exists()) {
                        for(snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task
                            if(task.status == 2) {
                                tasksList.add(task)
                            }
                        }

                        if(tasksList.isNotEmpty()) {
                            binding.textInfo.text = ""
                        } else {
                            binding.textInfo.text = getText(R.string.text_info_no_tasks_found_with_done_status)
                        }

                        tasksList.reverse()
                        initAdapter()
                    } else {
                        binding.textInfo.text = getText(R.string.text_info_no_task_registered)
                    }

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), getText(R.string.error_getting_done_tasks_list), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)

        taskAdapter = TaskAdapter(requireContext(), tasksList) { task, select ->
            optionSelected(task, select)
        }

        binding.rvTask.adapter = taskAdapter
    }

    private fun optionSelected(task: Task, select: Int) {
        when(select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)

                Toast.makeText(requireContext(), getText(R.string.msg_success_delete_task), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteTask(task: Task) {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .removeValue()

        tasksList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}