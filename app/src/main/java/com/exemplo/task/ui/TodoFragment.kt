package com.exemplo.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentTodoBinding
import com.exemplo.task.helper.FirebaseHelper
import com.exemplo.task.helper.getFormatedString
import com.exemplo.task.helper.showBottomSheet
import com.exemplo.task.model.Task
import com.exemplo.task.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TodoFragment: Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val tasksList = mutableListOf<Task>()

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getTasks()
    }

    private fun initClicks() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_taskFormFragment)
        }
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
                            if(task.status == 0) {
                                tasksList.add(task)
                            }
                        }

                        if(tasksList.isNotEmpty()) {
                            binding.textInfo.text = ""
                        } else {
                            binding.textInfo.text = getText(R.string.text_info_no_tasks_found_with_todo_status)
                        }

                        tasksList.reverse()
                        initAdapter()
                    } else {
                        binding.textInfo.text = getText(R.string.text_info_no_task_registered)
                    }

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(requireContext(), getText(R.string.error_getting_todo_tasks_list), Toast.LENGTH_LONG).show()
                    showBottomSheet(null, R.string.text_button_bottom_sheet, getFormatedString(R.string.error_getting_todo_tasks_list))
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

//                Toast.makeText(requireContext(), getText(R.string.msg_success_delete_task), Toast.LENGTH_LONG).show()
                showBottomSheet(R.string.text_success, R.string.text_ok, getString(R.string.msg_success_delete_task))
            }

            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToTaskFormFragment(task)

                findNavController().navigate(action)
            }

            TaskAdapter.SELECT_NEXT -> {
                task.status = 1

                updateTask(task)
            }
        }
    }

    private fun updateTask(task: Task) {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
//                    Toast.makeText(requireContext(), getString(R.string.message_on_complete_edit_task), Toast.LENGTH_LONG).show()
                    showBottomSheet(R.string.text_success, R.string.text_ok, getString(R.string.message_on_complete_edit_task))
                } else {
//                    Toast.makeText(requireContext(), getString(R.string.error_on_failure_edit_task, task.exception?.message.toString()), Toast.LENGTH_LONG).show()
                    showBottomSheet(null, R.string.text_button_bottom_sheet, getString(R.string.error_on_failure_edit_task, task.exception?.message.toString()))
                }
            }.addOnFailureListener { error ->
                binding.progressBar.isVisible = false

//                Toast.makeText(requireContext(), getString(R.string.error_on_failure_edit_task, error.message.toString()), Toast.LENGTH_LONG).show()
                showBottomSheet(null, R.string.text_button_bottom_sheet, getString(R.string.error_on_failure_edit_task, error.message.toString()))
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